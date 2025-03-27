package cz.radovanmoncek.client.modules.games.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.Viewport;
import cz.radovanmoncek.client.modules.games.models.GameStateRequestFlatBuffersSerializable;
import cz.radovanmoncek.client.ship.parents.states.ClientState;
import cz.radovanmoncek.client.ship.tables.GameState;
import cz.radovanmoncek.client.ship.tables.GameStatus;
import cz.radovanmoncek.client.ship.tables.Player;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GameSessionRunningClientState implements ClientState {
    private static final Logger logger = Logger.getLogger(GameSessionRunningClientState.class.getName());
    private final Consumer<GameStateRequestFlatBuffersSerializable> unicast;
    private static final float speed = 8.0f;
    private Sprite player1,
            player2,
            rock,
            rock2;
    private Player lastPlayerState,
            lastPlayerState2;
    private Texture background;
    private Texture rockTexture;
    private Texture rockTexture2;
    private int oldFlipAngle = 90;
    private BitmapFont font;
    private final AtomicReference<String> gameCode = new AtomicReference<>("");
    private boolean joined = false,
            startReceived = false;
    private final AtomicBoolean stateRequested = new AtomicBoolean(false);
    private Animation<Sprite> animationState,
            idleAnimation,
            walkingAnimation;

    public GameSessionRunningClientState(final Consumer<GameStateRequestFlatBuffersSerializable> unicast) {

        this.unicast = unicast;
    }

    @Override
    public void processGameState(GameState gameState) {

        if (Objects.isNull(gameState)) {

            return;
        }

        if (gameState.game().status() == GameStatus.START_SESSION || gameState.game().status() == GameStatus.JOIN_SESSION)
            startReceived = true;

        if (gameCode.get().isBlank() && gameState.game().gameCode() != null)
            gameCode.set(gameState
                    .game()
                    .gameCode());

        if (Objects.nonNull(gameState.player1())) {

            final var player1 = gameState.player1();
            final var translationX = player1.x() / 100f - lastPlayerState.x() / 100f;
            final var translationY = player1.y() / 100f - lastPlayerState.y() / 100f;
            final var rotation = player1.rotationAngle() - lastPlayerState.rotationAngle();

            logger.log(Level.INFO, "Rendering player state x: {0} y: {1} rotationAngle: {2}", new Object[]{translationX, translationY, player1.rotationAngle()});

            if ((player1.rotationAngle() == 90 || player1.rotationAngle() == 270) && oldFlipAngle != player1.rotationAngle()) {

                oldFlipAngle = player1.rotationAngle();

                this.player1.flip(true, false);

                Arrays.stream(walkingAnimation.getKeyFrames()).forEach(keyFrame -> keyFrame.flip(true, false));
                Arrays.stream(idleAnimation.getKeyFrames()).forEach(keyFrame -> keyFrame.flip(true, false));
            }

            this.player1.translate(translationX, translationY);

            for (final var keyFrame : idleAnimation.getKeyFrames()) {

                keyFrame.translate(translationX, translationY);
            }

            Arrays.stream(walkingAnimation.getKeyFrames()).forEach(keyFrame -> keyFrame.translate(translationX, translationY));

            lastPlayerState = player1;
        }

        if (Objects.nonNull(gameState.player2())) {

            joined = true;

            final var player2 = gameState.player2();
            final var translationX = player2.x() / 100f - lastPlayerState2.x() / 100f;
            final var translationY = player2.y() / 100f - lastPlayerState2.y() / 100f;
            final var rotation = player2.rotationAngle() - lastPlayerState2.rotationAngle();

            logger.log(Level.INFO, "Rendering player state x: {0} y: {1} rotationAngle: {2}", new Object[]{translationX, translationY, player2.rotationAngle()});

            if ((player2.rotationAngle() == 90 || player2.rotationAngle() == 270) && oldFlipAngle != player2.rotationAngle()) {
                oldFlipAngle = player2.rotationAngle();
                this.player2.flip(true, false);
            }
            this.player2.translate(translationX, translationY);

            lastPlayerState2 = player2;

            stateRequested.set(false);

            return;
        }

        joined = false;

        stateRequested.set(false);
    }

    @Override
    public void noViewportRender(Viewport viewport, SpriteBatch batch, float deltaTime) {

        //https://www.bilibili.com/video/BV1Ta4y147gs?uid=425631546134793134376773&spm_id_from=333.788.videopod.episodes&p=13
        font.draw(batch, gameCode.get(), 30f, 30f);
    }

    @Override
    public void render(Viewport viewport, SpriteBatch batch, float deltaTime) {

        processInputs();

        batch.draw(background, 0, 0, viewport.getWorldWidth(), viewport.getWorldHeight());

        rock.draw(batch);
        rock2.draw(batch);
        //player1.draw(batch);
        animationState.getKeyFrame(deltaTime, true).draw(batch);

        if (joined)
            player2.draw(batch);
    }

    private void processInputs() {

        if(!startReceived || stateRequested.get())
            return;

        GameStateRequestFlatBuffersSerializable serializable = null;

        if (Gdx.input.isKeyPressed(Input.Keys.W)) {

            serializable = new GameStateRequestFlatBuffersSerializable()
                    .withGameStatus(GameStatus.STATE_CHANGE)
                    .withPosition(new int[]{lastPlayerState.x(), Math.round(lastPlayerState.y() + speed), 0});
        }
        else if (Gdx.input.isKeyPressed(Input.Keys.A)) {

            serializable = new GameStateRequestFlatBuffersSerializable()
                    .withGameStatus(GameStatus.STATE_CHANGE)
                    .withPosition(new int[]{Math.round(lastPlayerState.x() - speed), lastPlayerState.y(), 270});
        }
        else if (Gdx.input.isKeyPressed(Input.Keys.S)) {

            serializable = new GameStateRequestFlatBuffersSerializable()
                    .withGameStatus(GameStatus.STATE_CHANGE)
                    .withPosition(new int[]{lastPlayerState.x(), Math.round(lastPlayerState.y() - speed), 180});
        }
        else if (Gdx.input.isKeyPressed(Input.Keys.D)) {

            serializable = new GameStateRequestFlatBuffersSerializable()
                    .withGameStatus(GameStatus.STATE_CHANGE)
                    .withPosition(new int[]{Math.round(lastPlayerState.x() + speed), lastPlayerState.y(), 90});
        }

        if (serializable == null)
            return;

        animationState = walkingAnimation;

        unicast.accept(serializable);
        stateRequested.set(true);
    }

    @Override
    public void registered() {

        Gdx.input.setInputProcessor(new InputAdapter() {

            @Override
            public boolean keyDown(int keycode) {

                if (keycode == Input.Keys.ESCAPE) {

                    final var serializable = new GameStateRequestFlatBuffersSerializable().withGameStatus(GameStatus.STOP_SESSION);

                    unicast.accept(serializable);

                    return true;
                }

                return false;
            }

            @Override
            public boolean keyUp(int keycode) {

                animationState = idleAnimation;

                return false;
            }
        });
    }

    public void initialize(LinkedList<Disposable> disposables) {

        //https://rgsdev.itch.io/free-cc0-modular-animated-vector-characters-2d
        final var player1Texture = new Texture("characters/Free 2D Animated Vector Game Character Sprites/Full body animated characters/Char 1/with hands/idle_0.png");
        final var player2Texture = new Texture("characters/Free 2D Animated Vector Game Character Sprites/Full body animated characters/Char 4/with hands/idle_0.png");
        final var generator = new FreeTypeFontGenerator(Gdx.files.internal("font/PressStart2P-Regular.ttf"));
        final var parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();

        parameter.size = 16;

        idleAnimation = new Animation<>(1f / 6f,
                new Sprite(new Texture("characters/Free 2D Animated Vector Game Character Sprites/Full body animated characters/Char 4/with hands/idle_0.png")),
                new Sprite(new Texture("characters/Free 2D Animated Vector Game Character Sprites/Full body animated characters/Char 4/with hands/idle_1.png")),
                new Sprite(new Texture("characters/Free 2D Animated Vector Game Character Sprites/Full body animated characters/Char 4/with hands/idle_2.png")),
                new Sprite(new Texture("characters/Free 2D Animated Vector Game Character Sprites/Full body animated characters/Char 4/with hands/idle_3.png")),
                new Sprite(new Texture("characters/Free 2D Animated Vector Game Character Sprites/Full body animated characters/Char 4/with hands/idle_4.png")),
                new Sprite(new Texture("characters/Free 2D Animated Vector Game Character Sprites/Full body animated characters/Char 4/with hands/idle_5.png"))
        );
        walkingAnimation = new Animation<>(1f / 6f,
                new Sprite(new Texture("characters/Free 2D Animated Vector Game Character Sprites/Full body animated characters/Char 4/with hands/walk_0.png")),
                new Sprite(new Texture("characters/Free 2D Animated Vector Game Character Sprites/Full body animated characters/Char 4/with hands/walk_1.png")),
                new Sprite(new Texture("characters/Free 2D Animated Vector Game Character Sprites/Full body animated characters/Char 4/with hands/walk_2.png")),
                new Sprite(new Texture("characters/Free 2D Animated Vector Game Character Sprites/Full body animated characters/Char 4/with hands/walk_3.png")),
                new Sprite(new Texture("characters/Free 2D Animated Vector Game Character Sprites/Full body animated characters/Char 4/with hands/walk_4.png")),
                new Sprite(new Texture("characters/Free 2D Animated Vector Game Character Sprites/Full body animated characters/Char 4/with hands/walk_5.png"))
        );
        animationState = idleAnimation;
        background = new Texture("characters/Free 2D Animated Vector Game Character Sprites/Environment/ground_white.png");
        rockTexture = new Texture("characters/Free 2D Animated Vector Game Character Sprites/Environment/rock1.png");
        rockTexture2 = new Texture("characters/Free 2D Animated Vector Game Character Sprites/Environment/rock2.png");
        player1 = new Sprite(player1Texture);
        player2 = new Sprite(player2Texture);
        rock = new Sprite(rockTexture);
        rock2 = new Sprite(rockTexture2);
        font = generator.generateFont(parameter);

        generator.dispose();

        font.setColor(Color.BLACK);
        //https://stackoverflow.com/questions/33633395/how-set-libgdx-bitmap-font-size
        //https://stackoverflow.com/questions/12466385/how-can-i-draw-text-using-libgdx-java
        //https://stackoverflow.com/questions/34046216/font-and-viewport-libgdx

        rock.setSize(1, 1);
        rock.translate(4f, 3f);

        rock2.setSize(1, 1);
        rock2.translate(2f, 1.32f);

        player1.setSize(1, 1);
        player1.setOrigin(0.5f, 0.5f);

        player2.setSize(1, 1);
        player2.setOrigin(0.5f, 0.5f);

        for (final var keyFrame : idleAnimation.getKeyFrames()) {

            keyFrame.setSize(1, 1);
            keyFrame.setOrigin(0.5f, 0.5f);
            disposables.add(keyFrame.getTexture());
        }

        for (final var keyFrame : walkingAnimation.getKeyFrames()) {

            keyFrame.setSize(1, 1);
            keyFrame.setOrigin(0.5f, 0.5f);
            disposables.add(keyFrame.getTexture());
        }

        disposables.add(player1Texture);
        disposables.add(player2Texture);
        disposables.add(rockTexture);
        disposables.add(rockTexture2);
        disposables.add(font);

        lastPlayerState = new Player();
        lastPlayerState2 = new Player();
    }
}
