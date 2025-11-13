package vista;

import java.awt.*;
import javax.swing.*;
import controlador.ControladorJuego;
import vista.Ventana;

public class PanelMenu extends JPanel {


	private static final long serialVersionUID = -3876247889876139787L;
	private final String[] difficulties = {"CADETE", "GUERRERO", "MASTER"};
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
        JButton scoresButton = new JButton("PUNTAJES");
        scoresButton.setFont(new Font("Monospaced", Font.PLAIN, 16));
        scoresButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        scoresButton.addActionListener(e -> {
            Component parent = SwingUtilities.getWindowAncestor(this);
            if (parent instanceof Ventana) {
                ((Ventana) parent).showScoresPanel(); // <<< LINK TO NEW VIEW
            }
        });
        add(scoresButton);
        
        add(Box.createVerticalStrut(10)); // Spacer
        
        JButton creditButton = new JButton("AÑADIR UN CRÉDITO");
        creditButton.setFont(new Font("Monospaced", Font.PLAIN, 16));
        creditButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Link the button to the Controller's method
        creditButton.addActionListener(e -> {
            ControladorJuego controller = ControladorJuego.getInstancia();
            controller.añadirCredito();
            
            // Optional: Provide instant feedback
            JOptionPane.showMessageDialog(this, 
                "Crédito añadido. Créditos actuales: " + controller.getCreditos(), 
                "Crédito", JOptionPane.INFORMATION_MESSAGE);
        });
        
        add(creditButton);
    }
    
    
 // Inside vista/PanelMenu.java

    private void startGame() {
        String difficulty;
        
        // 1. Determine the canonical (internal) difficulty string
        switch((String) difficultySelector.getSelectedItem()) {
        case "CADETE":
        	difficulty = "FACIL";
        	break;
        case "GUERRERO":
        	difficulty = "NORMAL"; // Assuming GUERRERO maps to NORMAL difficulty
        	break;
        case "MASTER":
        	difficulty = "DIFICIL";
        	break;
        default:
        	difficulty = "NORMAL";
        }
        
        ControladorJuego controller = ControladorJuego.getInstancia();
        
        // 2. Set the chosen difficulty in the Model (must happen before the check)
        controller.setDificultad(difficulty); 
        
        // 3. Attempt to start the game (The Controller performs the credit check and Model initialization)
        // The Controller will show a dialog if the credit check fails.
        boolean startedSuccessfully = controller.iniciarJuego();
        
        if (startedSuccessfully) {
            // 4. If the Controller confirmed success (credit consumed, timer started), 
            //    then and only then, swap the View.
            
            // --- View Swap Logic ---
            
            // Find the parent JFrame (Ventana)
            Ventana parentFrame = (Ventana) SwingUtilities.getWindowAncestor(this);
            
            if (parentFrame != null) {
                // Swap to the game panel and set focus to the ship
                
                // Note: This logic needs to be robust, so we move the entire swap 
                // logic directly here from the problematic showGamePanel method.
                
                parentFrame.getContentPane().removeAll();
                
                // Re-add HUD and Game Panel using the frame's BorderLayout
                parentFrame.add(parentFrame.getPanelHUD(), BorderLayout.EAST); 
                parentFrame.add(parentFrame.getPanelPrincipal(), BorderLayout.CENTER);
                
                // Set the content pane's preferred size to trigger the resize
                parentFrame.getContentPane().setPreferredSize(new Dimension(
                    parentFrame.getPanelPrincipal().getPreferredSize().width + parentFrame.getPanelHUD().getPreferredSize().width,
                    parentFrame.getPanelPrincipal().getPreferredSize().height
                ));
                
                parentFrame.pack();
                
                // CRITICAL: Set the focus immediately after swap
                if (parentFrame.getPanelPrincipal().getImagenNave() != null) {
                    parentFrame.getPanelPrincipal().getImagenNave().requestFocusInWindow();
                }
                
                parentFrame.revalidate();
                parentFrame.repaint();
            }
        }
        // If startedSuccessfully is false, the Controller has already shown a warning, 
        // and the view remains correctly on the Menu Panel.
    }
}