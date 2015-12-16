package me.selslack.codingame.zombies.server;

import me.selslack.codingame.zombies.CodinGameCommunicator;
import me.selslack.codingame.zombies.GameState;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

public class GameStateBuilder {
    static public GameState build(String id) {
        GameState result = new GameState();

        InputStream input = Optional.ofNullable(GameStateBuilder.class.getResourceAsStream(String.format("%s.cgstate", id)))
            .orElseThrow(() -> new RuntimeException(String.format("No saved state found for id: %s", id)));

        // Deserialize input string to the supplied state
        new CodinGameCommunicator(input).readState(result, false);

        try {
            input.close();
        }
        catch (IOException ignored) {
            // Nothing to do here
        }

        return result;
    }
}
