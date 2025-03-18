package cz.radovanmoncek.client.modules.games.states;

import cz.radovanmoncek.client.modules.games.handlers.ExampleServerChannelHandler;
import cz.radovanmoncek.client.modules.games.models.GameStateRequestFlatBuffersSerializable;
import cz.radovanmoncek.client.ship.parents.states.ClientState;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.List;

public class MainMenuClientState implements ClientState {
    private boolean newSessionSelected = true,
                    joinSessionSelected = false;

    @Override
    public void render() {

        /*graphics.setColor(Color.WHITE);

        if (newSessionSelected)
            graphics.setColor(Color.YELLOW);

        final var newSessionText = "New Session";

        graphics
                .drawString(
                        newSessionText,
                        gameRenderer.getWidth() / 2 - graphics.getFontMetrics().stringWidth(newSessionText) / 2,
                        gameRenderer.getHeight() / 2 - graphics.getFontMetrics().getHeight() / 2
                );

        graphics.setColor(Color.WHITE);

        if (joinSessionSelected)
            graphics.setColor(Color.YELLOW);

        final var joinSessionText = "Join Session";

        graphics.drawString(
                joinSessionText,
                gameRenderer.getWidth() / 2 - graphics.getFontMetrics().stringWidth(joinSessionText) / 2,
                gameRenderer.getHeight() / 2 + graphics.getFontMetrics().getHeight() / 2
        );*/
    }

    @Override
    public void onKeyPress() {

        /*switch (keyEvent.getKeyCode()) {

            case KeyEvent.VK_ENTER -> exampleServerChannelHandler.clientState = newSessionSelected ?
                    new ExampleServerChannelHandler.NewSessionSelectedClientState() :
                    new ExampleServerChannelHandler.JoinSessionSelectedClientState();

            case KeyEvent.VK_W -> {

                newSessionSelected = true;
                joinSessionSelected = false;
            }

            case KeyEvent.VK_S -> {

                newSessionSelected = false;
                joinSessionSelected = true;
            }
        }*/
    }
}
