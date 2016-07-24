package me.selslack.codingame.zombies;

public enum HumanType {
    ASH("Ash", 1000),
    ZOMBIE("Zombie", 400),
    HUMAN("Human", 0);

    private final String type;
    private final int speed;

    HumanType(String type, int speed) {
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
