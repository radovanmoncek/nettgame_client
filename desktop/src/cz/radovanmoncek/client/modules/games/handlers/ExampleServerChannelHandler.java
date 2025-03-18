package cz.radovanmoncek.client.modules.games.handlers;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import cz.radovanmoncek.client.modules.games.states.GameSessionRunningClientState;
import cz.radovanmoncek.client.modules.games.states.JoinSessionSelectedClientState;
import cz.radovanmoncek.client.modules.games.states.MainMenuClientState;
import cz.radovanmoncek.client.ship.parents.states.ClientState;
import cz.radovanmoncek.client.ship.tables.GameState;
import cz.radovanmoncek.client.modules.games.models.GameStateRequestFlatBuffersSerializable;
import cz.radovanmoncek.client.ship.parents.handlers.ServerChannelHandler;
import cz.radovanmoncek.client.ship.tables.GameStatus;
import io.netty.channel.ChannelHandlerContext;

import java.awt.event.KeyEvent;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

//todo: client side prediction time delta time topic in Thesis ???? ????
public class ExampleServerChannelHandler extends ServerChannelHandler<GameState> implements ApplicationListener {
    private static final Logger logger = Logger.getLogger(ExampleServerChannelHandler.class.getName());
    /**
     * State (state machine) design pattern
     */
    private ClientState clientState;
    private final Queue<GameState> gameStates;
    SpriteBatch batch;
    Texture img;
    GameStateRequestFlatBuffersSerializable sessionHostPlayer;

    {

        gameStates = new LinkedList<>();
        clientState = new MainMenuClientState();

        Executors
                .newSingleThreadExecutor()
                .submit(this::startGUI);
    }

    @Override
    public void create() {

        batch = new SpriteBatch();
        /*img = new Texture("badlogic.jpg");*/
    }

    @Override
    public void render() {

        ScreenUtils.clear(0, 0, 0, 1);

        clientState.render();
        /*batch.begin();
        batch.draw(img, x++, y++);
        batch.end();*/
    }

    @Override
    public void dispose() {

        batch.dispose();
        //img.dispose();

        disconnect();
    }
    @Override
    public void resize(int width, int height) {
    }
    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    protected void channelRead0(final ChannelHandlerContext channelHandlerContext, final GameState gameState) {

        switch (gameState.game().status()) {

            case GameStatus.START_SESSION, GameStatus.JOIN_SESSION -> clientState = new GameSessionRunningClientState();

            case GameStatus.STATE_CHANGE -> {

                final var playerState = gameState.player1();

                if (playerState == null) {

                    return;
                }

                sessionHostPlayer = new GameStateRequestFlatBuffersSerializable(
                        playerState.x(),
                        playerState.y(),
                        playerState.rotationAngle(),
                        sessionHostPlayer.name(),
                        gameState.game().status(),
                        sessionHostPlayer.gameCode()
                );

                gameStates.offer(gameState);
            }
        }

        logger.log(Level.INFO, "Session response received from the server {0}", gameState);
    }

    //todo: proper non-naive implementation (client-side prediction)
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {

        super.exceptionCaught(ctx, cause);
    }

    private void startGUI() {

        final var config = new Lwjgl3ApplicationConfiguration();

        config.setForegroundFPS(60);
        config.setTitle("example nettgame client");
        config.setFullscreenMode(Lwjgl3ApplicationConfiguration.getDisplayMode());

        new Lwjgl3Application(this, config);
    }

    private void sendPlayerInput(final KeyEvent keyEvent, int x, int y) {

        int requestedX = x;
        int requestedY = y;
        int requestedRotationAngle = 0;

        switch (keyEvent.getKeyCode()) {

            case KeyEvent.VK_W -> requestedY = y - 8;

            case KeyEvent.VK_A -> {
                requestedX = x - 8;
                requestedRotationAngle = 270;
            }

            case KeyEvent.VK_S -> {
                requestedY = y + 8;
                requestedRotationAngle = 180;
            }

            case KeyEvent.VK_D -> {
                requestedX = x + 8;
                requestedRotationAngle = 90;
            }

            default -> {

                return;
            }
        }

        unicast(new GameStateRequestFlatBuffersSerializable(requestedX, requestedY, requestedRotationAngle, "", GameStatus.STATE_CHANGE, ""));
    }
}
