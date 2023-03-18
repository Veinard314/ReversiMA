import javax.swing.*;
import java.awt.*;

public class ScorePanel extends JPanel {

    private JLabel scoreLabel;
    private JLabel playerLabel;

    public ScorePanel() {
        setLayout(new GridLayout(2, 1));

        scoreLabel = new JLabel("Score: 0 - 0", SwingConstants.CENTER);
        add(scoreLabel);

        playerLabel = new JLabel("Player: Black", SwingConstants.CENTER);
        add(playerLabel);
    }

    public void updateScore(int blackScore, int whiteScore) {
        scoreLabel.setText("Score: " + blackScore + " - " + whiteScore);
    }

    public void updatePlayer(String player) {
        playerLabel.setText("Player: " + player);
    }
}