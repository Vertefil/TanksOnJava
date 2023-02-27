package vertefil.tanks;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import vertefil.tanks.units.Tank;

//Создаём пулю
public class Bullet {
    //Ссылка на владельца
    private Tank owner;
    private Vector2 postion; //коорды в векторах
    private Vector2 velocity;//Speed в векторах
    private int damage;
    private boolean active; //Проверка на выстрел
    // Ограничение дальности стрельбы
    private float currentTime;
    private float maxTime;

    //Геттер проверка на acive //Инкапсуляция
    public boolean isActive() {
        return active;
    }

    public Tank getOwner() {
        return owner;
    }

    public int getDamage() {
        return damage;
    }

    //Геттер на postion
    public Vector2 getPostion() {
        return postion;
    }

    //сама пуля
    public Bullet() {
        this.postion = new Vector2();
        this.velocity = new Vector2();
        this.active = false; //встретила препятствие
        this.damage = 0;
    }

    //Активация
    public void activate(Tank owner, float x, float y, float vx, float vy, int damage, float maxTime) {
        this.owner = owner;
        this.active = true;
        //.set принимает и меняет сразу x y
        this.postion.set(x, y);
        this.velocity.set(vx, vy);
        this.damage = damage;
        this.maxTime = maxTime;
        this.currentTime = 0.0f; //При активации обнуляем
    }

    //Деактивация
    public void deactivate() {
        active = false;
    }

    //Физика
    public void update(float dt) {
        //mulAdd - velocity * dt и прибавляем к position
        postion.mulAdd(velocity, dt);
        currentTime += dt; //Время снаряда
        if (currentTime >= maxTime) {
            deactivate();//если снаряд живёт дольше, то деактив
        }
        // вылет за границу
        if (postion.x < 0.0f || postion.x > Gdx.graphics.getWidth() || postion.y < 0.0f || postion.y > Gdx.graphics.getHeight()) {
            deactivate();
        }
    }

}
