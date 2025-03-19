package cz.radovanmoncek.client.ship.parents.states;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.Viewport;
import cz.radovanmoncek.client.modules.games.models.GameStateRequestFlatBuffersSerializable;
import cz.radovanmoncek.client.ship.tables.GameState;

import java.util.LinkedList;
import java.util.Queue;
import java.util.function.Consumer;

/**
 * State machine pattern for reacting and representing different states of the currently running game session.
 * @author Radovan Monček
 */
public interface ClientState {

    void render(Viewport viewport, SpriteBatch batch);

    void onKeyPress(Consumer<GameStateRequestFlatBuffersSerializable> unicast);

    void start(LinkedList<Disposable> disposables);

    void processGameState(Queue<GameState> gameStates);

    void escapePressed(Consumer<GameStateRequestFlatBuffersSerializable> unicast);
}
