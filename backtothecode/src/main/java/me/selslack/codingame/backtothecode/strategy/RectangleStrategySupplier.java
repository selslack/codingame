package me.selslack.codingame.backtothecode.strategy;

import me.selslack.codingame.backtothecode.GameState;
import me.selslack.codingame.backtothecode.MovementUtils;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class RectangleStrategySupplier implements StrategySupplier {
    static int[][][] staticRectangles = new int[][][] {
        new int[][] { new int[] { 0,  0  }, new int[] { 12, 10 } },
        new int[][] { new int[] { 0,  10 }, new int[] { 12, 19 } },
        new int[][] { new int[] { 12, 0  }, new int[] { 23, 10 } },
        new int[][] { new int[] { 12, 10 }, new int[] { 23, 19 } },
        new int[][] { new int[] { 23, 0  }, new int[] { 34, 10 } },
        new int[][] { new int[] { 23, 10 }, new int[] { 34, 19 } },
    };

    @Override
    public List<AbstractStrategy> get(GameState state, int playerId) {
        List<AbstractStrategy> result = new LinkedList<>();

        for (int[][] staticRectangle : staticRectangles) {
            int closestDistance = Integer.MAX_VALUE;
            int[] closestPoint = null;
            LinkedList<int[]> rectangle = MovementUtils.drawRectangle(staticRectangle[0], staticRectangle[1], false);
            LinkedList<int[]> sequence;

            for (int[] point : rectangle) {
                int currentDistance = state.field.manhattan(state.getPlayer(playerId).getPosition(), point);

                if (currentDistance < closestDistance) {
                    closestDistance = currentDistance;
                    closestPoint = point;
                }
            }

            if (closestPoint == null) {
                throw new RuntimeException("WTF, no closest point found");
            }

            while (!Arrays.equals(closestPoint, rectangle.peekFirst())) {
                rectangle.addLast(rectangle.pollFirst());
            }

            sequence = MovementUtils.moveToPoint(state.getPlayer(playerId).getPosition(), closestPoint, false, false);
            sequence.pollLast();
            sequence.addAll(rectangle);

            result.add(new MovementSequenceStrategy(state, playerId, sequence));
        }

        return result;
    }
}
