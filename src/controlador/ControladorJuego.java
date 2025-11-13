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
	
	private int creditos = 0;

	
	private int vidas;
	private int nivel;
	private String dificultad;
	private String estado;
	private List<Proyectiles> proyectiles = new ArrayList<>();
	private Oleada oleada;
	private List<MuroEnergia> muros = new ArrayList<>();
	
	private final int COOLDOWN_TICKS = 18; // Shoot once every 15 game ticks (~250ms at 66 FPS)
	private int ticksSinceLastShot = 0;
	
	private final int PAUSA_NIVEL_COMPLETADO = 120; // ticks
	private int pausaContador = 0;
	
	private PanelPrincipal vistaPanel; 
	
	private boolean screenFlash = false; 
	private final int FLASH_ON_TICKS = 15; //ticks
	private final int FLASH_OFF_TICKS = 20; //ticks

	private final double BASE_PIXELS_HORIZONTAL = 6.0;
	private final double BASE_PIXELS_CAIDA = 20.0;
	private final int PAUSA_VIDA_PERDIDA = 100;
	private boolean shipFlashState = false;
	
	private final int PLAYER_PROJ_OFFSET = 28;
	private final int ENEMY_PROJ_OFFSET = 18;
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
	    double difficultyFactor = getDifficultyFactor();
	    
	    double levelMultiplier = 1.0 + (this.nivel - 1) * 0.13;
	    
	    double finalSpeed = BASE_PIXELS_HORIZONTAL * difficultyFactor * levelMultiplier;
	    
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
		int SHIELD_WIDTH = 48;
	    int GAP = 80;
	    int Y_POS = 350;
	    int TOTAL_WIDTH = 4 * SHIELD_WIDTH + 3 * GAP;
	    int START_X = (800 - TOTAL_WIDTH) / 2;

	    for (int i = 0; i < 4; i++) {
	        int x = START_X + i * (SHIELD_WIDTH + GAP) + SHIELD_ALIGNMENT_OFFSET_X;
	        muros.add(new MuroEnergia(x, Y_POS));
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
	        
	    	int totalCycle = FLASH_ON_TICKS + FLASH_OFF_TICKS;
	        
	        int cycleTime = pausaContador % totalCycle;

	        if (cycleTime < FLASH_ON_TICKS) {
	            screenFlash = true;
	        } else {
	            screenFlash = false;
	        }
	        
	        if (vistaPanel != null) { 
	            vistaPanel.repaint();
	        }
	        
	    	if(pausaContador >= PAUSA_NIVEL_COMPLETADO) {
	    		gameTimer.stop();
	    		aumentarNivel();
	    	}
	    }
	    else if (this.estado != null && this.estado.equals("VIDA_PERDIDA")) {
	        pausaContador++;
	        
	        
	        if (pausaContador % 5 == 0) {
	            this.shipFlashState = !this.shipFlashState;
	        }
	        
	        if (vistaPanel != null) { 
	            vistaPanel.actualizarVista(); 
	        }

	        if (pausaContador >= PAUSA_VIDA_PERDIDA) {
	        	limpiarModeloProyectiles();
	        	vistaPanel.limpiarProyectiles();
	            this.shipFlashState = false;
	            this.estado = "JUGANDO";
	        }
	    }
	    else if (this.estado != null && this.estado.equals("GAME_OVER")) {

	        manejarFinDeJuego(this.puntaje); 
	        
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
	        JOptionPane.showMessageDialog(vistaPanel, 
	            "¡NECESITAS UN CRÉDITO PARA JUGAR!", 
	            "CRÉDITO REQUERIDO", JOptionPane.WARNING_MESSAGE);
	        return false;
	    }

	    if (!gameTimer.isRunning()) {
	        this.estado = "JUGANDO";
	        
	        if (vistaPanel != null) {
	            vistaPanel.inicializarVistaDeMuros(muros);
	            vistaPanel.inicializarVistaDeInvasores();
	        }
	        
	        gameTimer.start();
	        return true;
	    }
	    return false;
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
	        vistaPanel.inicializarVistaDeInvasores();
	        vistaPanel.inicializarVistaDeMuros(muros);
	    }
	    
	    // 5. Restart the timer
	    gameTimer.start();
	    this.estado = "JUGANDO";
	}
	
	private void verificarEstadoDelJuego() {
	    if (oleada.isEmpty()) {
	        this.estado = "PAUSA_POST_NIVEL"; 
	        pausaContador = 0; 
	        System.out.println("Nivel Completado"); 
	    }	
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
		                
		                if (vidas <= 0) {
		                    this.estado = "GAME_OVER";
		                }
		                
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
	

	public void añadirCredito() {
	    this.creditos++;; 
	}

	public boolean consumirCredito() {
	    if (this.creditos >= 1) {
	        this.creditos--; 
	        return true;
	    }
	    return false;
	}
	
	private void manejarFinDeJuego(int finalScore) {
	    
		Ranking ranking = new Ranking();
	    
	    String title = "GAME OVER";
	    String message = "¡TU PUNTAJE FINAL ES: " + finalScore + "!\n\nIngresa tu nombre para guardar tu récord:";
	    
	    RegistroJugador topPlayer = ranking.obtenerTop();
	    if (topPlayer != null) {
	        message += "\n(El record actual es: " + topPlayer.getPuntaje() + ")";
	    }

	    String playerName = JOptionPane.showInputDialog(
	        vistaPanel, 
	        message, 
	        title, 
	        JOptionPane.QUESTION_MESSAGE
	    );

	    if (playerName != null && !playerName.trim().isEmpty()) {
	        
	        boolean scoreWasSaved = ranking.registrarPuntaje(playerName.trim(), finalScore);
	        
	  
	        if (scoreWasSaved) {
	            JOptionPane.showMessageDialog(vistaPanel, 
	                "¡Puntaje de " + finalScore + " guardado para " + playerName.trim() + "!", 
	                "Guardado Exitoso", JOptionPane.INFORMATION_MESSAGE);
	        } else {
	             JOptionPane.showMessageDialog(vistaPanel, 
	                "Tu puntaje (" + finalScore + ") no superó tu récord anterior.", 
	                "Puntaje No Guardado", JOptionPane.INFORMATION_MESSAGE);
	        }
	    } else {
	        JOptionPane.showMessageDialog(vistaPanel, 
	            "Puntaje no guardado. Vuelve a intentarlo la próxima vez.", 
	            "Fin del Juego", JOptionPane.INFORMATION_MESSAGE);
	    }
	    
	    ControladorJuego.getInstancia().reiniciarJuegoTotal(); 
	}
	
	public void reiniciarJuegoTotal() {
	    this.puntaje = 0;
	    this.nivel = 1;
	    this.vidas = 3;
	    this.estado = null;
	    
	    gameTimer.stop();
	    this.proyectiles.clear();
	    this.muros.clear();
	    
	    if (vistaPanel != null) {
	        vistaPanel.limpiarVista();
	    }
	    
	    Ventana mainFrame = Ventana.getInstancia();
	    if (mainFrame != null) {
	        mainFrame.showMenuPanel();
	    }
	}

}
