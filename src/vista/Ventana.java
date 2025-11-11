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

	public Ventana() {
        super("Space Invaders");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout()); 
        
		panelPrincipal = new PanelPrincipal();
		panelPrincipal.setBackground(Color.BLACK);
        panelHUD = new PanelHUD(); 
        
        panelPrincipal.setPreferredSize(new Dimension(800, 600)); 
        
        ControladorJuego.getInstancia().setVistaHUD(panelHUD); 
        
        add(panelPrincipal, BorderLayout.CENTER); 
        add(panelHUD, BorderLayout.EAST);         

        pack();
		setVisible(true);
	}
}
