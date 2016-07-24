package me.selslack.codingame.zombies;

import java.util.function.Supplier;

public class Human implements Cloneable {
    final public HumanType type;
    final public int id;

    public int x;
    public int y;
    public boolean isAlive;
    public Supplier<Human> future;

    public Human(HumanType type, int id, int x, int y) {
        this.type = type;
        this.id = id;
        this.x = x;
        this.y = y;
        this.future = () -> new Human(type, id, x, y);
        this.isAlive = true;
    }

    @Override
    public Human clone() {
        try {
            return (Human) super.clone();
        }
        catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String toString() {
        return "Human{type=" + type + ", id=" + id + ", x=" + x + ", y=" + y + ", isAlive=" + isAlive + "}";
    }
}
