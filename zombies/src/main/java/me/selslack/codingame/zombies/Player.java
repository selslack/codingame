package me.selslack.codingame.zombies;

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
        // @todo implement logic
    }

    public static void main(String args[]) {
        new Player(System.in, System.out).run();
    }
}