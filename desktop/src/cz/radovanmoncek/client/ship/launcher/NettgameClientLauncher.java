package cz.radovanmoncek.client.ship.launcher;

import cz.radovanmoncek.client.modules.games.handlers.ExampleServerChannelHandler;
import cz.radovanmoncek.client.ship.builders.NettgameClientBootstrapBuilder;
import cz.radovanmoncek.client.ship.directors.NettgameClientBootstrapDirector;

public final class NettgameClientLauncher {

    public static void main(String[] args) {

        new NettgameClientBootstrapDirector(new NettgameClientBootstrapBuilder())
                .makeDefaultGameClientBootstrapBuilder()
                .buildChannelHandler(new ExampleServerChannelHandler())
                .build()
                .run();
    }
}
