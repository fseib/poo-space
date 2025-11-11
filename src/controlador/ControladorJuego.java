package controlador;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Timer;

import modelo.AreaDeJuego;
import modelo.ModeloInvasor;
import modelo.ModeloNave;
import modelo.MuroEnergia;
import modelo.Oleada;
import modelo.Proyectiles;
import vista.PanelHUD;
import vista.PanelPrincipal;

public class ControladorJuego implements ActionListener {

	private static ControladorJuego instancia;
	private Timer gameTimer; 
    private final int DELAY = 15; // 15ms es ~66 FPS
    private PanelHUD vistaHUD;
    
	private ModeloNave modeloNave;
	private AreaDeJuego areaJuego;
	private int puntaje;
	private int vidas;
	private int nivel;
	private String dificultad;
	private String estado;
	private List<Proyectiles> proyectiles = new ArrayList<>();
	private Oleada oleada;
	private MuroEnergia muro;
	
	private final int COOLDOWN_TICKS = 18; // Shoot once every 15 game ticks (~250ms at 66 FPS)
	private int ticksSinceLastShot = 0;
	
	private final int PAUSA_NIVEL_COMPLETADO = 120; // e.g., 100 game ticks for a pause
	private int pausaContador = 0; // Counter for the pause
	
	private PanelPrincipal vistaPanel; 
	
	private boolean screenFlash = false; 
	private final int FLASH_ON_TICKS = 15; // NEW: Flash ON for 15 ticks (~225ms)
	private final int FLASH_OFF_TICKS = 20; // NEW: Flash OFF for 20 ticks (~300ms)

	private final double BASE_PIXELS_HORIZONTAL = 6.0; // Use doubles for accurate scaling
	private final double BASE_PIXELS_CAIDA = 20.0;
	private final int PAUSA_VIDA_PERDIDA = 100; // Total pause duration (1 second)
	private boolean shipFlashState = false; // NEW: Controls the ship's current icon (blue/white)
	public void setVista(PanelPrincipal panel) { 
	    this.vistaPanel = panel;
	}
	
	public boolean isScreenFlashing() {
	    return screenFlash;
	}
	
	public boolean isShipFlashing() {
	    return shipFlashState;
	}
	
	public ControladorJuego() {
		this.puntaje = 0;
		this.nivel = 1;
		this.vidas = 3;
		this.dificultad = "NORMAL";
		
		areaJuego = new AreaDeJuego(600,800);
		
		int SHIP_WIDTH = 60;
	    int SCREEN_CENTER_X = 400;
	    
		int MODEL_START_X = SCREEN_CENTER_X - (SHIP_WIDTH / 2); // 400 - 30 = 370
		modeloNave = new ModeloNave(MODEL_START_X,500,getVelocidad(),60,30,areaJuego);

		
		int hSpeed = getVelocidadHorizontal();
	    int dSpeed = getVelocidadCaida();
	    
	    
		this.oleada = new Oleada(areaJuego, hSpeed, dSpeed); 
		
		gameTimer = new Timer(DELAY, this);
	}
	
	public void setVistaHUD(PanelHUD hud) {
	    this.vistaHUD = hud;
	}

	private void actualizarHUD() {
	    if (vistaHUD != null) {
	        vistaHUD.actualizarHUD(puntaje, vidas, nivel); 
	    }
	}
	
	public static ControladorJuego getInstancia() {
	    if (instancia == null) {
	        instancia = new ControladorJuego(); 
	    }
	    return instancia;
	}
	
	public List<Proyectiles> getProyectiles() {
		return this.proyectiles;
	}
	
	public Proyectiles getProyectilById(int id) {
	    for (modelo.Proyectiles p : proyectiles) {
	        if (p.getId() == id) {
	            return p;
	        }
	    }
	    return null;
	}
	
	public ModeloNave getModeloNave() {
		return this.modeloNave;
	}
	@Override
	public void actionPerformed(ActionEvent e) {
	    if (this.estado != null && this.estado.equals("JUGANDO")) {
	    	ticksSinceLastShot++;	
	        // --- 1. MOVIMIENTO/STATE UPDATE ---
	        
	        actualizarProyectiles(); 
	        oleada.actualizarMovimiento();
	        
	        Proyectiles enemigoProyectil = oleada.intentarDispararEnemigo(getDifficultyFactor());
	        if (enemigoProyectil != null) {
	            proyectiles.add(enemigoProyectil); // Add the new enemy shot to the master list
	        }
	        
	        // --- 2. COLLISION CHECK ---
	        
	        verificarTodasLasColisiones();
	        verificarEstadoDelJuego();
	        actualizarHUD();
	        
	        // --- 3. REDIBUJADO ---

	        if (vistaPanel != null) { 
	            vistaPanel.actualizarVista();
	        }
	    
	    }
	    else if (this.estado != null && this.estado.equals("PAUSA_POST_NIVEL")) {
	    	pausaContador++;
	        
	        // Turn flash ON when the counter hits a multiple of 'interval'
	    	int totalCycle = FLASH_ON_TICKS + FLASH_OFF_TICKS;
	        
	        // Use the counter modulus the total cycle to determine the ON/OFF state
	        int cycleTime = pausaContador % totalCycle;

	        // Flash is ON for the first part of the cycle
	        if (cycleTime < FLASH_ON_TICKS) {
	            screenFlash = true;
	        } else {
	            screenFlash = false;
	        }
	        
	        if (vistaPanel != null) { 
	            vistaPanel.repaint(); // Call repaint to show the flash
	        }
	        
	    	if(pausaContador >= PAUSA_NIVEL_COMPLETADO) {
	    		gameTimer.stop(); // <-- STOP TIMER HERE, right before the expensive reset
	    		aumentarNivel();
	    	}
	    }
	    else if (this.estado != null && this.estado.equals("VIDA_PERDIDA")) {
	        pausaContador++;
	        
	        
	        if (pausaContador % 5 == 0) { // Toggle ON/OFF rapidly
	            this.shipFlashState = !this.shipFlashState;
	        }
	        
	        if (vistaPanel != null) { 
	            // We call the full update to ensure the ship component is redrawn immediately.
	            vistaPanel.actualizarVista(); 
	        }

	        if (pausaContador >= PAUSA_VIDA_PERDIDA) {
	        	limpiarModeloProyectiles();
	        	vistaPanel.limpiarProyectiles();
	            this.shipFlashState = false; // Ensure ship returns to normal state
	            this.estado = "JUGANDO"; // Resume the game loop
	        }
	    }
	}
	
	private void limpiarModeloProyectiles() {
	    this.proyectiles.clear();
	}
	
	public void actualizarProyectiles() {
	    for (Proyectiles p : proyectiles) {
	        p.actualizar(); 
	        	        if (p.getY() < 0 || p.getY() > 600) { 
	            p.desactivar();
	        }
	    }
	    

	    proyectiles.removeIf(p -> !p.isActivo()); 
	}
	
	
	
	public int moverNaveDerecha() {
		return modeloNave.moverDerecha();
	}
	
	public int moverNaveIzquierda() {
		return modeloNave.moverIzquierda();
	}
	
	public int getVelocidad() {
	return 8;
	}
	
	
	public int dispararNave() {
		if(ticksSinceLastShot >= COOLDOWN_TICKS) {

			Proyectiles nuevoProyectil = modeloNave.disparar();
			proyectiles.add(nuevoProyectil);
			
			ticksSinceLastShot = 0;
			
			return nuevoProyectil.getId();
		}
		
		return -1;
	}

    public List<ModeloInvasor> getInvasoresActivos() {
        if (oleada == null) return new ArrayList<>();
        return oleada.getInvasoresActivos();
    }

    public ModeloInvasor getInvasorById(int id) {
        if (oleada == null) return null;
        return oleada.getInvasorById(id);
    }
	
	public void iniciarJuego() {
		if (!gameTimer.isRunning()) {
	        this.estado = "JUGANDO";
	        
	        // 1. GUARANTEE ALL VISUALS ARE CREATED FIRST
	        if (vistaPanel != null) {
	            vistaPanel.inicializarVistaDeInvasores(); // BLOCKING CALL: Guarantees invaders/views are ready.
	        }
	        
	        // 2. ONLY START THE TIMER AFTER THE VIEW IS READY
	        gameTimer.start(); // Timer starts TICKING NOW that all models/views are built.
	    }
	}
	
	private void aumentarNivel() {
	    this.nivel++; 
	    this.puntaje += 200;

	    proyectiles.clear(); 
	    
	    int hSpeed = getVelocidadHorizontal();
	    int dSpeed = getVelocidadCaida();
	    
	    this.oleada = new Oleada(areaJuego, hSpeed, dSpeed); // Assuming Oleada now takes 'nivel'
	    

	    if (vistaPanel != null) {
	    	actualizarHUD();

	        vistaPanel.limpiarVista(); // We will add this method to remove ALL components
	        vistaPanel.inicializarVistaDeInvasores(); // Recreate the visual invader components
	    }
	    
	    // 5. Restart the timer
	    gameTimer.start();
	    this.estado = "JUGANDO";
	}
	
	private void verificarEstadoDelJuego() {
	    // 1. Check Win Condition
	    if (oleada.isEmpty()) {
	        this.estado = "PAUSA_POST_NIVEL"; // New state to hold the pause
	        pausaContador = 0; // Start the counter
	        System.out.println("Nivel Completado"); // Optional console check
	    }
	    // ... (Loss condition logic here later) ...
	}
	
	public int getVelocidadHorizontal() {
	    // 1. Calculate the difficulty and level factor
	    double difficultyFactor = getDifficultyFactor();
	    
	    // Level scaling: 10% increase per level beyond 1
	    double levelMultiplier = 1.0 + (this.nivel - 1) * 0.13;
	    
	    // 2. Apply all factors to the BASE speed
	    double finalSpeed = BASE_PIXELS_HORIZONTAL * difficultyFactor * levelMultiplier;
	    
	    // 3. Return the final, rounded pixel value (minimum 1)
	    return Math.max(1, (int) Math.round(finalSpeed));
	}

	public int getVelocidadCaida() {
	    // The drop speed should use the same factors
	    double difficultyFactor = getDifficultyFactor();
	    // ... (same difficulty factor calculation as above) ...
	    
	    double levelMultiplier = 1.0 + (this.nivel - 1) * 0.12;

	    double finalDrop = BASE_PIXELS_CAIDA * difficultyFactor * levelMultiplier;
	    
	    return Math.max(1, (int) Math.round(finalDrop));
	}
	
	private double getDifficultyFactor() {

		   String currentDificultad = (dificultad != null) ? dificultad : "NORMAL";	
		    if (currentDificultad != null) {
		        switch (currentDificultad.toUpperCase()) {
		            case "FACIL": 
		                return 0.5;
		            case "DIFICIL": 
		                return 1.4;
		                
		            default:
		                return 0.8;
		        }
		    }
		    
		    return 0.8;
	}
	
	public void verificarTodasLasColisiones() {
			
		checkColisionEntreProyectiles();
		
		checkColisionDeEnemigos();
		
		checkColisionDeJugador();
	    
	}
	
	private void checkColisionEntreProyectiles() {
	List<Proyectiles> shots = new ArrayList<>(proyectiles);
	    
	    for (int i = 0; i < shots.size(); i++) {
	        Proyectiles projA = shots.get(i);
	        
	        if (!projA.isActivo()) continue;
	        
	        for (int j = i + 1; j < shots.size(); j++) {
	            Proyectiles projB = shots.get(j);
	            
	            if (!projB.isActivo()) continue;
	            
	            // OPTIONAL: Check if projA is player and projB is enemy (or vice-versa)
	             if (projA.esDelJugador() == projB.esDelJugador()) continue; // Ignore same-side shots

	            // Use the same collision checker. APPLY OFFSETS CAREFULLY!
	            // Assuming player projectile uses +30 offset, enemy projectile uses 0 offset.
	            
	            int projAX = projA.getX() + (projA.esDelJugador() ? 30 : 0); 
	            int projBX = projB.getX() + (projB.esDelJugador() ? 30 : 0);

	            if (haColisionado(projAX, projA.getY(), projA.getAncho(), projA.getAlto(),
	                              projBX, projB.getY(), projB.getAncho(), projB.getAlto())) {
	                
	                // Collision detected: Deactivate both projectiles.
	                projA.desactivar();
	                projB.desactivar();
	                // Break inner loop and move to the next projA
	                break; 
	            }
	        }
	    }
	}
	
	private void checkColisionDeEnemigos() {
		
	    List<ModeloInvasor> invasoresActivos = oleada.getInvasoresActivos(); 
	    
	    for (Proyectiles projectile : new ArrayList<>(proyectiles)) {
	        if (projectile.isActivo()) { 
	            
	            for (ModeloInvasor invader : new ArrayList<>(invasoresActivos)) {
	                if (invader.isActivo()) {
	                    
	                    if (haColisionado(projectile.getX() + 30, projectile.getY(), projectile.getAncho(), projectile.getAlto(), invader.getX(), invader.getY(), invader.getAncho(), invader.getAlto())  && projectile.esDelJugador()) {	                        
	                        projectile.desactivar(); 
	                        invader.destruir();      
	                        	                        
	                        puntaje += invader.getScore(); 
	                        
	                        actualizarHUD(); 
	                        
	                        break; 
	                    }
	                }
	            }
	        }
	    }
	    
		
	}
	
	public void setDificultad(String newDificultad) {
	    this.dificultad = newDificultad;
	}
	
	
	private void checkColisionDeJugador() {
		   for (Proyectiles projectile : new ArrayList<>(proyectiles)) {
		        // Assuming you can identify enemy shots (e.g., if projectile.esDelJuegador() == false)
		        if (projectile.isActivo() && !projectile.esDelJugador() ) { 
		            
		            // 1. Check vs. Player's Ship
		            if (haColisionado(projectile.getX() + 30, projectile.getY(), projectile.getAncho(), projectile.getAlto(),
		                              modeloNave.getX(), modeloNave.getY(), modeloNave.getAncho(), modeloNave.getAlto())) {
		                
		                projectile.desactivar(); // Destroy the enemy projectile
		                vidas--;                 // Player takes damage
		                
		                // You must update the ship's state (e.g., flash, temporary invulnerability)
		                // modeloNave.hit();
		                
		                actualizarHUD();
		                this.estado = "VIDA_PERDIDA"; // <-- NEW STATE
		                pausaContador = 0;
		                
		                // Check if the game is over
		                if (vidas <= 0) {
		                    this.estado = "GAME_OVER";
		                }
		                
		                // Later: Add logic to check if vidas <= 0 for GAME_OVER state
		            }
		            
		            // 2. Check vs. Shields (MuroEnergia)
		            /*
		            if (muro != null && muro.checkCollision(projectile)) {
		                projectile.desactivar();
		            }
		            */
		        }
		    }
	}
	
	private boolean haColisionado(int x1, int y1, int w1, int h1, 
            int x2, int y2, int w2, int h2) {

			return x1 < x2 + w2 && 
			x1 + w1 > x2 && 
			y1 < y2 + h2 && 
			y1 + h1 > y2;   
}

}
