package main.piece.gamepiece;

import main.gamepanel.GamePanel;
import main.piece.Piece;

public class Bishop extends Piece {

    public Bishop(int color, int col, int row) {
        super(color, col, row);

        if (color == GamePanel.WHITE) {
            image = getImage("white/bishop-white");
        } else {
            image = getImage("black/bishop-black");
        }
    }
}
