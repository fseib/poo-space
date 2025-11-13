package vista;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Image;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class PanelHUD extends JPanel {
    
    private static final long serialVersionUID = 1L;
    private JLabel scoreLabel;
    private JPanel livesPanel;
    private JLabel levelLabel;
    private final Font gameFont = new Font("Monospaced", Font.BOLD, 18);
    
    private final int SHIP_ICON_WIDTH = 30; 
    private final int SHIP_ICON_HEIGHT = 15; 
    private final int MAX_ICONS_SHOWN = 4;

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
        
        livesPanel = new JPanel();
        livesPanel.setBackground(Color.BLACK);
        livesPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        livesPanel.setAlignmentX(Component.RIGHT_ALIGNMENT);
        
        livesPanel.setPreferredSize(new Dimension(150, SHIP_ICON_HEIGHT + 5)); 
        add(livesPanel);
        
        levelLabel = new JLabel("LEVEL: 1");
        levelLabel.setForeground(Color.RED);
        levelLabel.setFont(gameFont);
        levelLabel.setAlignmentX(Component.RIGHT_ALIGNMENT);
        add(levelLabel);
    }
    
    public void actualizarHUD(int score, int lives, int level) {
        scoreLabel.setText(String.format("SCORE: %06d", score));

        livesPanel.removeAll();

        int iconsToShow = Math.min(lives, MAX_ICONS_SHOWN);
        

        if(lives <= MAX_ICONS_SHOWN)
        for (int i = 0; i < iconsToShow; i++) {
            JLabel shipIconLabel = createShipIconLabel(); 
            livesPanel.add(shipIconLabel);
        }
        else {
        	JLabel shipIconLabel = createShipIconLabel(); 
            livesPanel.add(shipIconLabel);
            JLabel multiplierLabel = new JLabel("x " + lives);
            multiplierLabel.setForeground(Color.RED);
            multiplierLabel.setFont(gameFont);
            livesPanel.add(multiplierLabel);
        }
        
        livesPanel.revalidate();
        livesPanel.repaint(); 
        
        levelLabel.setText("LEVEL: " + level);

    }
    
    private JLabel createShipIconLabel() {
        JLabel label = new JLabel();
        
        ImageIcon originalIcon = new ImageIcon("player.png");
        Image imageScaled = originalIcon.getImage().getScaledInstance(
            SHIP_ICON_WIDTH, SHIP_ICON_HEIGHT, Image.SCALE_SMOOTH
        );
        label.setIcon(new ImageIcon(imageScaled));
        
        return label;
    }
}