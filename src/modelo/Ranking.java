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
	private final String archivoRanking = "highscores.txt"; // Use a final file name
	
	public Ranking() {
		
		
		this.registros = new ArrayList	<>();
		cargarDesdeArchivo();
	}
	
	
	public boolean registrarPuntaje(String nombre, int puntaje) {
		int indice = chequearJugador(nombre, puntaje);
	    
	    if (indice > -1) {
	        // Player exists: check if new score is higher.
	        if (registros.get(indice).getPuntaje() < puntaje) {
	            registros.remove(indice);
	            insertarNuevoRegistro(nombre, puntaje);
	            guardarEnArchivo();
	            return true; // <<< SCORE WAS UPDATED/SAVED
	        }
	    } else {
	        // New player: insert the score.
	        insertarNuevoRegistro(nombre, puntaje);
	        guardarEnArchivo();
	        return true; // <<< SCORE WAS INSERTED/SAVED
	    }
	    
	    return false;
	}
	
	public RegistroJugador obtenerTop() {
        // Find the record with the maximum score.
        // If the list isn't sorted, we need to find the max score manually:
        return registros.stream()
                        .max(Comparator.comparingInt(RegistroJugador::getPuntaje))
                        .orElse(null); // Returns null if the list is empty
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
        // If the list is empty, any score is a new record.
        if (registros.isEmpty()) {
            return true;
        }
        // Assuming the list is always sorted (highest score first).
        // If not, we find the maximum score in the list.
        RegistroJugador topPlayer = obtenerTop();
        return puntajeFinal > topPlayer.getPuntaje();
    }
	
	public void guardarEnArchivo() {
        // Format: NAME,SCORE
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(archivoRanking))) {
            for (RegistroJugador registro : registros) {
                writer.write(registro.getNombre() + "," + registro.getPuntaje() + "\n");
            }
        } catch (IOException e) {
            System.err.println("Error al guardar el archivo de ranking: " + e.getMessage());
        }
    }
    
    public void cargarDesdeArchivo() {
        // Load records from the file
        try (BufferedReader reader = new BufferedReader(new FileReader(archivoRanking))) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                String[] partes = linea.split(",");
                if (partes.length == 2) {
                    try {
                        String nombre = partes[0];
                        int puntaje = Integer.parseInt(partes[1]);
                        // Use a private helper method to add loaded records without re-saving
                        registros.add(new RegistroJugador(nombre, puntaje)); 
                    } catch (NumberFormatException e) {
                        System.err.println("Error de formato de puntaje en archivo: " + linea);
                    }
                }
            }
            // Sort the loaded data once
            registros.sort(Comparator.comparingInt(RegistroJugador::getPuntaje).reversed());
            
        } catch (IOException e) {
            // File may not exist yet, which is fine for the first run
            System.out.println("Archivo de ranking no encontrado. Se creará uno nuevo al guardar.");
        }
    }
    
    // Getter for the list (useful for the Scoreboard View)
    public List<RegistroJugador> getRegistros() {
        return registros;
    }
	
}
