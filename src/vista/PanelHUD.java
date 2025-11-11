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
    private JPanel livesPanel; // NEW: Container for the ship icons
    private JLabel levelLabel;
    private final Font gameFont = new Font("Monospaced", Font.BOLD, 18);
    
    private final int SHIP_ICON_WIDTH = 30; 
    private final int SHIP_ICON_HEIGHT = 15; 
    private final int MAX_ICONS_SHOWN = 4; // Max number of ships to display before showing "x 4"

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
        // Use FlowLayout to lay icons horizontally
        livesPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 5, 0)); // Align right, 5px gap
        livesPanel.setAlignmentX(Component.RIGHT_ALIGNMENT);
        
        // Set a predictable size for the lives panel
        livesPanel.setPreferredSize(new Dimension(150, SHIP_ICON_HEIGHT + 5)); 
        add(livesPanel); // Add the container panel to the HUD
        
        levelLabel = new JLabel("LEVEL: 1");
        levelLabel.setForeground(Color.RED);
        levelLabel.setFont(gameFont);
        levelLabel.setAlignmentX(Component.RIGHT_ALIGNMENT);
        add(levelLabel);
    }
    
    public void actualizarHUD(int score, int lives, int level) {
        scoreLabel.setText(String.format("SCORE: %06d", score));

        livesPanel.removeAll(); // Clear existing icons

        int iconsToShow = Math.min(lives, MAX_ICONS_SHOWN);
        
        // 1. Draw the required number of ship icons
        for (int i = 0; i < iconsToShow; i++) {
            // You need a method or a class that returns a JLabel with the scaled ship icon
            JLabel shipIconLabel = createShipIconLabel(); 
            livesPanel.add(shipIconLabel);
        }
        
        // 2. Handle "x N" if lives exceed the maximum display limit
        if (lives > MAX_ICONS_SHOWN) {
            JLabel multiplierLabel = new JLabel("x " + lives);
            multiplierLabel.setForeground(Color.RED);
            multiplierLabel.setFont(gameFont);
            livesPanel.add(multiplierLabel);
        }
        
        livesPanel.revalidate();
        livesPanel.repaint(); // Force redraw of the icons
        // --- END LIVES DISPLAY LOGIC ---
        
        levelLabel.setText("LEVEL: " + level);

    }
    
    private JLabel createShipIconLabel() {
        JLabel label = new JLabel();
        
        // CRITICAL: You must get the scaled ship icon here. 
        // This logic should match what's in ImagenNave's constructor.
        // For simplicity, we just create a scaled icon.
        ImageIcon originalIcon = new ImageIcon("player.png");
        Image imageScaled = originalIcon.getImage().getScaledInstance(
            SHIP_ICON_WIDTH, SHIP_ICON_HEIGHT, Image.SCALE_SMOOTH
        );
        label.setIcon(new ImageIcon(imageScaled));
        
        return label;
    }
}