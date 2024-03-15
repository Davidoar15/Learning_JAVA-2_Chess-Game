package main.piece.gamepiece;

import main.gamepanel.GamePanel;
import main.piece.Piece;
import main.typepiece.Type;

public class Queen extends Piece {

    public Queen(int color, int col, int row) {
        super(color, col, row);

        type = Type.QUEEN;

        if (color == GamePanel.WHITE) {
            image = getImage("white/queen-white");
        } else {
            image = getImage("black/queen-black");
        }
    }

    public boolean canMove(int targetCol, int targetRow) {
        if (isWithinBoard(targetCol, targetRow) && isSameSquare(targetCol, targetRow) == false) {
            // Move to Left | Right
            if (targetCol == preCol || targetRow == preRow) {
                if (
                    isValidSquare(targetCol, targetRow) &&
                    pieceIsOnStraightLine(targetCol, targetRow) == false
                ) {
                    return true;
                }
            }

            // Diagonal Move
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
