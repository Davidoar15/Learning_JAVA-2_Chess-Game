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

    public boolean canMove(int targetCol, int targetRow) {
        if (isWithinBoard(targetCol, targetRow) && isSameSquare(targetCol, targetRow) == false) {
            // Move Value based in Color
            int moveValue;
            if (color == GamePanel.WHITE) {
                moveValue = -1;
            } else {
                moveValue = 1;
            }

            // Check Hitting Piece
            hittingPiece = getHittingPiece(targetCol, targetRow);

            // One Square Move
            if (targetCol == preCol && targetRow == preRow + moveValue && hittingPiece == null) {
                return true;
            }

            // Two Square Move
            if (
                targetCol == preCol && targetRow == preRow + moveValue*2 &&
                hittingPiece == null && moved == false &&
                pieceIsOnStraightLine(targetCol, targetRow) == false
            ) {
                return true;
            }

            // Diagonal Capture
            if (
                Math.abs(targetCol - preCol) == 1 && targetRow == preRow + moveValue && 
                hittingPiece != null && hittingPiece.color != color
            ) {
                return true;
            }
        }

        return false;
    }
}
