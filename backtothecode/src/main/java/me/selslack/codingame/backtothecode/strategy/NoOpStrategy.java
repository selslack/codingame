package me.selslack.codingame.backtothecode.strategy;

import me.selslack.codingame.backtothecode.GameState;

import java.util.Arrays;

public class NoOpStrategy extends MovementSequenceStrategy {
    public NoOpStrategy(GameState state, int playerId) {
        super(state, playerId, Arrays.asList(state.getPlayer(playerId).getPosition()));
    }

    @Override
    public String toString() {
        return "NoOpStrategy";
    }
}
