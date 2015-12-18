package me.selslack.codingame.zombies

import spock.lang.Specification

class UtilsTest extends Specification {
    def "test projection fast correctness"() {
        expect:
        Utils.projectionFast(x1, y1, x2, y2, d).collect { it.round(9) } == Utils.projectionPrecise(x1, y1, x2, y2, d).collect { it.round(9) }

        where:
        x1   | y1   | x2   | y2   | d
        1000 | 1000 | 2000 | 1000 | 400
        1000 | 1000 | 2000 | 2000 | 400
        1000 | 1000 | 1000 | 2000 | 400
        1000 | 1000 | 0    | 2000 | 400
        1000 | 1000 | 0    | 1000 | 400
        1000 | 1000 | 0    | 0    | 400
        1000 | 1000 | 1000 | 0    | 400
        1000 | 1000 | 2000 | 0    | 400

        1000 | 1000 | 2000 | 1001 | 400
        1000 | 1000 | 1001 | 2000 | 400
        1000 | 1000 | 0    | 1001 | 400
        1000 | 1000 | 1001 | 0    | 400

        1000 | 1000 | 2000 | 999  | 400
        1000 | 1000 | 999  | 2000 | 400
        1000 | 1000 | 0    | 999  | 400
        1000 | 1000 | 999  | 0    | 400
    }
}
