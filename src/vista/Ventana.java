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
 	private PanelMenu panelMenu;
 	private PanelScores panelScores;
 	private static Ventana instancia;
 	
 	public PanelPrincipal getPanelPrincipal() {
 	    return panelPrincipal;
 	}

 	public PanelHUD getPanelHUD() {
 	    return panelHUD;
 	}
 	
 	public Ventana() {
         super("Space Invaders");
 		setDefaultCloseOperation(EXIT_ON_CLOSE);
         setLayout(new BorderLayout()); 
         
         instancia = this;
         
 		panelPrincipal = new PanelPrincipal();
         panelHUD = new PanelHUD(); 
         panelMenu = new PanelMenu();
         panelScores = new PanelScores();
         
 		panelPrincipal.setBackground(Color.BLACK);
         panelPrincipal.setPreferredSize(new Dimension(800, 600)); 
         
         ControladorJuego.getInstancia().setVistaHUD(panelHUD); 
         
         showMenuPanel(); 

         pack();
 		setVisible(true);
 	}
 	
 	public static Ventana getInstancia() {
        return instancia;
    }
 	

 	public void showMenuPanel() {
        getContentPane().removeAll();
        add(panelMenu, BorderLayout.CENTER);
        revalidate();
        repaint();
    }

 
    public void showGamePanel() {
        getContentPane().removeAll();
        
        add(panelHUD, BorderLayout.EAST); 
        add(panelPrincipal, BorderLayout.CENTER);
        
        getContentPane().setPreferredSize(new Dimension(
            panelPrincipal.getPreferredSize().width + panelHUD.getPreferredSize().width,
            panelPrincipal.getPreferredSize().height
        ));
        
        pack();
        revalidate();
        repaint();
        
        if (panelPrincipal.getImagenNave() != null) {
            panelPrincipal.getImagenNave().requestFocusInWindow();
        }
        
        ControladorJuego.getInstancia().iniciarJuego(); 
    }
    
    public void showScoresPanel() {
        getContentPane().removeAll();
        panelScores.displayScores(); 
        add(panelScores, BorderLayout.CENTER);
        pack();
        revalidate();
        repaint();
    }
}
