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

        JLabel titleLabel = new JLabel("SPACE INVADERS");
        titleLabel.setFont(new Font("Monospaced", Font.BOLD, 50));
        titleLabel.setForeground(Color.CYAN);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        add(Box.createVerticalStrut(150));
        add(titleLabel);
        add(Box.createVerticalStrut(50));

        difficultySelector = new JComboBox<>(difficulties);
        difficultySelector.setFont(new Font("Monospaced", Font.PLAIN, 16));
        difficultySelector.setMaximumSize(new Dimension(200, 30));
        difficultySelector.setAlignmentX(Component.CENTER_ALIGNMENT);
        difficultySelector.setSelectedIndex(1); // Set default to NORMAL
        add(difficultySelector);
        add(Box.createVerticalStrut(20));

        JButton startButton = new JButton("START GAME");
        startButton.setFont(new Font("Monospaced", Font.BOLD, 16));
        startButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        startButton.addActionListener(e -> startGame()); 
        add(startButton);
        add(Box.createVerticalStrut(10));

        JButton scoresButton = new JButton("PUNTAJES");
        scoresButton.setFont(new Font("Monospaced", Font.PLAIN, 16));
        scoresButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        scoresButton.addActionListener(e -> {
            Component parent = SwingUtilities.getWindowAncestor(this);
            if (parent instanceof Ventana) {
                ((Ventana) parent).showScoresPanel();
            }
        });
        add(scoresButton);
        
        add(Box.createVerticalStrut(10));
        
        JButton creditButton = new JButton("AÑADIR UN CRÉDITO");
        creditButton.setFont(new Font("Monospaced", Font.PLAIN, 16));
        creditButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        creditButton.addActionListener(e -> {
            ControladorJuego controller = ControladorJuego.getInstancia();
            controller.añadirCredito();
            
            JOptionPane.showMessageDialog(this, 
                "Crédito añadido. Créditos actuales: " + controller.getCreditos(), 
                "Crédito", JOptionPane.INFORMATION_MESSAGE);
        });
        
        add(creditButton);
    }
    
    
    private void startGame() {
        String difficulty;
        
        switch((String) difficultySelector.getSelectedItem()) {
        case "CADETE":
        	difficulty = "FACIL";
        	break;
        case "GUERRERO":
        	difficulty = "NORMAL";
        	break;
        case "MASTER":
        	difficulty = "DIFICIL";
        	break;
        default:
        	difficulty = "NORMAL";
        }
        
        ControladorJuego controller = ControladorJuego.getInstancia();
        
        controller.setDificultad(difficulty); 
        
        boolean startedSuccessfully = controller.iniciarJuego();
        
        if (startedSuccessfully) {
            Ventana parentFrame = (Ventana) SwingUtilities.getWindowAncestor(this);
            
            if (parentFrame != null) {
                parentFrame.getContentPane().removeAll();
                
                parentFrame.add(parentFrame.getPanelHUD(), BorderLayout.EAST); 
                parentFrame.add(parentFrame.getPanelPrincipal(), BorderLayout.CENTER);
                
                parentFrame.getContentPane().setPreferredSize(new Dimension(
                    parentFrame.getPanelPrincipal().getPreferredSize().width + parentFrame.getPanelHUD().getPreferredSize().width,
                    parentFrame.getPanelPrincipal().getPreferredSize().height
                ));
                
                parentFrame.pack();
                
                if (parentFrame.getPanelPrincipal().getImagenNave() != null) {
                    parentFrame.getPanelPrincipal().getImagenNave().requestFocusInWindow();
                }
                
                parentFrame.revalidate();
                parentFrame.repaint();
            }
        }
    }
}