package vertefil.tanks.units;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Vector2;
import vertefil.tanks.GameScreen;
import vertefil.tanks.Item;
import vertefil.tanks.ScreenManager;
import vertefil.tanks.Weapon;
import vertefil.tanks.utils.Direction;
import vertefil.tanks.utils.KeysControl;
import vertefil.tanks.utils.Utils;

//Player наследник танка
public class PlayerTank extends Tank {
    //Основной класс
    //Управление
    KeysControl keysControl;
    //Надписи игроков
    StringBuilder tmpString;
    int index;
    int score;  //Очки
    int lives; //Жизни

    public PlayerTank(int index, GameScreen game, KeysControl keysControl, TextureAtlas atlas) {
        // Инициализируем
        super(game);
        this.index = index;
        this.ownerType = TankOwner.PLAYER;
        this.keysControl = keysControl;
        this.weapon = new Weapon(atlas);
        this.texture = atlas.findRegion("playerTankBase");
        this.textureHp = atlas.findRegion("bar");
        this.position = new Vector2(100.0f, 100.0f);
        speed = 100.0f; //пиксели на секунду
        this.width = texture.getRegionWidth();
        this.height = texture.getRegionHeight();
        this.hpMax = 15;
        this.hp = this.hpMax;
        this.circle = new Circle(position.x, position.y, (width + height) / 2);
        this.lives = 6;
        this.tmpString = new StringBuilder();
    }

    //Метод начисления очков
    public void addScore(int amount) {
        score += amount;
    }

    //Метод Дестрой у плеера
    @Override
    public void destroy() {
        //У плеера кончилось хп, минус жизнь, фул хп
        lives--;
        hp = hpMax;
        if (lives == 0) {

            ScreenManager.getInstance().setScreen(ScreenManager.ScreenType.MENU);
        }
    }

    //Физика
    public void update(float dt) {
        checkMovement(dt);
        // Получаем коорды мышки
        //В LibGdx система отсчёта graphics с лев нижнего угла
        //А input с верхнего левого
        //Получаем правильные пиксели мыши
        //unproject - отдаём координаты мышки на экране
        //Преобразуй вектор в коорды мира и верни обратно
        ScreenManager.getInstance().getViewport().unproject(tmp);
        if (keysControl.getTargeting() == KeysControl.Targeting.MOUSE) {
            rotateTurretToPoint(gameScreen.getMousePosition().x, gameScreen.getMousePosition().y, dt);
            //Если был клик в рабочую область, то огонь!
            //upd, если зажата
            if (Gdx.input.isTouched()) {
                fire();
            }
        } else {
            if(Gdx.input.isKeyPressed(keysControl.getRotateTurretLeft())){
                //Поворачиваем башню влево
                turretAngle = Utils.makeRotation(turretAngle, turretAngle + 90.0f, 270.0f, dt);
                //Проверка на то, что угол входит в пределы Pi
                turretAngle = Utils.angleToFromNegPiToPosPi(turretAngle);
            }
            if(Gdx.input.isKeyPressed(keysControl.getRotateTurretRight())){
                //Поворачиваем башню вправо
                turretAngle = Utils.makeRotation(turretAngle, turretAngle - 90.0f, 270.0f, dt);
                //Проверка на то, что угол входит в пределы Pi
                turretAngle = Utils.angleToFromNegPiToPosPi(turretAngle);
            }
            if (Gdx.input.isKeyPressed(keysControl.getFire())) {
                fire();
            }
        }
        super.update(dt);
    }

    //Реализация PowerUps
    public void consumePowerUp(Item item) {
        switch (item.getType()){
            case MEDKIT:
                hp +=5;
                if (hp >hpMax) {
                    hp = hpMax;
                }
            case SHIELD:
                addScore(1000); //Надо заменить на щит
                break;
        }
    }

    //рисуем худ
    public void renderHUD(SpriteBatch batch, BitmapFont font24) {
        //Строки прописывать через StringBuilder
        tmpString.setLength(0); //очищаем
        //Добавляем строки для каждого игрока
        tmpString.append("Player: ").append(index);
        tmpString.append("\nScore: ").append(score);
        tmpString.append("\nlives: ").append(lives);
        font24.draw(batch,tmpString, 20 + (index - 1) * 200, 700);
    }


    //Проверка на нажатие
    public void checkMovement(float dt) {
        //Пометка на скорость, чтобы избавиться от зависимости от фпс
        //надо x += speed * dt, где dt - 1/fps

        //Пишем управление на клавиши
        //Избавляемся от диагонали путём else if
        if (Gdx.input.isKeyPressed(keysControl.getLeft())) {
            move(Direction.LEFT, dt);
        } else if (Gdx.input.isKeyPressed(keysControl.getRight())) {
            move(Direction.RIGHT, dt);
        } else if (Gdx.input.isKeyPressed(keysControl.getUp())) {
            move(Direction.UP, dt);
        } else if (Gdx.input.isKeyPressed(keysControl.getDown())) {
            move(Direction.DOWN, dt);
        }
    }


}
