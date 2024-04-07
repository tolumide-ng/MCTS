package org.example.lib;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Arrays;

public class Node implements Comparable<Node> {
    public double[] score;
    public double games;
    public Move move;
    public ArrayList<Node> unvisitedChildren;
    public ArrayList<Node> children;
    public Node parent;
    public int player;
    public double[] pess; //pessimism
    public double[] opti; // optimism
    public boolean pruned;

    @Override
    public String toString() {
        String value = "\tscore = " + Arrays.toString(score) +
                "\tgames = " + games +
                "\tchildren = " + children.size() +
                "\tplayer = " + player +
                "\tpess = " + Arrays.toString(pess) +
                "\topti = " + Arrays.toString(opti) +
                "\tpruned = " + pruned + "\n\n";
        return value;
    }

    /**
     * This is a special Node constructor that merges multiple
     * root nodes into a single main node.
     * @param rootNodes
     */
    public Node(ArrayList<Node> rootNodes) {
        LinkedList<Node> childnodes = new LinkedList<>();

        for (Node n : rootNodes) {
            for (Node child : n.children) {
                childnodes.add(child);
            }
        }

        Collections.sort(childnodes);
        children = new ArrayList<Node>();

        while (!childnodes.isEmpty()) {
            LinkedList<Node> tnodes = new LinkedList<>();
            Node currNode = childnodes.get(0);
            childnodes.remove(0);

            while (!childnodes.isEmpty() && childnodes.get(0).compareTo(currNode) == 0) {
                tnodes.add(childnodes.get(0));
                childnodes.remove(0);
            }

            children.add(new Node(tnodes));
        }
    }

    /**
     * This is a Node constructor that constructs a
     * new node by combining the stats for all nodes
     * passed into it
     * @param nodes
     */
    private Node(LinkedList<Node> nodes) {
        move = nodes.get(0).move;
        score = new double[nodes.get(0).score.length];
        for (Node n : nodes) {
            games += n.games;
            for (int i = 0; i < score.length; i++) {
                score[i] += n.score[i];
            }
        }
    }


    /**
     * This create a root node
     * 
     * @param b
     */
    public Node(Board b) {
        children = new ArrayList<>();
        player = b.getCurrentPlayer();
        score = new double[b.getQuantityOfPlayers()];
        pess = new double[b.getQuantityOfPlayers()];
        opti = new double[b.getQuantityOfPlayers()];
        for (int i = 0; i < b.getQuantityOfPlayers(); i++) {
            opti[i] = 1;
        }
    }

    /**
     * This creates non-root nodes
     * 
     * @param b
     * @param m
     * @param prnt
     */
    public Node(Board b, Move m, Node prnt) {
        children = new ArrayList<>();
        parent = prnt;
        move = m;
        Board tempBoard = b.duplicate();
        tempBoard.makeMove(m);
        player = tempBoard.getCurrentPlayer();
        score = new double[b.getQuantityOfPlayers()];
        pess = new double[b.getQuantityOfPlayers()];
        opti = new double[b.getQuantityOfPlayers()];
        for (int i = 0; i < b.getQuantityOfPlayers(); i++) {
            opti[i] = 1;
        }
    }


    /**
     * Return the upper confidence bound of this state
     * 
     * @param c
     *  typically sqrt(2). Increase to emphasize exploration. 
     *                     Decrease to increase exploitation.
     * @param t
     * @return
     */
    public double upperConfidenceBound(double c) {
        return score[parent.player] / games + c * Math.sqrt(Math.log(parent.games + 1) / games);
    }

    /**
     * Update the tree with the new score.
     * 
     * @param scr
     */
    public void backPropagateScore(double[] scr) {
        // System.out.println("|||");
        this.games++;
        for (int i = 0; i < scr.length; i++) {
            // this.score[i] += scr[i];
            this.score[i] += scr[i];
        }

        if (parent != null) {
            parent.backPropagateScore(scr);
        }
    }


    /**
     * Expand this node by populating its list of unvisited child nodes
     * 
     * @param currentBoard
     */
    public void expandNode(Board currentBoard) {
        ArrayList<Move> legalMoves = currentBoard.getMoves(CallLocation.treePolicy);
        unvisitedChildren = new ArrayList<>();
        for (int i = 0; i < legalMoves.size(); i++) {
            Node tempState = new Node(currentBoard, legalMoves.get(i), this);
            unvisitedChildren.add(tempState);
        }
    }

    /**
     * Set the boundaries in the given node and propagate the values back up the tree
     * 
     * @param optimistic
     * @param pessimistic
     * (what is this?????)
     */
    public void backPropagateBounds(double[] score) {
        for (int i = 0; i < score.length; i++) {
            opti[i] = score[i];
            pess[i] = score[i];
        }

        if (parent != null) {
            parent.backPropagateBoundsHelper();
        }
    }

    private void backPropagateBoundsHelper() {
        for (int i = 0; i < opti.length; i++) {
            if (player != -1) {
                if (i == player) {
                    opti[i] = Integer.MIN_VALUE;
                    pess[i] = Integer.MIN_VALUE;
                } else {
                    opti[i] = Integer.MAX_VALUE;
                    opti[i] = Integer.MAX_VALUE;
                }
            } else {
                // this is a random/environment mode
                opti[i] = Integer.MIN_VALUE;
                pess[i] = Integer.MAX_VALUE;
            }
        }

        for (int i = 0; i < opti.length; i++) {
            for (Node c : children) {
                if (player != -1) {
                    if (i == player) {
                        if (opti[i] < c.opti[i]) {
                            opti[i] = c.opti[i];
                        }
                        if (pess[i] < c.pess[i]) {
                            pess[i] = c.pess[i];
                        }
                    } else {
                        if (opti[i] > c.opti[i]) {
                            opti[i] = c.opti[i];
                        }
                        if (pess[i] > c.pess[i]) {
                            pess[i] = c.pess[i];
                        }
                    }
                } else {
                    // this is a random/environment node
                    if (opti[i] < c.opti[i]) {
                        opti[i] = c.opti[i];
                    }
                    if (pess[i] > c.pess[i]) {
                        pess[i] = c.pess[i];
                    }
                }
            }
        }

        // this compares against a dummy node with bounds 1 0
        // if not all children have been explored
        if (!unvisitedChildren.isEmpty()) {
            for (int i = 0; i < opti.length; i++) {
                if (i == player) {
                    opti[i] = 1;
                } else {
                    pess[i] = 0;
                }
            }
        }

        pruneBranches();
        if (parent != null) {
            parent.backPropagateBoundsHelper();
        }
    }
    
    private void pruneBranches() {
        for (Node s : children) {
            if (pess[player] > s.opti[player]) {
                s.pruned = true;
            }
        }
    }


    /**
     * Select a child node at random and return it
     * 
     * @param board
     * @return
     */
    public int randomSelect(Board board) {
        double[] weights = board.getMoveWeights();

        double totalWeight = 0.0d;
        for (int i = 0; i < weights.length; i++) {
            totalWeight += weights[i];
        }

        int randomIndex = -1;
        double random = Math.random() * totalWeight;
        for (int i = 0; i < weights.length; ++i) {
            random -= weights[i];
            if (random <= 0.0d) {
                randomIndex = i;
                break;
            }
        }
        return randomIndex;
    }

    @Override
    public int compareTo(Node o) {
        return move.compareTo(o.move);
    }
}
