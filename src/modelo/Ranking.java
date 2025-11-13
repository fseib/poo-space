package modelo;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Ranking {
	private List<RegistroJugador> registros;
	private final String archivoRanking = "highscores.txt"; 
	
	public Ranking() {
		
		
		this.registros = new ArrayList	<>();
		cargarDesdeArchivo();
	}
	
	
	public boolean registrarPuntaje(String nombre, int puntaje) {
		int indice = chequearJugador(nombre, puntaje);
	    
	    if (indice > -1) {
	        if (registros.get(indice).getPuntaje() < puntaje) {
	            registros.remove(indice);
	            insertarNuevoRegistro(nombre, puntaje);
	            guardarEnArchivo();
	            return true;
	        }
	    } else {
	        insertarNuevoRegistro(nombre, puntaje);
	        guardarEnArchivo();
	        return true; 
	    }
	    
	    return false;
	}
	
	public RegistroJugador obtenerTop() {
        return registros.stream()
                        .max(Comparator.comparingInt(RegistroJugador::getPuntaje))
                        .orElse(null);
    }
	
	public void insertarNuevoRegistro(String nombre, int puntaje) {
		RegistroJugador nuevoRegistro = new RegistroJugador(nombre, puntaje);
		registros.add(nuevoRegistro);
		registros.sort(Comparator.comparingInt(RegistroJugador::getPuntaje).reversed());
	}
	
	public int chequearJugador(String nombre, int puntaje) {
		for (int i = 0; i < registros.size(); i++) {
			RegistroJugador jugador = registros.get(i);
	        
	        if (jugador.getNombre().equalsIgnoreCase(nombre)) {
	            return i;
	        }
	    }
	    

	    return -1;
	}
	
	public boolean esNuevoRecord(int puntajeFinal) {
        if (registros.isEmpty()) {
            return true;
        }
        RegistroJugador topPlayer = obtenerTop();
        return puntajeFinal > topPlayer.getPuntaje();
    }
	
	public void guardarEnArchivo() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(archivoRanking))) {
            for (RegistroJugador registro : registros) {
                writer.write(registro.getNombre() + "," + registro.getPuntaje() + "\n");
            }
        } catch (IOException e) {
            System.err.println("Error al guardar el archivo de ranking: " + e.getMessage());
        }
    }
    
    public void cargarDesdeArchivo() {
        try (BufferedReader reader = new BufferedReader(new FileReader(archivoRanking))) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                String[] partes = linea.split(",");
                if (partes.length == 2) {
                    try {
                        String nombre = partes[0];
                        int puntaje = Integer.parseInt(partes[1]);
                        registros.add(new RegistroJugador(nombre, puntaje)); 
                    } catch (NumberFormatException e) {
                        System.err.println("Error de formato de puntaje en archivo: " + linea);
                    }
                }
            }
            registros.sort(Comparator.comparingInt(RegistroJugador::getPuntaje).reversed());
            
        } catch (IOException e) {
            System.out.println("Archivo de ranking no encontrado. Se creará uno nuevo al guardar.");
        }
    }
    
    public List<RegistroJugador> getRegistros() {
        return registros;
    }
	
}
