package me.selslack.codingame.zombies;

import me.selslack.codingame.zombies.mcts.*;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Scanner;

public class Player {
    final private Communicator communicator;

    public Player(Communicator communicator) {
        this.communicator = communicator;
    }

    public void run() {
        GameState state = new GameState();

        while (true) {
            try {
                communicator.readState(state);
                communicator.wasteNextRead();

                Waypoint p = new Solver(state, 95).run();

                communicator.sendCommand(p);

                Game.process(state, p.x, p.y);
            }
            catch (InterruptedException e) {
                break;
            }

            if (state.isTerminal()) {
                break;
            }
        }
    }

    public static void main(String args[]) {
        new Player(new CodinGameCommunicator()).run();
    }
}