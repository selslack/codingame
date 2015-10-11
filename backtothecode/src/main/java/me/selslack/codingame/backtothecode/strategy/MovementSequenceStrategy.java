package me.selslack.codingame.backtothecode.strategy;

import me.selslack.codingame.backtothecode.Game;
import me.selslack.codingame.backtothecode.GameState;

import java.util.Arrays;
import java.util.DoubleSummaryStatistics;
import java.util.List;

public class MovementSequenceStrategy extends AbstractStrategy {
    private final List<int[]> sequence;

    public MovementSequenceStrategy(GameState state, int playerId, List<int[]> sequence) {
        super(state, playerId);

        this.sequence = sequence;
    }

    public int[] getMove(int round) {
        return sequence.get(round - startRound);
    }

    public int getRemainingMoves(int round) {
        return Math.max(0, sequence.size() - (round - startRound));
    }

    public double getSafety() {
        DoubleSummaryStatistics statistics = new DoubleSummaryStatistics();

        for (int i = state.round; !isFinished(i); i++) {
            statistics.accept(Game.influence(state, playerId, getMove(i)));
        }

        return statistics.getAverage();
    }

    @Override
    public String toString() {
        return "MovementSequenceStrategy{" + Arrays.deepToString(sequence.toArray()) + "}";
    }
}
