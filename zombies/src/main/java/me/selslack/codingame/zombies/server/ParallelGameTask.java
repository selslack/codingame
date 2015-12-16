package me.selslack.codingame.zombies.server;

import me.selslack.codingame.zombies.GameState;
import me.selslack.codingame.zombies.mcts.Config;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.RecursiveTask;
import java.util.stream.IntStream;

public class ParallelGameTask extends RecursiveTask<List<Integer>> {
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
                new SimpleGameTask(states[0], config).compute()
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
