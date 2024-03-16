package main.gamepanel;

import main.board.Board;
import main.mouse.Mouse;
import main.piece.Piece;
import main.piece.gamepiece.Bishop;
import main.piece.gamepiece.King;
import main.piece.gamepiece.Knight;
import main.piece.gamepiece.Pawn;
import main.piece.gamepiece.Queen;
import main.piece.gamepiece.Rook;
import main.typepiece.Type;

import javax.swing.JPanel;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.ArrayList;

public class GamePanel extends JPanel implements Runnable {
    
    public static final int WIDTH = 900;
    public static final int HEIGHT = 656;
    final int FPS = 60;
    Thread gameThread;
    Board board = new Board();
    Mouse mouse = new Mouse();

    // Color
    public static final int WHITE = 0;
    public static final int BLACK = 1;
    int currentColor = WHITE;

    // Pieces
    public static ArrayList<Piece> pieces = new ArrayList<>();
    public static ArrayList<Piece> simPieces = new ArrayList<>();
    ArrayList<Piece> promotionPieces = new ArrayList<>();
    Piece activePiece;
    public static Piece castlingPiece;

    // Booleans
    boolean canMove;
    boolean validSquare;
    boolean promotion;

    public GamePanel() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(Color.BLACK);

        addMouseMotionListener(mouse);
        addMouseListener(mouse);

        setPieces();
        copyPieces(pieces, simPieces);
    }

    public void launchGame() {
        gameThread = new Thread(this);
        gameThread.start();
    }

    public void setPieces() {
        // * WHITES
        pieces.add(new Pawn(WHITE, 0, 6));
        pieces.add(new Pawn(WHITE, 1, 6));
        pieces.add(new Pawn(WHITE, 2, 6));
        pieces.add(new Pawn(WHITE, 3, 6));
        pieces.add(new Pawn(WHITE, 4, 6));
        pieces.add(new Pawn(WHITE, 5, 6));
        pieces.add(new Pawn(WHITE, 6, 6));
        pieces.add(new Pawn(WHITE, 7, 6));

        pieces.add(new Rook(WHITE, 0, 7));
        pieces.add(new Rook(WHITE, 7, 7));

        pieces.add(new Knight(WHITE, 1, 7));
        pieces.add(new Knight(WHITE, 6, 7));

        pieces.add(new Bishop(WHITE, 2, 7));
        pieces.add(new Bishop(WHITE, 5, 7));

        pieces.add(new Queen(WHITE, 3, 7));

        pieces.add(new King(WHITE, 4, 7));

        // * BLACKS
        pieces.add(new Pawn(BLACK, 0, 1));
        pieces.add(new Pawn(BLACK, 1, 1));
        pieces.add(new Pawn(BLACK, 2, 1));
        pieces.add(new Pawn(BLACK, 3, 1));
        pieces.add(new Pawn(BLACK, 4, 1));
        pieces.add(new Pawn(BLACK, 5, 1));
        pieces.add(new Pawn(BLACK, 6, 1));
        pieces.add(new Pawn(BLACK, 7, 1));

        pieces.add(new Rook(BLACK, 0, 0));
        pieces.add(new Rook(BLACK, 7, 0));

        pieces.add(new Knight(BLACK, 1, 0));
        pieces.add(new Knight(BLACK, 6, 0));

        pieces.add(new Bishop(BLACK, 2, 0));
        pieces.add(new Bishop(BLACK, 5, 0));

        pieces.add(new Queen(BLACK, 3, 0));

        pieces.add(new King(BLACK, 4, 0));
    }

    private void copyPieces(ArrayList<Piece> source, ArrayList<Piece> target) {
        target.clear();
        
        for (int i = 0; i < source.size(); i++) {
            target.add(source.get(i));
        }
    }

    @Override
    public void run() {
        // ? GAME LOOP
        double drawInterval = 1000000000/FPS;
        double delta = 0;
        long lastTime = System.nanoTime();
        long currentTime;

        while(gameThread != null) {
            currentTime = System.nanoTime();

            delta += (currentTime - lastTime)/drawInterval;
            lastTime = currentTime;

            if (delta >= 1) {
                update();
                repaint();
                delta--;
            }
        }
    }

    private void update() {

        if (promotion) {
            promoting();
        } else {
            if (mouse.pressed) {
                if (activePiece == null) {
                    for (Piece piece : simPieces) {
                        if (
                            piece.color == currentColor && 
                            piece.col == mouse.x/Board.SQUARE_SIZE && 
                            piece.row == mouse.y/Board.SQUARE_SIZE
                        ) {
                            activePiece = piece;
                        } 
                    }
                } else {
                    simulate();
                }
            } else {
                if (activePiece != null) {
                    if (validSquare) { // Move Confirmed
                        copyPieces(simPieces, pieces); // Update piece List in case of captured and removed during simulation!
                        activePiece.updatePosition();
    
                        if (castlingPiece != null) {
                            castlingPiece.updatePosition();
                        }
    
                        if (canPromove()) {
                            promotion = true;
                        } else {
                            changePlayer();
                        }
    
                    } else { // Invalid Move -> RESET
                        copyPieces(pieces, simPieces);
                        activePiece.resetPosition();
                        activePiece = null;
                    }
                }
            }
        }
    }

    private void simulate() {
        canMove = false;
        validSquare = false;

        // Reset Piece List. For restoring the removed piece during simulation!
        copyPieces(pieces, simPieces); 

        // Reset Castling Piece's Position
        if (castlingPiece != null) {
            castlingPiece.col = castlingPiece.preCol;
            castlingPiece.x = castlingPiece.getX(castlingPiece.col);
            castlingPiece = null;
        }

        activePiece.x = mouse.x - Board.HALF_SQUARE_SIZE;
        activePiece.y = mouse.y - Board.HALF_SQUARE_SIZE;
        activePiece.col = activePiece.getCol(activePiece.x);
        activePiece.row = activePiece.getRow(activePiece.y);

        if (activePiece.canMove(activePiece.col, activePiece.row)) {
            canMove = true;

            if (activePiece.hittingPiece != null) {
                simPieces.remove(activePiece.hittingPiece.getIndex());
            }

            checkCastling();

            validSquare = true;
        }
    }

    private void checkCastling() {
        if (castlingPiece != null) {
            if (castlingPiece.col == 0) {
                castlingPiece.col += 3;
            } else if (castlingPiece.col == 7) {
                castlingPiece.col -= 2;
            }
            castlingPiece.x = castlingPiece.getX(castlingPiece.col);
        }
    }

    private void changePlayer() {
        if (currentColor == WHITE) {
            currentColor = BLACK;

            // Reset Two Stepped Status (Black)
            for (Piece piece : pieces) {
                if (piece.color == BLACK) {
                    piece.twoStepped = false;
                }
            }
        } else {
            currentColor = WHITE;

            // Reset Two Stepped Status (White)
            for (Piece piece : pieces) {
                if (piece.color == WHITE) {
                    piece.twoStepped = false;
                }
            }
        }

        activePiece = null;
    }

    private boolean canPromove() {
        if (activePiece.type == Type.PAWN) {
            if (
                currentColor == WHITE && activePiece.row == 0 || 
                currentColor == BLACK && activePiece.row == 7
            ) {
                promotionPieces.clear();

                promotionPieces.add(new Knight(currentColor, 9, 2));
                promotionPieces.add(new Bishop(currentColor, 9, 3));
                promotionPieces.add(new Rook(currentColor, 9, 4));
                promotionPieces.add(new Queen(currentColor, 9, 5));

                return true;
            }
        }

        return false;
    }

    private void promoting() {
        if (mouse.pressed) {
            for (Piece piece : promotionPieces) {
                if (piece.col == mouse.x/Board.SQUARE_SIZE && piece.row == mouse.y/Board.SQUARE_SIZE) {
                    switch (piece.type) {
                        case KNIGHT:
                            simPieces.add(new Knight(currentColor, activePiece.col, activePiece.row));
                            break;
                    
                        case BISHOP:
                            simPieces.add(new Bishop(currentColor, activePiece.col, activePiece.row));
                            break;

                        case ROOK:
                            simPieces.add(new Rook(currentColor, activePiece.col, activePiece.row));
                            break;

                        case QUEEN:
                            simPieces.add(new Queen(currentColor, activePiece.col, activePiece.row));
                            break;

                        default:
                            break;
                    }
                    simPieces.remove(activePiece.getIndex());
                    copyPieces(simPieces, pieces);

                    activePiece = null;
                    promotion = false;
                    changePlayer();
                }
            }
        }
    }

    public void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);

        Graphics2D graphics2d = (Graphics2D)graphics;

        // Draw Board
        board.draw(graphics2d);

        // Draw Pieces
        for (Piece piece : simPieces) {
            piece.draw(graphics2d);
        }

        if (activePiece != null) {
            if (canMove) {
                graphics2d.setColor(Color.YELLOW);
                graphics2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.65f));
                graphics2d.fillRect(activePiece.col*Board.SQUARE_SIZE, activePiece.row*Board.SQUARE_SIZE, Board.SQUARE_SIZE, Board.SQUARE_SIZE);
                graphics2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
            }

            activePiece.draw(graphics2d);
        }

        // Messages
        graphics2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        graphics2d.setFont(new Font("Book Antiqua", Font.PLAIN, 30));
        graphics2d.setColor(Color.WHITE);

        if (promotion) {
            graphics2d.drawString("Promote To...!", 680, 120);
            for (Piece piece : promotionPieces) {
                graphics2d.drawImage(piece.image, piece.getX(piece.col), piece.getY(piece.row), Board.SQUARE_SIZE, Board.SQUARE_SIZE, null);
                
                graphics2d.setColor(Color.WHITE);
                graphics2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.2f));
                graphics2d.fillRect(piece.getX(piece.col), piece.getY(piece.row), Board.SQUARE_SIZE, Board.SQUARE_SIZE);
                graphics2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
            }
        } else {
            if (currentColor == WHITE) {
                graphics2d.drawString("Whites's Turn!", 680, 580);
            } else {
                graphics2d.drawString("Blacks's Turn!", 680, 100);
            }
        }
    }
}
