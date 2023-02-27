import javax.swing.*;
import java.awt.*;


public class ReversiApp {


    public static void main(String [] args) {
        ChessBoard chessBoard = new ChessBoard();
        chessBoard.initBoard();
        chessBoard.showBoard();
        //System.out.println(chessBoard.countChips(ChessBoard.CS_WHITE));

        EventQueue.invokeLater(() -> {
            JFrame frame = new JFrame();
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.add(new ScaledPanel(chessBoard));
            frame.pack();
            frame.setVisible(true);
        });

    }


}
