package vista;

import java.awt.*;
import javax.swing.*;
import controlador.ControladorJuego;
import vista.Ventana; // Need to import Ventana to cast the parent

public class PanelMenu extends JPanel {
    private final String[] difficulties = {"FACIL", "NORMAL", "DIFICIL"};
    private JComboBox<String> difficultySelector;

    public PanelMenu() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(Color.BLACK);
        setPreferredSize(new Dimension(800, 600)); 

        // --- 1. Title ---
        JLabel titleLabel = new JLabel("SPACE INVADERS");
        titleLabel.setFont(new Font("Monospaced", Font.BOLD, 50));
        titleLabel.setForeground(Color.CYAN);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        add(Box.createVerticalStrut(150));
        add(titleLabel);
        add(Box.createVerticalStrut(50));

        // --- 2. Difficulty Selector ---
        difficultySelector = new JComboBox<>(difficulties);
        difficultySelector.setFont(new Font("Monospaced", Font.PLAIN, 16));
        difficultySelector.setMaximumSize(new Dimension(200, 30));
        difficultySelector.setAlignmentX(Component.CENTER_ALIGNMENT);
        difficultySelector.setSelectedIndex(1); // Set default to NORMAL
        add(difficultySelector);
        add(Box.createVerticalStrut(20));

        // --- 3. Start Game Button ---
        JButton startButton = new JButton("START GAME");
        startButton.setFont(new Font("Monospaced", Font.BOLD, 16));
        startButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        // Link the button to the startGame method
        startButton.addActionListener(e -> startGame()); 
        add(startButton);
        add(Box.createVerticalStrut(10));

        // --- 4. Scores Button ---
        JButton scoresButton = new JButton("HIGH SCORES");
        scoresButton.setFont(new Font("Monospaced", Font.PLAIN, 16));
        scoresButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        scoresButton.addActionListener(e -> JOptionPane.showMessageDialog(this, "High Scores coming soon!")); 
        add(scoresButton);
    }
    
    // --- Transition Logic ---
    private void startGame() {
        String difficulty = (String) difficultySelector.getSelectedItem();
        ControladorJuego controller = ControladorJuego.getInstancia();
        
        // 1. Tell the controller the chosen difficulty (Model Update)
        controller.setDificultad(difficulty); 
        
        // 2. Tell the main window (Ventana) to switch views (View Management)
        Component parent = SwingUtilities.getWindowAncestor(this);
        if (parent instanceof Ventana) {
            ((Ventana) parent).showGamePanel();
        }
    }
}