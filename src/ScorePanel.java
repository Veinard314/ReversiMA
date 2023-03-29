import javax.swing.*;
import java.awt.*;

public class ScorePanel extends JPanel {

    private JLabel scoreLabel;
    private JLabel playerLabel;

    private JButton restartButton;

    public ScorePanel() {
        setLayout(new GridLayout(3, 1));

        scoreLabel = new JLabel("Score: 0 - 0", SwingConstants.CENTER);
        add(scoreLabel);

        playerLabel = new JLabel("Ход белых", SwingConstants.CENTER);
        add(playerLabel);

        restartButton = new JButton("Начать игру заново");
        add(restartButton);
    }

    public void updateScore(int whiteScore, int blackScore) {
        scoreLabel.setText("Score: " + whiteScore + " - " + blackScore);
    }

    public void updatePlayer(String player) {
        playerLabel.setText(player);
    }
}