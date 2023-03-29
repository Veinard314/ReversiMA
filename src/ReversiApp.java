import javax.swing.*;
import java.awt.*;


public class ReversiApp {


    public static void main(String [] args) {
    /*
        // проверка работы конструктора с копированием - заполняем не по умолчанию
        ChessBoard chessBoard = new ChessBoard();
        CBoard board = new CBoard();
        for (int j = 0; j < CBoard.CB_YHEIGHT; j++) {
            for (int i = 0; i < CBoard.CB_XWIDTH; i++) {
                if (i > 0 && i < CBoard.CB_XWIDTH - 1 && j > 0 && j < CBoard.CB_YHEIGHT - 1) board.set(i ,j, ChessBoard.CS_EMPTY);
                else board.set(i, j, ChessBoard.CS_OUT);
            }
        }
        board.set(2, 2, ChessBoard.CS_BLACK);
        board.set(3, 2, ChessBoard.CS_WHITE);
        board.set(2, 3, ChessBoard.CS_WHITE);
        board.set(3, 3, ChessBoard.CS_BLACK);

        CBoard tmpBoard = board.clone();


        chessBoard.showBoardNew(tmpBoard);

        for (int j = 0; j < CBoard.CB_YHEIGHT; j++) {
            for (int i = 0; i < CBoard.CB_XWIDTH; i++) {
                board.set(i, j, 0);
            }
        }

        chessBoard.showBoardNew(tmpBoard);

        System.out.println("---------------------------");
        // проверка работы конструктора с копированием
        //ChessBoard chessBoard = new ChessBoard(board);
       */

        ChessBoard chessBoard = new ChessBoard();
        chessBoard.initBoard();

        ScorePanel scorePanel = new ScorePanel();
        //chessBoard.showBoard();
        //System.out.println(chessBoard.countChips(ChessBoard.CS_WHITE));

        EventQueue.invokeLater(() -> {
            JFrame frame = new JFrame();
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setResizable(false);

            frame.add(new ScaledPanel(chessBoard, scorePanel), BorderLayout.CENTER);
            frame.add(scorePanel, BorderLayout.NORTH);

            frame.pack();
            frame.setVisible(true);
        });

    }


}
