package me.selslack.codingame.zombies;

public class Utils {
    static public int distance(int x1, int y1, int x2, int y2) {
        return (int) Math.floor(
            Math.sqrt(Math.pow(x1 - x2, 2.0) + Math.pow(y1 - y2, 2.0))
        );
    }

    static public int distance(Human h1, Human h2) {
        return distance(h1.x, h1.y, h2.x, h2.y);
    }
}
