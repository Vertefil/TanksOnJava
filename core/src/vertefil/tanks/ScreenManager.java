package vertefil.tanks;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import vertefil.tanks.utils.GameType;

//Singleton глобальный объект, единственный на всю программу
//Управляем экранами
public class ScreenManager {
    //enum для типов экрана
    public enum ScreenType {
        MENU, GAME;
    }

    private static ScreenManager ourInstance = new ScreenManager();

    public static ScreenManager getInstance() {
        return ourInstance;
    }

    private ScreenManager() {
    }

    //Размера мира
    public static final int WORLD_WIDTH = 1280;
    public static final int WORLD_HEIGHT = 720;

    //Инициализируем и опрокидываем ссылки
    private Game game;
    private GameScreen gameScreen;
    private MenuScreen menuScreen;

    //Viewport camera для расширения экрана и следование за игроком
    private Viewport viewport;
    private Camera camera;

    //Геттеры
    public Viewport getViewport() {
        return viewport;
    }

    public Camera getCamera() {
        return camera;
    }

    public void init(Game game, SpriteBatch batch) {
        this.game = game;
        //OrthographicCamera камера которая смотрит на прямую плоскость x или y
        this.camera = new OrthographicCamera(WORLD_WIDTH, WORLD_HEIGHT);
        this.camera.position.set(WORLD_WIDTH / 2, WORLD_HEIGHT / 2, 0);
        this.camera.update();
        //FitViewport подгонка экрана под различные размеры путём подгонки и сохранения масштаба
        //StretchViewport старается заполнить весь экран, путём сжимания и растяжения картинка
        //ScreenViewport старается заполнить весь экран с масштабом, но картинка уйдёт за экран
        this.viewport = new FitViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);
        this.gameScreen = new GameScreen(batch);
        this.menuScreen = new MenuScreen(batch);
    }

    //Метод для масштабирования
    public void resize(int width, int height) {
        //Когда меняется размер окна, мы говорим viewport вот новые высота и ширина
        //Меняй, масштабируй, обновляй
        viewport.update(width, height);
        viewport.apply();
    }

    //Метод для перехода между экранами
    //Object... позволяет добавлять что-либо в аргументы
    //Первым будет ждать Тип игры на 1 или 2 игроков
    public void setScreen(ScreenType screenType, Object... args) {
        //После выхода вкл мышку
        Gdx.input.setCursorCatched(false);
        //Сохраняем текущий экран
        Screen currentScreen = game.getScreen();
        switch (screenType) {
            //Если выбрали Menu, то переходим в menuScreen
            case MENU:
                game.setScreen(menuScreen);
                break;
            //Если выбрали игру, то переходим в gameScreen
            case GAME:
                //Первый аргумент будет принимать кол-во игроков
                gameScreen.setGameType((GameType)args[0]);
                game.setScreen(gameScreen);
                break;
        }

        //Проверка
        if (currentScreen != null) {
            //Если мы только начали игру, то освобождаем экран
            currentScreen.dispose();
        }

    }

}
