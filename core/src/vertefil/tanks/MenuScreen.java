package vertefil.tanks;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import vertefil.tanks.utils.GameType;

public class MenuScreen extends AbstractScreen {
    private SpriteBatch batch;
    // Атлас помогает разгрузить память и код работает быстрее
    private TextureAtlas atlas;
    //Создадим BitmapFont
    private BitmapFont font24;
    //Для добавления звука опрокидываем в assets wav, mp3 и добавляем в игру
    private Sound sound; //Короткие звуки, пар-но много может быть
    private Music music; //Долгая музыка 1-2 макс пар-но, поточно грузиться с диска

    //Кнопки, менюшки и тд
    private Stage stage;

    //Геттеры
    //Создавая экран отдаём ссылку на batch
    public MenuScreen(SpriteBatch batch) {
        this.batch = batch;
    }

    //Переносим всё из create в show
    @Override
    public void show() {
        music = Gdx.audio.newMusic(Gdx.files.internal("MenuOst.mp3"));
        music.play();
        music.setVolume(0.2f);
        // Благодаря texturepack.gui создали атлас текстур
        // Инициализируем атлас
        // Атлас помогает разгрузить память и код работает быстрее
        atlas = new TextureAtlas("game.pack");
        // Проверка используется ли атлас
        //atlas.dispose();
        //Добавляем фонт
        font24 = new BitmapFont(Gdx.files.internal("font24.fnt"));
        stage = new Stage();
        //Добавляем элементы управления
        //Скин - набор обложек
        Skin skin = new Skin(); //Skin(badlogic)
        //Добавляем кнопку и вырезаем текстурку
        skin.add("simpleButton", new TextureRegion(atlas.findRegion("SimpleButton")));
        //Стиль кнопки
        TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
        //Когда она нажата(up)
        textButtonStyle.up = skin.getDrawable("simpleButton");
        //Выбрали шрифт для кнопки
        textButtonStyle.font = font24;

        //Объединение в группы похожих элементов управления и тд
        Group group = new Group();
        //Кнопка паузы
        final TextButton start1Button = new TextButton("Start 1P", textButtonStyle);
        final TextButton start2Button = new TextButton("Start 2P", textButtonStyle);
        final TextButton exitButton = new TextButton("Exit", textButtonStyle);

        //Вешаем Listener на паузу который ждём клика
        start1Button.addListener(new ClickListener() {
            //Переопределяем метод клик(Что будет если кликнули)
            public void clicked(InputEvent event, float x, float y) {
                ScreenManager.getInstance().setScreen(ScreenManager.ScreenType.GAME, GameType.ONE_PLAYER);
            }
        });
        //Вешаем Listener на Starts который ждём клика
        start2Button.addListener(new ClickListener() {
            //Переопределяем метод клик(Что будет если кликнули)
            public void clicked(InputEvent event, float x, float y) {
                ScreenManager.getInstance().setScreen(ScreenManager.ScreenType.GAME, GameType.TWO_PLAYERS);
            }
        });
        exitButton.addListener(new ClickListener() {
            //Переопределяем метод клик(Что будет если кликнули)
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });

        //Расположение элементов управления
        start1Button.setPosition(0, 80);
        start2Button.setPosition(0, 40);
        exitButton.setPosition(0, 0);
        group.addActor(start1Button);
        group.addActor(start2Button);
        group.addActor(exitButton);
        //Теперь группа будет двигаться вместе
        group.setPosition(580, 40);
        //Теперь просто добавляем группу как актёра
        stage.addActor(group);
        //Stage может обрабатывать действия со стороны пользователя
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        update(delta);
        Gdx.gl.glClearColor(0, 0.5f, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.draw();

    }

    //Метод для физики
    public void update(float dt) {
        stage.act(dt); //Обновляем stage
    }

    @Override
    public void dispose() {
        atlas.dispose();
        font24.dispose();
        stage.dispose();
        music.stop();
    }
}
