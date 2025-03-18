package cz.radovanmoncek.test.client;

import cz.radovanmoncek.client.modules.games.handlers.ExampleServerChannelHandler;
import cz.radovanmoncek.client.ship.bootstrap.NettgameClientBootstrap;
import cz.radovanmoncek.client.ship.builders.NettgameClientBootstrapBuilder;
import cz.radovanmoncek.client.ship.directors.NettgameClientBootstrapDirector;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.awt.*;

public class GameClientSystemTest {
    private static Robot robot;
    private static NettgameClientBootstrap bootstrap;

    @BeforeAll
    static void setup() throws AWTException {

        robot = new Robot();
        bootstrap = new NettgameClientBootstrapDirector(new NettgameClientBootstrapBuilder())
                .makeDefaultGameClientBootstrapBuilder()
                .buildChannelHandler(new ExampleServerChannelHandler())
                .build();
    }

    @Test
    void runTest() {

        bootstrap.run();
    }
}
