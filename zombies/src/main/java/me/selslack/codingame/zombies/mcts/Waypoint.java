package me.selslack.codingame.zombies.mcts;

import me.selslack.codingame.zombies.Human;

import java.util.Objects;

public class Waypoint {
    final public int x;
    final public int y;

    protected int marker = 0;

    protected Waypoint(final int x, final int y) {
        this.x = x;
        this.y = y;
    }

    protected Waypoint(final Human human) {
        this(human.x, human.y);
    }

    @Override
    public String toString() {
        return "Waypoint{x=" + x + ", y=" + y + "}";
    }
}
