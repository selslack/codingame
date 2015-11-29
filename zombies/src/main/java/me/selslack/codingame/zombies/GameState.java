package me.selslack.codingame.zombies;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class GameState implements Cloneable {
    private Human ash;
    private List<Human> humans;
    private List<Human> zombies;

    public int score;

    public GameState() {
        this.ash = new Human(Human.Type.ASH, 0, 0, 0);
        this.humans = new LinkedList<>();
        this.zombies = new LinkedList<>();
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
        result.humans = this.humans.stream().filter(v -> v.isAlive).map(Human::clone).collect(Collectors.toCollection(LinkedList::new));
        result.zombies = this.zombies.stream().filter(v -> v.isAlive).map(Human::clone).collect(Collectors.toCollection(LinkedList::new));

        return result;
    }

    @Override
    public String toString() {
        return "GameState{" +
            "ash=" + ash +
            ", humans=" + humans +
            ", zombies=" + zombies +
            '}';
    }
}
