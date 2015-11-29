package me.selslack.codingame.zombies.server;

import me.selslack.codingame.zombies.GameState;
import me.selslack.codingame.zombies.Human;

public class GameStateBuilder {
    public GameState state = new GameState();

    public GameStateBuilder(int x, int y) {
        state.getAsh().x = x;
        state.getAsh().y = y;
    }

    public GameStateBuilder addHuman(int id, int x, int y) {
        state.getHumans()
            .add(id, new Human(Human.Type.HUMAN, id, x, y));

        return this;
    }

    public GameStateBuilder addZombie(int id, int x, int y) {
        state.getZombies()
            .add(id, new Human(Human.Type.ZOMBIE, id, x, y));

        return this;
    }
}
