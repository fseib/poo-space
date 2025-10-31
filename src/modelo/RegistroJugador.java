package modelo;

import java.util.Date;

public class RegistroJugador {
	private String nombre;
	private int puntajeMaximo;
	private Date fecha;
	
	public RegistroJugador(String nombre, int puntajeMaximo) {
		this.nombre = nombre;
		this.puntajeMaximo = puntajeMaximo;
		this.fecha = new Date();
	}
	
	public String getNombre() {
		return this.nombre;
	}
	
	public int getPuntaje() {
		return this.puntajeMaximo;
	}
}
