package vertefil.tanks;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import vertefil.tanks.units.BotTank;
import vertefil.tanks.units.Tank;

//Эмиттер для ботов
public class BotEmitter {

    //array of bots
    private BotTank[] bots;
    //Макс кол-во ботов
    public static final int MAX_BOTS_COUNT = 200;

    public BotTank[] getBots() {
        return bots;
    }

    //Конструктор эммитера
    //Правило при работе с эмиттерами и LibGdx создать объекты при запуске, а потом работать с ними
    public BotEmitter(GameScreen gameScreen, TextureAtlas atlas) {
        //Создаём массив ботов
        this.bots = new BotTank[MAX_BOTS_COUNT];
        //Инициализируем их
        for (int i = 0; i < bots.length; i++) {
            this.bots[i] = new BotTank(gameScreen, atlas);
        }
    }

    //Активация бота
    public void activate(float x, float y) {
        for (int i = 0; i < bots.length; i++) {
            if (!bots[i].isActive()) {
                bots[i].activate(x, y);
                break;
            }
        }
    }

    //отрисовка эмиттеров
    public void render(SpriteBatch batch) {
        for (int i = 0; i < bots.length; i++) {
            if (bots[i].isActive()) {
                bots[i].render(batch);
            }
        }
    }

    //Просчёт физики
    public void update(float dt) {
        for (int i = 0; i < bots.length; i++) {
            if (bots[i].isActive()) {
                bots[i].update(dt);
            }
        }
    }

}
