package vista;

import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

public class ImagenInvasor extends JLabel {
	
	private static final long serialVersionUID = 8700232532579393479L;
	private int alto;
	private int ancho;
	
	public ImagenInvasor() {
		ancho = 50;
		alto = 50;
		Image imagen = new ImageIcon("down.png").getImage();
		Image imagenAEscala = imagen.getScaledInstance(ancho, alto, Image.SCALE_SMOOTH);
		ImageIcon icono = new ImageIcon(imagenAEscala);
		setIcon(icono);
	}
	
	public void mover(int x, int y) {
		setBounds(x, y, ancho, alto);
	}
}
