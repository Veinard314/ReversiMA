import java.awt.*;
import java.awt.event.*;
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

    }


    private class ComponentHandler extends ComponentAdapter {

        @Override
        public void componentResized(ComponentEvent e) {
            repaint();
        }
    }

    private class MouseHandler extends MouseAdapter {


        @Override
        public void mouseClicked(MouseEvent e) {
            int col = (int) (e.getX() / DIAM) + 1;
            int row = (int) (e.getY() / DIAM) + 1;
            if (chessBoard.isLegalMove(col, row, ChessBoard.CS_WHITE)) {
                chessBoard.SetSquare(col, row, ChessBoard.CS_WHITE);
                chessBoard.flipChips(ChessBoard.CS_WHITE);
                repaint();
                // авто ход черных
                //Thread.sleep(2000);
                CPair pair = new CPair(0, 0);
                int res = chessBoard.Test(ChessBoard.CS_BLACK, pair);
                System.out.println(res);
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
