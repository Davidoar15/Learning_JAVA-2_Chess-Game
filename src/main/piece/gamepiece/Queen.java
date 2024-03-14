package main.piece.gamepiece;

import main.gamepanel.GamePanel;
import main.piece.Piece;

public class Queen extends Piece {

    public Queen(int color, int col, int row) {
        super(color, col, row);

        if (color == GamePanel.WHITE) {
            image = getImage("white/queen-white");
        } else {
            image = getImage("black/queen-black");
        }
    }
}
