import javax.swing.*;
import java.awt.*;


public class ReversiApp {


    public static void main(String [] args) {

        ChessBoard chessBoard = new ChessBoard();
        chessBoard.initBoard();

        EventQueue.invokeLater(() -> {
            JFrame frame = new JFrame();
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setResizable(false);

            ScaledPanel scaledPanel = new ScaledPanel(chessBoard);

            frame.add(scaledPanel, BorderLayout.CENTER);
            frame.add(scaledPanel.scorePanel, BorderLayout.NORTH);

            frame.pack();
            frame.setVisible(true);

        });

    }


}
