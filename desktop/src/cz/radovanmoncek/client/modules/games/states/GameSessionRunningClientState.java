package cz.radovanmoncek.client.modules.games.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.Viewport;
import cz.radovanmoncek.client.modules.games.models.GameStateRequestFlatBuffersSerializable;
import cz.radovanmoncek.client.ship.parents.states.ClientState;
import cz.radovanmoncek.client.ship.tables.GameState;
import cz.radovanmoncek.client.ship.tables.GameStatus;
import cz.radovanmoncek.client.ship.tables.Player;

import java.util.LinkedList;
import java.util.Objects;
import java.util.Queue;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GameSessionRunningClientState implements ClientState {
    private static final float speed = 8.0f;
    private Sprite player1,
            player2,
            rock,
            rock2;
    private Player lastPlayerState;
    private Texture background;
    private Texture rockTexture;
    private static final Logger logger = Logger.getLogger(GameSessionRunningClientState.class.getName());
    private Texture rockTexture2;
    private int oldFlipAngle = 90;
    private BitmapFont font;
    private String gameCode;

    @Override
    public void processGameState(Queue<GameState> gameStates) {

        //final var delta = Gdx.graphics.getDeltaTime();
        final var latestGameState = gameStates.poll();

        if (latestGameState != null && Objects.nonNull(latestGameState.player1())) {
            if(gameCode == null)
                gameCode = latestGameState
                        .game()
                        .gameCode();

            final var player1 = latestGameState.player1();
            final var translationX = player1.x() / 100f - lastPlayerState.x() / 100f;
            final var translationY = player1.y() / 100f - lastPlayerState.y() / 100f;
            final var rotation = player1.rotationAngle() - lastPlayerState.rotationAngle();

            logger.log(Level.INFO, "Rendering player state x: {0} y: {1} rotationAngle: {2}", new Object[]{translationX, translationY, player1.rotationAngle()});

            if((player1.rotationAngle() == 90 || player1.rotationAngle() == 270) && oldFlipAngle != player1.rotationAngle()) {
                oldFlipAngle = player1.rotationAngle();
                this.player1.flip(true, false);
            }
            this.player1.translate(translationX, translationY);

            lastPlayerState = player1;
        }
    }

    @Override
    public void escapePressed(Consumer<GameStateRequestFlatBuffersSerializable> unicast) {

        final var serializable = new GameStateRequestFlatBuffersSerializable(
                GameStatus.STOP_SESSION,
                0,
                0,
                0,
                "",
                ""
        );

        unicast.accept(serializable);
    }

    @Override
    public void render(Viewport viewport, SpriteBatch batch) {

        batch.draw(background, 0, 0, viewport.getWorldWidth(), viewport.getWorldHeight());

        //https://www.bilibili.com/video/BV1Ta4y147gs?uid=425631546134793134376773&spm_id_from=333.788.videopod.episodes&p=13
        font.draw(batch, gameCode, 1f, 3f);
        rock.draw(batch);
        rock2.draw(batch);
        player1.draw(batch);
    }

    @Override
    public void onKeyPress(Consumer<GameStateRequestFlatBuffersSerializable> unicast) {

        GameStateRequestFlatBuffersSerializable serializable = null;

        if (Gdx.input.isKeyPressed(Input.Keys.W)) {

            serializable = new GameStateRequestFlatBuffersSerializable(
                    GameStatus.STATE_CHANGE,
                    lastPlayerState.x(),
                    Math.round(lastPlayerState.y() + speed),
                    0,
                    "",
                    ""
            );
        } else if (Gdx.input.isKeyPressed(Input.Keys.A)) {

            serializable = new GameStateRequestFlatBuffersSerializable(
                    GameStatus.STATE_CHANGE,
                    Math.round(lastPlayerState.x() - speed),
                    lastPlayerState.y(),
                    270,
                    "",
                    ""
            );
        } else if (Gdx.input.isKeyPressed(Input.Keys.S)) {

            serializable = new GameStateRequestFlatBuffersSerializable(
                    GameStatus.STATE_CHANGE,
                    lastPlayerState.x(),
                    Math.round(lastPlayerState.y() - speed),
                    180,
                    "",
                    ""
            );
        } else if (Gdx.input.isKeyPressed(Input.Keys.D)) {

            serializable = new GameStateRequestFlatBuffersSerializable(
                    GameStatus.STATE_CHANGE,
                    Math.round(lastPlayerState.x() + speed),
                    lastPlayerState.y(),
                    90,
                    "",
                    ""
            );
        }

        if (serializable == null)
            return;

        unicast.accept(serializable);
    }

    @Override
    public void start(LinkedList<Disposable> disposables) {

        //https://rgsdev.itch.io/free-cc0-modular-animated-vector-characters-2d
        final var player1Texture = new Texture("characters/Free 2D Animated Vector Game Character Sprites/Full body animated characters/Char 1/with hands/idle_0.png");
        final var player2Texture = player1Texture;

        background = new Texture("characters/Free 2D Animated Vector Game Character Sprites/Environment/ground_white.png");
        rockTexture = new Texture("characters/Free 2D Animated Vector Game Character Sprites/Environment/rock1.png");
        rockTexture2 = new Texture("characters/Free 2D Animated Vector Game Character Sprites/Environment/rock2.png");
        player1 = new Sprite(player1Texture);
        player2 = new Sprite(player2Texture);
        rock = new Sprite(rockTexture);
        rock2 = new Sprite(rockTexture2);
        font = new BitmapFont();

        font.setColor(Color.BLACK);
        //https://stackoverflow.com/questions/33633395/how-set-libgdx-bitmap-font-size
        //https://stackoverflow.com/questions/12466385/how-can-i-draw-text-using-libgdx-java
        font.getData().setScale(.1f);

        rock.setSize(1, 1);
        rock.translate(4f, 3f);

        rock2.setSize(1, 1);
        rock2.translate(2f, 1.32f);

        player1.setSize(1, 1);
        player1.setOrigin(0.5f, 0.5f);

        disposables.add(player1Texture);
        disposables.add(player2Texture);
        disposables.add(rockTexture);
        disposables.add(rockTexture2);
        disposables.add(font);

        lastPlayerState = new Player();
    }
}
