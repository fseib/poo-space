package modelo;

public class Proyectiles {
	private int posicionX;
	private int posicionY;
	private int velocidad;;
	private boolean esDelJuegador;
	private boolean activo;
	
	public Proyectiles(int posicionX, boolean esDelJugador, int velocidad) {
		this.posicionX = posicionX;
		this.esDelJuegador = esDelJugador;
		this.velocidad = velocidad;
		
		if(esDelJugador) {
			posicionY = 0;
		}
		else {
			posicionY = 600;
		}
		
		this.activo = true;
	}
	
	public void actualizar() {
		if(esDelJuegador) {
			this.posicionY += velocidad;
		}
		else {
			this.posicionY -= velocidad;
		}
	}
	
	public int getX() {
		return posicionX;
	}
	
	public int getY() {
		return posicionY;
	}
	
	public void colisionCon(int proyectilX, int proyectilY, int objetoX, int objectoY) {
		
	}
}
