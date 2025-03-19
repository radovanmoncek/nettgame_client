package cz.radovanmoncek.client.modules.games.handlers;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import cz.radovanmoncek.client.modules.games.states.GameSessionRunningClientState;
import cz.radovanmoncek.client.modules.games.states.MainMenuClientState;
import cz.radovanmoncek.client.ship.parents.states.ClientState;
import cz.radovanmoncek.client.ship.tables.GameState;
import cz.radovanmoncek.client.ship.parents.handlers.ServerChannelHandler;
import cz.radovanmoncek.client.ship.tables.GameStatus;
import io.netty.channel.ChannelHandlerContext;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

//todo: client side prediction time delta time topic in Thesis ???? ????
public class ExampleServerChannelHandler extends ServerChannelHandler<GameState> implements ApplicationListener {
    private static final Logger logger = Logger.getLogger(ExampleServerChannelHandler.class.getName());
    private final Queue<ClientState> clientStates;
    private final Queue<GameState> gameStates;
    private final LinkedList<Disposable> disposables;
    /**
     * State (state machine) design pattern
     */
    private ClientState clientState;
    private Viewport viewport;
    private SpriteBatch batch;

    public ExampleServerChannelHandler(final boolean windowed) {

        gameStates = new LinkedList<>();
        disposables = new LinkedList<>();
        clientStates = new LinkedList<>();

        Executors
                .defaultThreadFactory()
                .newThread(() -> startGUI(windowed))
                .start();
    }

    private void startGUI(final boolean windowed) {

        final var config = new Lwjgl3ApplicationConfiguration();

        config.setForegroundFPS(60);
        config.setTitle("example nettgame client");
        config.setWindowedMode(800, 600);

        if(!windowed)
            config.setFullscreenMode(Lwjgl3ApplicationConfiguration.getDisplayMode());

        new Lwjgl3Application(this, config);
    }

    @Override
    public void create() {

        viewport = new FitViewport(8, 6);
        batch = new SpriteBatch();
        clientStates.offer(new MainMenuClientState());
    }

    @Override
    public void render() {

        ScreenUtils.clear(Color.BLACK);

        viewport.apply();

        batch.setProjectionMatrix(viewport.getCamera().combined);
        batch.begin();

        final var newClientState = clientStates.poll();

        if (newClientState != null) {

            clientState = newClientState;

            Gdx
                    .input
                    .setInputProcessor(new InputAdapter() {

                        @Override
                        public boolean keyTyped(char character) {

                            clientState.onKeyPress(ExampleServerChannelHandler.this::unicast);

                            return true;
                        }

                        @Override
                        public boolean keyDown(int keycode) {
                            Gdx.app.log("ExampleServerChannelHandler.keyDown", keycode + "");

                            if(keycode == Input.Keys.ESCAPE)
                                clientState.escapePressed(ExampleServerChannelHandler.this::unicast);

                            return true;
                        }
                    });

            newClientState.start(disposables);

            clientState.processGameState(gameStates);
        }

        clientState.render(viewport, batch);

        batch.end();
    }
    @Override
    public void dispose() {

        disposables.forEach(Disposable::dispose);
        batch.dispose();

        disconnect();
    }
    @Override
    public void resize(int width, int height) {

        viewport.update(width, height, true);
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

            case GameStatus.START_SESSION, GameStatus.JOIN_SESSION -> {

                gameStates.clear();
                gameStates.offer(gameState);
                clientStates.offer(new GameSessionRunningClientState());
                clientState.processGameState(gameStates);
            }

            case GameStatus.STATE_CHANGE -> {

                gameStates.offer(gameState);
                clientState.processGameState(gameStates);
            }

            case GameStatus.STOP_SESSION -> {

                gameStates.offer(gameState);
                clientState.processGameState(gameStates);
                gameStates.clear();
                clientStates.offer(new MainMenuClientState());
            }

            case GameStatus.INVALID_STATE -> logger.warning("Invalid game state received");
        }

        logger.log(Level.INFO, "Session response received from the server {0}", gameState);
    }
    //todo: proper non-naive implementation (client-side prediction)

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {

        super.exceptionCaught(ctx, cause);
    }
}
