package me.selslack.codingame.zombies.mcts;

import me.selslack.codingame.zombies.Game;
import me.selslack.codingame.zombies.GameState;
import me.selslack.codingame.zombies.Human;
import me.selslack.codingame.zombies.Waypoint;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class Solver {
    private Node root;

    final private Random random = new Random();
    final private SolverStatistics statistics;
    final private List<MoveGenerator> generators;

    public Solver(GameState initial) {
        this(initial, Collections.emptyList());
    }

    public Solver(GameState initial, List<MoveGenerator> generators) {
        this.root = new Node(null, initial.clone(), getAvailableMoves(initial, Strategy.RANDOM));
        this.statistics = new SolverStatistics();
        this.generators = generators;
    }

    public Waypoint run() {
        statistics.reset();

        while (statistics.getRunningTime(TimeUnit.MILLISECONDS) < 95) {
            Node node = root;

            // Select
            while (node.undiscovered.isEmpty() && !node.children.isEmpty()) {
                node = node.children.stream()
                    .sorted(Comparator.comparingDouble((Node v) -> Solver.nodeValue(v)).reversed())
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException("There are no valid children"));
            }

            // Expand
            if (!node.undiscovered.isEmpty()) {
                GameState newState = Game.process(
                    node.state.clone(),
                    node.undiscovered.remove(0)
                );

                if (node.undiscovered.isEmpty()) {
                    node.undiscovered = Collections.emptyList();
                    node.state = null;
                }

                node = new Node(node, newState, getAvailableMoves(newState, Strategy.RANDOM));
            }

            int playoutScore = node.state.isTerminal()
                ? node.state.score
                : playout(node.state.clone());

            // Backpropagate
            while (node != null) {
                node.score = Math.max(playoutScore, node.score);
                node.visits++;

                node = node.parent;
            }

            statistics.done();
        }

        root = root.children.stream()
            .sorted(Comparator.comparingInt((Node v) -> -v.score))
            .findFirst()
            .orElse(root);

        root.parent = null;

        return new Waypoint(root.ash);
    }

    static public double nodeValue(Node node) {
        double uctBias = 0.7 * Math.sqrt(Math.log(node.parent.visits) / node.visits);

        return uctBias;
    }

    public List<Waypoint> getAvailableMoves(GameState state, Strategy strategy) {
        List<Strategy> strategies = Arrays.asList(strategy.getNext());
        List<Waypoint> result = new LinkedList<>();

        if (state.isTerminal()) {
            return Collections.emptyList();
        }

        generators
            .stream()
            .filter(v -> strategies.contains(v.getStrategy()))
            .flatMap(v -> v.generate(state).stream())
            .filter(v -> v.x >= 0 && v.x < 16000 && v.y >= 0 && v.y < 9000)
            .forEach(result::add);

        if (result.isEmpty()) {
            return Collections.emptyList();
        }

        return result;
    }

    public Optional<Waypoint> getRandomMove(GameState state, Strategy strategy) {
        List<Waypoint> moves = getAvailableMoves(state, strategy);
        int count = moves.size();

        if (count < 1) {
            return Optional.empty();
        }
        else {
            return Optional.of(
                moves.get(random.nextInt(count))
            );
        }
    }

    public int playout(GameState state) {
        GameState start = state.clone();

        for (int i = 0; !start.isTerminal(); i++) {
            Game.process(start, getRandomMove(start, i > 2 ? Strategy.EXTINGUISH : Strategy.RANDOM).get());
        }

        return start.score;
    }

    public SolverStatistics getStatistics() {
        return statistics;
    }

    static public class Node {
        public int score = 0;
        public int visits = 0;
        public Node parent;
        public GameState state;
        public List<Waypoint> undiscovered;

        final public List<Node> children;
        final public Human ash;

        protected Node(Node parent, GameState state, List<Waypoint> undiscovered) {
            this.parent = parent;
            this.state = state;
            this.undiscovered = undiscovered;

            this.children = new LinkedList<>();
            this.ash = state.getAsh();

            if (parent != null) {
                parent.children.add(this);
            }
        }

        @Override
        public String toString() {
            return "Node{score=" + score + ", visits=" + visits + ", point=" + new Waypoint(ash) + "}";
        }
    }

    static public class SolverStatistics {
        Optional<LongSummaryStatistics> statistics = Optional.empty();
        long startTime = 0L;
        long prevInvocationTime = 0L;

        protected void reset() {
            statistics = Optional.of(new LongSummaryStatistics());
            startTime = prevInvocationTime = System.nanoTime();
        }

        protected void done() {
            statistics.get().accept(
                System.nanoTime() - prevInvocationTime
            );

            prevInvocationTime = System.nanoTime();
        }

        protected long getRunningTime(TimeUnit timeUnit) {
            return timeUnit.convert(System.nanoTime() - startTime, TimeUnit.NANOSECONDS);
        }

        @Override
        public String toString() {
            if (statistics.isPresent()) {
                return "SolverStatistics{count=" + statistics.get().getCount() + ", average=" + String.format("%.5g", statistics.get().getAverage() / 1E6d) + "ms}";
            }
            else {
                return "SolverStatistics{NO DATA}";
            }
        }
    }
}
