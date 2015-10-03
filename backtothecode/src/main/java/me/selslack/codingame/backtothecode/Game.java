package me.selslack.codingame.backtothecode;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Game mechanics.
 */
final public class Game {
    static private int[][][] fillCheckPositions = new int[][][] {
        new int[][] { Direction.UP._dimensions, Direction.LEFT._dimensions, },
        new int[][] { Direction.UP._dimensions, },
        new int[][] { Direction.UP._dimensions, Direction.RIGHT._dimensions, },
        new int[][] { Direction.RIGHT._dimensions, },
        new int[][] { Direction.DOWN._dimensions, Direction.RIGHT._dimensions, },
        new int[][] { Direction.DOWN._dimensions, },
        new int[][] { Direction.DOWN._dimensions, Direction.LEFT._dimensions, },
        new int[][] { Direction.LEFT._dimensions, },
    };

    static public double influence(GameState state, int playerId, int... point) {
        int closest = Integer.MAX_VALUE;
        int mine = Integer.MAX_VALUE;

        for (GameState.Player player : state.getPlayers()) {
            int current = state.field.manhattan(point, player.getPosition());

            if (closest > current) {
                closest = current;
            }

            if (player.getId() == playerId) {
                mine = current;
            }
        }

        if (mine == closest) {
            return 1.0;
        }
        else {
            return 1.0 / (mine - closest + 1);
        }
    }

    static public GameState move(GameState state, int playerId, Direction direction) {
        int[] newPosition = state.field
            .sum(state.getPlayer(playerId).getPosition(), direction._dimensions)
            .orElse(new int[]{ -1, -1 });

        return move(state, playerId, newPosition);
    }

    static public GameState move(GameState state, int playerId, int[] newPosition) {
        GameState.Player player = state.getPlayer(playerId);
        int connectionCount = 0;

        if (state.field.manhattan(player.getPosition(), newPosition) > 1 && newPosition[0] != -1) {
            throw new IllegalArgumentException("Invalid position for player: " + playerId);
        }

        player.setPosition(newPosition);

        if (!player.isActive()) {
            return state;
        }

        state.field.set(playerId, v -> v == -1, newPosition);

        for (Direction directionConnection : Direction.directions()) {
            Optional<int[]> connCoords = state.field.sum(player.getPosition(), directionConnection.getDimensions());

            if (! connCoords.isPresent()) {
                continue;
            }

            if (state.field.get(connCoords.get()) == playerId) {
                connectionCount++;
            }
        }

        if (connectionCount < 2) {
            return state;
        }

        for (int[][] point : fillCheckPositions) {
            state.field = fill(
                state.field,
                playerId,
                state.field.sum(player.getPosition(), point)
                    .orElse(player.getPosition())
            );
        }

        return state;
    }

    static private GameField fill(GameField field, int playerId, int[] coordinates) {
        if (field.get(coordinates) >= 0) {
            return field;
        }

        GameField shadow = field.clone();

        try (Stream<int[]> stream = StreamSupport.stream(new AdjacentCellSpliterator(field, coordinates), false)) {
            for (int[] point : (Iterable<int[]>) stream::iterator) {
                for (int[][] check : fillCheckPositions) {
                    Optional<int[]> sum = field.sum(point, check);

                    if (! sum.isPresent()) {
                        return field;
                    }

                    int data = field.get(sum.get());

                    if (data != playerId && data > 0) {
                        return field;
                    }

                    shadow.set(playerId, sum.get());
                }
            }
        }

        return shadow;
    }

    static public enum Direction {
        UP   (0, -1),
        DOWN (0,  1),
        LEFT (-1, 0),
        RIGHT(1,  0);

        private final int[]  _dimensions;

        private static final Direction[] _values = values();

        Direction(int... dimensions) {
            _dimensions = dimensions;
        }

        public int[] getDimensions() {
            return _dimensions;
        }

        public static Direction[] directions() {
            return _values;
        }
    }

    private static class AdjacentCellSpliterator implements Spliterator<int[]> {
        private GameField field;
        private LinkedList<int[]> queue = new LinkedList<>();
        private Map<Integer, int[]> visited = new HashMap<>(256);

        public AdjacentCellSpliterator(GameField field, int[] initial) {
            this.field = field;

            // add initial cell to queue
            _addCellToQueue(initial);
        }

        private void _addCellToQueue(int[] cell) {
            Integer hash = field.hash(cell);

            if (visited.containsKey(hash)) {
                return ;
            }

            queue.addFirst(cell);
            visited.put(hash, cell);
        }

        @Override
        public boolean tryAdvance(Consumer<? super int[]> action) {
            if (queue.isEmpty()) {
                return false;
            }

            int[] cell = queue.remove();

            for (int[][] points : fillCheckPositions) {
                Optional<int[]> coordinates = field.sum(cell, points);

                if (! coordinates.isPresent()) {
                    continue;
                }

                if (field.get(coordinates.get()) >= 0) {
                    continue;
                }

                _addCellToQueue(coordinates.get());
            }

            if (field.get(cell) < 0) {
                action.accept(cell);
            }

            return true;
        }

        @Override
        public Spliterator<int[]> trySplit() {
            return null;
        }

        @Override
        public long estimateSize() {
            return Long.MAX_VALUE;
        }

        @Override
        public int characteristics() {
            return DISTINCT | NONNULL;
        }
    }
}
