package controlador;


import java.util.ArrayList;
import java.util.List;

import modelo.AreaDeJuego;
import modelo.ModeloNave;
import modelo.MuroEnergia;
import modelo.Oleada;
import modelo.Proyectiles;
import vista.ProyectilVista;

public class ControladorJuego {

	private static ControladorJuego instancia;
	
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
	
	
	public ControladorJuego() {
		areaJuego = new AreaDeJuego(600,800);
		modeloNave = new ModeloNave(400,300,10,50,50,areaJuego);
		puntaje = 0;
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
	
	public int getVelocidad() {
		return 1;
	}
	
	public int dispararNave() {
		
		Proyectiles nuevoProyectil = modeloNave.disparar();
		proyectiles.add(nuevoProyectil);
		
		return nuevoProyectil.getX();
	}
	

}
