package me.selslack.codingame.zombies

import spock.lang.*

class GameTest extends Specification {
    def "test movement"() {
        given:
        def zombie = new Human(Human.Type.ZOMBIE, 0, 5000, 1000)

        when:
        Game.humanMovement(zombie, 6000, 2000)

        then:
        zombie.x == 5282
        zombie.y == 1282
    }
}