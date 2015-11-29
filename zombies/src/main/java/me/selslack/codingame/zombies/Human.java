package me.selslack.codingame.zombies;

public class Human implements Cloneable {
    final public Type type;
    final public int id;

    public int x;
    public int y;
    public boolean isAlive;

    public Human(Type type, int id, int x, int y) {
        this.type = type;
        this.id = id;
        this.x = x;
        this.y = y;
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

    public enum Type {
        ASH("Ash", 1000),
        ZOMBIE("Zombie", 400),
        HUMAN("Human", 0);

        private final String type;
        private final int speed;

        Type(String type, int speed) {
            this.type = type;
            this.speed = speed;
        }

        public int getSpeed() {
            return speed;
        }

        @Override
        public String toString() {
            return "HumanType{type='" + type + "', speed=" + speed + "}";
        }
    }
}
