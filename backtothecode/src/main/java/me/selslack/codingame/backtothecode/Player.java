package me.selslack.codingame.backtothecode;

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
        GameState state = InputReader.readOpponentCount(in);
        Solver solver = new Solver();

        // game loop
        while (true) {
            InputReader.readRoundNumber(state, in);
            InputReader.readPlayerState(state, 0, in);

            for (int i = 0; i < state.getPlayers().length - 1; i++) {
                InputReader.readPlayerState(state, i + 1, in);
            }

            for (int i = 0; i < 20; i++) {
                InputReader.readMapState(state, i, in);
            }

            long start = System.nanoTime();
            int[] result = solver.solve(state, (byte) 0);

            out.println(result[0] + " " + result[1] + " " + String.format("%.2f", (float) (System.nanoTime() - start) / 1000000) + " MS");
        }
    }

    public static void main(String args[]) {
        new Player(System.in, System.out).run();
    }
}