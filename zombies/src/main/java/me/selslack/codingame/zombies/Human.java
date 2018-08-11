package me.selslack.codingame.zombies;

public class Human implements Cloneable {
    final public HumanType type;
    final public int id;

    public int x;
    public int y;

    public Human(HumanType type, int id, int x, int y) {
        this.type = type;
        this.id = id;
        this.x = x;
        this.y = y;
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
        return "Human{type=" + type + ", id=" + id + ", x=" + x + ", y=" + y + "}";
    }
}
