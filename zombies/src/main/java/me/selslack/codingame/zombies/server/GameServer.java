package me.selslack.codingame.zombies.server;

import me.selslack.codingame.zombies.Game;
import me.selslack.codingame.zombies.GameState;
import me.selslack.codingame.zombies.Player;
import me.selslack.codingame.zombies.mcts.Waypoint;

import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class GameServer {
    static final private ExecutorService executor = Executors.newFixedThreadPool(2);
    static final public GameState[] cases = new GameState[3];

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

        // Case 4: CG
        cases[2] = new GameStateBuilder(7500, 7000)
            .addHuman(0, 5000, 3000)
            .addHuman(1, 6000, 8000)
            .addZombie(0, 2000, 2000)
            .addZombie(1, 2000, 3000)
            .addZombie(2, 2000, 4000)
            .addZombie(3, 3000, 1000)
            .addZombie(4, 3000, 5000)
            .addZombie(5, 4000, 1000)
            .addZombie(6, 4000, 5000)
            .addZombie(7, 5000, 1000)
            .addZombie(8, 5000, 5000)
            .addZombie(9, 6000, 1000)
            .addZombie(10, 6000, 5000)
            .addZombie(11, 9000, 1000)
            .addZombie(12, 9000, 2000)
            .addZombie(13, 9000, 3000)
            .addZombie(14, 9000, 4000)
            .addZombie(15, 9000, 5000)
            .state;
    }

    final private GameServerCommunicator communicator;

    final private GameState state;
    final private Runnable player;

    public GameServer(GameState start) {
        this.state = start;
        this.communicator = new GameServerCommunicator();
        this.player = () -> {
            new Player(communicator).run();
        };
    }

    public int run() {
        Future<?> future = executor.submit(player);

        try {
            while (!state.isTerminal()) {
                Optional<Waypoint> result = communicator.sendState(state).readCommand(130, TimeUnit.MILLISECONDS);

                if (result.isPresent()) {
                    Game.process(state, result.get().x, result.get().y);
                }
                else {
                    throw new InterruptedException("Client didn't provide response in time");
                }
            }

            return state.score;
        }
        catch (InterruptedException e) {
            return 0;
        }
    }
}
