package me.selslack.codingame.zombies.server;

import me.selslack.codingame.zombies.Game;
import me.selslack.codingame.zombies.GameState;
import me.selslack.codingame.zombies.Player;
import me.selslack.codingame.zombies.mcts.Config;

import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;

public class SimpleGameTask extends RecursiveTask<Integer> {
    final private GameState state;
    final private Config config;

    public SimpleGameTask(GameState start, Config config) {
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