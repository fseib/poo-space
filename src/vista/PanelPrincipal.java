package vista;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

import controlador.ControladorJuego;
import modelo.ModeloInvasor;
import modelo.Proyectiles;

public class PanelPrincipal extends JPanel {
	/**
	 * 
	 */
	
	private static final long serialVersionUID = -1345199283640062391L;
	private int ancho;
	private int alto;
	private ImagenNave imagenNave;
	private List<ProyectilVista> proyectiles  = new ArrayList<>();;
	private List<ImagenInvasor> invasoresVista = new ArrayList<>();
	
	@Override
	protected void paintComponent(Graphics g) {
	    super.paintComponent(g); // Draws black background, then components draw themselves.
	    drawFlashOverlay(g);	
	    // This code runs *under* the components.
	    // If you see no flash, it means the color is too close to black, OR
	    // the problem is with the Controller's logic that triggers the flash.
	}
	
	
	public PanelPrincipal() {
		this.alto = 600;
		this.ancho = 800;
		setLayout(null);
		setPreferredSize(new Dimension(ancho,alto));
		imagenNave = new ImagenNave();
		add(imagenNave);
		imagenNave.mover(400,500);
		imagenNave.setFocusable(true);
	    ControladorJuego.getInstancia().setVista(this); 
	    ControladorJuego.getInstancia().iniciarJuego();
		imagenNave.addKeyListener(new MiPropioKeyAdapter() {
			int x;
			
			@Override
			public void keyTyped(KeyEvent e) {
				System.out.println(e.getKeyChar());
				if(e.getKeyChar() == 'a')
					x = ControladorJuego.getInstancia().moverNaveIzquierda();
				else
					if(e.getKeyChar() == 'd')
						x = ControladorJuego.getInstancia().moverNaveDerecha();
				
				if(e.getKeyChar() == 'w') {
					
					int proyectilId =	ControladorJuego.getInstancia().dispararNave();
					
					
					if(proyectilId != -1) {
					     ProyectilVista pro = new ProyectilVista(proyectilId); 
				            
				            Proyectiles modeloInicial = ControladorJuego.getInstancia().getProyectilById(proyectilId);
				            final int PRO_WIDTH = 8;
				            final int PRO_HEIGHT = 15;
					        final int SHIP_WIDTH_OFFSET = 30; 

				            final int PRO_START_Y = 500 - PRO_HEIGHT; 

				            pro.setBounds(
				                    modeloInicial.getX() + SHIP_WIDTH_OFFSET,
				                    modeloInicial.getY(),
				                    PRO_WIDTH, 
				                    PRO_HEIGHT
				                );
				            
				            proyectiles.add(pro);
				            add(pro);
				            revalidate(); 
				            repaint();
					}
		            
		       
				}
				imagenNave.mover(x, 500);
			}
		});
	}
	
	public void limpiarVista() {
	    // 1. Remove all projectiles
	    for (ProyectilVista pv : proyectiles) {
	        remove(pv);
	    }
	    proyectiles.clear();
	    
	    // 2. Remove all invaders
	    for (ImagenInvasor iv : invasoresVista) {
	        remove(iv);
	    }
	    invasoresVista.clear();
	    
	    // 3. Trigger a full panel redraw
	    revalidate(); 
	    repaint();
	}
	
	
	public void inicializarVistaDeInvasores() {
	    ControladorJuego controller = ControladorJuego.getInstancia();
	    List<ModeloInvasor> modelosInvasores = controller.getInvasoresActivos(); 

	    for (ModeloInvasor mi : modelosInvasores) {
	        ImagenInvasor newInvaderView = new ImagenInvasor(
	            mi.getId(), 
	            mi.getInvaderType()
	        );
	        
	        newInvaderView.setBounds(mi.getX(), mi.getY(), mi.getAncho(), mi.getAlto());
	        
	        invasoresVista.add(newInvaderView);
	        add(newInvaderView);
	    }
	    
	    revalidate(); 
	    repaint();
	}
	
	public void actualizarVista() {
	    ControladorJuego controller = ControladorJuego.getInstancia();
	    

	    proyectiles.removeIf(pv -> {
	        Proyectiles modelo = controller.getProyectilById(pv.getModeloId());
	        
	        final int PRO_WIDTH = 8;
	        final int PRO_HEIGHT = 15;
	        final int SHIP_WIDTH_OFFSET = 30; 
	        
	        if (modelo == null || !modelo.isActivo()) { 
	            remove(pv);
	            return true;
	        } else {
	        	
	        
	            pv.setBounds(
	                modelo.getX() + SHIP_WIDTH_OFFSET, 
	                modelo.getY(),                    
	                PRO_WIDTH, 
	                PRO_HEIGHT
	            );
	            return false;
	        }
	    });
	    
	    List<ModeloInvasor> modelosInvasores = controller.getInvasoresActivos(); 
        
        invasoresVista.removeIf(iv -> {
            ModeloInvasor modelo = controller.getInvasorById(iv.getModeloId());
            
            if (modelo == null || !modelo.isActivo()) {
                remove(iv); 
                return true; 
            } else {
                iv.setBounds(modelo.getX(), modelo.getY(), modelo.getAncho(), modelo.getAlto());
                return false;
            }
        });
        
       

	    repaint();
	    revalidate();
	}	
	
	
	
	private void drawFlashOverlay(Graphics g) {
	    if (ControladorJuego.getInstancia().isScreenFlashing()) {
	        Graphics2D g2d = (Graphics2D) g;
	        
	        g2d.setColor(new Color(255, 255, 255, 200)); 
	        
	        g2d.fillRect(0, 0, getWidth(), getHeight());
	    }
	    else {
	    	 Graphics2D g2d = (Graphics2D) g;
		        
		        g2d.setColor(new Color(0, 0, 0, 200)); 
		        
		        g2d.fillRect(0, 0, getWidth(), getHeight());
	    }
	}


}
