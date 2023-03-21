import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import javax.swing.*;



/** @author John B. Matthews; distribution per GNU Public License */
public class ScaledPanel extends JPanel {

    private ChessBoard chessBoard;

    private static final int SIZE = 8; // 8x8 board
    private static final int DIAM = SIZE * 10; // checker size
    private static final int maxX = SIZE * DIAM;
    private static final int maxY = SIZE * DIAM;

    private static final Color white = new Color(0xF0F0C0);
    private static final Color light = new Color(0x40C040);
    private static final Color dark = new Color(0x404040);

    public ScaledPanel(ChessBoard chessBoard) {

        //super(true);

        this.chessBoard = chessBoard;

        this.setPreferredSize(new Dimension(maxX, maxY));

        this.addComponentListener(new ComponentHandler());
        this.addMouseListener(new MouseHandler());
        this.setFocusable(true);
    }

    @Override
    public void paintComponent(Graphics g) {
        Graphics2D g2D = (Graphics2D) g;

        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                g2D.setColor(dark);
                g2D.fillRect(col * DIAM, row * DIAM, DIAM, DIAM);
                g2D.setColor(light);
                g2D.drawRect(col * DIAM, row * DIAM, DIAM, DIAM);
                switch (chessBoard.GetSquare(col + 1, row + 1)) {
                    case ChessBoard.CS_WHITE:
                        g2D.setColor(white);
                        g2D.fillOval(col * DIAM + 2, row * DIAM + 2, DIAM - 4, DIAM - 4);
                        break;
                    case ChessBoard.CS_BLACK:
                        g2D.setColor(Color.BLACK);
                        g2D.fillOval(col * DIAM + 2, row * DIAM + 2, DIAM - 4, DIAM - 4);
                        break;
                }

            }
        }
        //
        if ((chessBoard.lastMove.x > 0) && (chessBoard.lastMove.x <= CBoard.CB_DIM) && (chessBoard.lastMove.y > 0) && (chessBoard.lastMove.y <= CBoard.CB_DIM)) {
            g2D.setColor(Color.red);
            g2D.fillOval((chessBoard.lastMove.x - 1) * DIAM + 2, (chessBoard.lastMove.y - 1) * DIAM + 2,  7, 7);
        }
        //
    }


    private class ComponentHandler extends ComponentAdapter {

        @Override
        public void componentResized(ComponentEvent e) {
            repaint();
        }
    }

    private class MouseHandler extends MouseAdapter {


        @Override
        public void mousePressed(MouseEvent e) {
            int col = (int) (e.getX() / DIAM) + 1;
            int row = (int) (e.getY() / DIAM) + 1;

            // есть ошибка с ходами, проверить
            if (chessBoard.isGameOver(chessBoard.getCBoard())) {
                System.out.println("Game over");
                int playerW = chessBoard.countChipsNew(ChessBoard.CS_WHITE, chessBoard.getCBoard());
                int playerB = 64 - playerW;
                if (playerW > playerB) {
                    System.out.println("White wins");
                } else if (playerB >playerW) {
                    System.out.println("Black wins");
                } else System.out.println("Its draw");
                String c = String.valueOf(playerW) + ":" + String.valueOf(playerB);
                System.out.println(c);
                return;
            }

            if (chessBoard.findPossibleMoves(ChessBoard.CS_WHITE,chessBoard.getCBoard()).size() > 0) {
                if (chessBoard.isLegalMove(col, row, ChessBoard.CS_WHITE)) {
                    ArrayList<CPair> moves = new ArrayList<CPair>();
                    String a = "Ход белых:" + "(" + String.valueOf(col) + "," + String.valueOf(row) + ")";
                    System.out.println(a);
                    moves.clear();
                    //chessBoard.Test2(ChessBoard.CS_WHITE, moves);

                    chessBoard.findFlippedChips(col, row, ChessBoard.CS_WHITE);
                    chessBoard.SetSquare(col, row, ChessBoard.CS_WHITE);
                    chessBoard.flipChips(ChessBoard.CS_WHITE);
                    paintImmediately(0,0, getWidth(), getHeight()); ///!!! можно не все перерисовывать!
                    // авто ход черных
                    if (chessBoard.findPossibleMoves(ChessBoard.CS_BLACK, chessBoard.getCBoard()).size() > 0) {
                        //chessBoard.showBoard();
                        //CPair pair = new CPair(0, 0);
                        //int res = chessBoard.Test(ChessBoard.CS_BLACK, pair);
                        //String b = "Ход черных (робот): ("+ String.valueOf(pair.x)+","+String.valueOf(pair.y)+") из доступных "+  String.valueOf(res);
                        //System.out.println(b);
                        //
                        /*
                        moves.clear();
                        chessBoard.Test3(ChessBoard.CS_BLACK, 4, chessBoard.getCBoard(), pair);
                        String b = "Ход черных (робот): ("+ String.valueOf(pair.x)+","+String.valueOf(pair.y)+")";
                        System.out.println(b);
                        //chessBoard.Test2(ChessBoard.CS_BLACK, moves);
                        //
                        chessBoard.findFlippedChips(pair.x, pair.y, ChessBoard.CS_BLACK);
                        chessBoard.SetSquare(pair.x, pair.y, ChessBoard.CS_BLACK);
                        chessBoard.flipChips(ChessBoard.CS_BLACK);
                        */

                        System.out.println("Ход черных:");
                        //
                        //CPair pair = chessBoard.mainMoveSearch(ChessBoard.CS_BLACK, 10, chessBoard.getCBoard());
                        CPair pair = chessBoard.mainMultiThreadMoveSearch(ChessBoard.CS_BLACK, 10, chessBoard.getCBoard());
                        //
                        chessBoard.lastMove = pair;
                        //
                        String k = "Лучший  (" + String.valueOf(pair.x) + "," + String.valueOf(pair.y) + ") n=" + String.valueOf(chessBoard.n);
                        System.out.println(k);

                        chessBoard.findFlippedChips(pair.x, pair.y, ChessBoard.CS_BLACK);
                        chessBoard.SetSquare(pair.x, pair.y, ChessBoard.CS_BLACK);
                        chessBoard.flipChips(ChessBoard.CS_BLACK);


                        repaint();
                    } else System.out.println("Black pass");
                    String l = "W:" + String.valueOf(chessBoard.countChips(ChessBoard.CS_WHITE)) + " B:" + String.valueOf(chessBoard.countChips(ChessBoard.CS_BLACK));
                    System.out.println(l);
                }

            } else { // нет хода для белых - ходит черный робот
                System.out.println("Ход черных после пропуска белых:");

                CPair pair = chessBoard.mainMoveSearch(ChessBoard.CS_BLACK, 6, chessBoard.getCBoard());

                String k = "Лучший  (" + String.valueOf(pair.x) + "," + String.valueOf(pair.y) + ") n=" + String.valueOf(chessBoard.n);
                System.out.println(k);

                chessBoard.findFlippedChips(pair.x, pair.y, ChessBoard.CS_BLACK);
                chessBoard.SetSquare(pair.x, pair.y, ChessBoard.CS_BLACK);
                chessBoard.flipChips(ChessBoard.CS_BLACK);

                repaint();

            }



         /*
            int player = (e.getButton() == MouseEvent.BUTTON1) ? ChessBoard.CS_WHITE : ChessBoard.CS_BLACK;

            if (chessBoard.isLegalMove(col, row, player)) {
                chessBoard.SetSquare(col, row, player);
                chessBoard.flipChips(player);
                repaint();

          */
            }
        }
 }
