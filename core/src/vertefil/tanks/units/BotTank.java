package vertefil.tanks.units;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import vertefil.tanks.GameScreen;
import vertefil.tanks.Weapon;
import vertefil.tanks.utils.Direction;

//Боты
public class BotTank extends Tank {
    //Предпочитаемое движение
    Direction prefferedDirection;
    //Таймер действия
    float aiTimer;
    //Таймер смены действия
    float aiTimerTo;
    //Активный или нет
    boolean active;
    //Радиус реакции бота
    float pursuitRadius;
    //Фикс залипания ботов, через 3 вектор z
    Vector3 lastPosition;

    public boolean isActive() {
        return active;
    }

    public BotTank(GameScreen gameScreen, TextureAtlas atlas) {
        // Инициализируем
        // Делаем слабее
        super(gameScreen);
        this.ownerType = TankOwner.AI;
        this.weapon = new Weapon(atlas);
        this.texture = atlas.findRegion("botTankBase");
        this.textureHp = atlas.findRegion("bar");
        this.position = new Vector2(500.0f, 500.0f);
        speed = 100.0f; //пиксели на секунду
        this.width = texture.getRegionWidth(); //Тк текстурка частичная
        this.height = texture.getRegionHeight(); //Получаем расширение региона атласа
        this.hpMax = 3;
        this.hp = this.hpMax;
        this.aiTimerTo = 3.0f; //Каждые 3 секунды менять действие
        this.prefferedDirection = Direction.UP;
        this.circle = new Circle(position.x, position.y, (width + height) / 2);
        this.pursuitRadius = 300.0f;
        this.lastPosition = new Vector3(0.0f, 0.0f, 0.0f);
    }

    //Бот активирован
    public void activate(float x, float y) {
        //Задаём хар-ки боту при активации
        hpMax = 3;
        hp = hpMax;
        position.set(x, y);
        prefferedDirection = Direction.values()[MathUtils.random(0, Direction.values().length - 1)];
        angle = prefferedDirection.getAngle();
        active = true;
        aiTimer = 0.0f;
    }

    //Метод дестрой для бот
    @Override
    public void destroy() {
        gameScreen.getItemEmitter().generateRandomItem(position.x, position.y,2, 0.5f);
        active = false;
    }

    //Физика ботов
    public void update(float dt) {
        aiTimer += dt;
        // Если время превысило aiTimerTo
        if (aiTimer >= aiTimerTo) {
            // Обнуляем
            aiTimer = 0.0f;
            // Генерим рандомное значение в диапазоне, чтобы выглядело живее
            aiTimerTo = MathUtils.random(3.5f, 5.0f);
            // Создали массив вариаций движений и берём рандомное направление
            prefferedDirection = Direction.values()[MathUtils.random(0, Direction.values().length - 1)];
            angle = prefferedDirection.getAngle();
        }
        move(prefferedDirection, dt);
        //Даём ботам приоритет по игрокам
        PlayerTank prefferedTarget = null;
        if(gameScreen.getPlayers().size() == 1){
            prefferedTarget = gameScreen.getPlayers().get(0);
        } else {
            //Задаём максимальное число и вычисляем расстояние до игроков
            //Стреляем в того, который ближе
            float minDist = Float.MAX_VALUE;
            for (int i = 0; i < gameScreen.getPlayers().size(); i++) {
                PlayerTank player = gameScreen.getPlayers().get(i);
                //Расстояние между плеером и ботом
                float dst = this.position.dst(player.getPosition());
                if(dst < minDist){
                    minDist = dst;
                    prefferedTarget = player;
                }
            }
        }

        //Расстояние между плеером и ботом
        float dst = this.position.dst(prefferedTarget.getPosition());
        // Если плеер в зоне видимости
        if (dst < pursuitRadius) {
            rotateTurretToPoint(prefferedTarget.getPosition().x, prefferedTarget.getPosition().y, dt);
            fire();
        }
        //Убираем залипание
        //Если бот стоит на месте
        if (Math.abs(position.x - lastPosition.x) < 0.5f && Math.abs(position.y - lastPosition.y) < 0.5f) {
            //Начинаем отсчёт таймера
            lastPosition.z += dt;
            //Если бот стоит больше 1\4 сек-ы
            //Увеличиваем aiTimer и бот двигается
            if (lastPosition.z > 0.3f) {
                aiTimer += 10.0f;
            }
        } else {
            //Если двигается, то просто сохраняем коорды и время = 0
            lastPosition.x = position.x;
            lastPosition.y = position.y;
            lastPosition.z = 0.0f;
        }
        // Все поверки желательно делать в конце
        super.update(dt);
    }

}
