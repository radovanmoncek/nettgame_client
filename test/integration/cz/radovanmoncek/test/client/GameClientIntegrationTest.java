package cz.radovanmoncek.test.client;

import cz.radovanmoncek.client.modules.games.models.GameStateRequestFlatBuffersSerializable;
import cz.radovanmoncek.client.ship.bootstrap.NettgameClientBootstrap;
import cz.radovanmoncek.client.ship.builders.NettgameClientBootstrapBuilder;
import cz.radovanmoncek.client.ship.directors.NettgameClientBootstrapDirector;
import cz.radovanmoncek.client.ship.parents.handlers.ServerChannelHandler;
import cz.radovanmoncek.client.ship.tables.GameStatus;
import cz.radovanmoncek.client.ship.tables.GameState;
import io.netty.channel.ChannelHandlerContext;
import org.junit.jupiter.api.*;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class GameClientIntegrationTest {
    private static NettgameClientBootstrap player1, player2;
    public static boolean finished = false;
    private static TestingGameStateServerChannelHandler gameStateTestingGameStateServerChannelHandler1, gameStateTestingGameStateServerChannelHandler2;
    private static int x1 = 1000, y1 = 1000, x2 = 1000, y2 = 1000;

    @BeforeAll
    static void setup() {

        gameStateTestingGameStateServerChannelHandler1 = new TestingGameStateServerChannelHandler();

        player1 = new NettgameClientBootstrapDirector(new NettgameClientBootstrapBuilder())
                .makeDefaultGameClientBootstrapBuilder()
                .buildChannelHandler(gameStateTestingGameStateServerChannelHandler1)
                .build();
        player1.run();
    }

    @Test
    @Order(0)
    void nicknameOver8CharactersTest() throws InterruptedException {

        gameStateTestingGameStateServerChannelHandler1.unicast(new GameStateRequestFlatBuffersSerializable(GameStatus.START_SESSION, 0, 0, 0, "VeryLongNicknameThatIsOverEightCharacters", ""));

        var gameState = gameStateTestingGameStateServerChannelHandler1.poll();

        assertNotNull(gameState);

        assertEquals(GameStatus.INVALID_STATE, gameState.game().status());
    }

    @Test
    @Order(1)
    void emptyPlayerNameTest() throws InterruptedException {

        gameStateTestingGameStateServerChannelHandler1.unicast(new GameStateRequestFlatBuffersSerializable(GameStatus.START_SESSION, 0, 0, 0, "", ""));

        final var gameState = gameStateTestingGameStateServerChannelHandler1.poll();

        assertNotNull(gameState);

        assertEquals(GameStatus.INVALID_STATE, gameState.game().status());
    }

    @Test
    @Order(2)
    void sessionStartTest() throws InterruptedException {

        final var sessionRequestProtocolDataUnit = new GameStateRequestFlatBuffersSerializable(GameStatus.START_SESSION, 0, 0, 0, "Test", "");

        gameStateTestingGameStateServerChannelHandler1.unicast(sessionRequestProtocolDataUnit);

        final var gameState = gameStateTestingGameStateServerChannelHandler1.poll();

        assertNotNull(gameState);

        assertEquals("Test", gameState.player1().name());
        assertEquals(GameStatus.START_SESSION, gameState.game().status());
        assertEquals(1000, gameState.player1().x());
        assertEquals(1000, gameState.player1().y());
        assertEquals(0, gameState.player1().rotationAngle());
    }

    @Test
    @Order(3)
    void sessionValidStateWithinBoundsTest() throws InterruptedException {

        gameStateTestingGameStateServerChannelHandler1.poll();

        gameStateTestingGameStateServerChannelHandler1.unicast(new GameStateRequestFlatBuffersSerializable(GameStatus.STATE_CHANGE, 1008, 1008, 90, "", ""));

        final var gameState = gameStateTestingGameStateServerChannelHandler1.poll();

        assertNotNull(gameState);
        assertNotNull(gameState.game());

        assertEquals(GameStatus.STATE_CHANGE, gameState.game().status());

        assertNotNull(gameState.player1());

        assertEquals(1008, gameState.player1().x());
        assertEquals(1008, gameState.player1().y());
        assertEquals(90, gameState.player1().rotationAngle());
    }

    @Test
    @Order(4)
    void sessionInvalidStateOverBoundsTest() throws Exception {

        gameStateTestingGameStateServerChannelHandler1.unicast(new GameStateRequestFlatBuffersSerializable(
                GameStatus.STATE_CHANGE,
                801,
                601,
                15,
                "",
                ""));

        final var gameState = gameStateTestingGameStateServerChannelHandler1.poll();

        assertNotNull(gameState);

        assertEquals(GameStatus.INVALID_STATE, gameState.game().status());
    }

    @Test
    @Order(5)
    void sessionInvalidStateUnderBoundsTest() throws Exception {

        gameStateTestingGameStateServerChannelHandler1.unicast(new GameStateRequestFlatBuffersSerializable(
                GameStatus.STATE_CHANGE,
                -1,
                -1,
                -15,
                "",
                ""));

        final var gameState = gameStateTestingGameStateServerChannelHandler1.poll();

        assertNotNull(gameState);

        assertEquals(GameStatus.INVALID_STATE, gameState.game().status());
    }

    @Test
    @Order(6)
    void sessionValidStateMoveDeltaTest() throws Exception {

        gameStateTestingGameStateServerChannelHandler1.unicast(new GameStateRequestFlatBuffersSerializable(
                GameStatus.STATE_CHANGE,
                1000,
                1008,
                180,
                "",
                "")
        );

        var gameState = gameStateTestingGameStateServerChannelHandler1.poll();

        assertNotNull(gameState);
        assertNotNull(gameState.player1());

        assertEquals(GameStatus.STATE_CHANGE, gameState.game().status());
        assertEquals(1000, gameState.player1().x());
        assertEquals(1008, gameState.player1().y());
        assertEquals(180, gameState.player1().rotationAngle());

        gameStateTestingGameStateServerChannelHandler1.unicast(new GameStateRequestFlatBuffersSerializable(
                GameStatus.STATE_CHANGE,
                1008,
                1008,
                270,
                "",
                "")
        );

        gameState = gameStateTestingGameStateServerChannelHandler1.poll();

        assertNotNull(gameState);
        assertNotNull(gameState.player1());

        assertEquals(GameStatus.STATE_CHANGE, gameState.game().status());
        assertEquals(1008, gameState.player1().x());
        assertEquals(1008, gameState.player1().y());
        assertEquals(270, gameState.player1().rotationAngle());
    }

    @Test
    @Order(7)
    void sessionInvalidStateMoveDeltaTest() throws Exception {

        gameStateTestingGameStateServerChannelHandler1.unicast(new GameStateRequestFlatBuffersSerializable(
                GameStatus.STATE_CHANGE,
                2,
                6,
                45,
                "",
                "")
        );

        var gameState = gameStateTestingGameStateServerChannelHandler1.poll();

        assertNotNull(gameState);

        assertEquals(GameStatus.INVALID_STATE, gameState.game().status());

        gameStateTestingGameStateServerChannelHandler1.unicast(new GameStateRequestFlatBuffersSerializable(GameStatus.STATE_CHANGE, 4, 4, 45, "", ""));

        gameState = gameStateTestingGameStateServerChannelHandler1.poll();

        assertNotNull(gameState);

        assertEquals(GameStatus.INVALID_STATE, gameState.game().status());
    }

    @Test
    @Order(8)
    void sessionEndTest() throws Exception {

        gameStateTestingGameStateServerChannelHandler1.unicast(new GameStateRequestFlatBuffersSerializable(GameStatus.STOP_SESSION, 0, 0, 0, "", ""));

        final var gameState = gameStateTestingGameStateServerChannelHandler1.poll();

        assertNotNull(gameState);

        assertEquals(GameStatus.STOP_SESSION, gameState.game().status());
    }

    @Test
    @Order(9)
    void joinSessionTest() throws Exception {

        final var instanceField = NettgameClientBootstrap.class.getDeclaredField("instance");

        instanceField.setAccessible(true);
        instanceField.set(player1, null);

        gameStateTestingGameStateServerChannelHandler2 = new TestingGameStateServerChannelHandler();

        player2 = new NettgameClientBootstrapDirector(new NettgameClientBootstrapBuilder())
                .makeDefaultGameClientBootstrapBuilder()
                .buildChannelHandler(gameStateTestingGameStateServerChannelHandler2)
                .build();
        player2.run();

        gameStateTestingGameStateServerChannelHandler1.unicast(new GameStateRequestFlatBuffersSerializable(
                GameStatus.START_SESSION,
                0,
                0,
                0,
                "Test",
                "")
        );

        var gameState = gameStateTestingGameStateServerChannelHandler1.poll();

        assertNotNull(gameState);

        assertEquals(GameStatus.START_SESSION, gameState.game().status());

        final var gameCode = gameState
                .game()
                .gameCode();

        assertNotNull(gameCode);

        gameStateTestingGameStateServerChannelHandler1.poll();

        //We need to wait until the game server is ready to serve us our session.
        TimeUnit.MILLISECONDS.sleep(4000);

        gameStateTestingGameStateServerChannelHandler2.unicast(new GameStateRequestFlatBuffersSerializable(
                GameStatus.JOIN_SESSION,
                0,
                0,
                0,
                "Test2",
                gameCode
                )
        );

        var gameStateSecondPlayer = gameStateTestingGameStateServerChannelHandler2.poll();

        assertNotNull(gameStateSecondPlayer);

        assertEquals(GameStatus.JOIN_SESSION, gameStateSecondPlayer.game().status());

        assertNotNull(gameStateSecondPlayer.player1());
        assertNotNull(gameStateSecondPlayer.player2());
        assertNotNull(gameStateSecondPlayer.player1().name());
        assertNotNull(gameStateSecondPlayer.player2().name());

        gameState = gameStateTestingGameStateServerChannelHandler1.poll();

        assertNotNull(gameState);

        assertEquals(GameStatus.JOIN_SESSION, gameState.game().status());
        assertEquals("Test", gameState.player1().name());
        assertEquals("Test2", gameState.player2().name());

        assertNotNull(gameStateSecondPlayer);

        assertEquals(GameStatus.JOIN_SESSION, gameStateSecondPlayer.game().status());
        assertEquals("Test2", gameStateSecondPlayer.player1().name());
        assertEquals("Test", gameStateSecondPlayer.player2().name());

        gameStateTestingGameStateServerChannelHandler2.poll();

        gameStateTestingGameStateServerChannelHandler1.poll();
    }

    @RepeatedTest(100)
    @Order(10)
    void twoPlayerSessionTest() throws Exception {

        gameStateTestingGameStateServerChannelHandler1.unicast(new GameStateRequestFlatBuffersSerializable(
                GameStatus.STATE_CHANGE,
                x1 + 8,
                y1,
                90,
                "",
                ""));

        var gameStateFirstPlayer = gameStateTestingGameStateServerChannelHandler1.poll();

        assertNotNull(gameStateFirstPlayer);

        assertEquals(GameStatus.STATE_CHANGE, gameStateFirstPlayer.game().status());

        var gameStateSecondPlayer = gameStateTestingGameStateServerChannelHandler2.poll();

        assertNotNull(gameStateSecondPlayer);

        assertEquals(GameStatus.STATE_CHANGE, gameStateSecondPlayer.game().status());
        assertEquals(x1 + 8, gameStateFirstPlayer.player1().x());
        assertEquals(y1, gameStateFirstPlayer.player1().y());
        assertEquals(90, gameStateFirstPlayer.player1().rotationAngle());
        assertEquals(x1 + 8, gameStateSecondPlayer.player2().x());
        assertEquals(y1, gameStateSecondPlayer.player2().y());
        assertEquals(90, gameStateSecondPlayer.player2().rotationAngle());

        x1 = gameStateFirstPlayer.player1().x();
        y1 = gameStateFirstPlayer.player1().y();

        gameStateTestingGameStateServerChannelHandler2.unicast(new GameStateRequestFlatBuffersSerializable(
                GameStatus.STATE_CHANGE,
                x2,
                y2 + 8,
                90,
                "",
                "")
        );

        gameStateSecondPlayer = gameStateTestingGameStateServerChannelHandler2.poll();

        assertNotNull(gameStateSecondPlayer);

        assertEquals(GameStatus.STATE_CHANGE, gameStateSecondPlayer.game().status());

        gameStateFirstPlayer = gameStateTestingGameStateServerChannelHandler1.poll();

        assertNotNull(gameStateFirstPlayer);

        assertEquals(GameStatus.STATE_CHANGE, gameStateFirstPlayer.game().status());
        assertEquals(x2, gameStateFirstPlayer.player2().x());
        assertEquals(y2 + 8, gameStateFirstPlayer.player2().y());
        assertEquals(90, gameStateFirstPlayer.player2().rotationAngle());
        assertEquals(x2, gameStateSecondPlayer.player1().x());
        assertEquals(y2 + 8, gameStateSecondPlayer.player1().y());
        assertEquals(90, gameStateSecondPlayer.player1().rotationAngle());

        x2 = gameStateSecondPlayer.player1().x();
        y2 = gameStateSecondPlayer.player1().y();
    }

    @Test
    @Order(11)
    void shutdownJoinedPlayerGameClientTest() {

        gameStateTestingGameStateServerChannelHandler2.disconnect();
    }

    /**
     * Inspired by {@link io.netty.channel.embedded.EmbeddedChannel}
     */
    @AfterAll
    static void tearDown() {

        gameStateTestingGameStateServerChannelHandler1.disconnect();

        finished = true;
    }

    public static class TestingGameStateServerChannelHandler extends ServerChannelHandler<GameState> {

        private final LinkedBlockingQueue<GameState> queue = new LinkedBlockingQueue<>();

        @Override
        protected void channelRead0(ChannelHandlerContext ctx, GameState msg) {

            queue.offer(msg);
        }

        @Override
        public void unicast(Object msg) {

            super.unicast(msg);
        }

        public GameState poll() throws InterruptedException {

            return queue.poll(10000, TimeUnit.MILLISECONDS);
        }

        public void disconnect() {

            queue.clear();

            super.disconnect();
        }
    }
}
