package me.selslack.codingame.zombies.mcts;

enum Strategy {
    RANDOM {
        @Override
        public Strategy[] getNext() {
            return new Strategy[] { RANDOM, EXTINGUISH };
        }
    },

    EXTINGUISH {
        @Override
        public Strategy[] getNext() {
            return new Strategy[] { EXTINGUISH };
        }
    };

    abstract public Strategy[] getNext();
}
