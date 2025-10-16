package vista;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public abstract class MiPropioKeyAdapter implements KeyListener {
	
	@Override
	public abstract void keyTyped(KeyEvent e);
	
	@Override
	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub
	}
	
	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
	}
}
