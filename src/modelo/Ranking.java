package modelo;

import java.util.List;

public class Ranking {
	private List<RegistroJugador> registros;
	private String archivoRanking;
	
	public Ranking() {
		cargarDesdeArchivo();
	}
	
	public RegistroJugador obtenerTop() {
		return registros.getLast();
	}
	
	public void guardarEnArchivo() {
		//Guarda en un archivo (path archivoRanking) todos los registros
	}
	
	public void cargarDesdeArchivo() {
		//Carga desde un archivo (path archivoRanking) todos los registros a registros
	}
	
	public void registrarPuntaje(String nombre, int puntaje) {
		int indice = chequearJugador(nombre, puntaje);
		if(indice > -1) {
			if(registros.get(indice).getPuntaje() < puntaje) {
				registros.remove(indice);
				insertarNuevoRegistro(nombre, puntaje);
			}
		}
		else {
			insertarNuevoRegistro(nombre, puntaje);
		}
		
	}
	
	public void insertarNuevoRegistro(String nombre, int puntaje) {
		RegistroJugador nuevoRegistro = new RegistroJugador(nombre, puntaje);
		registros.add(nuevoRegistro);
	}
	
	public int chequearJugador(String nombre, int puntaje) {
		for (int i = 0; i < registros.size(); i++) {
			RegistroJugador jugador = registros.get(i);
	        
	        if (jugador.getNombre().equals(nombre)) {
	            return i;
	        }
	    }
	    

	    return -1;
	}
	
}
