package org.example.lib.support;

import org.example.lib.Board;

/**
 * Create a class implementing this intefrace and instantiate it.
 * Pass the instance to the MCTS instance uding the
 * (@link #setHeuristicFunction(HeuristicFunction h) setHeuristicFunction) method.
 */

public interface HeuristicFunction {
    public double h(Board board);
}
