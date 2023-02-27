package vertefil.tanks;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;

//Включение PowerUps в игру вся работа с ними
public class ItemEmitter {

    private Item[] items;

    private TextureRegion[][] regions;

    //Геттеры
    public Item[] getItems() {
        return items;
    }

    public ItemEmitter(TextureAtlas atlas) {
        items = new Item[50];
        for (int i = 0; i < items.length; i++) {
            items[i] = new Item();
        }
        regions = new TextureRegion(atlas.findRegion("powerUps")).split(30, 30);
    }
    //Отрисовка
    public void render(SpriteBatch batch){
        //Делаем покадравую анимацию
        for (int i = 0; i < items.length; i++) {
            if(items[i].isActive()){
                //Каждую 0.2 сек меняем картинку, чтобы не выйти за пределлы массива
                //Делим целочисленно на длину индекса
                int frameIndex = (int)(items[i].getTime() / 0.2f) % regions[items[i].getType().index].length;
                batch.draw(regions[items[i].getType().index][frameIndex],items[i].getPosition().x - 15,items[i].getPosition().y - 15);
            }
        }
    }

    //Метод для генерации powerUps
    public void generateRandomItem (float x, float y, int count, float probability) {
        //Не более count штук
        for (int q = 0; q < count; q++) {
            float n = MathUtils.random(0.0f,1.0f);
            if(n < probability) {
                //Выбор случайного типа
                int type = MathUtils.random(0,Item.Type.values().length - 1);
                for (int i = 0; i < items.length; i++) {
                    //Если не активен, то активируем и отрисовываем
                    if(!items[i].isActive()){
                        items[i].setup(x,y, Item.Type.values()[type]);
                        break;
                    }
                }
            }
        }
    }

    //Расчёты
    public void update (float dt) {
        for (int i = 0; i < items.length; i++) {
            if(items[i].isActive()){
                items[i].update(dt);
            }
        }
    }





}
