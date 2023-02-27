package vertefil.tanks.units;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Vector2;
import vertefil.tanks.GameScreen;
import vertefil.tanks.TanksRpgGame;
import vertefil.tanks.utils.Direction;
import vertefil.tanks.utils.Utils;
import vertefil.tanks.Weapon;

public abstract class Tank {
    //Убераем private, чтобы дать пакетный доступ
    //Говорим танку что он знает game
    GameScreen gameScreen;
    //Владельцы танков
    TankOwner ownerType;
    Weapon weapon; //Виды оружия
    // Текстурки в регионах
    TextureRegion texture;
    TextureRegion textureHp;
    //Коорды танков в векторах
    Vector2 position;
    //Временные расчёты
    Vector2 tmp;
    //Задём окружность - ХитБокс
    Circle circle;

    //healtPoints
    int hp;
    int hpMax;

    float speed;
    float angle; //Угол поворота танка
    float turretAngle;
    float fireTimer;

    int width;
    int height;

    //Геттеры
    public Vector2 getPosition() {
        return position;
    }

    public Circle getCircle() {
        return circle;
    }

    public TankOwner getOwnerType() {
        return ownerType;
    }

    //Создаём танк
    public Tank(GameScreen gameScreen) {
        this.gameScreen = gameScreen;
        this.tmp = new Vector2(0.0f, 0.0f);
    }

    //Отрисовка танка
    public void render(SpriteBatch batch) {
        //Самый расширенный вариант, orgX - якорь картинки,сама картинка(40x40),размер Х У,  ,так же размеры и флипы false
        //x-16,y-16, чтобы попасть в центр картинки UPD:pos.x - wdth /2, pos.y - hght / 2
        //Танк, с углом самого танка //Работа с TextureRegion
        batch.draw(texture, position.x - width / 2, position.y - height / 2, width / 2, height / 2, width, height, 1, 1, angle);
        //Пушка, с углом самой пушки
        batch.draw(weapon.getTexture(), position.x - width / 2, position.y - height / 2, width / 2, height / 2, width, height, 1, 1, turretAngle);
        //Полоска хп
        if (hp < hpMax) { //Если фул хп, то бар не отображается
            // Задаём цвет полоскам
            batch.setColor(0, 0, 0, 0.8f);
            // пропорцией вырисовываем хпбар (hp/hpMax) * 40
            batch.draw(textureHp, position.x - width / 2 - 2, position.y + width / 2 - 10, 44, 12); //фул хп
            batch.setColor(0, 1, 0, 0.8f);
            batch.draw(textureHp, position.x - width / 2, position.y + width / 2 - 8, ((float) hp / hpMax) * 40, 8);
            batch.setColor(1, 1, 1, 1);
        }
    }

    //Метод для нанесения урона
    public void takeDamage(int damage) {
        //Попали здоровье уменьшилось
        hp -= damage;
        //Если хп кончилось, свой destroy
        if (hp <= 0) {
            destroy();
        }
    }

    //Метод для уничтожения
    public abstract void destroy();

    public void update(float dt) {
        fireTimer += dt; //Расчёт на медленное оружие
        //Проверка границы экрана
        if (position.x < 0.0f) {
            position.x = 0.0f;
        }
        if (position.x > Gdx.graphics.getWidth()) {
            position.x = Gdx.graphics.getWidth();
        }
        if (position.y < 0.0f) {
            position.y = 0.0f;
        }
        if (position.y > Gdx.graphics.getHeight()) {
            position.y = Gdx.graphics.getHeight();
        }
        // Привязка ХитБокса к танку
        circle.setPosition(position);
    }

    //Метод для проверки на препятствие
    public void move(Direction direction, float dt) {
        //Получаем текущие знания
        tmp.set(position);
        //Добавляем в временные переменные
        tmp.add(speed * direction.getVx() * dt, speed * direction.getVy() * dt);
        //Просчитываем будущее движение, если свободно, то двигаемся
        if (gameScreen.getMap().isAreaClear(tmp.x, tmp.y, width / 2)) {
            //Унифицировали, движение одинаковое, как для бота, так и для игрока
            //Задаём угол по направлению
            angle = direction.getAngle();
            //Если всё хорошо, то передаём просчёты tmp в position
            position.set(tmp);
        }
    }


    //Метод вращения башни
    public void rotateTurretToPoint(float pointX, float pointY, float dt) {
        //Угол между турелью и мышкой
        float angleTo = Utils.getAngle(position.x, position.y, pointX, pointY);
        //Поворачиваем башню в сторону мыши
        turretAngle = Utils.makeRotation(turretAngle, angleTo, 180.0f, dt);
        //Проверка на то, что угол входит в пределы Pi
        turretAngle = Utils.angleToFromNegPiToPosPi(turretAngle);

    }

    //Метод для стрельбы
    public void fire() {
        //Если часто жмут кнопку, то выстреливаем и запускаем fireTimer и обнуляем после
        if (fireTimer >= weapon.getFirePeriod()) {
            fireTimer = 0.0f;
            //Если пуля не активна, то можем стрелять
            float angleRad = (float) Math.toRadians(turretAngle);


            //Задаём свои координаты, и добавляем скорость * на син и кос в радианах
            gameScreen.getBulletEmitter().activate(this, position.x, position.y, weapon.getProjectileSpeed() * (float) Math.cos(angleRad), weapon.getProjectileSpeed() * (float) Math.sin(angleRad), weapon.getDamage(), weapon.getProjectileLifetime());
        }
    }
}
