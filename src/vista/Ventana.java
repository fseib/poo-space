package vista;

import java.awt.Color;

import javax.swing.JFrame;

public class Ventana extends JFrame {
	
	private static final long serialVersionUID = 4328409579032353425L;
	private PanelPrincipal panelPrincipal;
	
	public Ventana() {
		panelPrincipal = new PanelPrincipal();
		panelPrincipal.setBackground(Color.BLACK);
		setContentPane(panelPrincipal);
		pack();
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setVisible(true);
	}
}
