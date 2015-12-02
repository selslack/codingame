package me.selslack.codingame.zombies.server;

import me.selslack.codingame.zombies.Game;
import me.selslack.codingame.zombies.GameState;
import me.selslack.codingame.zombies.Player;
import me.selslack.codingame.zombies.mcts.Config;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.IntStream;

public class GameServer {
    static final public GameState[] cases = new GameState[4];

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
            .addZombie(16, 10000, 1000)
            .addZombie(17, 10000, 5000)
            .addZombie(18, 11000, 1000)
            .addZombie(19, 11000, 3000)
            .addZombie(20, 11000, 5000)
            .addZombie(21, 12000, 1000)
            .addZombie(22, 12000, 3000)
            .addZombie(23, 12000, 4000)
            .addZombie(24, 12000, 5000)
            .addZombie(25, 13000, 1000)
            .addZombie(26, 13000, 3000)
            .state;

        // Case 7: combo opportunity
        cases[3] = new GameStateBuilder(500, 4500)
            .addHuman(0, 100, 4000)
            .addHuman(1, 130, 5000)
            .addHuman(2, 10, 4500)
            .addHuman(3, 500, 3500)
            .addHuman(4, 10, 5500)
            .addHuman(5, 100, 3000)
            .addZombie(0, 8000, 4500)
            .addZombie(1, 9000, 4500)
            .addZombie(2, 10000, 4500)
            .addZombie(3, 11000, 4500)
            .addZombie(4, 12000, 4500)
            .addZombie(5, 13000, 4500)
            .addZombie(6, 14000, 4500)
            .addZombie(7, 15000, 3500)
            .addZombie(8, 14500, 2500)
            .addZombie(9, 15900, 500)
            .state;
    }

    static public class GameTask extends RecursiveTask<Integer> {
        final private GameState state;
        final private Config config;

        public GameTask(GameState start, Config config) {
            this.config = config;
            this.state = start.clone();
        }

        @Override
        protected Integer compute() {
            Player player = new Player(state.clone(), new InternalCommunicator(), config);

            while (!state.isTerminal()) {
                Game.process(state, ForkJoinTask.adapt(player::process).fork().join());
            }

            return state.score;
        }
    }

    static public class ParallelGameTask extends RecursiveTask<List<Integer>> {
        final private GameState[] states;
        final private Config config;
        final private int from;
        final private int to;

        public ParallelGameTask(Config config, GameState state, int count) {
            this(config, IntStream.range(0, count).mapToObj(v -> state).toArray(GameState[]::new));
        }

        public ParallelGameTask(Config config, GameState... states) {
            this(config, states, 0, states.length);
        }

        private ParallelGameTask(Config config, GameState[] states, int from, int to) {
            this.config = config;
            this.states = states;
            this.from = from;
            this.to = to;
        }

        @Override
        protected List<Integer> compute() {
            int length = to - from;

            if (length == 0) {
                return Collections.emptyList();
            }
            else if (length == 1) {
                return Collections.singletonList(
                    new GameTask(states[0], config).compute()
                );
            }
            else {
                List<Integer> results = new LinkedList<>();

                int leftCount = length / 2;

                ParallelGameTask leftTask = new ParallelGameTask(config, states, from, from + leftCount);
                ParallelGameTask rightTask = new ParallelGameTask(config, states, from + leftCount, to);

                rightTask.fork();

                results.addAll(leftTask.compute());
                results.addAll(rightTask.join());

                return results;
            }
        }
    }
}
