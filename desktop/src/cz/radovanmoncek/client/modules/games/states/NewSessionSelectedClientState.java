package cz.radovanmoncek.client.modules.games.states;

import cz.radovanmoncek.client.modules.games.handlers.ExampleServerChannelHandler;
import cz.radovanmoncek.client.modules.games.models.GameStateRequestFlatBuffersSerializable;
import cz.radovanmoncek.client.ship.parents.states.ClientState;
import cz.radovanmoncek.client.ship.tables.GameStatus;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.List;

public class NewSessionSelectedClientState implements ClientState {

    private final ExampleServerChannelHandler exampleServerChannelHandler;
    private final StringBuilder userInput = new StringBuilder();

    public NewSessionSelectedClientState(ExampleServerChannelHandler exampleServerChannelHandler) {
        this.exampleServerChannelHandler = exampleServerChannelHandler;
    }

    @Override
    public void render() {

        /*graphics.setColor(Color.BLUE);

        graphics.setFont(new Font("Arial", Font.BOLD, 12));

        graphics.drawRect(
                gameRenderer.getWidth() / 2 - gameRenderer.getWidth() / 8,
                gameRenderer.getHeight() / 2 - gameRenderer.getHeight() / 16,
                gameRenderer.getWidth() / 4,
                gameRenderer.getHeight() / 8
        );

        if (userInput.isEmpty()) {

            graphics.drawString("Please enter your nickname", gameRenderer.getWidth() / 2, gameRenderer.getHeight() / 2);

            return;
        }

        graphics.drawString(userInput.toString(), gameRenderer.getWidth() / 2, gameRenderer.getHeight() / 2);*/
    }

    @Override
    public void onKeyPress() {

        /*switch (keyEvent.getKeyCode()) {

            case KeyEvent.VK_ENTER -> {

                if (userInput.isEmpty())
                    return;

                exampleServerChannelHandler.unicast(new GameStateRequestFlatBuffersSerializable(0, 0, 0, userInput.toString(), GameStatus.START_SESSION, ""));
            }

            case KeyEvent.VK_BACK_SPACE -> {

                if (userInput.isEmpty())
                    return;

                userInput.deleteCharAt(userInput.length() - 1);
            }

            default -> {

                if (userInput.length() > 8 || !String.copyValueOf(new char[]{keyEvent.getKeyChar()}).matches("[a-z0-9]"))
                    return;

                userInput.append(keyEvent.getKeyChar());
            }
        }*/
    }
}
