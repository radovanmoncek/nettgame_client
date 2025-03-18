package cz.radovanmoncek.client.modules.games.codecs;

import cz.radovanmoncek.client.modules.games.models.GameStateRequestFlatBuffersSerializable;
import com.google.flatbuffers.FlatBufferBuilder;
import cz.radovanmoncek.client.ship.parents.codecs.FlatBuffersEncoder;
import cz.radovanmoncek.client.ship.parents.models.FlatBuffersSerializable;

import java.util.logging.Level;
import java.util.logging.Logger;

public class GameStateRequestFlatBufferEncoder extends FlatBuffersEncoder<GameStateRequestFlatBuffersSerializable> {
    private static final Logger logger = Logger.getLogger(GameStateRequestFlatBufferEncoder.class.getName());

    @Override
    protected byte[] encodeBodyAfterHeader(FlatBuffersSerializable flatBuffersSerializable, FlatBufferBuilder flatBufferBuilder) {

        logger.log(Level.INFO, "Encoding {0}", flatBuffersSerializable);

        return flatBuffersSerializable.serialize(flatBufferBuilder);
    }

    @Override
    protected byte[] encodeHeader(FlatBuffersSerializable flatBuffersSerializable, FlatBufferBuilder flatBufferBuilder) {

        return new byte[]{'g'};
    }
}
