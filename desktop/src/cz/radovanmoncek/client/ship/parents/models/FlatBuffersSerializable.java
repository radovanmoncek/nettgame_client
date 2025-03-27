package cz.radovanmoncek.client.ship.parents.models;

import com.google.flatbuffers.FlatBufferBuilder;

public interface FlatBuffersSerializable {

    byte [] serialize(FlatBufferBuilder builder);
}
