package me.selslack.codingame.zombies;

import java.io.InputStream;
import java.util.Scanner;

public class CodinGameCommunicator implements Communicator {
    final private Scanner reader;

    public CodinGameCommunicator(InputStream stream) {
        this.reader = new Scanner(stream);
    }

    public CodinGameCommunicator(Scanner scanner) {
        this.reader = scanner;
    }

    @Override
    public void readState(GameState state, boolean toDevNull) {
        if (toDevNull) {
            // Ash
            reader.nextInt();
            reader.nextInt();

            // Humans
            for (int i = reader.nextInt(); i > 0; i--) {
                reader.nextInt();
                reader.nextInt();
                reader.nextInt();
            }

            // Zombies
            for (int i = reader.nextInt(); i > 0; i--) {
                reader.nextInt();
                reader.nextInt();
                reader.nextInt();
                reader.nextInt();
                reader.nextInt();
            }
        }
        else {
            // Setup
            state.getHumans().clear();
            state.getZombies().clear();

            // Ash
            state.getAsh().x = reader.nextInt();
            state.getAsh().y = reader.nextInt();

            // Humans
            for (int i = reader.nextInt(); i > 0; i--) {
                state.getHumans().add(
                    new Human(
                        Human.Type.HUMAN,
                        reader.nextInt(),
                        reader.nextInt(),
                        reader.nextInt()
                    )
                );
            }

            // Zombies
            for (int i = reader.nextInt(); i > 0; i--) {
                state.getHumans().add(
                    new Human(
                        Human.Type.ZOMBIE,
                        reader.nextInt(),
                        reader.nextInt(),
                        reader.nextInt()
                    )
                );

                reader.nextInt();
                reader.nextInt();
            }
        }
    }

    @Override
    public void sendCommand(Waypoint action) {
        System.out.println(action.x + " " + action.y);
    }
}
