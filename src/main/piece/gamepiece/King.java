package main.piece.gamepiece;

import main.gamepanel.GamePanel;
import main.piece.Piece;

public class King extends Piece {

    public King(int color, int col, int row) {
        super(color, col, row);

        if (color == GamePanel.WHITE) {
            image = getImage("white/king-white");
        } else {
            image = getImage("black/king-black");
        }
    }
}