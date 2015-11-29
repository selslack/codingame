package me.selslack.codingame.zombies;

import java.util.Scanner;

public class InputReader {
    static public void readPlayerData(GameState state, Scanner in, boolean ignoreServerData) {
        if (ignoreServerData) {
            in.nextInt();
            in.nextInt();
        }
        else {
            state.getAsh().x = in.nextInt();
            state.getAsh().y = in.nextInt();
        }
    }

    static public void readHumansData(GameState state, Scanner in, boolean ignoreServerData) {
        if (ignoreServerData) {
            for (int i = in.nextInt(); i > 0; i--) {
                in.nextInt();
                in.nextInt();
                in.nextInt();
            }
        }
        else {
            state.getHumans().clear();

            for (int i = in.nextInt(); i > 0; i--) {
                state.getHumans().add(new Human(Human.Type.HUMAN, in.nextInt(), in.nextInt(), in.nextInt()));
            }
        }
    }

    static public void readZombieData(GameState state, Scanner in, boolean ignoreServerData) {
        if (ignoreServerData) {
            for (int i = in.nextInt(); i > 0; i--) {
                in.nextInt();
                in.nextInt();
                in.nextInt();
                in.nextInt();
                in.nextInt();
            }
        }
        else {
            state.getZombies().clear();

            for (int i = in.nextInt(); i > 0; i--) {
                state.getZombies().add(new Human(Human.Type.ZOMBIE, in.nextInt(), in.nextInt(), in.nextInt()));

                // useless
                in.nextInt();
                in.nextInt();
            }
        }
    }
}
