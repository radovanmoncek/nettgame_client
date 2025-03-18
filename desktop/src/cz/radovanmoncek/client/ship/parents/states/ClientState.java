package cz.radovanmoncek.client.ship.parents.states;

/**
 * State machine pattern for reacting and representing different states of the currently running game session.
 * @author Radovan Monček
 */
public interface ClientState {

    void render();

    void onKeyPress();
}
