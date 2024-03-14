package main.piece.gamepiece;

import main.gamepanel.GamePanel;
import main.piece.Piece;

public class Knight extends Piece {

    public Knight(int color, int col, int row) {
        super(color, col, row);

        if (color == GamePanel.WHITE) {
            image = getImage("white/knight-white");
        } else {
            image = getImage("black/knight-black");
        }
    }
}
