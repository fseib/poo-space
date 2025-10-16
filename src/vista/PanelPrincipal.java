package vista;

import java.awt.Dimension;
import java.awt.event.KeyEvent;

import javax.swing.JPanel;

import controlador.ControladorJuego;

public class PanelPrincipal extends JPanel {
	/**
	 * 
	 */
	
	private static final long serialVersionUID = -1345199283640062391L;
	private int ancho;
	private int alto;
	private ImagenNave imagenNave;
	private ImagenInvasor imagenInvasor;
//	private ControladorJuego controladorJuego;
	
	public PanelPrincipal() {
		this.alto = 600;
		this.ancho = 800;
		setLayout(null);
		setPreferredSize(new Dimension(ancho,alto));
		imagenNave = new ImagenNave();
		imagenInvasor = new ImagenInvasor();
		add(imagenNave);
		add(imagenInvasor);
		imagenInvasor.mover(400, 100);
		imagenNave.mover(400,500);
		imagenNave.setFocusable(true);
//		controladorJuego = new ControladorJuego();
		//imagenNave.addKeyListener(new ManejoTecla());
		imagenNave.addKeyListener(new MiPropioKeyAdapter() {
			int x;
			
			@Override
			public void keyTyped(KeyEvent e) {
				System.out.println(e.getKeyChar());
				//Izquierda 44 y derecha 46
				if(e.getKeyChar() == 'a')
					x = ControladorJuego.getInstancia().moverNaveIzquierda();
				else
					if(e.getKeyChar() == 'd')
						x = ControladorJuego.getInstancia().moverNaveDerecha();
				
				imagenNave.mover(x, 500);
			}
		});
	}
	
	class ManejoTecla extends MiPropioKeyAdapter{
		
		
		int x; 
		
		@Override
		public void keyTyped(KeyEvent e) {
			//Izquierda 44 y derecha 46
			if((int)e.getKeyChar() == 44) {
				x = ControladorJuego.getInstancia().moverNaveIzquierda();
			}
			else {
				if((int)e.getKeyChar() == 46) {
					x = ControladorJuego.getInstancia().moverNaveDerecha();
				}
			}
			imagenNave.mover(x, 300);
		}
	}
}
