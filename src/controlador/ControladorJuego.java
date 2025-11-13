package controlador;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.Timer;

import modelo.AreaDeJuego;
import modelo.ModeloInvasor;
import modelo.ModeloNave;
import modelo.MuroEnergia;
import modelo.Oleada;
import modelo.Proyectiles;
import modelo.Ranking;
import modelo.RegistroJugador;
import modelo.SeccionMuro;
import vista.PanelHUD;
import vista.PanelPrincipal;
import vista.Ventana;

public class ControladorJuego implements ActionListener {

	private static ControladorJuego instancia;
	private Timer gameTimer; 
    private final int DELAY = 15; // 15ms es ~66 FPS
    private PanelHUD vistaHUD;
    
	private ModeloNave modeloNave;
	private AreaDeJuego areaJuego;
	
	private int puntaje;
	private int puntajeParaNuevaVida = 0;
	
	private int creditos = 0; // Number of available credits

	
	private int vidas;
	private int nivel;
	private String dificultad;
	private String estado;
	private List<Proyectiles> proyectiles = new ArrayList<>();
	private Oleada oleada;
	private List<MuroEnergia> muros = new ArrayList<>();
	
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
	
	private final int PLAYER_PROJ_OFFSET = 28; // NEW
	private final int ENEMY_PROJ_OFFSET = 18;  // NEW (Assuming Invader W=40)
	private final int SHIELD_ALIGNMENT_OFFSET_X = 15;
	
	public ControladorJuego() {
		this.puntaje = 0;
		this.nivel = 1;
		this.vidas = 3;
		this.dificultad = "NORMAL";

		areaJuego = new AreaDeJuego(600,800);
		
		crearMuros();
		
		int SHIP_WIDTH = 60;
	    int SCREEN_CENTER_X = 400;
	    
		int MODEL_START_X = SCREEN_CENTER_X - (SHIP_WIDTH / 2);
		modeloNave = new ModeloNave(MODEL_START_X,500,getVelocidad(),60,30,areaJuego);

		
		int hSpeed = getVelocidadHorizontal();
	    int dSpeed = getVelocidadCaida();
	    
	    
		this.oleada = new Oleada(areaJuego, hSpeed, dSpeed); 
		
		gameTimer = new Timer(DELAY, this);
	}
	
	
	public void setVista(PanelPrincipal panel) { 
	    this.vistaPanel = panel;
	}
	
	public void setVistaHUD(PanelHUD hud) {
	    this.vistaHUD = hud;
	}
	
	public void setDificultad(String newDificultad) {
	    this.dificultad = newDificultad;
	}
	
	public List<Proyectiles> getProyectiles() {
		return this.proyectiles;
	}
	
	public ModeloNave getModeloNave() {
		return this.modeloNave;
	}
	
	public String getEstado() {
		return this.estado;
	}
	
	public int getCreditos() {
		return this.creditos;
	}
	
	public boolean isScreenFlashing() {
	    return screenFlash;
	}
	
	public boolean isShipFlashing() {
	    return shipFlashState;
	}
	
	
	public static ControladorJuego getInstancia() {
	    if (instancia == null) {
	        instancia = new ControladorJuego(); 
	    }
	    return instancia;
	}
	
	public Proyectiles getProyectilById(int id) {
	    for (modelo.Proyectiles p : proyectiles) {
	        if (p.getId() == id) {
	            return p;
	        }
	    }
	    return null;
	}
	
	public int getVelocidad() {
		return 8;
	}
	
    public List<ModeloInvasor> getInvasoresActivos() {
        if (oleada == null) return new ArrayList<>();
        return oleada.getInvasoresActivos();
    }

    public ModeloInvasor getInvasorById(int id) {
        if (oleada == null) return null;
        return oleada.getInvasorById(id);
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
	    double difficultyFactor = getDifficultyFactor();
	    
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
	
	private void crearMuros() {
		int SHIELD_WIDTH = 48; // Assuming 3 sections wide * 16px/section
	    int GAP = 80;
	    int Y_POS = 350;
	    int TOTAL_WIDTH = 4 * SHIELD_WIDTH + 3 * GAP;
	    int START_X = (800 - TOTAL_WIDTH) / 2; // Center the group in the 800-wide screen

	    for (int i = 0; i < 4; i++) {
	        int x = START_X + i * (SHIELD_WIDTH + GAP) + SHIELD_ALIGNMENT_OFFSET_X;
	        muros.add(new MuroEnergia(x, Y_POS)); // MuroEnergia constructor builds the sections
	    }
	}

	private void actualizarHUD() {
	    if (vistaHUD != null) {
	        vistaHUD.actualizarHUD(puntaje, vidas, nivel); 
	    }
	}
	
	
	@Override
	public void actionPerformed(ActionEvent e) {
	    if (this.estado != null && this.estado.equals("JUGANDO")) {
	    	ticksSinceLastShot++;	
	    	if(puntajeParaNuevaVida > 499) {
	    		this.vidas++;
	    		this.puntajeParaNuevaVida = 0;
	    	}
	        // --- 1. MOVIMIENTO/STATE UPDATE ---
	        
	        actualizarProyectiles(); 
	        oleada.actualizarMovimiento();
	        
	        Proyectiles enemigoProyectil = oleada.intentarDispararEnemigo(getDifficultyFactor());
	        if (enemigoProyectil != null) {
	            proyectiles.add(enemigoProyectil);
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
	    else if (this.estado != null && this.estado.equals("GAME_OVER")) {
	        
	        // This block runs once per tick until the state is reset, 
	        // so we must prevent running the heavy logic multiple times.
	        
	        // CRITICAL: Call the final score management sequence
	        manejarFinDeJuego(this.puntaje); 
	        
	        // NOTE: The manejarFinDeJuego() method should handle the final view switch 
	        // (i.e., calling reiniciarJuegoTotal()) which resets the state and returns to the menu.
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
	
	
	
	public int dispararNave() {
		if(ticksSinceLastShot >= COOLDOWN_TICKS) {

			Proyectiles nuevoProyectil = modeloNave.disparar();
			proyectiles.add(nuevoProyectil);
			
			ticksSinceLastShot = 0;
			
			return nuevoProyectil.getId();
		}
		
		return -1;
	}


	
	public boolean iniciarJuego() {
		if (!consumirCredito()) {
	        // BLOCK: Show warning message and DO NOT start the game.
	        JOptionPane.showMessageDialog(vistaPanel, 
	            "¡NECESITAS UN CRÉDITO PARA JUGAR!", 
	            "CRÉDITO REQUERIDO", JOptionPane.WARNING_MESSAGE);
	        return false; // Signal failure to the View
	    }

	    // 2. Proceed with successful start logic (Only runs if credit consumed)
	    if (!gameTimer.isRunning()) {
	        this.estado = "JUGANDO";
	        
	        if (vistaPanel != null) {
	            vistaPanel.inicializarVistaDeMuros(muros);
	            vistaPanel.inicializarVistaDeInvasores();
	        }
	        
	        gameTimer.start();
	        return true; // Signal success
	    }
	    return false; // Already running or other minor failure
	}
	
	private void aumentarNivel() {
	    this.nivel++; 
	    this.puntaje += 200;
	    this.puntajeParaNuevaVida += 200;
	    proyectiles.clear(); 
	    muros.clear();
	    
	    int hSpeed = getVelocidadHorizontal();
	    int dSpeed = getVelocidadCaida();
	    
	    this.oleada = new Oleada(areaJuego, hSpeed, dSpeed);
	    crearMuros();

	    if (vistaPanel != null) {
	    	actualizarHUD();

	        vistaPanel.limpiarVista();
	        vistaPanel.inicializarVistaDeInvasores(); // Recreate the visual invader components
	        vistaPanel.inicializarVistaDeMuros(muros);
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
	

	
	public void verificarTodasLasColisiones() {
			
		checkColisionEntreProyectiles();
		
		checkColisionDeEnemigos();
		
		checkColisionDeJugador();
	    
		checkColisionConMuros();
	}
	
	private void checkColisionConMuros() {
		
	    for (Proyectiles projectile : new ArrayList<>(proyectiles)) {
	        if (projectile.isActivo()) {
	            

	            int projOffsetX = projectile.esDelJugador() ? PLAYER_PROJ_OFFSET : ENEMY_PROJ_OFFSET; 
	            int projX = projectile.getX() + projOffsetX;
	            
	            for (MuroEnergia muro : muros) { 
	                for (SeccionMuro seccion : muro.getSecciones()) { 
	                    
	                    if (!seccion.estaDestruida()) {
	                        
	                        if (haColisionado(projX, projectile.getY(), projectile.getAncho(), projectile.getAlto(), 
	                            seccion.getX(), seccion.getY(), seccion.getAncho(), seccion.getAlto())) 
	                        {
	                            seccion.recibirGolpe(projectile.esDelJugador()); 
	                            projectile.desactivar();
	                            break; 
	                        }
	                    }
	                }
	                if (!projectile.isActivo()) break; 
	            }
	        }
	    }
	}
	
	private void checkColisionEntreProyectiles() {
	List<Proyectiles> shots = new ArrayList<>(proyectiles);
	    
	    for (int i = 0; i < shots.size(); i++) {
	        Proyectiles projA = shots.get(i);
	        
	        if (!projA.isActivo()) continue;
	        
	        for (int j = i + 1; j < shots.size(); j++) {
	            Proyectiles projB = shots.get(j);
	            
	            if (!projB.isActivo()) continue;
	            
	             if (projA.esDelJugador() == projB.esDelJugador()) continue; 
	            
	            int projAX = projA.getX() + (projA.esDelJugador() ? 30 : 0); 
	            int projBX = projB.getX() + (projB.esDelJugador() ? 30 : 0);

	            if (haColisionado(projAX, projA.getY(), projA.getAncho(), projA.getAlto(),
	                              projBX, projB.getY(), projB.getAncho(), projB.getAlto())) {
	                

	                projA.desactivar();
	                projB.desactivar();
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
	                        puntajeParaNuevaVida += invader.getScore();
	                        
	                        actualizarHUD(); 
	                        
	                        break; 
	                    }
	                }
	            }
	        }
	    }
	    
		
	}
	

	
	private void checkColisionDeJugador() {
		   for (Proyectiles projectile : new ArrayList<>(proyectiles)) {
		        if (projectile.isActivo() && !projectile.esDelJugador() ) { 
		            
		            if (haColisionado(projectile.getX() + 30, projectile.getY(), projectile.getAncho(), projectile.getAlto(),
		                              modeloNave.getX(), modeloNave.getY(), modeloNave.getAncho(), modeloNave.getAlto())) {
		                
		                projectile.desactivar(); 
		                vidas--;
		                
		                
		                actualizarHUD();
		                this.estado = "VIDA_PERDIDA";
		                pausaContador = 0;
		                
		                // Check if the game is over
		                if (vidas <= 0) {
		                    this.estado = "GAME_OVER";
		                }
		                
		                // Later: Add logic to check if vidas <= 0 for GAME_OVER state
		            }
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
	

	// Add a public method for the "Añadir un credito" button
	public void añadirCredito() {
	    this.creditos++;
	    // Optional: Update the HUD immediately if it displays credits
	    // actualizarHUD(); 
	}

	// Add a method to check and consume a credit before starting a game
	public boolean consumirCredito() {
	    if (this.creditos >= 1) {
	        this.creditos--;
	        // Optional: Update the HUD after consumption
	        // actualizarHUD(); 
	        return true;
	    }
	    return false;
	}
	
	private void manejarFinDeJuego(int finalScore) {
	    
		Ranking ranking = new Ranking();
	    
	    // Check if the score is high enough to be recorded
	    // Note: The Ranking model handles the complexity of "new high score" or "player's personal best."
	    // We will assume any score is eligible for saving, and the Ranking class handles the comparison.

	    String title = "GAME OVER";
	    String message = "¡TU PUNTAJE FINAL ES: " + finalScore + "!\n\nIngresa tu nombre para guardar tu récord:";
	    
	    // 1. Check if a top score exists to display the current record.
	    RegistroJugador topPlayer = ranking.obtenerTop();
	    if (topPlayer != null) {
	        message += "\n(El record actual es: " + topPlayer.getPuntaje() + ")";
	    }

	    // 2. Prompt the player for their name using a Swing dialog (View interaction)
	    String playerName = JOptionPane.showInputDialog(
	        vistaPanel, 
	        message, 
	        title, 
	        JOptionPane.QUESTION_MESSAGE
	    );

	    // 3. Process the input and save
	    if (playerName != null && !playerName.trim().isEmpty()) {
	        
	        // CRITICAL FIX: Capture the return value from the model!
	        boolean scoreWasSaved = ranking.registrarPuntaje(playerName.trim(), finalScore);
	        
	        // Save the changes to the file immediately (only if saved, but redundant if registrarPuntaje calls it)
	        // ranking.guardarEnArchivo(); // We assume registrarPuntaje handles the save
	        
	        if (scoreWasSaved) {
	            JOptionPane.showMessageDialog(vistaPanel, 
	                "¡Puntaje de " + finalScore + " guardado para " + playerName.trim() + "!", 
	                "Guardado Exitoso", JOptionPane.INFORMATION_MESSAGE);
	        } else {
	            // New message for when the score is lower than their existing best
	             JOptionPane.showMessageDialog(vistaPanel, 
	                "Tu puntaje (" + finalScore + ") no superó tu récord anterior.", 
	                "Puntaje No Guardado", JOptionPane.INFORMATION_MESSAGE);
	        }
	    } else {
	        JOptionPane.showMessageDialog(vistaPanel, 
	            "Puntaje no guardado. Vuelve a intentarlo la próxima vez.", 
	            "Fin del Juego", JOptionPane.INFORMATION_MESSAGE);
	    }
	    
	    // 4. Transition back to the Main Menu (Required for a clean reset)
	    // We need the Ventana instance to switch the view.
	    ControladorJuego.getInstancia().reiniciarJuegoTotal(); 
	}
	
	// Inside ControladorJuego.java (NEW method to facilitate app restart/menu return)
	public void reiniciarJuegoTotal() {
		// 1. Reset all critical Model fields
	    this.puntaje = 0;
	    this.nivel = 1;
	    this.vidas = 3;
	    this.estado = null;
	    
	    // 2. Stop the timer and clear models
	    gameTimer.stop();
	    this.proyectiles.clear();
	    this.muros.clear();
	    
	    // 3. Clear the game visuals
	    if (vistaPanel != null) {
	        vistaPanel.limpiarVista();
	    }
	    
	    // 4. CRITICAL: Trigger the return to the Main Menu (View switch)
	    Ventana mainFrame = Ventana.getInstancia();
	    if (mainFrame != null) {
	        mainFrame.showMenuPanel();
	    }
	}

}
