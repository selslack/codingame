package me.selslack.codingame.zombies.server

import spock.lang.*

class GameStateBuilderTest extends Specification {
    @Unroll
    def "test build state #id"() {
        given:
        def state = GameStateBuilder.build(id)

        expect:
        state.getAsh().x == x
        state.getAsh().y == y
        state.getHumans().size() == humans
        state.getZombies().size() == zombies

        where:
        id          | x    | y    | humans | zombies
        "01-simple" | 0    | 0    | 1      | 1
        "05-3_vs_3" | 7500 | 2000 | 2      | 3
    }

    def "test build invalid id"() {
        when:
        GameStateBuilder.build("invalid")

        then:
        thrown(RuntimeException)
    }
}
