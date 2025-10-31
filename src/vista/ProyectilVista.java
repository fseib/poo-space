package vista;

import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

public class ProyectilVista extends JLabel {

	private static final long serialVersionUID = 3710860010698029847L;
	private int alto;
	private int ancho;
	
	public ProyectilVista(int x, boolean isPlayer, int velocidad) {
		ancho = 8;
		alto = 15;
		Image imagen = new ImageIcon("disparo.png").getImage();
		Image imagenAEscala = imagen.getScaledInstance(ancho, alto, Image.SCALE_SMOOTH);
		ImageIcon icono = new ImageIcon(imagenAEscala);
		setIcon(icono);
	}

   }