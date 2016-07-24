package me.selslack.codingame.zombies.mcts;

import me.selslack.codingame.zombies.GameState;
import me.selslack.codingame.zombies.Human;
import me.selslack.codingame.zombies.Waypoint;

import java.util.LinkedList;
import java.util.List;

public class SolverNode {
    public int score = 0;
    public int visits = 0;
    public SolverNode parent;
    public GameState state;
    public List<Waypoint> undiscovered;

    final public List<SolverNode> children;
    final public Human ash;

    protected SolverNode(SolverNode parent, GameState state, List<Waypoint> undiscovered) {
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
