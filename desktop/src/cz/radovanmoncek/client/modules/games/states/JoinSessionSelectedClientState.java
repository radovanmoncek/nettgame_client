package cz.radovanmoncek.client.modules.games.states;

import cz.radovanmoncek.client.modules.games.handlers.ExampleServerChannelHandler;
import cz.radovanmoncek.client.modules.games.models.GameStateRequestFlatBuffersSerializable;
import cz.radovanmoncek.client.ship.parents.states.ClientState;
import cz.radovanmoncek.client.ship.tables.GameStatus;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.List;

public class JoinSessionSelectedClientState implements ClientState {
    private final ExampleServerChannelHandler exampleServerChannelHandler;
    private final StringBuilder userInput = new StringBuilder();

    private String nickname;

    public JoinSessionSelectedClientState(ExampleServerChannelHandler exampleServerChannelHandler) {
        this.exampleServerChannelHandler = exampleServerChannelHandler;
    }

    @Override
    public void render() {

        /*graphics.setColor(Color.BLUE);

        graphics.setFont(new Font("Arial", Font.PLAIN, 12));

        graphics.drawRect(
                gameRenderer.getWidth() / 2 - gameRenderer.getWidth() / 8,
                gameRenderer.getHeight() / 2 - gameRenderer.getHeight() / 16,
                gameRenderer.getWidth() / 4,
                gameRenderer.getHeight() / 8
        );

        if (userInput.isEmpty()) {

            final var text = nickname == null ? "Please enter your nickname" : "Please enter session code";

            graphics
                    .drawString(
                            text,
                            gameRenderer.getWidth() / 2 - graphics.getFontMetrics().stringWidth(text) / 2,
                            gameRenderer.getHeight() / 2 - graphics.getFontMetrics().getHeight() / 2
                    );

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

                if (nickname == null) {

                    nickname = userInput.toString();

                    userInput.delete(0, userInput.length());

                    return;
                }

                if (userInput.length() != 8)
                    return;

                exampleServerChannelHandler.unicast(new GameStateRequestFlatBuffersSerializable(0, 0, 0, nickname, GameStatus.JOIN_SESSION, userInput.toString()));
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
