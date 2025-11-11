package vista;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import javax.swing.BoxLayout; // <-- NEW IMPORT
import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class PanelHUD extends JPanel {
    
    private static final long serialVersionUID = 1L;
    private JLabel scoreLabel;
    private JLabel livesLabel;
    private JLabel levelLabel;
    private final Font gameFont = new Font("Monospaced", Font.BOLD, 18);

    public PanelHUD() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS)); 
        setBackground(Color.BLACK);
        setPreferredSize(new Dimension(200, 600));         

        add(Box.createVerticalStrut(50)); 
        
        JLabel highScoreLabel = new JLabel("HIGH SCORE");
        highScoreLabel.setForeground(Color.CYAN);
        highScoreLabel.setFont(gameFont);
        highScoreLabel.setAlignmentX(Component.RIGHT_ALIGNMENT); 
        add(highScoreLabel);

        scoreLabel = new JLabel("000000");
        scoreLabel.setForeground(Color.GREEN);
        scoreLabel.setFont(gameFont);
        scoreLabel.setAlignmentX(Component.RIGHT_ALIGNMENT);
        add(scoreLabel);

        add(Box.createVerticalStrut(10));
        
        JLabel oneUpLabel = new JLabel("1UP");
        oneUpLabel.setForeground(Color.GREEN);
        oneUpLabel.setFont(gameFont);
        oneUpLabel.setAlignmentX(Component.RIGHT_ALIGNMENT);
        add(oneUpLabel);
        
        livesLabel = new JLabel("LIVES: 3");
        livesLabel.setForeground(Color.RED);
        livesLabel.setFont(gameFont);
        livesLabel.setAlignmentX(Component.RIGHT_ALIGNMENT);
        add(livesLabel);
        
        levelLabel = new JLabel("LEVEL: 1");
        levelLabel.setForeground(Color.RED);
        levelLabel.setFont(gameFont);
        levelLabel.setAlignmentX(Component.RIGHT_ALIGNMENT);
        add(levelLabel);
    }
    
    public void actualizarHUD(int score, int lives, int level) {
        scoreLabel.setText(String.format("SCORE: %06d", score));
        livesLabel.setText("LIVES: " + lives);
        levelLabel.setText("LEVEL: " + level);

    }
}