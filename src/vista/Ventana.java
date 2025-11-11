package vista;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JFrame;

import controlador.ControladorJuego;

public class Ventana extends JFrame {
	
	private static final long serialVersionUID = 4328409579032353425L;
	private PanelPrincipal panelPrincipal;
    private PanelHUD panelHUD; 
 	private PanelMenu panelMenu; // <-- NEW FIELD

 	public Ventana() {
         super("Space Invaders");
 		setDefaultCloseOperation(EXIT_ON_CLOSE);
         setLayout(new BorderLayout()); // Use a primary layout for the frame
         
         // Initialize all panels
 		panelPrincipal = new PanelPrincipal();
         panelHUD = new PanelHUD(); 
         panelMenu = new PanelMenu(); // <-- Initialize Menu

         // Game panel setup
 		panelPrincipal.setBackground(Color.BLACK);
         panelPrincipal.setPreferredSize(new Dimension(800, 600)); 
         
         // Pass references (done only once)
         ControladorJuego.getInstancia().setVistaHUD(panelHUD); 
         
         // Start on the menu screen
         showMenuPanel(); 

         pack();
 		setVisible(true);
 	}
 	

 	public void showMenuPanel() {
        getContentPane().removeAll(); // Clear existing content
        add(panelMenu, BorderLayout.CENTER);
        revalidate();
        repaint();
    }

 
    /**
     * Transitions from the menu to the main game view and starts the game loop.
     */
    public void showGamePanel() {
        getContentPane().removeAll(); // Clear existing content (the Menu)
        
        add(panelHUD, BorderLayout.EAST); 
        add(panelPrincipal, BorderLayout.CENTER);
        
        // 2. Set the content pane's preferred size to trigger the resize
        getContentPane().setPreferredSize(new Dimension(
            panelPrincipal.getPreferredSize().width + panelHUD.getPreferredSize().width,
            panelPrincipal.getPreferredSize().height
        ));
        
        pack(); // Resizes the frame
        revalidate();
        repaint();
        
        // 3. CRITICAL FOCUS FIX: Give the ship the keyboard focus
        if (panelPrincipal.getImagenNave() != null) {
            panelPrincipal.getImagenNave().requestFocusInWindow();
        }
        
        // Start the game loop AFTER the panels are added
        ControladorJuego.getInstancia().iniciarJuego(); 
    }
}
