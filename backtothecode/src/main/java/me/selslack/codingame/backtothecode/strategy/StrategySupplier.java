package me.selslack.codingame.backtothecode.strategy;

import me.selslack.codingame.backtothecode.GameState;

import java.util.List;

public interface StrategySupplier {
    List<AbstractStrategy> get(GameState state, int playerId);
}
