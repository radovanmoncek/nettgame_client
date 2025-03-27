package cz.radovanmoncek.client.modules.games.codecs;

import cz.radovanmoncek.client.ship.parents.codecs.FlatBuffersDecoder;
import cz.radovanmoncek.client.ship.tables.GameState;

import java.nio.ByteBuffer;

public class GameStateFlatBufferDecoder extends FlatBuffersDecoder<GameState> {

    @Override
    protected boolean decodeHeader(ByteBuffer in) {

        return in.get() == 'G';
    }

    @Override
    protected GameState decodeBodyAfterHeader(ByteBuffer in) {

        return GameState.getRootAsGameState(in);
    }
}
