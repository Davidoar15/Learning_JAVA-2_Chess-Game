package main.piece;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

import main.board.Board;
import main.gamepanel.GamePanel;

public class Piece {
    
    public BufferedImage image;
    public int x, y;
    public int col, row, preCol, preRow;
    public int color;
    public Piece hittingPiece;

    public Piece(int color, int col, int row) {
        this.color = color;
        this.col = col;
        this.row = row;
        x = getX(col);
        y = getY(row);
        preCol = col;
        preRow = row;
    }

    public BufferedImage getImage(String imagePath) {
        BufferedImage image = null;

        try {
            image = ImageIO.read(getClass().getResourceAsStream("../../res/pieces/"+imagePath+".png"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return image;
    }

    public int getX(int col) {
        return col*Board.SQUARE_SIZE;
    }

    public int getY(int row) {
        return row*Board.SQUARE_SIZE;
    }

    public int getCol(int x) {
        return (x + Board.HALF_SQUARE_SIZE)/Board.SQUARE_SIZE;
    }

    public int getRow(int y) {
        return (y + Board.HALF_SQUARE_SIZE)/Board.SQUARE_SIZE;
    }

    public int getIndex() {
        for (int i = 0; i < GamePanel.simPieces.size(); i++) {
            if (GamePanel.simPieces.get(i) == this) {
                return i;
            }
        }

        return 0;
    }

    public void updatePosition() {
        x = getX(col);
        y = getY(row);

        preCol = getCol(x);
        preRow = getRow(y);
    }

    public void resetPosition() {
        col = preCol;
        row = preRow;

        x = getX(col);
        y = getY(row);
    }

    public boolean canMove(int targetCol, int targetRow) {
        return false;
    }

    public boolean isWithinBoard(int targetCol, int targetRow) {
        if (targetCol >= 0 && targetCol <= 7 && targetRow >= 0 && targetRow <= 7) {
            return true;
        }

        return false;
    }

    public Piece getHittingPiece(int targetCol, int targetRow) {
        for (Piece piece : GamePanel.simPieces) {
            if (piece.col == targetCol && piece.row == targetRow && piece != this) {
                return piece;
            }
        }

        return null;
    }

    public boolean isValidSquare(int targetCol, int targetRow) {
        hittingPiece = getHittingPiece(targetCol, targetRow);

        if (hittingPiece == null) { // Square is Free
            return true;
        } else { // Square is Occupied
            if (hittingPiece.color != this.color) { // If Different Color, piece can be Capture
                return true;
            } else {
                hittingPiece = null;
            }
        }

        return false;
    }

    public void draw(Graphics2D graphics2d) {
        graphics2d.drawImage(image, x, y, Board.SQUARE_SIZE, Board.SQUARE_SIZE, null);
    }
}
