package me.selslack.codingame.backtothecode.heuristics;

import me.selslack.codingame.backtothecode.Game;
import me.selslack.codingame.backtothecode.GameState;
import me.selslack.codingame.backtothecode.strategy.FillNearestZoneStrategy;
import me.selslack.codingame.backtothecode.strategy.FillRectangleStrategy;

import java.util.Arrays;
import java.util.Deque;
import java.util.DoubleSummaryStatistics;

public class StrategyHeuristic implements Heuristic {
    @Override
    public double score(GameState state, int playerId, int[] point) {
        DoubleSummaryStatistics statistics = new DoubleSummaryStatistics();
        Deque<Deque<int[]>> strategies = FillRectangleStrategy.doIt(state.field, playerId, point);

        while (!strategies.isEmpty()) {
            Deque<int[]> sequence = strategies.pollFirst();
            DoubleSummaryStatistics safetyStats = new DoubleSummaryStatistics();

            if (!Arrays.equals(sequence.peekFirst(), point)) {
                continue;
            }

            while (!sequence.isEmpty()) {
                int value = state.field.get(sequence.peekLast());

                if (value == playerId) {
                    sequence.pollLast();
                }
                else {
                    break;
                }
            }

            GameState future = state.clone();
            int length = sequence.size();

            while (!sequence.isEmpty()) {
                safetyStats.accept(Game.influence(state, playerId, sequence.peekFirst()));
                Game.move(future, playerId, sequence.pollFirst());
            }

            // @todo use influence rating to be more realistic
            statistics.accept(
                (future.field.count(v -> v == playerId) - state.field.count(v -> v == playerId)) * 1.0 / length
            );
        }

        return statistics.getAverage();
    }
}
