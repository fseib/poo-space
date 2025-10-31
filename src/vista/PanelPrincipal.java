package vista;

import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

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
	private List<ProyectilVista> proyectiles  = new ArrayList<>();;
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
				
				if(e.getKeyChar() == 'w') {
					int projectileStartX =	ControladorJuego.getInstancia().dispararNave();
		            
		            ProyectilVista pro = new ProyectilVista(projectileStartX, true, 1); 
		            

		            final int PRO_WIDTH = 8;
		            final int PRO_HEIGHT = 15;
		            
		            final int PRO_START_Y = 500 - PRO_HEIGHT; 

		            pro.setBounds(
		                projectileStartX + 25, // Center the projectile view component on the ship's X
		                PRO_START_Y, 
		                PRO_WIDTH, 
		                PRO_HEIGHT
		            );
		            
		            proyectiles.add(pro);
		            add(pro);
		            revalidate(); 
		            repaint();
				}
				imagenNave.mover(x, 500);
			}
		});
	}
	
	
	//El timer recibe un evento con la posicion de todo. Mueve las cosas a donde corresponde y chequea colisiones y cosas etc
	//Luego si es necesario cambia el estado y redibuja

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
