package cz.radovanmoncek.client.modules.games.models;

import com.google.flatbuffers.FlatBufferBuilder;
import cz.radovanmoncek.client.ship.parents.models.FlatBuffersSerializable;
import cz.radovanmoncek.client.ship.tables.GameStateRequest;
import cz.radovanmoncek.client.ship.tables.GameStatus;

import java.util.logging.Level;
import java.util.logging.Logger;

public record GameStateRequestFlatBuffersSerializable(int x, int y, int rotationAngle, String name, byte gameStatus, String gameCode) implements FlatBuffersSerializable {
    private static final Logger logger = Logger.getLogger(GameStateRequestFlatBuffersSerializable.class.getName());

    @Override
    public byte[] serialize(FlatBufferBuilder builder) {

        if (gameCode == null || name == null){

            logger.log(Level.SEVERE, "Serializable %s invalid", this);

            return new byte[0];
        }

        final var name = builder.createString(this.name);
        final var gameCode = builder.createString(this.gameCode);

        GameStateRequest.startGameStateRequest(builder);
        GameStateRequest.addGameStatusRequest(builder, gameStatus);

        switch (gameStatus) {

            case GameStatus.STATE_CHANGE -> {

                GameStateRequest.addX(builder, x);
                GameStateRequest.addY(builder, y);
                GameStateRequest.addRotationAngle(builder, rotationAngle);
            }

            case GameStatus.START_SESSION -> GameStateRequest.addName(builder, name);

            case GameStatus.JOIN_SESSION -> {

                GameStateRequest.addName(builder, name);
                GameStateRequest.addGameCode(builder, gameCode);
            }
        }

        final var gameStateRequest = GameStateRequest.endGameStateRequest(builder);

        builder.finish(gameStateRequest);

        return builder.sizedByteArray();
    }
}
