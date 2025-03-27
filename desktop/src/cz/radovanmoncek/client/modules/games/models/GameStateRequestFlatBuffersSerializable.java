package cz.radovanmoncek.client.modules.games.models;

import com.google.flatbuffers.FlatBufferBuilder;
import cz.radovanmoncek.client.ship.parents.models.FlatBuffersSerializable;
import cz.radovanmoncek.client.ship.tables.GameStateRequest;
import cz.radovanmoncek.client.ship.tables.GameStatus;

import java.util.logging.Level;
import java.util.logging.Logger;

public class GameStateRequestFlatBuffersSerializable implements FlatBuffersSerializable {
    private static final Logger logger = Logger.getLogger(GameStateRequestFlatBuffersSerializable.class.getName());
    private byte gameStatus;
    private int[] position;
    private String name = "";
    private String gameCode = "";

    public GameStateRequestFlatBuffersSerializable withGameStatus(byte gameStatus) {
        this.gameStatus = gameStatus;
        return this;
    }

    public GameStateRequestFlatBuffersSerializable withPosition(int[] position) {
        this.position = position;
        return this;
    }

    public GameStateRequestFlatBuffersSerializable withName(String name) {
        this.name = name;
        return this;
    }

    public GameStateRequestFlatBuffersSerializable withGameCode(String gameCode) {
        this.gameCode = gameCode;
        return this;
    }

    @Override
    public byte[] serialize(FlatBufferBuilder builder) {

        final var name = builder.createString(this.name);
        final var gameCode = builder.createString(this.gameCode);

        GameStateRequest.startGameStateRequest(builder);
        GameStateRequest.addGameStatusRequest(builder, gameStatus);

        if(position != null) {

            GameStateRequest.addX(builder, position[0]);
            GameStateRequest.addY(builder, position[1]);
            GameStateRequest.addRotationAngle(builder, position[2]);
        }

        GameStateRequest.addName(builder, name);
        GameStateRequest.addGameCode(builder, gameCode);

        final var gameStateRequest = GameStateRequest.endGameStateRequest(builder);

        builder.finish(gameStateRequest);

        return builder.sizedByteArray();
    }
}
