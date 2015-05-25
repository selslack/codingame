package me.selslack.codingame.tron;

import java.lang.*;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.*;

public class Player {
    public static void main(String args[]) {
        Scanner in = new Scanner(System.in);
        AI      ai = new AI();

        // game loop
        while (true) {
            int N = in.nextInt(); // total number of players (2 to 4).
            int P = in.nextInt(); // your player number (0 to 3).

            ai.game.initialize(N, P);

            for (int i = 0; i < N; i++) {
                ai.game.push(i, in.nextInt(), in.nextInt(), in.nextInt(), in.nextInt());
            }

            System.out.println(ai.makeDecision());
        }
    }
}

final class GameField implements Cloneable {
    final
    private int[]   _dimensions;
    private byte[]  _map;

    public GameField(final int... dimensions) {
        _dimensions = dimensions;
        _map        = new byte[Arrays.stream(_dimensions).filter(i -> i >= 0).reduce((a, b) -> a * b).orElse(0)];
    }

    public void fill(byte value) {
        fill(value, v -> true);
    }

    public void fill(byte value, Predicate<Byte> predicate) {
        for (int i = 0; i < _map.length; i++) {
            if (predicate.test(_map[i])) {
                _map[i] = value;
            }
        }
    }

    public void set(byte value, final int... dimensions) {
        _map[index(dimensions)] = value;
    }

    public byte get(final int... dimensions) {
        return _map[index(dimensions)];
    }

    public int index(final int... dimensions) {
        if (!(_dimensions.length == dimensions.length)) {
            throw new IllegalArgumentException("Dimensions do not match");
        }

        int result = 0;

        for (byte i = 0; i < _dimensions.length; i++) {
            if (!(dimensions[i] >= 0 && dimensions[i] < _dimensions[i])) {
                throw new IllegalArgumentException("Invalid dimension given for axis " + i + ": " + Arrays.deepToString(IntStream.of(dimensions).boxed().toArray()));
            }

            result = dimensions[i] + _dimensions[i] * result;
        }

        return result;
    }

    public boolean check(final int... dimensions) {
        if (!(_dimensions.length == dimensions.length)) {
            return false;
        }

        for (byte i = 0; i < _dimensions.length; i++) {
            if (!(dimensions[i] >= 0 && dimensions[i] < _dimensions[i])) {
                return false;
            }
        }

        return true;
    }

    @Override
    protected GameField clone() {
        GameField result;

        try {
            result = (GameField) super.clone();
        }
        catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }

        result._map = Arrays.copyOf(this._map, this._map.length);

        return result;
    }
}

class Game {
    enum Direction {
        UP   ("UP",     0, -1),
        DOWN ("DOWN",   0,  1),
        LEFT ("LEFT",  -1,  0),
        RIGHT("RIGHT",  1,  0);

        private final String _movement;
        private final int[]  _dimensions;

        private static final Random          _random = new Random();
        private static final Direction[]     _values = values();

        Direction(String movement, int... dimensions) {
            _movement   = movement;
            _dimensions = dimensions;
        }

        public String getMovement() {
            return _movement;
        }

        public int[] getDimensions() {
            return _dimensions;
        }

        public static Direction[] directions() {
            return _values;
        }

        public static Direction random() {
            return _values[_random.nextInt(_values.length)];
        }
    }

    static class TronGameField implements Cloneable {
        static final private int
            DIMENSIONS = 2,
            X          = 30,
            Y          = 20;

        final private int _playerCount;

        private GameField _field;
        private int[][]   _positions;
        private boolean[] _defeated;

        TronGameField(int playerCount) {
            _playerCount = playerCount;
            _field       = new GameField(X, Y);
            _positions   = new int[_playerCount][DIMENSIONS];
            _defeated    = new boolean[_playerCount];

            // Mark every cell as default region
            _field.fill(Byte.MIN_VALUE);
        }

        public Byte getValue(int... coordinates) {
            return _field.check(coordinates) ? _field.get(coordinates) : null;
        }

        public boolean isDefeated(int playerId) {
            return _defeated[playerId];
        }

        public void push(int playerId, int... coordinates) {
            if (!(playerId >= 0 && playerId < _playerCount)) {
                throw new IllegalArgumentException("Invalid playerId");
            }

            if (!(coordinates.length == DIMENSIONS)) {
                throw new IllegalArgumentException("Invalid coordinates");
            }

            if (coordinates[0] == -1) {
                _defeated[playerId] = true;
            }
            else {
                if (isDefeated(playerId)) {
                    throw new IllegalArgumentException("Player " + playerId + " has been defeated. This mustn't happen in real life.");
                }

                _field.set((byte) playerId, coordinates);
            }

            _positions[playerId] = coordinates;
        }

        @Override
        protected TronGameField clone() {
            TronGameField result;

            try {
                result = (TronGameField) super.clone();
            }
            catch (CloneNotSupportedException e) {
                throw new RuntimeException(e);
            }

            result._field     = _field.clone();
            result._defeated  = _defeated.clone();
            result._positions = Arrays.stream(result._positions).map((ints) -> ints.clone()).toArray((value) -> new int[value][]);

            return result;
        }
    }

    public int pId  = 0,
               pNum = -1;

    private LinkedList<TronGameField> _fields;

    public void initialize(int playerCount, int playerId)
    {
        if (!(playerCount > 0 && playerId >= 0 && playerId < playerCount)) {
            throw new IllegalArgumentException("Invalid initialization options");
        }

        if (_fields != null) {
            if (!(playerCount == pNum && playerId == pId)) {
                throw new IllegalArgumentException("Can not reinitialize the same game with different configuration");
            }

            return ;
        }

        pId = playerId;
        pNum = playerCount;

        _fields = new LinkedList<>();
        _fields.add(new TronGameField(playerCount));
    }

    private TronGameField _getActiveField() {
        return _fields.getFirst();
    }

    public void savepoint() {
        _fields.addFirst(
            _fields.getFirst().clone()
        );
    }

    public void revert() {
        if (_fields.size() == 1) {
            throw new RuntimeException("No saved state present");
        }

        _fields.removeFirst();
    }

    public void push(int playerId, int _1, int _2, int x, int y) {
        _getActiveField().push(playerId, x, y);
    }

    public void push(int playerId, Direction direction) {
        int[] coordinates = getPlayerPosition(playerId).clone();

        for (int i = 0; i < TronGameField.DIMENSIONS; i++) {
            coordinates[i] += direction._dimensions[i];
        }

        _getActiveField().push(playerId, coordinates);
    }

    public int[] getPlayerPosition(int playerId) {
        return _getActiveField()._positions[playerId];
    }

    public boolean isCellAvailable(int... dimensions) {
        Byte value = _getActiveField().getValue(dimensions);
        return value != null && (value == Byte.MIN_VALUE || _getActiveField().isDefeated(value));
    }

    public boolean isCellAvailable(Direction direction, int... dimensions) {
        int[] coordinates = dimensions.clone();

        for (int i = 0; i < TronGameField.DIMENSIONS; i++) {
            coordinates[i] += direction._dimensions[i];
        }

        Byte value = _getActiveField().getValue(coordinates);
        return value != null && (value == Byte.MIN_VALUE || _getActiveField().isDefeated(value));
    }
}

class AI {
    interface Evaluator {
        int CELL_VALUE = 1000;
        public int evaluate(Game game, int playerId);
    }

    static class PossibleMovementsEvaluator implements Evaluator {
        final static class Cell {
            final private int[] _dimensions;

            Cell(int... dimensions) {
                _dimensions = dimensions;
            }

            public Cell add(int... dimensions) {
                int[] result = new int[_dimensions.length];

                for (int i = 0; i < _dimensions.length; i++) {
                    result[i] = _dimensions[i] + dimensions[i];
                }

                return new Cell(result);
            }

            public Cell add(Game.Direction direction) {
                return add(direction.getDimensions());
            }

            @Override
            public boolean equals(Object other) {
                return this == other || !(other == null || getClass() != other.getClass()) && Arrays.equals(_dimensions, ((Cell) other)._dimensions);
            }

            @Override
            public int hashCode() {
                int result = 0;

                for (int dimension : _dimensions) {
                    result = 37 * result + dimension;
                }

                return result;
            }
        }

        private static class FreeAdjacentCellSpliterator implements Spliterator<Cell> {
            private Game        _game;
            private Queue<Cell> _queue   = new LinkedList<>();
            private Set<Cell>   _visited = new HashSet<>(256);

            public FreeAdjacentCellSpliterator(Game game, int[] initial) {
                _game = game;

                // add initial cell to queue
                _addCellToQueue(new Cell(initial));
            }

            private void _addCellToQueue(Cell cell) {
                if (_visited.contains(cell)) {
                    return ;
                }

                _queue.add(cell);
                _visited.add(cell);
            }

            @Override
            public boolean tryAdvance(Consumer<? super Cell> action) {
                if (_queue.isEmpty()) {
                    return false;
                }

                Cell cell = _queue.remove();

                for (Game.Direction direction : Game.Direction.values()) {
                    Cell predicted = cell.add(direction);

                    if (_game.isCellAvailable(predicted._dimensions)) {
                        _addCellToQueue(predicted);
                    }
                }

                if (_game.isCellAvailable(cell._dimensions)) {
                    action.accept(cell);
                }

                return true;
            }

            @Override
            public Spliterator<Cell> trySplit() {
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

        @Override
        public int evaluate(Game game, int playerId) {
            return (int) StreamSupport.stream(new FreeAdjacentCellSpliterator(game, game.getPlayerPosition(playerId)), false).limit(200).count();
        }
    }

    final public Game game = new Game();

    public String makeDecision() {
        Evaluator evaluator    = new PossibleMovementsEvaluator();
        String    bestMovement = "OOPS!";
        int       bestScore    = 0;

        for (Game.Direction direction : Game.Direction.values()) {
            if (!game.isCellAvailable(direction, game.getPlayerPosition(game.pId))) {
                continue;
            }

            game.savepoint();
            game.push(game.pId, direction);

            int score = evaluator.evaluate(game, game.pId);

            if (score > bestScore) {
                bestScore    = score;
                bestMovement = direction.getMovement();
            }

            game.revert();
        }

        System.out.println(bestScore);
        System.out.println(bestMovement);

        return bestMovement;
    }
}