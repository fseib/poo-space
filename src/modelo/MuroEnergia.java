package modelo;

import java.util.ArrayList;
import java.util.List;

public class MuroEnergia {
    
    private List<SeccionMuro> secciones = new ArrayList<>();
    private final int id;
    private static int nextId = 0;

    public MuroEnergia(int startX, int startY) {
        this.id = nextId++;
        
        final int SECTION_SIZE = new SeccionMuro(0, 0).getAncho();
        
        for (int i = 0; i < 3; i++) { 
            for (int j = 0; j < 5; j++) {
                int x = startX + i * SECTION_SIZE;
                int y = startY + j * SECTION_SIZE;
                
                if (!(i == 0 && j == 4) && !(i == 2 && j == 4)) {
                   secciones.add(new SeccionMuro(x, y));
                }
            }
        }
    }

    public List<SeccionMuro> getSecciones() {
        return secciones;
    }
    
    public boolean checkCollision(Proyectiles projectile, int projectileXOffset) {
        for (SeccionMuro seccion : secciones) {
            if (!seccion.estaDestruida()) {
          
                return true; 
            }
        }
        return false;
    }
}