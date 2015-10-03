package me.selslack.codingame.backtothecode;

import java.util.function.IntFunction;
import java.util.function.IntPredicate;

public class RectangleUtils {
    final private GameField field;
    final private int[] position;

    public RectangleUtils(GameField field, final int[] position) {
        this.position = position;
        this.field = field.clone();
    }

    public int up(int width, int playerId, boolean ignoreSelf) {
        return histogramHeight(
            position[0],
            l -> ++l,
            l -> l < GameState.X && l <= position[0] + width,
            position[1],
            p -> --p,
            p -> p >= 0,
            (f, l, p) -> f.get(l, p),
            v -> v < 0 || (ignoreSelf && v == playerId)
        );
    }

    public int down(int width, int playerId, boolean ignoreSelf) {
        return histogramHeight(
            position[0],
            l -> ++l,
            l -> l < GameState.X && l <= position[0] + width,
            position[1],
            p -> ++p,
            p -> p < GameState.Y,
            (f, l, p) -> f.get(l, p),
            v -> v < 0 || (ignoreSelf && v == playerId)
        );
    }

    public int right(int height, int playerId, boolean ignoreSelf) {
        return histogramHeight(
            position[1],
            l -> ++l,
            l -> l < GameState.Y && l <= position[1] + height,
            position[0],
            p -> ++p,
            p -> p < GameState.X,
            (f, l, p) -> f.get(p, l),
            v -> v < 0 || (ignoreSelf && v == playerId)
        );
    }

    public int left(int height, int playerId, boolean ignoreSelf) {
        return histogramHeight(
            position[1],
            l -> ++l,
            l -> l < GameState.Y && l <= position[1] + height,
            position[0],
            p -> --p,
            p -> p >= 0,
            (f, l, p) -> f.get(p, l),
            v -> v < 0 || (ignoreSelf && v == playerId)
        );
    }

    private int histogramHeight(
        int line, IntFunction<Integer> nextLine, IntPredicate ifLine,
        int position, IntFunction<Integer> nextPos, IntPredicate ifPos,
        FieldValueFetcher getter,
        IntPredicate ifEmpty
    ) {
        int result = Integer.MAX_VALUE;

        for (int i = line; ifLine.test(i); i = nextLine.apply(i)) {
            int current = 0;
            boolean firstPosition = true;

            for (int p = position; ifPos.test(p); p = nextPos.apply(p)) {
                int value = getter.apply(field, i, p);

                if (!ifEmpty.test(value)) {
                    break;
                }
                else {
                    if (firstPosition) {
                        firstPosition = false;
                    }
                    else {
                        current++;
                    }
                }
            }

            if (result > current) {
                result = current;
            }
        }

        return result;
    }

    @FunctionalInterface
    interface FieldValueFetcher {
        int apply(GameField field, int line, int position);
    }
}