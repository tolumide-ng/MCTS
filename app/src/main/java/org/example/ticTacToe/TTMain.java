package org.example.ticTacToe;

import java.util.Arrays;

import org.example.lib.FinalSelectionPolicy;
import org.example.lib.MCTS;
import org.example.lib.Move;

public class TTMain {
    public static void main(String[] args) {
        MCTS mcts = new MCTS();
        mcts.setExplorationConstant(0.2);
        mcts.setTimeDisplay(true);
        mcts.enableRootParallelisation(9);
        Move move;
        mcts.setOptimisticBias(0.0d);
        mcts.setPessimisticBias(0.0d);
        mcts.setMoveSelectionPolicy(FinalSelectionPolicy.robustChild);
        int[] scores = new int[3];

        for (int i = 0; i < 100; i++) {
            TicTacToe ttt = new TicTacToe();
            while (!ttt.gameOver()) {
                System.out.println("\n\n********************************** GAME " + i + " **********************************");
                // int x = if(ttt.currentPlayer === 1) {return 2} else {return 1};
                int pplayer = ttt.currentPlayer == 1 ? 2 : 1;
                System.out.println("|||--- player " + pplayer + "'s turn ---|||");
                move = mcts.runMCTS_UCT(ttt, 120000, false);
                // move = mcts.runMCTS_UCT(ttt, 100, false);
                // System.out.print("this is move" + move);
                ttt.makeMove(move);
                for (int[] row : ttt.board) {
                    for (int col : row) {
                        System.out.print(col + " ");
                    }
                    System.out.println("");
                }
            }
                
                System.out.println("<<<<<<<<<<<<<<<<<<<<<<<<-----**GAME OVER**------->>>>>>>>>>>>>>>>>>>>>>>>\n\n\n\n");
                // ttt.bPrint();
                // System.out.println("00000000000000000000000000000000000000000000000000000000\n");
                            
                double []scr = ttt.getScore();
                if (scr[0] > 0.9) {
                    scores[0]++; // player 1
                } else if (scr[1] > 0.9) {
                    scores[1]++; // player 2
                } else
                    scores[2]++; // draw
                
                // System.out.println(Arrays.toString(scr));
                // System.out.println("--------------------------------------------- \n\n");
                // System.out.println(Arrays.toString(scores));
        }
    }
}
