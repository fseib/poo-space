package vista;

import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

public class ProyectilVista extends JLabel {

	private static final long serialVersionUID = 3710860010698029847L;
	private final int modeloId;
	private int alto;
	private int ancho;
	
	public ProyectilVista(int modeloId) {
		this.modeloId = modeloId;
		ancho = 4;
		alto = 9;
		Image imagen = new ImageIcon("disparo.png").getImage();
		Image imagenAEscala = imagen.getScaledInstance(ancho, alto, Image.SCALE_SMOOTH);
		ImageIcon icono = new ImageIcon(imagenAEscala);
		setIcon(icono);
	}
	
	public int getModeloId() {
		return this.modeloId;
	}
   }