package vista;

import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

public class ImagenNave extends JLabel {
	
	private static final long serialVersionUID = 8700232532579393479L;
	private int alto;
	private int ancho;
	private ImageIcon originalSprite;
	private ImageIcon flashSprite;
	
	public ImagenNave() {
		ancho = 60;
		alto = 30;
        
        // 1. Load Original (Blue) Image and store it
		Image originalImage = new ImageIcon("player.png").getImage();
		Image imageScaled = originalImage.getScaledInstance(ancho, alto, Image.SCALE_SMOOTH);
		
		this.originalSprite = new ImageIcon(imageScaled); // Store the blue icon
        
        // 2. Load or Create Flash (White) Image and store it
        // Since you confirmed you have the white image, let's load it here:
        // *** You need a file named 'player_flash.png' or similar for this load to work ***
        Image flashImage = new ImageIcon("playerhit.png").getImage(); 
        Image flashScaled = flashImage.getScaledInstance(ancho, alto, Image.SCALE_SMOOTH);
        
        this.flashSprite = new ImageIcon(flashScaled); // Store the white icon
        
        // Set the default icon (blue)
		setIcon(this.originalSprite);
	}
	
	public void mover(int x, int y) {
		setBounds(x, y, ancho, alto);
	}
	
	public void updateFlash(boolean flashing) {
        if (flashing) {
            this.setIcon(flashSprite); // Set to white icon
        } else {
            this.setIcon(originalSprite); // Set to blue icon
        }
    }
}
