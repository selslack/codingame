package me.selslack.codingame.zombies.mcts;

import me.selslack.codingame.zombies.Game;
import me.selslack.codingame.zombies.GameState;
import me.selslack.codingame.zombies.Waypoint;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class Solver {
    private SolverNode root;

    final private Random random = new Random();
    final private SolverStatistics statistics;
    final private List<MoveGenerator> generators;

    public Solver(GameState initial) {
        this(initial, Collections.emptyList());
    }

    public Solver(GameState initial, List<MoveGenerator> generators) {
        this.root = new SolverNode(null, initial.clone(), getAvailableMoves(initial, Strategy.RANDOM));
        this.statistics = new SolverStatistics();
        this.generators = generators;
    }

    public Waypoint run() {
        statistics.reset();

        while (statistics.getRunningTime(TimeUnit.MILLISECONDS) < 95) {
            SolverNode node = root;

            // Select
            while (node.undiscovered.isEmpty() && !node.children.isEmpty()) {
                node = node.children.stream()
                    .sorted(Comparator.comparingDouble((SolverNode v) -> Solver.nodeValue(v)).reversed())
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

                node = new SolverNode(node, newState, getAvailableMoves(newState, Strategy.RANDOM));
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
            .sorted(Comparator.comparingInt((SolverNode v) -> -v.score))
            .findFirst()
            .orElse(root);

        root.parent = null;

        return new Waypoint(root.ash);
    }

    static public double nodeValue(SolverNode node) {
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
}
