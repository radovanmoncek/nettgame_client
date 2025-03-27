package cz.radovanmoncek.client.ship.utilities;

import java.util.logging.Level;
import java.util.logging.Logger;

public final class LoggingUtilities {

    private LoggingUtilities() {}

    public static void changeGlobalLoggingLevel(final Level level) {

        Logger
                .getLogger("")
                .setLevel(level);

        for (final var handler : Logger.getLogger("").getHandlers()) {

            handler.setLevel(level);
        }
    }
}
