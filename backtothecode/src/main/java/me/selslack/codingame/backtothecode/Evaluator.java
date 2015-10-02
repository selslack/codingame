package me.selslack.codingame.backtothecode;

import me.selslack.codingame.backtothecode.heuristics.Heuristic;

import java.util.HashMap;
import java.util.Map;

public class Evaluator {
    Map<Heuristic, Double> heuristics = new HashMap<>();

    public void addHeuristic(Heuristic heuristic, double influence) {
        heuristics.put(heuristic, influence);
    }

    public double evaluate(GameState state, byte playerId, Game.Direction direction) {
        double score = 0.0;

        for (Map.Entry<Heuristic, Double> entry : heuristics.entrySet()) {
            long startTime = System.nanoTime();
            score += entry.getKey().score(state.clone(), playerId, direction) * entry.getValue();
            System.err.println(entry.getKey().getClass().toString() + ": " + ((System.nanoTime() - startTime) / 1000000.0));
        }

        return score;
    }
}
