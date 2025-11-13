package vista;

// Conceptual structure for vista/ImagenMuro.java

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JPanel;
import modelo.SeccionMuro; // Need to import the Model object

public class ImagenMuro extends JPanel {
    
    private final SeccionMuro modeloSeccion; // Link to the Model section
    private final int TILE_SIZE = 25; // Match your SeccionMuro.ancho

    public ImagenMuro(SeccionMuro modelo) {
        this.modeloSeccion = modelo;
        
        // Set fixed size based on the Model section's size
        setPreferredSize(new Dimension(modelo.getAncho(), modelo.getAlto()));
        setSize(modelo.getAncho(), modelo.getAlto());
        setOpaque(false); // CRITICAL: Makes the background transparent
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        if (modeloSeccion.estaDestruida()) {
            return; // Don't draw anything if the block is destroyed
        }
        
        Graphics2D g2d = (Graphics2D) g;
        
        // Use a color that signals integrity (e.g., Green/Yellow/Red)
        if (modeloSeccion.getIntegridad() > 0.66) {
            g2d.setColor(Color.GREEN); // High integrity
        } else if (modeloSeccion.getIntegridad() > 0.33) {
            g2d.setColor(Color.YELLOW); // Medium integrity
        } else {
            g2d.setColor(Color.RED);   // Low integrity
        }
        
        // --- VISUAL DAMAGE EFFECT ---
        // This is the simplest way to represent health. 
        // For a more realistic "bitten" effect, you would draw individual pixels here.
        
        int blockWidth = modeloSeccion.getAncho();
        int blockHeight = modeloSeccion.getAlto();
        
        // Draw the main, healthy block (a simple rectangle)
        g2d.fillRect(0, 0, blockWidth, blockHeight);
        
        // --- Simulate Missing Pixels (The "Sprite-less Damage") ---
        // We will randomly erase small rectangles based on the damage level (Integrity < 1.0)
        
        if (modeloSeccion.getIntegridad() < 1.0) {
            g2d.setColor(Color.BLACK); // Use the background color to 'erase' parts
            
            // Number of "holes" to draw is inverse to integrity (e.g., 50 damage sections)
            int totalDamageSections = (int)((1.0 - modeloSeccion.getIntegridad()) * 50); 
            
            for (int i = 0; i < totalDamageSections; i++) {
                // Randomly generate coordinates for a small 'hole'
                int holeX = (int)(Math.random() * (blockWidth - 2));
                int holeY = (int)(Math.random() * (blockHeight - 2));
                
                // Draw a small 2x2 or 3x3 black rectangle to simulate damage/missing pieces
                g2d.fillRect(holeX, holeY, 2, 2); 
            }
        }
    }
    
    public SeccionMuro getModeloSeccion () {
    	return this.modeloSeccion;
    }
}