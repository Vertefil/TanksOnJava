package vertefil.tanks;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import vertefil.tanks.units.BotTank;
import vertefil.tanks.units.PlayerTank;
import vertefil.tanks.units.Tank;
import vertefil.tanks.utils.GameType;
import vertefil.tanks.utils.KeysControl;

import java.util.ArrayList;
import java.util.List;
//Наследуем AbstractScreen и GameScreen уже без мусора
public class GameScreen extends AbstractScreen {
    private SpriteBatch batch;
    //Создадим BitmapFont
    private BitmapFont font24;
    private TextureAtlas atlas;
    //Список игроков
    private List<PlayerTank> players;
    //Вшиваем PowerUps
    private ItemEmitter itemEmitter;
    //Карта
    private Map map;
    //Тип игры
    private GameType gameType;

    public void setGameType(GameType gameType) {
        this.gameType = gameType;
    }

    //Пуля
    //Без статика, тк статика видна везде
    private BulletEmitter bulletEmitter;
    //Боты
    private BotEmitter botEmitter;
    //Таймер для ботов
    private float gameTimer;
    //Мировой таймер
    private float worldTimer;
    //Кнопки, менюшки и тд
    private Stage stage;
    //Пауза
    private boolean paused;
    //Добавляем вектор для курсора
    private Vector2 mousePosition;
    //Курсор
    private TextureRegion cursor;

    //Для добавления звука опрокидываем в assets wav, mp3 и добавляем в игру
    private Sound sound; //Короткие звуки, пар-но много может быть
    private Music music; //Долгая музыка 1-2 макс пар-но, поточно грузиться с диска
    private static final boolean FRIENDLY_FIRE = true;

    //Геттеры
    public Map getMap() {
        return map;
    }

    public List<PlayerTank> getPlayers() {
        return players;
    }

    public BulletEmitter getBulletEmitter() {
        return bulletEmitter;
    }

    public Vector2 getMousePosition() {
        return mousePosition;
    }

    public ItemEmitter getItemEmitter() {
        return itemEmitter;
    }

    //Создавая экран отдаём ссылку на batch
    public GameScreen(SpriteBatch batch) {
        this.batch = batch;
        this.gameType = GameType.ONE_PLAYER;
    }

    //Переносим всё из create в show
    @Override
    public void show() {
        //Создали звук
        sound = Gdx.audio.newSound(Gdx.files.internal("GameOver.mp3"));
        sound.setVolume(0,0.6f);
        //Запускаем звук
        //sound.play();
        //Для музыки тоже самое
        music = Gdx.audio.newMusic(Gdx.files.internal("GameOst.mp3"));
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
        //Добавляем атлас в каждый объект с Texture
        //Создаём список игроков
        players = new ArrayList<>();
        //Добавляем игроков
        players.add(new PlayerTank(1, this,KeysControl.createStandardControl1(),atlas));
        if (gameType == GameType.TWO_PLAYERS) {
            players.add(new PlayerTank(2, this, KeysControl.createStandardControl2(), atlas));
        }
        bulletEmitter = new BulletEmitter(atlas);
        cursor = new TextureRegion(atlas.findRegion("cursor"));
        map = new Map(atlas);
        botEmitter = new BotEmitter(this, atlas);
        itemEmitter = new ItemEmitter(atlas);
        gameTimer = 6.0f;
        stage = new Stage();
        //Делаем курсор
        mousePosition = new Vector2();
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
        TextButton pauseButton = new TextButton("Pause", textButtonStyle);
        TextButton exitButton = new TextButton("Exit", textButtonStyle);

        //Вешаем Listener на паузу который ждём клика
        pauseButton.addListener(new ClickListener() {
            //Переопределяем метод клик(Что будет если кликнули)
            public void clicked(InputEvent event, float x, float y) {
                paused = !paused;
            }
        });
        exitButton.addListener(new ClickListener() {
            //Переопределяем метод клик(Что будет если кликнули)
            public void clicked(InputEvent event, float x, float y) {
                ScreenManager.getInstance().setScreen(ScreenManager.ScreenType.MENU);

            }
        });

        //Расположение элементов управления
        pauseButton.setPosition(0, 40);
        exitButton.setPosition(0, 0);
        group.addActor(pauseButton);
        group.addActor(exitButton);
        //Теперь группа будет двигаться вместе
        group.setPosition(1135, 640);
        //Теперь просто добавляем группу как актёра
        stage.addActor(group);
        //Stage может обрабатывать действия со стороны пользователя
        Gdx.input.setInputProcessor(stage);
        //Отлавливаем курсор и отключаем дефолтную мышку
        Gdx.input.setCursorCatched(true);
    }

    @Override
    public void render(float delta) {
        update(delta);
        Gdx.gl.glClearColor(0,0,0,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        //Gdx считывает цвета 0-1; надо из 255-> gdx делением на 255
        //ScreenUtils.clear(0, 0, 0, 1);
        batch.begin(); //начало рисовки
        //Камера следует за танком
        //ScreenManager.getInstance().getCamera().position.set(player.getPosition().x, player.getPosition().y,0 );
        //ScreenManager.getInstance().getCamera().update();

        //Задаём батчу какое место занимает камера
        batch.setProjectionMatrix(ScreenManager.getInstance().getCamera().combined);
        //в GDX 0,0 - левый нижний угол
        //Рисуем слоями 1 слой задник, потом текстурки, потом танк и тд
        //Отрисовка карты
        map.render(batch);

        //Рисуем всех игроков
        for (int i = 0; i < players.size(); i++) {
            players.get(i).render(batch);
        }
        //отрисовка ботов после игрока
        botEmitter.render(batch);
        //отрисовка пули
        bulletEmitter.render(batch);
        //Отрисовка PowerUps
        itemEmitter.render(batch);
        //Отрисовка худа для игроков
        for (int i = 0; i < players.size(); i++) {
            players.get(i).renderHUD(batch,font24);
        }
        //Рисуем курсор, центрируем путём смещения на центр текстуры
        batch.end(); //конец рисовки
        //Рисуем HUD можно вне батча
        stage.draw();
        //Рисуем заново, поверх Stage курсор
        batch.begin();
        batch.draw(cursor,mousePosition.x - cursor.getRegionWidth() / 2,mousePosition.y - cursor.getRegionHeight() /2,cursor.getRegionWidth() / 2, cursor.getRegionHeight()/2,cursor.getRegionWidth(),cursor.getRegionHeight(), 1, 1, -worldTimer * 50);
        batch.end();
    }

    //Метод для физики
    public void update(float dt) {
        //Задали коорды мышки
        mousePosition.set(Gdx.input.getX(), Gdx.input.getY());
        //Сказали скрин менеджеру сделать unproject позиций
        ScreenManager.getInstance().getViewport().unproject(mousePosition);
        //Пока не на паузе работаем
        worldTimer += dt;
        if (!paused) {
            gameTimer += dt;
            //Создаём бота рандомно
            if (gameTimer > 5.0f) {
                gameTimer = 0.0f;
                //UPD:Проверка на препятствие
                float coordX, coordY;
                //Задаём коорды x,y для создания бота, и если они находяться в блоках, то крутим следующие
                do {
                    coordX = MathUtils.random(0, Gdx.graphics.getWidth());
                    coordY = MathUtils.random(0, Gdx.graphics.getHeight());
                } while (!map.isAreaClear(coordX, coordY, 20));

                botEmitter.activate(coordX, coordY);
            }
            //Update всех игроков
            for (int i = 0; i < players.size(); i++) {
                players.get(i).update(dt);
            }
            botEmitter.update(dt);
            bulletEmitter.update(dt);
            itemEmitter.update(dt);
            CheckCollisions();
        }
        stage.act(dt); //Обновляем stage
    }

    //Проверка на колизию / попадание
    public void CheckCollisions() {
        for (int i = 0; i < bulletEmitter.getBullets().length; i++) {
            // Локальная ссылка на пулю, после пропадании пули, уходит из стека
            Bullet bullet = bulletEmitter.getBullets()[i];
            if (bullet.isActive()) { // Активная пуля, может столкнуться только с активным ботом
                for (int j = 0; j < botEmitter.getBots().length; j++) {
                    BotTank bot = botEmitter.getBots()[j];
                    if (bot.isActive()) {
                        // Содержи ли внутри себя круг что-либо
                        //bullet.getOwner() != bot Боты не смогут попадать в себя
                        //Проверка с ботами
                        if (checkBulletAndTank(bot, bullet) && bot.getCircle().contains(bullet.getPostion())) {
                            bullet.deactivate();
                            bot.takeDamage(bullet.getDamage());
                            break;
                        }
                    }
                }
                //Проверка на пулю с каждым игроком
                for (int j = 0; j < players.size(); j++) {
                    PlayerTank player = players.get(j);
                    //Проверка с плеером
                    if (checkBulletAndTank(player, bullet) && player.getCircle().contains(bullet.getPostion())) {
                        bullet.deactivate();
                        player.takeDamage(bullet.getDamage());
                    }
                }
                //Проверка на стену
                map.checkWallAndBulletsCollisions(bullet);

            }
        }
        for (int i = 0; i < itemEmitter.getItems().length; i++) {
            if(itemEmitter.getItems()[i].isActive()) {
                Item item = itemEmitter.getItems()[i];
                for (int j = 0; j < players.size(); j++) {
                    if(players.get(j).getCircle().contains(item.getPosition())){
                        players.get(j).consumePowerUp(item);
                        item.deactivate();
                        break;
                    }
                }
            }
        }
    }

    //Метод для реализации friendlyFire
    public boolean checkBulletAndTank(Tank tank, Bullet bullet) {
        if (!FRIENDLY_FIRE) {
            //Если выкл, то танк попадает только по чужому танку
            return tank.getOwnerType() != bullet.getOwner().getOwnerType();
        } else {
            //Если вкл, то танк не попадает только по себе
            return tank != bullet.getOwner();
        }
    }



    @Override
    public void dispose() {
        font24.dispose();
        atlas.dispose();
        sound.play();
        music.stop();
    }
}
