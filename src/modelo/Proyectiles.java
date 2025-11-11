package modelo;

public class Proyectiles {
	private int posicionX;
	private int posicionY;
	private int alto;
	private int ancho;
	private int velocidad;;
	private boolean esDelJugador;
	private boolean activo;
	
	private final int id;
    private static int nextId = 0; 
	
	public Proyectiles(int posicionX, int posicionY, boolean esDelJugador, int velocidad) {
		this.id = nextId++; 
		this.posicionX = posicionX;
		this.esDelJugador = esDelJugador;
		this.velocidad = velocidad;
		this.ancho = 8;
		this.alto = 15;
		this.posicionY = posicionY;
		
		if(esDelJugador) {
			this.velocidad *= -1;
		}
		else {
			this.velocidad *= 0.9;
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

	public boolean esDelJugador() {
		return this.esDelJugador;
	}
	public boolean isActivo() {
		return activo;
	}
	
	public void desactivar() {
		this.activo = false;
	}
}
