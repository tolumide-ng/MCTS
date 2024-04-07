package org.example.ticTacToe;

import java.util.Objects;

import org.example.lib.Move;

public class TicTacToeMove implements Move {
    public int x;
    public int y;

    TicTacToeMove(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public int compareTo(Move o) {
        TicTacToeMove oo = (TicTacToeMove) o;

        if (this.x == oo.x && this.y == oo.y) {
            return 0;
        }

        if (this.x > oo.x || this.y > oo.y) {
            return 1;
        }

        return -1;
    }

    @Override
    public boolean equals(Object m) {
        TicTacToeMove mm = (TicTacToeMove) m;

        return this.x == mm.x && this.y == mm.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.x, this.y);
    }

    @Override
    public String toString() {
        return ("(x=" + x + ", y=" + y + ")");
    }
}

