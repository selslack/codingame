package me.selslack.codingame.backtothecode;

import java.util.*;
import java.util.function.Consumer;
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

    static public GameState move(GameState state, int playerId, Direction direction) {
        GameField field = state.getField();
        GameState.Player player = state.getPlayer(playerId);
        int connectionCount = 0;
        int[] newPosition = state.getField()
            .sum(player.getPosition(), direction._dimensions)
            .orElse(new int[]{ -1, -1 });

        player.setPosition(newPosition);

        if (! player.isActive()) {
            return state;
        }

        field.set(playerId, v -> v == -1, newPosition);

        for (Direction directionConnection : Direction.directions()) {
            Optional<int[]> connCoords = field.sum(player.getPosition(), directionConnection.getDimensions());

            if (! connCoords.isPresent()) {
                continue;
            }

            if (field.get(connCoords.get()) == playerId) {
                connectionCount++;
            }
        }

        if (connectionCount < 2) {
            return state;
        }

        for (int[][] point : fillCheckPositions) {
            fill(
                field,
                playerId,
                field.sum(player.getPosition(), point)
                    .orElse(player.getPosition())
            );
        }

        return state;
    }

    static private void fill(GameField field, int playerId, int[] coordinates) {
        if (field.get(coordinates) >= 0) {
            return ;
        }

        int[][] fill = StreamSupport.stream(new AdjacentCellSpliterator(field, coordinates), false).toArray(int[][]::new);

        for (int[] point : fill) {
            for (int[][] check : fillCheckPositions) {
                Optional<int[]> sum = field.sum(point, check);

                if (! sum.isPresent()) {
                    return ;
                }

                int data = field.get(sum.get());

                if (data != playerId && data > 0) {
                    return ;
                }
            }
        }

        for (int[] point : fill) {
            field.set((byte) playerId, point);
        }
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
        private Queue<int[]> queue = new LinkedList<>();
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

            queue.add(cell);
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
