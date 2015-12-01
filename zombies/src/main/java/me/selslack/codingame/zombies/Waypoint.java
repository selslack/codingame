package me.selslack.codingame.zombies;

public class Waypoint {
    final public int x;
    final public int y;

    public Waypoint(final int x, final int y) {
        this.x = x;
        this.y = y;
    }

    public Waypoint(final Waypoint waypoint) {
        this(waypoint.x, waypoint.y);
    }

    public Waypoint(final Human human) {
        this(human.x, human.y);
    }

    @Override
    public String toString() {
        return "Waypoint{x=" + x + ", y=" + y + "}";
    }
}
