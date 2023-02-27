package vertefil.tanks;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class TanksRpgGame extends Game {
    private SpriteBatch batch;//класс для рисования на поле

    //Идеи:
    // ================================
    //	+Размер 40 на 40
    //	+Завести константы
    //	+Полоса ХП
    //	+Прицеливание мышкой
    //	+Эмитер для пуль нужен
    //	+Отделить башню от корпуса
    //	+Не выходить за экран
    //	+Разрушение стен /карта (материалы стен)
    //	+Боты
    //  +Продумать игру на 2-х
    //  +Добавить воду
    //  Полоса повышения уровня
    //	+PowerUps
    //	+Разные виды оружия
    //  +Интерфейс (работа с кнопками)
    //  +Ограничить радиус стрельбы
    //  +Звуки
    //  +Анимация
    //  +Старт игры
    //  Добавить очки за убийство ботов
    //  Окончание игры
    //



    //Старт, создание места для отрисовки
    @Override
    public void create() {
        batch = new SpriteBatch();
        //Работаем с 1 batch, при смене мы старый dispose
        ScreenManager.getInstance().init(this, batch);
        //Переходим на экран игры
        ScreenManager.getInstance().setScreen(ScreenManager.ScreenType.MENU);
    }


    //Сама отрисовка, рендер тех или иных объектов
    @Override
    public void render() {
        //Пометка на скорость, чтобы избавиться от зависимости от фпс
        //надо x += speed * dt, где dt - 1/fps
        float dt = Gdx.graphics.getDeltaTime();
        //Запрашиваем ссылку на текущий экран
        //и в нём рендерим с dt
        getScreen().render(dt);
    }

    //Очищение объектов, текстурок и тп
    @Override
    public void dispose() {
        batch.dispose();
    }
}
