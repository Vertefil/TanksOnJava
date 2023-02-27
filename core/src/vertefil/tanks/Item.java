package vertefil.tanks;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

//PowerUps
public class Item {
    //Enum для типов поверАпсов
    public enum Type {
        SHIELD(0), MEDKIT(1);
        int index;

        Type(int index) {
            this.index = index;
        }
    }

    //Инициализация позиции, времени, активности
    private Vector2 position;
    private Vector2 velocity;
    private Type type;
    private float time;
    private float timeMax;
    private boolean active;

    //Геттеры
    public float getTime() {
        return time;
    }

    public boolean isActive() {
        return active;
    }

    public void deactivate() {
        this.active = false;
    }

    public Vector2 getPosition() {
        return position;
    }

    public Type getType() {
        return type;
    }

    //Хар-ки Item
    public Item() {
        this.position = new Vector2(0, 0);
        this.velocity = new Vector2(0, 0);
        this.type = Type.SHIELD;
        this.timeMax = 5.0f;
        this.time = 0.0f;
        this.active = true;
    }

    //Активация и появление PowerUps
    public void setup(float x, float y, Type type) {
        //Появляется в точке
        this.position.set(x, y);
        //Получает рандомную скорость и летит туда
        this.velocity.set(MathUtils.random(-50, 50), MathUtils.random(-50, 50));
        this.type = type;
        this.active = true;
        this.time = 0.0f;
    }

    //Вычисления
    public void update(float dt) {
        time += dt;
        position.mulAdd(velocity, dt);
        if(time > timeMax){
            deactivate();
        }
    }

}
