package cz.radovanmoncek.client.modules.games.states;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import cz.radovanmoncek.client.modules.games.models.GameStateRequestFlatBuffersSerializable;
import cz.radovanmoncek.client.ship.parents.states.ClientState;
import cz.radovanmoncek.client.ship.tables.GameState;
import cz.radovanmoncek.client.ship.tables.GameStatus;

import java.util.LinkedList;
import java.util.Queue;
import java.util.function.Consumer;

public class MainMenuClientState implements ClientState {
    private Stage stage;
    private Texture background;
    private TextField input;

    @Override
    public void processGameState(Queue<GameState> gameStates) {

    }

    @Override
    public void escapePressed(Consumer<GameStateRequestFlatBuffersSerializable> unicast) {

    }

    @Override
    public void render(Viewport viewport, SpriteBatch batch) {

        batch.draw(background, 0, 0, viewport.getWorldWidth(), viewport.getWorldHeight());

        stage.draw();

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
    public void start(LinkedList<Disposable> disposables) {

        final var tempListener = Gdx.input.getInputProcessor();

        background = new Texture("badlogic.jpg");
        stage = new Stage();

        //https://stackoverflow.com/questions/21488311/how-to-create-a-button-in-libgdx
        final var font = new BitmapFont();
        final var style = new TextButton.TextButtonStyle();

//        final var buttonAtlas = new TextureAtlas(Gdx.files.internal("buttons/buttons.pack"));

        Gdx.input.setInputProcessor(stage);

        //final var skin = new Skin();

        //      skin.addRegions(buttonAtlas);

        style.font = font;
        style.fontColor = Color.WHITE;
        //    style.up = skin.getDrawable("up-button");
        //  style.down = skin.getDrawable("down-button");
        //style.checked = skin.getDrawable("checked-button");

        final var button = new TextButton("New Session", style);
        final var buttonJoin = new TextButton("Join existing session", style);

        button.setX(Gdx.graphics.getWidth() / (float) 2 - button.getWidth() / 2);
        buttonJoin.setX(Gdx.graphics.getWidth() / (float) 2 - buttonJoin.getWidth() / 2);
        button.setY(Gdx.graphics.getHeight() / (float) 2);
        buttonJoin.setY(Gdx.graphics.getHeight() / (float) 2 - 50);

        button.addListener(new ChangeListener() {

            @Override
            public void changed(ChangeEvent event, Actor actor) {

                stage.clear();

                final var inputStyle = new TextField.TextFieldStyle();

                inputStyle.font = font;
                inputStyle.fontColor = Color.WHITE;

                input = new TextField("Please enter your nickname", inputStyle);

                input.setX(Gdx.graphics.getWidth() / (float) 2 - input.getWidth() / 2);
                input.setY(Gdx.graphics.getHeight() / (float) 2);
                input.setSize(200, 100);

                stage.addActor(input);

                input.addListener(new InputListener() {

                    @Override
                    public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {

                        input.setText("");

                        return true;
                    }

                    @Override
                    public boolean keyTyped(InputEvent event, char character) {

                        if(character == 10) {

                            tempListener.keyTyped(character);
                        }

                        return true;
                    }
                });
            }
        });

        stage.addActor(button);
        stage.addActor(buttonJoin);

        disposables.add(stage);
        disposables.add(background);
    }

    @Override
    public void onKeyPress(Consumer<GameStateRequestFlatBuffersSerializable> unicast) {

        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {

            final var serializable = new GameStateRequestFlatBuffersSerializable(
                    GameStatus.START_SESSION,
                    0,
                    0,
                    0,
                    input
                            .getText()
                            .trim(),
                    ""
            );

            unicast.accept(serializable);
        }

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
