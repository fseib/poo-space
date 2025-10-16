package controlador;

import modelo.AreaDeJuego;
import modelo.ModeloNave;

public class ControladorJuego {

	private static ControladorJuego instancia;
	
	private ModeloNave modeloNave;
	private AreaDeJuego areaJuego;
	private int puntos;
	private int vidas;
	private int nivel;
	private int dificultad;
	private String estado;
	
	public ControladorJuego() {
		areaJuego = new AreaDeJuego(600,800);
		modeloNave = new ModeloNave(400,300,10,50,50,areaJuego);
		puntos = 0;
		vidas = 3;
	}
	
	public static ControladorJuego getInstancia() {
		if (instancia == null)
			instancia = new ControladorJuego();
		return instancia;
	}
	
	public int moverNaveDerecha() {
		return modeloNave.moverDerecha();
	}
	
	public int moverNaveIzquierda() {
		return modeloNave.moverIzquierda();
	}
	
	public int dispararNave() {
//		return modeloNave.disparar();

		return 0;
	}
}
