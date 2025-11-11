package vista;

import java.awt.Dimension;
import java.awt.Image;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

public class ImagenInvasor extends JLabel {

    private static final long serialVersionUID = 1L; 
    private final int modeloId;
    private final int invaderType;
    private final int ancho; 
    private final int alto;

    public ImagenInvasor(int modeloId, int invaderType) { 
        this.modeloId = modeloId;
        this.invaderType = invaderType;
        this.ancho = 40; 
        this.alto = 32;  
        
        loadSprite(invaderType); 
        
        setPreferredSize(new Dimension(ancho, alto)); 
        setSize(ancho, alto); 
    }

    private void loadSprite(int type) {
        String imagePath = "";
        switch (type) {
            case 0: imagePath = "red.png"; break;    
            case 1: imagePath = "yellow.png"; break; 
            case 2: imagePath = "green.png"; break;  
            default: imagePath = "red.png"; break; 
        }
        
        try {
            Image imagen = new ImageIcon(imagePath).getImage();
            Image imagenAEscala = imagen.getScaledInstance(ancho, alto, Image.SCALE_SMOOTH);
            ImageIcon icono = new ImageIcon(imagenAEscala);
            setIcon(icono);
        } catch (Exception e) {
            System.err.println("Error loading invader image: " + imagePath + " - " + e.getMessage());
        }
    }
    
    public int getModeloId() { return modeloId; }
    public int getAncho() { return ancho; }
    public int getAlto() { return alto; }
}