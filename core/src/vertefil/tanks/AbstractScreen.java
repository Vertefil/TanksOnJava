package vertefil.tanks;

import com.badlogic.gdx.Screen;
//Имплементируем класс скрин и добавляем его методы
public abstract class AbstractScreen implements Screen {
    //Когда кто-то тянет экран, срабатывает метод resize
    @Override
    public void resize(int width, int height) {
        ScreenManager.getInstance().resize(width, height);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }
}
