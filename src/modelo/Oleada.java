package modelo;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Oleada {

	private List<ModeloInvasor> naves = new ArrayList<>();
	
//    private final int totalFilas = 5;     
//    private final int totalColumnas = 10; 
//    private final int separacion = 50;     
    
    private final int totalFilas = 3;     
    private final int totalColumnas = 5; 
    private final int separacion = 70;    
    
    
    private  int velocidadHorizontal;
    private  int velocidadCaida;     
    
    private int direccionActual = 1;    
    private int movimientoContador = 0; 
    private final int PASOS_POR_MOVIMIENTO = 30;
    private int[] fasesPorFila;             

    private AreaDeJuego areaDeJuego; 
    
    private final Random random = new Random();

    public Oleada(AreaDeJuego area, int horizontalSpeed, int dropSpeed) {
        this.areaDeJuego = area;
        this.fasesPorFila = new int[totalFilas];
                
        this.velocidadHorizontal = horizontalSpeed;
        this.velocidadCaida = dropSpeed;
        inicializarOleada();
    }

    private void inicializarOleada() {
        for(int r = 0; r < totalFilas; r++) {
            fasesPorFila[r] = r * 5; 
        }

        for (int r = 0; r < totalFilas; r++) {
            int invaderType; 
            
            if (r == 0) { 
                invaderType = 0; 
            } else if (r == 1 || r == 2) { 
                invaderType = 1; 
            } else { 
                invaderType = 2; 
            }
            
            for (int c = 0; c < totalColumnas; c++) {
                int startX = 50 + c * separacion;
                int startY = 50 + r * separacion;
                
                ModeloInvasor invasor = new ModeloInvasor(
                    startX, startY, invaderType, r
                );
                naves.add(invasor);
            }
        }
    }
    
    public void actualizarMovimiento() {
        if (naves.isEmpty()) return;

        boolean hitEdge = false;
        
        for (ModeloInvasor invader : naves) {
            if (invader.isActivo()) {
                if ((invader.getX() + invader.getAncho() >= areaDeJuego.getAncho() - 10 && direccionActual == 1) ||
                    (invader.getX() <= 10 && direccionActual == -1)) {
                    hitEdge = true;
                    break; 
                }
            }
        }

        if (hitEdge) {
            direccionActual *= -1;
            for (ModeloInvasor invader : naves) {
                if (invader.isActivo()) {
                    invader.mover(0, velocidadCaida);
                }
            }
        } else {
            for (ModeloInvasor invader : naves) {
                 if (invader.isActivo()) {
                    int rowIndex = invader.getRow();
                    if ((movimientoContador + fasesPorFila[rowIndex]) % PASOS_POR_MOVIMIENTO == 0) {
                        invader.mover(velocidadHorizontal * direccionActual, 0); 
                    }
                }
            }
        }
        
        movimientoContador = (movimientoContador + 1) % PASOS_POR_MOVIMIENTO;
        
        naves.removeIf(inv -> !inv.isActivo());
    }

    public List<ModeloInvasor> getInvasoresActivos() {
        return naves; 
    }
    
    public boolean isEmpty() {
    	return naves.isEmpty();
    }
    
    public ModeloInvasor getInvasorById(int id) {
        for (ModeloInvasor inv : naves) {
            if (inv.getId() == id) return inv;
        }
        return null;
    }
    
    public Proyectiles intentarDispararEnemigo(double difficultyFactor) {
        if (naves.isEmpty()) {
            return null;
        }
        
        // --- Step 1: GLOBAL CHANCE CHECK (Only runs ONCE per tick) ---
        
        // We set a base chance that *any* invader will shoot this frame.
        // 0.005 (0.5%) means a shot is fired on average once every 200 ticks (approx 3 seconds).
        final double GLOBAL_BASE_CHANCE = 0.020; 
        
        // Total Chance = Base Chance * Difficulty Factor
        double totalFiringChance = GLOBAL_BASE_CHANCE * difficultyFactor;
        totalFiringChance = Math.min(totalFiringChance, 1.0); // Cap at 100%
        
        // Check if the game should fire a shot this frame.
        // NOTE: This reverses your original > check to use standard < probability checking.
        // If random value is less than the total chance, the shot is successful.
        if (random.nextDouble() < totalFiringChance) { 
            
            // --- Step 2: FIND SHOOTER (Only runs if the global chance succeeded) ---
            
            ModeloInvasor shooter = null;
            int maxAttempts = 10;
            
            // Loop to find an active invader to be the shooter
            for (int i = 0; i < maxAttempts; i++) {
                // Simple approach: Choose a random invader
                int index = random.nextInt(naves.size());
                ModeloInvasor candidate = naves.get(index);
                
                if (candidate.isActivo()) {
                    shooter = candidate;
                    break;
                }
            }
            
            if (shooter != null) {
                // Step 3: Create and return the projectile Model object
                int velocidadEnemiga = 5; 
                
                // Calculate precise starting position (centered on invader's body)
                // Note: I added a small offset to the X coordinate (e.g., -4) to center the 8-wide bullet (8/2=4) 
                // inside the invader's body, assuming the Model X is the invader's corner.
                int startX = shooter.getX() + (shooter.getAncho() / 2) - 4; 
                int startY = shooter.getY() + shooter.getAlto();     
                            
                Proyectiles nuevoProyectil = new Proyectiles(
                    startX, 
                    startY,
                    false, // esDelJugador = false
                    velocidadEnemiga
                );
                return nuevoProyectil;
            }
        }
        
        return null; // No shot fired this tick
    }
}