package me.selslack.codingame.zombies;

import me.selslack.codingame.zombies.mcts.*;

public class Player {
    final private Communicator communicator;
    final private GameState state;

    public Player(Communicator communicator) {
        this.communicator = communicator;
        this.state = new GameState();
    }

    public void run() {
        for (int i = 0; i < Integer.MAX_VALUE; i++) {
            communicator.readState(state, i > 0);
            communicator.sendCommand(process(state));
        }
    }

    public Waypoint process(GameState state) {
        Solver solver = new Solver(state, 95);
        Waypoint p = solver.run();

        System.out.println("Played games: " + solver.playouts);

        Game.process(state, p.x, p.y);

        return p;
    }

    public static void main(String args[]) {
        new Player(new CodinGameCommunicator()).run();
    }
}