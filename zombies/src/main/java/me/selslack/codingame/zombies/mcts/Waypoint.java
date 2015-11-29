package me.selslack.codingame.zombies.mcts;

import me.selslack.codingame.zombies.Human;

import java.util.Objects;

public class Waypoint {
    final public int x;
    final public int y;

    public int marker = 0;

    public Waypoint(final int x, final int y) {
        this.x = x;
        this.y = y;
    }

    public Waypoint(final Human human) {
        this(human.x, human.y);
    }

    @Override
    public String toString() {
        return "Waypoint{x=" + x + ", y=" + y + ", marker=" + marker + "}";
    }
}
