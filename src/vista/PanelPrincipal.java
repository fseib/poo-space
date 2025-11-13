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
import modelo.MuroEnergia;
import modelo.Proyectiles;
import modelo.SeccionMuro;

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
	private List<ImagenMuro> murosVista = new ArrayList<>();
	  final int PRO_WIDTH = 8;
      final int PRO_HEIGHT = 15;
      
	@Override
	protected void paintComponent(Graphics g) {
	    super.paintComponent(g);
	    drawFlashOverlay(g);
	}
	
	
	public PanelPrincipal() {
		this.alto = 600;
		this.ancho = 800;
		setLayout(null);
		setPreferredSize(new Dimension(ancho,alto));
		imagenNave = new ImagenNave();
		add(imagenNave);
		int shipModelX = ControladorJuego.getInstancia().getModeloNave().getX();
		imagenNave.mover(shipModelX, 500);
		imagenNave.setFocusable(true);
	    ControladorJuego.getInstancia().setVista(this); 
		imagenNave.addKeyListener(new MiPropioKeyAdapter() {
			int x;
			
			@Override
			public void keyTyped(KeyEvent e) {
				System.out.println(e.getKeyChar());
				if(e.getKeyChar() == 'a')	{
					if(ControladorJuego.getInstancia().getEstado() == "JUGANDO") {
						x = ControladorJuego.getInstancia().moverNaveIzquierda();
					}
				}
					
				else
					if(e.getKeyChar() == 'd'){
						if(ControladorJuego.getInstancia().getEstado() == "JUGANDO") {
							x = ControladorJuego.getInstancia().moverNaveDerecha();
						}
					}				
				if(e.getKeyChar() == 'w') {
					
					int proyectilId =	ControladorJuego.getInstancia().dispararNave();
					
					
					if(proyectilId != -1) {
					     ProyectilVista pro = new ProyectilVista(proyectilId); 
				            
				            Proyectiles modeloInicial = ControladorJuego.getInstancia().getProyectilById(proyectilId);
				            final int PRO_WIDTH = 8;
				            final int PRO_HEIGHT = 15;
					        final int SHIP_WIDTH_OFFSET = 30; 


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
	
	public void inicializarVistaDeMuros(List<MuroEnergia> murosModel) {
	    ControladorJuego controller = ControladorJuego.getInstancia();
	    
	    // Assuming you have a list of MuroEnergia objects in the Controller
	    // Loop through each MuroEnergia
	    for (MuroEnergia muroModel : murosModel) {
	        // Loop through each SeccionMuro inside the manager
	        for (SeccionMuro seccionModel : muroModel.getSecciones()) {
	            ImagenMuro newMuroView = new ImagenMuro(seccionModel); // Link view to model
	            
	            // Position the view component
	            newMuroView.setBounds(seccionModel.getX(), seccionModel.getY(), 
	                                   seccionModel.getAncho(), seccionModel.getAlto());
	            
	            murosVista.add(newMuroView);
	            add(newMuroView);
	        }
	    }
	    revalidate(); 
	}
	
	public void limpiarVista() {
	    for (ProyectilVista pv : proyectiles) {
	        remove(pv);
	    }
	    proyectiles.clear();
	    
	    for (ImagenInvasor iv : invasoresVista) {
	        remove(iv);
	    }
	    invasoresVista.clear();
	    
	    for (ImagenMuro sm: murosVista) {
	    	remove(sm);
	    }
	    murosVista.clear();
	    
	    revalidate(); 
	    repaint();
	}
	
	public void limpiarProyectiles() {
	    for (ProyectilVista pv : proyectiles) {
	        remove(pv);
	    }
	    proyectiles.clear();
	    
	    revalidate(); 
	    repaint();
	}
	
	public ImagenNave getImagenNave() {
 		return this.imagenNave;
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
	    
	    
	    // NEW: Ship Flash Update
	    if (imagenNave != null) {
	        // Pass the Controller's boolean state to the View component
	        imagenNave.updateFlash(controller.isShipFlashing()); 
	        imagenNave.setVisible(true); // Ensure the ship is always visible (not blinking off)
	    }

	    proyectiles.removeIf(pv -> {
	        Proyectiles modelo = controller.getProyectilById(pv.getModeloId());
	        
	      
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
	    
	    List<Proyectiles> modeloShots = controller.getProyectiles(); // <-- You need this getter
	    
	    for (Proyectiles modelo : modeloShots) {
	        // Check if this Model projectile already has a View component
	        boolean viewExists = false;
	        for (ProyectilVista pv : proyectiles) {
	            if (pv.getModeloId() == modelo.getId()) {
	                viewExists = true;
	                break;
	            }
	        }
	        
	        // If the Model exists but the View component doesn't, create it!
	        if (!viewExists) {
	        	ProyectilVista newPro = new ProyectilVista(modelo.getId());
	            
	            int invaderWidth = 32;  // Assuming invader width
	            
	            int offsetX = modelo.esDelJugador() ? 30 : (invaderWidth / 2) - 4; // e.g., 15 - 4 = 11
	            newPro.setBounds(
	                modelo.getX() + offsetX,
	                modelo.getY(), // <-- CRITICAL: Start Y below the invader
	                PRO_WIDTH, 
	                PRO_HEIGHT
	            );

	            proyectiles.add(newPro);
	            add(newPro);
	        }
	    }
	    
        
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
        
        murosVista.removeIf(im -> {
            if (im.getModeloSeccion().estaDestruida()) {
                remove(im);
                return true;
            }
            // Force a redraw of the section to show new damage/color
            im.repaint(); 
            return false;
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
