package modelo;

import java.util.ArrayList;
import java.util.List;

import controlador.ControladorJuego;

public class Oleada {

	private List<ModeloInvasor> naves = new ArrayList<>(); // <--- Corrected initialization
	private String direccion;
	private int velociadadActual;
	private int limiteIzquierdo;
	private int limiteDerecho;
    
    private final int totalFilas = 5;     
    private final int totalColumnas = 10; 
    private final int separacion = 50;     
    private  int velocidadHorizontal;
    private  int velocidadCaida;     
    
    private int direccionActual = 1;    
    private int movimientoContador = 0; 
    private final int PASOS_POR_MOVIMIENTO = 30;
    private int[] fasesPorFila;             

    private AreaDeJuego areaDeJuego; 

    public Oleada(AreaDeJuego area, int horizontalSpeed, int dropSpeed) {
        this.areaDeJuego = area;
        this.fasesPorFila = new int[totalFilas];
                
        this.velocidadHorizontal = horizontalSpeed; // Set directly
        this.velocidadCaida = dropSpeed;           // Set directly
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
                    startX, startY, 1,  areaDeJuego, invaderType, r
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
                    (invader.getX() <= 10 && direccionActual == -1)) { // Check if within 10 pixels of edge
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
}