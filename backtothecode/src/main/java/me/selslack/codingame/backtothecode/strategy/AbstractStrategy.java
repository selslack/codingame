package me.selslack.codingame.backtothecode.strategy;

import me.selslack.codingame.backtothecode.Game;
import me.selslack.codingame.backtothecode.GameState;

abstract public class AbstractStrategy {
    protected final GameState state;
    protected final int playerId;
    protected final int startRound;

    public AbstractStrategy(GameState state, int playerId) {
        this.state = state;
        this.playerId = playerId;
        this.startRound = state.round;
    }

    public int getStartRound() {
        return startRound;
    }

    public boolean isFinished(int round) {
        return getEffectiveRemainingMoves(round) == 0;
    }

    abstract public int[] getMove(int round);
    abstract public int getRemainingMoves(int round);
    abstract public double getSafety();

    public int getEffectiveRemainingMoves(int round) {
        int result = 0;

        for (int i = round; getRemainingMoves(i) > 0; i++) {
            if (state.field.get(getMove(i)) >= 0) {
                continue;
            }

            result = i - round + 1;
        }

        return result;
    }

    public int getPredictedScore() {
        try {
            GameState copy = state.clone();

            for (int i = state.round; !isFinished(i); i++) {
                Game.move(copy, playerId, getMove(i));

                if (!copy.getPlayer(playerId).isActive()) {
                    return 0;
                }
            }

            return copy.field.count(v -> v == playerId) - state.field.count(v -> v == playerId);
        }
        catch (Exception e) {
            return 0;
        }
    }

    public boolean isBackshiftRequired() {
        return false;
    }

    public void backshifted() {

    }
}
