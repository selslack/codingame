package me.selslack.codingame.zombies.server;

import me.selslack.codingame.zombies.CodinGameCommunicator;
import me.selslack.codingame.zombies.GameState;
import me.selslack.codingame.zombies.Human;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.Scanner;

public class GameStateBuilder {
    static public GameState build(String id) {
        GameState result = new GameState();

        InputStream input = Optional.ofNullable(GameStateBuilder.class.getResourceAsStream(String.format("%s.cgstate", id)))
            .orElseThrow(() -> new RuntimeException(String.format("No saved state found for id: %s", id)));

        deserialize(result, input);

        try {
            input.close();
        }
        catch (IOException ignored) {
            // Nothing to do here
        }

        return result;
    }

    /**
     * Note: this is very similar to referenced method except there is no object IDs
     * and no predicted zombie positions in the input.
     *
     * @see CodinGameCommunicator#readState(me.selslack.codingame.zombies.GameState, boolean)
     */
    static private void deserialize(GameState to, InputStream input) {
        Scanner scanner = new Scanner(input);

        // Setup
        to.getHumans().clear();
        to.getZombies().clear();

        // Ash
        to.getAsh().x = scanner.nextInt();
        to.getAsh().y = scanner.nextInt();

        // Humans
        for (int i = scanner.nextInt(); i > 0; i--) {
            to.getHumans().add(
                new Human(
                    Human.Type.HUMAN,
                    i,
                    scanner.nextInt(),
                    scanner.nextInt()
                )
            );
        }

        // Zombies
        for (int i = scanner.nextInt(); i > 0; i--) {
            to.getZombies().add(
                new Human(
                    Human.Type.ZOMBIE,
                    i,
                    scanner.nextInt(),
                    scanner.nextInt()
                )
            );
        }
    }
}
