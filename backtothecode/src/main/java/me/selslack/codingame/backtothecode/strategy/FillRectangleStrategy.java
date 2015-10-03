package me.selslack.codingame.backtothecode.strategy;

import me.selslack.codingame.backtothecode.GameField;
import me.selslack.codingame.backtothecode.GameState;
import me.selslack.codingame.backtothecode.RectangleUtils;

import java.util.Arrays;
import java.util.Deque;
import java.util.LinkedList;

public class FillRectangleStrategy {
    static final private int MIN_SIZE = 3;
    static final private int MAX_SIZE = 8;

    static public Deque<Deque<int[]>> doIt(GameField field, int playerId, int[] position) {
        RectangleUtils utils = new RectangleUtils(field, position);
        LinkedList<Deque<int[]>> result = new LinkedList<>();

        for (int w = MIN_SIZE; w <= MAX_SIZE && position[0] + w < GameState.X; w++) {
            int height = utils.up(w, playerId, false);

            if (height < MIN_SIZE) {
                break;
            }

            result.add(rectangle(position, new int[]{position[0] + w, position[1] - height}, true));
            result.add(rectangle(position, new int[]{position[0] + w, position[1] - height}, false));
        }

        for (int w = MIN_SIZE; w <= MAX_SIZE && position[0] + w < GameState.X; w++) {
            int height = utils.down(w, playerId, false);

            if (height < MIN_SIZE) {
                break;
            }

            result.add(rectangle(position, new int[]{position[0] + w, position[1] + height}, true));
            result.add(rectangle(position, new int[]{position[0] + w, position[1] + height}, false));
        }

        for (int h = MIN_SIZE; h <= MAX_SIZE && position[1] + h < GameState.Y; h++) {
            int width = utils.left(h, playerId, false);

            if (width < MIN_SIZE) {
                break;
            }

            result.add(rectangle(position, new int[]{position[0] - width, position[1] + h}, true));
            result.add(rectangle(position, new int[]{position[0] - width, position[1] + h}, false));
        }

        for (int h = MIN_SIZE; h <= MAX_SIZE && position[1] + h < GameState.Y; h++) {
            int width = utils.right(h, playerId, false);

            if (width < MIN_SIZE) {
                break;
            }

            result.add(rectangle(position, new int[]{position[0] + width, position[1] + h}, true));
            result.add(rectangle(position, new int[]{position[0] + width, position[1] + h}, false));
        }

        for (int w = MIN_SIZE; w <= MAX_SIZE && position[0] + w < GameState.X; w++) {
            int height = utils.up(w, playerId, true);

            if (height < MIN_SIZE) {
                break;
            }

            result.add(rectangle(position, new int[]{position[0] + w, position[1] - height}, true));
            result.add(rectangle(position, new int[]{position[0] + w, position[1] - height}, false));
        }

        for (int w = MIN_SIZE; w <= MAX_SIZE && position[0] + w < GameState.X; w++) {
            int height = utils.down(w, playerId, true);

            if (height < MIN_SIZE) {
                break;
            }

            result.add(rectangle(position, new int[]{position[0] + w, position[1] + height}, true));
            result.add(rectangle(position, new int[]{position[0] + w, position[1] + height}, false));
        }

        for (int h = MIN_SIZE; h <= MAX_SIZE && position[1] + h < GameState.Y; h++) {
            int width = utils.left(h, playerId, true);

            if (width < MIN_SIZE) {
                break;
            }

            result.add(rectangle(position, new int[]{position[0] - width, position[1] + h}, true));
            result.add(rectangle(position, new int[]{position[0] - width, position[1] + h}, false));
        }

        for (int h = MIN_SIZE; h <= MAX_SIZE && position[1] + h < GameState.Y; h++) {
            int width = utils.right(h, playerId, true);

            if (width < MIN_SIZE) {
                break;
            }

            result.add(rectangle(position, new int[]{position[0] + width, position[1] + h}, true));
            result.add(rectangle(position, new int[]{position[0] + width, position[1] + h}, false));
        }

        return result;
    }

    static private Deque<int[]> rectangle(int[] start, int[] end, boolean alternativePath) {
        LinkedList<int[]> result = new LinkedList<>();

        result.addAll(moveTo(start, end, alternativePath));
        result.pollLast();
        result.addAll(moveTo(end, start, alternativePath));
        result.pollLast();

        return result;
    }

    static private Deque<int[]> moveTo(int[] start, int[] end, boolean alternativePath) {
        LinkedList<int[]> result = new LinkedList<>();

        result.addFirst(start.clone());

        if (alternativePath) {
            if (start[0] < end[0]) {
                for (int i = start[0] + 1; i <= end[0]; i++) {
                    result.addLast(new int[] { i, start[1] });
                }
            }
            else if (start[0] > end[0]) {
                for (int i = start[0] - 1; i >= end[0]; i--) {
                    result.addLast(new int[] { i, start[1] });
                }
            }

            if (start[1] < end[1]) {
                for (int i = start[1] + 1; i <= end[1]; i++) {
                    result.addLast(new int[] { end[0], i });
                }
            }
            else if (start[1] > end[1]) {
                for (int i = start[1] - 1; i >= end[1]; i--) {
                    result.addLast(new int[] { end[0], i });
                }
            }
        }
        else {
            if (start[1] < end[1]) {
                for (int i = start[1] + 1; i <= end[1]; i++) {
                    result.addLast(new int[] { start[0], i });
                }
            }
            else if (start[1] > end[1]) {
                for (int i = start[1] - 1; i >= end[1]; i--) {
                    result.addLast(new int[] { start[0], i });
                }
            }

            if (start[0] < end[0]) {
                for (int i = start[0] + 1; i <= end[0]; i++) {
                    result.addLast(new int[] { i, end[1] });
                }
            }
            else if (start[0] > end[0]) {
                for (int i = start[0] - 1; i >= end[0]; i--) {
                    result.addLast(new int[] { i, end[1] });
                }
            }
        }

        return result;
    }
}
