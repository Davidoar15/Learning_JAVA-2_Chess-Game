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

    public boolean canMove(int targetCol, int targetRow) {
        if (isWithinBoard(targetCol, targetRow) && isSameSquare(targetCol, targetRow) == false) {
            if (Math.abs(targetCol - preCol) == Math.abs(targetRow - preRow)) {
                if (
                    isValidSquare(targetCol, targetRow) && 
                    pieceIsOnDiagonalLine(targetCol, targetRow) == false
                ) {
                    return true;
                }
            }
        }

        return false;
    }
}
