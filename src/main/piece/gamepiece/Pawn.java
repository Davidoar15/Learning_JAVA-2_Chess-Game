package main.piece.gamepiece;

import main.gamepanel.GamePanel;
import main.piece.Piece;

public class Pawn extends Piece {

    public Pawn(int color, int col, int row) {
        super(color, col, row);

        if (color == GamePanel.WHITE) {
            image = getImage("white/pawn-white");
        } else {
            image = getImage("black/pawn-black");
        }
    }
}
