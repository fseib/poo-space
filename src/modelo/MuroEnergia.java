package modelo;

import java.util.ArrayList;
import java.util.List;

public class MuroEnergia {
    
    private List<SeccionMuro> secciones = new ArrayList<>();
    private final int id;
    private static int nextId = 0;

    public MuroEnergia(int startX, int startY) {
        this.id = nextId++;
        // NOTE: This is where you define the shape of one shield.
        // We create a simple rectangular shield made of 3x5 sections.
        
        final int SECTION_SIZE = new SeccionMuro(0, 0).getAncho(); // 16 pixels
        
        for (int i = 0; i < 3; i++) { // 3 sections wide
            for (int j = 0; j < 5; j++) { // 5 sections high
                int x = startX + i * SECTION_SIZE;
                int y = startY + j * SECTION_SIZE;
                
                // Add a block unless it's the corner, to give it a classic "eaten" look (optional)
                if (!(i == 0 && j == 4) && !(i == 2 && j == 4)) {
                   secciones.add(new SeccionMuro(x, y));
                }
            }
        }
    }

    public List<SeccionMuro> getSecciones() {
        return secciones;
    }
    
    // NOTE: This check should NOT contain collision math (that's Controller's job)
    // but should only delegate to the SeccionMuro once the Controller finds a hit.
    // However, to keep the collision check concise, the Controller will need access to SeccionMuro data.
    
    public boolean checkCollision(Proyectiles projectile, int projectileXOffset) {
        for (SeccionMuro seccion : secciones) {
            // Check only if the section is not destroyed
            if (!seccion.estaDestruida()) {
                
                // CRITICAL: We need a utility method or logic to check AABB collision here,
                // passing the projectile's source to the damage method.
                
                // Since the Controller should do the final collision check, we simplify this:
                // If the Controller finds a hit, it will call damage directly on the section.
                return true; // Placeholder for collision detected
            }
        }
        return false;
    }
}