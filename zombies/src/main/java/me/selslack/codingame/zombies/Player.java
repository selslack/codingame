package me.selslack.codingame.zombies;

import me.selslack.codingame.zombies.mcts.*;

public class Player {
    final private Communicator communicator;
    final private GameState state;
    final private Solver solver;

    public Player(Communicator communicator, Config config) {
        this(new GameState(), communicator, config);
    }

    public Player(GameState state, Communicator communicator, Config config) {
        this.state = state;
        this.communicator = communicator;
        this.solver = new Solver(state, config);
    }

    public void run() {
        for (int i = 0; i < Integer.MAX_VALUE; i++) {
            communicator.readState(state, i > 0);
            communicator.sendCommand(process());
        }
    }

    public Waypoint process() {
        Waypoint p = solver.run();

        Game.process(state, p.x, p.y);

        return p;
    }

    public static void main(String args[]) {
        new Player(new CodinGameCommunicator(), new Config()).run();
    }
}