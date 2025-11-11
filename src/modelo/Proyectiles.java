package modelo;

public class Proyectiles {
	private int posicionX;
	private int posicionY;
	private int alto;
	private int ancho;
	private int velocidad;;
	private boolean esDelJuegador;
	private boolean activo;
	
	private final int id;
    private static int nextId = 0; 
	
	public Proyectiles(int posicionX, boolean esDelJugador, int velocidad) {
		this.id = nextId++; 
		this.posicionX = posicionX;
		this.esDelJuegador = esDelJugador;
		this.velocidad = velocidad;
		this.ancho = 8;
		this.alto = 15;
		
		if(esDelJugador) {
			posicionY = 500 - 15;
			this.velocidad *= -1;
		}
		else {
			posicionY = 600;
		}
		
		this.activo = true;
	}
	
	public void actualizar() {
	    this.posicionY += velocidad;
	}
	
	public int getId() {
        return id;
    }
	
	public int getX() {
		return posicionX;
	}
	
	public int getY() {
		return posicionY;
	}
	
	public int getAlto() {
		return this.alto;
	}
	
	public int getAncho() {
		return this.ancho;
	}
	public void colisionCon(int proyectilX, int proyectilY, int objetoX, int objectoY) {
		
	}
	
	public boolean isActivo() {
		return activo;
	}
	
	public void desactivar() {
		this.activo = false;
	}
}
