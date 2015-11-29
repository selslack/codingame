package me.selslack.codingame.zombies.mcts;

import me.selslack.codingame.zombies.Game;
import me.selslack.codingame.zombies.GameState;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class Solver {
    static final private Random rnd = new Random();

    final private Node root;
    final private GameState state;
    final private int timeLimit;

    public Solver(GameState state, int timeLimit) {
        this.root = new Node(null, state.getAsh().x, state.getAsh().y);
        this.state = state.clone();
        this.timeLimit = timeLimit;
    }

    public Waypoint run() {
        long startTime = System.currentTimeMillis();
        int plyLimit = 12;

        while (System.currentTimeMillis() - startTime < timeLimit) {
            GameState simulationState = state.clone();
            Node node = root;

            // Select
            while (node.expanded && !simulationState.isTerminal()) {
                node = node.children.stream()
                    .sorted(Comparator.comparingDouble(v -> -(v.score / (v.visits + Double.MIN_VALUE) + Math.sqrt(2 * Math.log(v.parent.visits) / (v.visits + Double.MIN_VALUE)))))
                    .findFirst()
                    .orElseGet(null);

                Game.process(simulationState, node.point.x, node.point.y, false);
            }

            // Expand
            if (!simulationState.isTerminal()) {
                for (Waypoint point : getPossibleMoves(simulationState)) {
                    node.children.add(new Node(node, point));
                }
            }

            node.expanded = true;

            // Playout
            for (int i = 0; i < plyLimit; i++) {
                if (simulationState.isTerminal()) {
                    break;
                }

                Waypoint action = getPossibleMoves(simulationState).stream()
                    .map(v -> {
                        v.marker = rnd.nextInt();
                        return v;
                    })
                    .sorted(Comparator.comparingInt(v -> v.marker))
                    .findFirst()
                    .orElse(null);

                Game.process(simulationState, action.x, action.y, false);
            }

            // Backpropagate
            while (node != null) {
                node.score = Math.max(simulationState.isLose() ? 0 : simulationState.score, node.score);
                node.visits++;

                node = node.parent;
            }
        }

        System.err.println(root.children);

        return root.children.stream().sorted(Comparator.comparingInt(v -> -v.score)).findFirst().orElse(null).point;
    }

    public List<Waypoint> getPossibleMoves(GameState state) {
        LinkedList<Waypoint> result = new LinkedList<>();

        result.addAll(new FlexibleWaypointGenerator(1000, 5).generate(state));
        result.addAll(new HumansWaypointGenerator().generate(state));
        result.addAll(new ZombiesWaypointGenerator().generate(state));

        return result;
    }

    static private class Node {
        public int score = 0;
        public int visits = 0;
        public boolean expanded = false;

        final public List<Node> children;
        final public Node parent;
        final public Waypoint point;

        private Node(Node parent, int x, int y) {
            this(parent, new Waypoint(x, y));
        }

        private Node(Node parent, Waypoint p) {
            this.children = new LinkedList<>();
            this.parent = parent;
            this.point = p;
        }

        @Override
        public String toString() {
            return "Node{score=" + score + ", visits=" + visits + ", expanded=" + expanded + ", point=" + point + "}";
        }
    }
}
