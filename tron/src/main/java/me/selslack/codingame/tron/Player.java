package me.selslack.codingame.tron;

import java.lang.*;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.StreamSupport;

public class Player {
    public static void main(String args[]) {
        Scanner in = new Scanner(System.in);
        AI      ai = new AI();

        // game loop
        while (true) {
            int N = in.nextInt(); // total number of players (2 to 4).
            int P = in.nextInt(); // your player number (0 to 3).

            ai.initialize(N, P);

            for (int i = 0; i < N; i++) {
                ai.push(i, in.nextInt(), in.nextInt(), in.nextInt(), in.nextInt());
            }

            System.out.println(ai.process());
        }
    }
}

enum Direction {
    UP   ("UP",     0, -1),
    DOWN ("DOWN",   0,  1),
    LEFT ("LEFT",  -1,  0),
    RIGHT("RIGHT",  1,  0);

    private final String _movement;
    private final int    _x, _y;

    private static final Random          _random = new Random();
    private static final List<Direction> _values = Collections.unmodifiableList(Arrays.asList(values()));

    Direction(String movement, int x, int y) {
        _movement = movement;
        _x        = x;
        _y        = y;
    }

    public String getMovement() {
        return _movement;
    }

    public int getX() {
        return _x;
    }

    public int getY() {
        return _y;
    }

    public static List<Direction> directions() {
        return _values;
    }

    public static Direction random() {
        return _values.get(_random.nextInt(_values.size()));
    }
}

class Game implements Cloneable {
    class GameState implements Cloneable {
        private byte      _x, _y;
        private boolean[] _defeated;
        private byte[][]  _map;
        private int[][]   _positions;

        public GameState(int players, int x, int y) {
            _x         = (byte) x;
            _y         = (byte) y;
            _defeated  = new boolean[players];
            _map       = new byte[x][y];
            _positions = new int[players][2];

            for (int xx = 0; xx < _x; xx++) {
                Arrays.fill(_map[xx], (byte) -1);
            }
        }

        public void push(int playerId, int x1, int y1) {
            if (x1 == -1 || _defeated[playerId]) {
                if (!_defeated[playerId]) {
                    for (byte x = 0; x < _x; x++) {
                        for (byte y = 0; y < _y; y++) {
                            if (_map[x][y] == playerId) {
                                _map[x][y] = (byte) -1;
                            }
                        }
                    }

                    _defeated[playerId]  = true;
                    _positions[playerId] = new int[] {-1, -1};
                }

                return ;
            }

            _positions[playerId] = new int[] {x1, y1};
            _map[x1][y1]         = (byte) playerId;
        }

        public int getX() {
            return _x;
        }

        public int getY() {
            return _y;
        }

        public int getPlayerCount() {
            return _defeated.length;
        }

        public int[] getPosition(int playerId) {
            return _positions[playerId];
        }

        public boolean isDefeated(int playerId) {
            return _defeated[playerId];
        }

        public boolean isCellEmpty(int x, int y) {
            return x >= 0 && y >= 0 & x < _x && y < _y && _map[x][y] == (byte) -1;
        }

        @Override
        protected GameState clone() {
            GameState result;

            try {
                result = (GameState) super.clone();
            }
            catch (CloneNotSupportedException e) {
                throw new RuntimeException(e);
            }

            result._defeated  = Arrays.copyOf(this._defeated, this._defeated.length);
            result._map       = Arrays.stream(result._map).map((bytes) -> bytes.clone()).toArray((value) -> new byte[value][]);
            result._positions = Arrays.stream(result._positions).map((ints) -> ints.clone()).toArray((value) -> new int[value][]);

            return result;
        }
    }

    private GameState _state;

    @Override
    protected Game clone() {
        Game result;

        try {
            result = (Game) super.clone();
        }
        catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }

        result._state = this._state.clone();

        return result;
    }
}

class GameState implements Cloneable {
    private byte      _x, _y;
    private boolean[] _defeated;
    private byte[][]  _map;
    private int[][]   _positions;

    public GameState(int players, int x, int y) {
        _x         = (byte) x;
        _y         = (byte) y;
        _defeated  = new boolean[players];
        _map       = new byte[x][y];
        _positions = new int[players][2];

        for (int xx = 0; xx < _x; xx++) {
            Arrays.fill(_map[xx], (byte) -1);
        }
    }

    public void push(int playerId, int x1, int y1) {
        if (x1 == -1 || _defeated[playerId]) {
            if (!_defeated[playerId]) {
                for (byte x = 0; x < _x; x++) {
                    for (byte y = 0; y < _y; y++) {
                        if (_map[x][y] == playerId) {
                            _map[x][y] = (byte) -1;
                        }
                    }
                }

                _defeated[playerId]  = true;
                _positions[playerId] = new int[] {-1, -1};
            }

            return ;
        }

        _positions[playerId] = new int[] {x1, y1};
        _map[x1][y1]         = (byte) playerId;
    }

    public int getX() {
        return _x;
    }

    public int getY() {
        return _y;
    }

    public int getPlayerCount() {
        return _defeated.length;
    }

    public int[] getPosition(int playerId) {
        return _positions[playerId];
    }

    public boolean isDefeated(int playerId) {
        return _defeated[playerId];
    }

    public boolean isCellEmpty(int x, int y) {
        return x >= 0 && y >= 0 & x < _x && y < _y && _map[x][y] == (byte) -1;
    }

    @Override
    protected GameState clone() {
        GameState result;

        try {
            result = (GameState) super.clone();
        }
        catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }

        result._defeated  = Arrays.copyOf(this._defeated, this._defeated.length);
        result._map       = Arrays.stream(result._map).map((bytes) -> bytes.clone()).toArray((value) -> new byte[value][]);
        result._positions = Arrays.stream(result._positions).map((ints) -> ints.clone()).toArray((value) -> new int[value][]);

        return result;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder(_x * _y + 150);

        result.append(super.toString());
        result.append("\n");

        for (int y = 0; y < _y; y++) {
            result.append(y % 10).append("|");

            for (int x = 0; x < _x; x++) {
                if (_map[x][y] == (byte) -1) {
                    result.append(" ");
                }
                else {
                    result.append(_map[x][y]);
                }
            }

            result.append("\n");
        }

        return result.toString();
    }
}

class GameRules {
    private static class Cell {
        private int _x, _y;

        Cell(int[] cell) {
            _x = cell[0];
            _y = cell[1];
        }

        Cell(Cell cell, Direction direction) {
            _x = cell._x + direction.getX();
            _y = cell._y + direction.getY();
        }

        public int getX() {
            return _x;
        }

        public int getY() {
            return _y;
        }

        @Override
        public boolean equals(Object o) {
            return this == o || !(o == null || getClass() != o.getClass()) && _x == ((Cell) o)._x && _y == ((Cell) o)._y;
        }

        @Override
        public int hashCode() {
            return Objects.hash(_x, _y);
        }
    }

    private static class FreeAdjacentCellSpliterator implements Spliterator<Cell> {
        private GameState   _state;
        private Queue<Cell> _queue   = new LinkedList<>();
        private Set<Cell>   _visited = new HashSet<>(256);

        public FreeAdjacentCellSpliterator(GameState state, Cell initial) {
            _state = state;

            // add initial cell to queue
            _addCellToQueue(initial);
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

            for (Direction direction : Direction.values()) {
                if (GameRules.isCellAvailable(_state, cell._x, cell._y, direction)) {
                    _addCellToQueue(new Cell(cell, direction));
                }
            }

            if (GameRules.isCellAvailable(_state, cell._x, cell._y)) {
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

    static public boolean isDefeated(GameState state, int playerId) {
        return state.isDefeated(playerId);
    }

    static public boolean isCellAvailable(GameState state, int x, int y) {
        return state.isCellEmpty(x, y);
    }

    static public boolean isCellAvailable(GameState state, int x, int y, Direction direction) {
        return state.isCellEmpty(x + direction.getX(), y + direction.getY());
    }

    static public boolean isCellAvailable(GameState state, int playerId, Direction direction) {
        return state.isCellEmpty(
            state.getPosition(playerId)[0] + direction.getX(),
            state.getPosition(playerId)[1] + direction.getY()
        );
    }

    static public Direction[] getPossibleDirections(GameState state, int playerId) {
        Direction[] result = new Direction[Direction.values().length];

        for (Direction direction : Direction.values()) {
            if (isCellAvailable(state, playerId, direction)) {
                result[direction.ordinal()] = direction;
            }
        }

        return result;
    }

    static public int getNormalizedCellCountAvailableSt(GameState state, int playerId) {
        if (isDefeated(state, playerId)) {
            return 0;
        }

        return (int) StreamSupport.stream(new FreeAdjacentCellSpliterator(state, new Cell(state.getPosition(playerId))), false).count();
    }

    static public void movePlayer(GameState state, int playerId, Direction direction) {
        if (isCellAvailable(state, playerId, direction)) {
            state.push(
                playerId,
                state.getPosition(playerId)[0] + direction.getX(),
                state.getPosition(playerId)[1] + direction.getY()
            );
        }
        else {
            state.push(
                playerId,
                -1,
                -1
            );
        }
    }
}

interface Evaluator {
    public int evaluate(GameState state, int playerId);
}

class PossibleMovementsEvaluator implements Evaluator {
    @Override
    public int evaluate(GameState state, int playerId) {
        return GameRules.getNormalizedCellCountAvailableSt(state, playerId) * 10;
    }
}

class AI {
    private int _playerId = 0;
    private GameState _state = null;

    public AI initialize(int playerCount, int playerId) {
        if (_state == null) {
            _state    = new GameState(playerCount, 30, 20);
            _playerId = playerId;
        }

        return this;
    }

    public void push(int playerId, int x0, int y0, int x1, int y1) {
        _state.push(playerId, x1, y1);
    }

    public String process() {
        DecisionTreeNode decisionTree = new DecisionTreeNode(null, null, -1, GameRules.getPossibleDirections(_state, _playerId));

        for (int i = 0; i < 10; i++) {
            GameState        prediction = _state.clone();
            DecisionTreeNode decision   = decisionTree;

            while (!decision.hasUnexploredPredictions()) {
                decision = decision.getRandomChildNode();
            }

            System.err.println(decision);
        }

        return "UP";
    }
}

class DecisionTreeNode {
    private DecisionTreeNode _parent;
    private Direction        _direction;
    private int              _playerId;
    private int              _score;

    private Map<Direction, DecisionTreeNode> _children = new HashMap<>(4);

    DecisionTreeNode(Direction direction, DecisionTreeNode parent, int playerId, Direction[] predictions) {
        _direction = direction;
        _parent    = parent;
        _playerId  = playerId;

        for (Direction prediction : predictions) {
            if (prediction == null) {
                continue;
            }

            _children.put(prediction, null);
        }
    }

    public Direction getUnexploredPrediction() {
        return _children.entrySet().stream().filter(v -> v.getValue() == null).map(Map.Entry::getKey).findFirst().orElse(null);
    }

    public boolean hasUnexploredPredictions() {
        return getUnexploredPrediction() != null;
    }

    public DecisionTreeNode getRandomChildNode() {
        return _children.entrySet().stream().sorted(Comparator.comparingInt(v -> new Random().nextInt())).findFirst().get().getValue();
    }
}