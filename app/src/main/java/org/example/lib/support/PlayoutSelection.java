package org.example.lib.support;

import org.example.lib.Board;

/**
 * Use this as a replacement for the conventional
 * playout function  during simulations. The idea
 * is to implement a function that takes a game
 * board, applies some random available move from
 * the current state and returns it.
 */
public interface PlayoutSelection {
    public void Process(Board board);
}
