package me.selslack.codingame.zombies;

import me.selslack.codingame.zombies.mcts.*;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Scanner;

public class Player {
    private final Scanner in;
    private final PrintStream out;

    public Player(InputStream in, OutputStream out) {
        this(in, new PrintStream(out, true));
    }

    public Player(InputStream in, PrintStream out) {
        this.in = new Scanner(in);
        this.out = out;
    }

    public void run() {
        GameState state = new GameState();
        boolean ignoreServerData = false;

        while (true) {
            InputReader.readPlayerData(state, in, ignoreServerData);
            InputReader.readHumansData(state, in, ignoreServerData);
            InputReader.readZombieData(state, in, ignoreServerData);

            ignoreServerData = true;

            Waypoint p = new Solver(state, 95).run();

            out.println(p.x + " " + p.y);

            Game.process(state, p.x, p.y, false);
        }
    }

    public static void main(String args[]) {
        new Player(System.in, System.out).run();
    }
}