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
        
		Image originalImage = new ImageIcon("player.png").getImage();
		Image imageScaled = originalImage.getScaledInstance(ancho, alto, Image.SCALE_SMOOTH);
		
		this.originalSprite = new ImageIcon(imageScaled);
		
        Image flashImage = new ImageIcon("playerhit.png").getImage(); 
        Image flashScaled = flashImage.getScaledInstance(ancho, alto, Image.SCALE_SMOOTH);
        
        this.flashSprite = new ImageIcon(flashScaled);

        
		setIcon(this.originalSprite);
	}
	
	public void mover(int x, int y) {
		setBounds(x, y, ancho, alto);
	}
	
	public void updateFlash(boolean flashing) {
        if (flashing) {
            this.setIcon(flashSprite);
        } else {
            this.setIcon(originalSprite);
        }
    }
}
