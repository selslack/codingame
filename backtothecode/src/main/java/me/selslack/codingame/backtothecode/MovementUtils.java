package me.selslack.codingame.backtothecode;

import java.util.LinkedList;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class MovementUtils {
    static public LinkedList<int[]> moveToPoint(int[] start, int[] end, boolean includeFirst, boolean alternativePath) {
        LinkedList<int[]> result = new LinkedList<>();

        if (includeFirst) {
            result.addFirst(start.clone());
        }

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

    static public LinkedList<int[]> drawRectangle(int[] start, int[] end, boolean alternativePath) {
        LinkedList<int[]> result = new LinkedList<>();

        result.addAll(moveToPoint(start, end, true, alternativePath));
        result.pollLast();
        result.addAll(moveToPoint(end, start, true, alternativePath));
        result.pollLast();

        return result;
    }
}