package modelo;

public class SeccionMuro {
    
    // --- Model State ---
    private double integridad = 1.0; // 1.0 = 100% integrity
    private final int coordenadaX;
    private final int coordenadaY;
    private final int ancho = 20; // Using standard 8-bit shield block size for example
    private final int alto = 12;
    private final int id;
    private static int nextId = 0;

    // --- Damage Constants (Model Rules) ---
    private static final double DAMAGE_JUGADOR = 0.10; // 10% damage (10 shots to destroy)
    private static final double DAMAGE_ENEMIGO = 0.05; // 5% damage (20 shots to destroy)

    public SeccionMuro(int x, int y) {
        this.id = nextId++;
        this.coordenadaX = x;
        this.coordenadaY = y;
    }

    /**
     * Applies damage to the section based on who fired the projectile.
     * @param esJugador True if the projectile was fired by the player.
     */
    public void recibirGolpe(boolean esJugador) {
        if (estaDestruida()) {
            return;
        }

        if (esJugador) {
            this.integridad -= DAMAGE_JUGADOR;
        } else {
            this.integridad -= DAMAGE_ENEMIGO;
        }
        
        // Ensure integrity doesn't fall below zero
        if (this.integridad < 0) {
            this.integridad = 0;
        }
    }
    
    // --- Getters for Controller/View ---
    public boolean estaDestruida() {
        return integridad <= 0;
    }
    
    public double getIntegridad() {
        return integridad;
    }
    
    public int getX() { return coordenadaX; }
    public int getY() { return coordenadaY; }
    public int getAncho() { return ancho; }
    public int getAlto() { return alto; }
    public int getId() { return id; }
}