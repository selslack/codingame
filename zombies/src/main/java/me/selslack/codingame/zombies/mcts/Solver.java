package me.selslack.codingame.zombies.mcts;

import me.selslack.codingame.zombies.Game;
import me.selslack.codingame.zombies.GameState;
import me.selslack.codingame.zombies.Human;
import me.selslack.codingame.zombies.Waypoint;

import java.util.*;

public class Solver {
    static final private Random rnd = new Random();

    private Node root;
    final private Config config;
    final private List<WaypointGenerator> generators;

    public Solver(GameState state, Config config) {
        this.generators = new LinkedList<>();
        this.generators.add(new FlexibleWaypointGenerator(config.firstFlexCircleDistance, config.firstFlexCircleCount));
//        this.generators.add(new FlexibleWaypointGenerator(config.secondFlexCircleDistance, config.secondFlexCircleCount));
//        this.generators.add(new FlexibleWaypointGenerator(150, 3));
//        this.generators.add(new HumansWaypointGenerator());
//        this.generators.add(new ZombiesWaypointGenerator());

        this.root = new Node(null, state.clone(), getPossibleMoves(state, generators));
        this.config = config;

    }

    public Waypoint run() {
        long startTime = System.currentTimeMillis();

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

                if (node.undiscovered.isEmpty()) {
                    node.undiscovered = Collections.EMPTY_LIST;

                    if (node.parent != null && node.parent.parent != null) {
                        node.state = null;
                    }
                }

                node = new Node(node, newState, getPossibleMoves(newState, generators));
            }

            int playoutScore = node.state.isTerminal()
                ? node.score
                : playout(node.state.clone(), generators, config.playoutDepth);

            // Backpropagate
            while (node != null) {
                node.score = Math.max(playoutScore, node.score);
                node.visits++;

                node = node.parent;
            }
        }

        root = root.children.stream()
            .sorted(Comparator.comparingInt((Node v) -> -v.score))
            .findFirst()
            .orElse(root);

        root.parent = null;

        return new Waypoint(root.ash);
    }

    static public int playout(GameState state, List<WaypointGenerator> generators, int plyLimit) {
        for (int i = 0; i < plyLimit; i++) {
            if (state.isTerminal()) {
                break;
            }

            List<Waypoint> actions = getPossibleMoves(state, generators);
            Waypoint action = actions.get(rnd.nextInt(actions.size()));

            Game.process(state, action);
        }

        return state.score;
    }

    static public List<Waypoint> getPossibleMoves(GameState state, List<WaypointGenerator> generators) {
        if (state.isTerminal() || generators.isEmpty()) {
            return Collections.EMPTY_LIST;
        }

        ArrayList<Waypoint> result = new ArrayList<>(32);

        for (WaypointGenerator generator : generators) {
            result.addAll(generator.generate(state));
        }

        return result;
    }

    static private class Node {
        public int score = 0;
        public int visits = 0;
        public Node parent;
        public GameState state;
        public List<Waypoint> undiscovered;

        final public List<Node> children;
        final public Human ash;

        private Node(Node parent, GameState state, List<Waypoint> undiscovered) {
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
}
