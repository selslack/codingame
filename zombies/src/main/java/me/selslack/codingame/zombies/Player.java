package me.selslack.codingame.zombies;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Comparator;
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

            GameState futureState = state.clone();

            Game.process(futureState, futureState.getAsh().x, futureState.getAsh().y, false);

            Human target = futureState.getZombies()
                .stream()
                .sorted(Comparator.comparingInt(v -> -Utils.threat(futureState.getAsh(), v, state.getZombies(), state.getHumans())))
                .findFirst()
                .orElseThrow(RuntimeException::new);

            out.println(target.x + " " + target.y);

            Game.process(state, target.x, target.y, true);

            if (state.getHumans().stream().filter(v -> v.isAlive).count() < 1 || state.getZombies().stream().filter(v -> v.isAlive).count() < 1) {
                break;
            }
        }
    }

    public static void main(String args[]) {
        new Player(System.in, System.out).run();
    }
}