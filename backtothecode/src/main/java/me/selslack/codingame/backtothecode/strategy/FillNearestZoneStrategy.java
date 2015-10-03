package me.selslack.codingame.backtothecode.strategy;

import me.selslack.codingame.backtothecode.Game;
import me.selslack.codingame.backtothecode.GameState;

import java.util.Deque;
import java.util.LinkedList;
import java.util.Optional;

public class FillNearestZoneStrategy {
    final private GameState state;
    final private int depth;

    public FillNearestZoneStrategy(GameState state) {
        this(state, 3);
    }

    public FillNearestZoneStrategy(GameState state, int depth) {
        this.state = state;
        this.depth = depth;
    }

    public Deque<Deque<int[]>> get(int playerId) {
        LinkedList<Deque<int[]>> options = new LinkedList<>();

        int minX = state.getPlayer(playerId).getPosition()[0] - depth;
        int maxX = state.getPlayer(playerId).getPosition()[0] + depth;
        int minY = state.getPlayer(playerId).getPosition()[1] - depth;
        int maxY = state.getPlayer(playerId).getPosition()[1] + depth;

        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {

            }
        }

        return options;
    }

    private Deque<int[]> shortestPath(int[] from, int[] to, Game.Direction... directions) {
        return new LinkedList<>();
    }
}
