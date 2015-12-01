package me.selslack.codingame.zombies.mcts;

import me.selslack.codingame.zombies.Game;
import me.selslack.codingame.zombies.GameState;
import me.selslack.codingame.zombies.Waypoint;

import java.util.*;

public class Solver {
    static final private Random rnd = new Random();

    private Node root;
    final private Config config;

    public int playouts = 0;

    public Solver(GameState state, Config config) {
        this.root = new Node(null, state.clone(), getPossibleMoves(state));
        this.config = config;
    }

    public Waypoint run() {
        long startTime = System.currentTimeMillis();

        playouts = 0;

        while (System.currentTimeMillis() - startTime < config.timeLimit) {
            Node node = root;

            // Select
            while (node.undiscovered.isEmpty() && !node.children.isEmpty()) {
                node = node.children.stream()
                    .sorted(Comparator.comparingDouble((Node v) -> v.score / v.visits + Math.sqrt(2 * Math.log(v.parent.visits) / v.visits)).reversed())
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException("There are no valid children"));
            }

            // Expand
            if (!node.undiscovered.isEmpty()) {
                GameState newState = Game.process(
                    node.state.clone(),
                    node.undiscovered.remove(0)
                );

                node = new Node(node, newState, getPossibleMoves(newState));
            }

            int playoutScore = node.state.isTerminal()
                ? node.score
                : playout(node.state.clone(), config.playoutDepth);

            // Backpropagate
            while (node != null) {
                node.score = Math.max(playoutScore, node.score);
                node.visits++;

                node = node.parent;
            }

            playouts++;
        }

        System.out.println(playouts);

        root = root.children.stream()
            .sorted(Comparator.comparingInt((Node v) -> v.score).thenComparingInt(v -> v.visits).reversed())
            .findFirst()
            .orElse(root);

        return new Waypoint(root.state.getAsh());
    }

    static public int playout(GameState state, int plyLimit) {
        for (int i = 0; i < plyLimit; i++) {
            if (state.isTerminal()) {
                break;
            }

            List<Waypoint> actions = getPossibleMoves(state);
            Waypoint action = actions.get(rnd.nextInt(actions.size()));

            Game.process(state, action);
        }

        return state.score;
    }

    static public List<Waypoint> getPossibleMoves(GameState state) {
        if (state.isTerminal()) {
            return Collections.EMPTY_LIST;
        }

        ArrayList<Waypoint> result = new ArrayList<>(64);

        result.addAll(new HumansWaypointGenerator().generate(state));
        result.addAll(new ZombiesWaypointGenerator().generate(state));

        return result;
    }

    static private class Node {
        public int score = 0;
        public int visits = 0;
        public Node parent;

        final public List<Waypoint> undiscovered;
        final public List<Node> children;
        final public GameState state;

        private Node(Node parent, GameState state, List<Waypoint> undiscovered) {
            this.parent = parent;
            this.state = state;
            this.undiscovered = undiscovered;
            this.children = new LinkedList<>();

            if (parent != null) {
                parent.children.add(this);
            }
        }

        @Override
        public String toString() {
            return "Node{score=" + score + ", visits=" + visits + ", point=" + new Waypoint(state.getAsh()) + "}";
        }
    }
}
