package vertefil.tanks;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;

//Карта
public class Map {
    //Создали енам для стен
    public enum WallType {
        //Виды препятствий
        HARD(0, 5, true, false, false),
        SOFT(1, 3, true, false, false),
        WATER(3,1,false, false, true),
        INDESTRUCTIBLE(2, 1, false, false, false),
        NONE(0, 0, false, true, true);

        //С верху вниз по картинке
        int index;
        int maxHp; //Здоровье
        //booleans на проходимость, разрушаемость и тп
        boolean destructible; //Разрушаема или нет?
        boolean isUnitPassable; //Танк пройдёт или нет?
        boolean isProjectilePassable; //Пуля пройдёт или нет?


        //Конструктор, который хранит св-ва стены
        WallType(int index, int maxHp, boolean destructible,boolean isUnitPassable, boolean isProjectilePassable) {
            this.index = index;
            this.maxHp = maxHp;
            this.destructible = destructible;
            this.isUnitPassable = isUnitPassable;
            this.isProjectilePassable = isProjectilePassable;
        }
    }

    //Класс для клетки, чтобы распознавать какая она
    private class Cell {
        WallType type;
        int hp;

        //Хар-ки клетки
        public Cell(WallType type) {
            this.type = type;
            this.hp = type.maxHp;
        }

        public void damage() {
            if (type.destructible) {
                hp--;
                if (hp <= 0) {
                    type = WallType.NONE;
                }
            }
        }

        //Метод смены стен
        public void changeType(WallType type) {
            this.type = type;
            this.hp = type.maxHp;
        }
    }

    public static final int SIZE_X = 64;
    public static final int SIZE_Y = 36;
    public static final int CELL_SIZE = 20;
    // TextureRegion ссылается на частичку текстуры
    private TextureRegion grassTexture;

    //Двумерный массив для регионов для стен
    private TextureRegion[][] wallsTexture;
    //Инициализация клеток
    private Cell cells[][];

    public Map(TextureAtlas atlas) {
        //Обращаемся к атласу, и тк он был запакован из пнг с названиями
        //Мы можем найти пиксели нужной текстуры через название
        this.grassTexture = atlas.findRegion("grass40");
        //Создали и заполнили двумерный массив разбив его на картинки по CELL_SIZE
        this.wallsTexture = new TextureRegion(atlas.findRegion("obstacles")).split(CELL_SIZE, CELL_SIZE);
        this.cells = new Cell[SIZE_X][SIZE_Y];
        //Генерим карту
        for (int i = 0; i < SIZE_X; i++) {
            for (int j = 0; j < SIZE_Y; j++) {
                cells[i][j] = new Cell(WallType.NONE);
                //Тип генерации шахматная доска
                int cx = i / 4;
                int cy = j / 4;

                if (cx % 2 == 0 && cy % 2 == 0) {
                    //Добавляем разные препятствия
                    if (MathUtils.random() < 0.7f) {
                       cells[i][j].changeType(WallType.HARD);
                       //cells[i][j].changeType(WallType.WATER);
                    } else {
                        cells[i][j].changeType(WallType.SOFT);

                    }
                }
            }
        }
        //Сверху и снизу поставили непробиваемые стены
        for (int i = 0; i < SIZE_X; i++) {
            cells[i][0].changeType(WallType.INDESTRUCTIBLE);
            cells[i][SIZE_Y - 1].changeType(WallType.INDESTRUCTIBLE);
        }
        for (int i = 0; i < SIZE_Y; i++) {
            cells[0][i].changeType(WallType.INDESTRUCTIBLE);
            cells[SIZE_X - 1][i].changeType(WallType.INDESTRUCTIBLE);
        }

    }

    // Метод разрушения стены
    public void checkWallAndBulletsCollisions(Bullet bullet) {
        //Узнаём позицию пули
        int cx = (int) (bullet.getPostion().x / CELL_SIZE);
        int cy = (int) (bullet.getPostion().y / CELL_SIZE);

        if (cx >= 0 && cy >= 0 && cx <= SIZE_X && cy <= SIZE_Y) {
            //Если непроходима для пули, то получает урон
            if (!cells[cx][cy].type.isProjectilePassable) {
                cells[cx][cy].damage();
                bullet.deactivate();
            }
        }
    }


    //Метод для коллизии
    public boolean isAreaClear(float x, float y, float halfSize) {
        //Найдём левые и правые границы
        int leftX = (int) ((x - halfSize) / CELL_SIZE);
        int rightX = (int) ((x + halfSize) / CELL_SIZE);
        //Нижние и верхние границы
        int bottomY = (int) ((y - halfSize) / CELL_SIZE);
        int topY = (int) ((y + halfSize) / CELL_SIZE);
        //Проверка на границы текстуры
        if (leftX < 0) {
            leftX = 0;// Скип исключения
        }
        if (rightX >= SIZE_X) {
            rightX = SIZE_X - 1;
        }
        if (bottomY < 0) {
            bottomY = 0;// Скип исключения
        }
        if (topY >= SIZE_Y) {
            topY = SIZE_Y - 1;
        }

        //Проверка на препятствием
        for (int i = leftX; i <= rightX; i++) {
            for (int j = bottomY; j <= topY; j++) {
                //Если под объектом есть препятствие
                if (!cells[i][j].type.isUnitPassable) {
                    return false;
                }
            }
        }
        return true;
    }

    public void render(SpriteBatch batch) {
        for (int i = 0; i < 1280 / 40; i++) {
            for (int j = 0; j < 720 / 40; j++) {
                batch.draw(grassTexture, i * CELL_SIZE * 2, j * CELL_SIZE * 2);
            }
        }

        for (int i = 0; i < SIZE_X; i++) {
            for (int j = 0; j < SIZE_Y; j++) {
                if (cells[i][j].type != WallType.NONE) {
                    //При рисованни будем обращаться к массиву текстур
                    //Левый индекс картинка по высоте от типа блока
                    //Правая от кол-ва хп, то есть насколько сломана
                    batch.draw(wallsTexture[cells[i][j].type.index][cells[i][j].hp - 1], i * CELL_SIZE, j * CELL_SIZE);
                }
            }
        }
    }
}
