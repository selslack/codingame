package me.selslack.codingame.zombies;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class GameState implements Cloneable {
    private Human ash;
    private List<Human> humans;
    private List<Human> zombies;

    public int tick;
    public int score;

    public GameState() {
        this.ash = new Human(HumanType.ASH, 0, 0, 0);
        this.humans = new LinkedList<>();
        this.zombies = new LinkedList<>();
        this.tick = 0;
        this.score = 0;
    }

    public Human getAsh() {
        return ash;
    }

    public List<Human> getHumans() {
        return humans;
    }

    public List<Human> getZombies() {
        return zombies;
    }

    public boolean isTerminal() {
        return isWin() || isLose();
    }

    public boolean isWin() {
        return zombies.isEmpty();
    }

    public boolean isLose() {
        return humans.isEmpty();
    }

    @Override
    public GameState clone() {
        GameState result;

        try {
            result = (GameState) super.clone();
        }
        catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }

        result.ash = this.ash.clone();
        result.humans = new LinkedList<>();
        result.zombies = new LinkedList<>();

        for (Human human : humans) {
            result.humans.add(human.clone());
        }

        for (Human zombie : zombies) {
            result.zombies.add(zombie.clone());
        }

        return result;
    }

    @Override
    public String toString() {
        return "GameState{score=" + score + ", terminal=" + isTerminal() + ", ash=" + ash + ", humans=" + humans + ", zombies=" + zombies + "}";
    }
}
