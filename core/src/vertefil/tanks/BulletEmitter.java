package vertefil.tanks;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import vertefil.tanks.units.Tank;

import java.awt.*;

//Задача Эмиттера управлять каким-либо пулом объектов
public class BulletEmitter {
    //array of bullets
    private Bullet[] bullets;
    private TextureRegion bulletTexture; //Текстурка пули
    public static final int MAX_BULLETS_COUNT = 500;

    public Bullet[] getBullets() {
        return bullets;
    }

    //Конструктор эммитера
    //Правило при работе с эмиттерами и LibGdx создать объекты при запуске, а потом работать с ними
    public BulletEmitter(TextureAtlas atlas) {
        this.bulletTexture = atlas.findRegion("projectile");
        //Создаём массив пуль
        this.bullets = new Bullet[MAX_BULLETS_COUNT];
        //Инициализируем их
        for (int i = 0; i < bullets.length; i++) {
            this.bullets[i] = new Bullet();
        }
    }

    //Если хотят активировать пулю
    public void activate(Tank owner, float x, float y, float vx, float vy, int damage,float maxTime) {
        for (int i = 0; i < bullets.length; i++) {
            if (!bullets[i].isActive()) {
                bullets[i].activate(owner,x, y, vx, vy, damage, maxTime);
                break;
            }
        }
    }

    //отрисовка эмиттеров
    public void render(SpriteBatch batch) {
        for (int i = 0; i < bullets.length; i++) {
            if (bullets[i].isActive()) {
                //
                batch.draw(bulletTexture, bullets[i].getPostion().x - 8, bullets[i].getPostion().y - 8);
            }
        }
    }

    //Просчёт физики
    public void update(float dt) {
        for (int i = 0; i < bullets.length; i++) {
            if (bullets[i].isActive()) {
                bullets[i].update(dt);
            }
        }
    }

}
