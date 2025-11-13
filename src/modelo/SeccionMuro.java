package modelo;

public class SeccionMuro {
    
    private double integridad = 1.0;
    private final int coordenadaX;
    private final int coordenadaY;
    private final int ancho = 20; 
    private final int alto = 12;
    private final int id;
    private static int nextId = 0;

    private static final double DAMAGE_JUGADOR = 0.10; // 10% daño
    private static final double DAMAGE_ENEMIGO = 0.05; // 5% daño
    
    public SeccionMuro(int x, int y) {
        this.id = nextId++;
        this.coordenadaX = x;
        this.coordenadaY = y;
    }

    public boolean estaDestruida() {
        return integridad <= 0;
    }
    
    public double getIntegridad() {
        return integridad;
    }
    
    public int getX() { 
    	return coordenadaX; 
    }
    
    public int getY() { 
    	return coordenadaY; 
    }
    
    public int getAncho() {
    	return ancho; 
    }
    
    public int getAlto() { 
    	return alto; 
    }
    
    public int getId() { 
    	return id; 
    }
    
    public void recibirGolpe(boolean esJugador) {
        if (estaDestruida()) {
            return;
        }

        if (esJugador) {
            this.integridad -= DAMAGE_JUGADOR;
        } else {
            this.integridad -= DAMAGE_ENEMIGO;
        }
        
        if (this.integridad < 0) {
            this.integridad = 0;
        }
    }
    
  
}