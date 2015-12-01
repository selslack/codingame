package me.selslack.codingame.zombies.mcts;

import me.selslack.codingame.zombies.Game;
import me.selslack.codingame.zombies.GameState;

import java.util.*;

public class Solver {
    static final private Random rnd = new Random();

    static final WaypointGenerator flexy = new FlexibleWaypointGenerator(1000, 5);

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

                Game.process(simulationState, node.point.x, node.point.y);
            }

            // Expand
            if (!simulationState.isTerminal()) {
                for (Waypoint point : getPossibleMoves(simulationState)) {
                    node.children.add(new Node(node, point));
                }
            }

            node.expanded = true;

            // Playout
            playout(simulationState, plyLimit);

            // Backpropagate
            while (node != null) {
                node.score = Math.max(simulationState.isLose() ? 0 : simulationState.score, node.score);
                node.visits++;

                node = node.parent;
            }
        }

        return root.children.stream().sorted(Comparator.comparingInt(v -> -v.score)).findFirst().orElse(null).point;
    }

    static public void playout(GameState state, int plyLimit) {
        for (int i = 0; i < plyLimit; i++) {
            if (state.isTerminal()) {
                break;
            }

            List<Waypoint> actions = getPossibleMoves(state);
            Waypoint action = actions.get(rnd.nextInt(actions.size()));

            Game.process(state, action.x, action.y);
        }
    }

    static public List<Waypoint> getPossibleMoves(GameState state) {
        ArrayList<Waypoint> result = new ArrayList<>(64);

        result.addAll(flexy.generate(state));
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
