package vista;


import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JPanel;
import modelo.SeccionMuro;

public class ImagenMuro extends JPanel {
    
    private final SeccionMuro modeloSeccion;
    private final int TILE_SIZE = 25; // Match your SeccionMuro.ancho

    public ImagenMuro(SeccionMuro modelo) {
        this.modeloSeccion = modelo;
        
        setPreferredSize(new Dimension(modelo.getAncho(), modelo.getAlto()));
        setSize(modelo.getAncho(), modelo.getAlto());
        setOpaque(false); 
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        if (modeloSeccion.estaDestruida()) {
            return; 
        }
        
        Graphics2D g2d = (Graphics2D) g;
        
        if (modeloSeccion.getIntegridad() > 0.66) {
            g2d.setColor(Color.GREEN);
        } else if (modeloSeccion.getIntegridad() > 0.33) {
            g2d.setColor(Color.YELLOW);
        } else {
            g2d.setColor(Color.RED);  
        }
        
        
        int blockWidth = modeloSeccion.getAncho();
        int blockHeight = modeloSeccion.getAlto();
        
        g2d.fillRect(0, 0, blockWidth, blockHeight);

        if (modeloSeccion.getIntegridad() < 1.0) {
            g2d.setColor(Color.BLACK);

            int totalDamageSections = (int)((1.0 - modeloSeccion.getIntegridad()) * 50); 
            
            for (int i = 0; i < totalDamageSections; i++) {
                int holeX = (int)(Math.random() * (blockWidth - 2));
                int holeY = (int)(Math.random() * (blockHeight - 2));
                
                g2d.fillRect(holeX, holeY, 2, 2); 
            }
        }
    }
    
    public SeccionMuro getModeloSeccion () {
    	return this.modeloSeccion;
    }
}