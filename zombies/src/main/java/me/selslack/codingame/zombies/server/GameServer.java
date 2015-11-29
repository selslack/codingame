package me.selslack.codingame.zombies.server;

import me.selslack.codingame.zombies.GameState;
import me.selslack.codingame.zombies.Human;
import me.selslack.codingame.zombies.Player;

import java.io.*;
import java.util.Scanner;

public class GameServer {
    static public GameState[] cases = new GameState[2];

    static {
        // Case 1: Simple
        cases[0] = new GameStateBuilder(0, 0)
            .addHuman(0, 8250, 4500)
            .addZombie(0, 8250, 8999)
            .state;

        // Case 2: 2 zombies
        cases[1] = new GameStateBuilder(5000, 0)
            .addHuman(0, 950, 6000)
            .addHuman(1, 8000, 6100)
            .addZombie(0, 3100, 7000)
            .addZombie(1, 11500, 7100)
            .state;
    }

    final private PipedOutputStream out;
    final private PipedInputStream in;

    final private PrintStream writer;
    final private Scanner reader;

    final private GameState state;
    final private Player player;

    public GameServer(GameState start) {
        this.state = start;
        this.out = new PipedOutputStream();
        this.in = new PipedInputStream();
        this.writer = new PrintStream(this.out);
        this.reader = new Scanner(this.in);

        try {
            this.player = new Player(
                new PipedInputStream(this.out),
                new PipedOutputStream(this.in)
            );
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public int run() {
        while (!state.isTerminal()) {
            // Cleanup
            state.getHumans().removeIf(v -> !v.isAlive);
            state.getZombies().removeIf(v -> !v.isAlive);

            // Ash
            writer.print(state.getAsh().x);
            writer.print(" ");
            writer.print(state.getAsh().y);
            writer.println();

            // Humans
            writer.println(state.getHumans().size());

            for (Human human : state.getHumans()) {
                writer.print(human.id);
                writer.print(" ");
                writer.print(human.x);
                writer.print(" ");
                writer.print(human.y);
                writer.println();
            }

            // Zombies
            writer.println(state.getZombies().size());

            for (Human zombie : state.getHumans()) {
                writer.print(zombie.id);
                writer.print(" ");
                writer.print(zombie.x);
                writer.print(" ");
                writer.print(zombie.y);
                writer.print(" ");
                writer.print(0);
                writer.print(" ");
                writer.print(0);
                writer.println();
            }

            // Read the response
            reader.nextInt();
            reader.nextInt();
        }

        return state.score;
    }
}
