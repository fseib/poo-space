package modelo;

public class ModeloInvasor {
	
	private final int id;
	private static int nextId = 0;
	private int coordenadaX;
	private int coordenadaY;
	private int ancho;
	private int alto;
    
    private final int invaderType; 
    private final int score;
    private final int row;        
    private boolean activo;       
	
	
	public ModeloInvasor(int coordenadaX, int coordenadaY,  int type, int row) {
		this.id = nextId++;
		this.coordenadaX = coordenadaX;
		this.coordenadaY = coordenadaY;
		this.ancho = 40;
		this.alto = 32;
        this.invaderType = type;
        this.row = row;        
        this.activo = true;     
        
        if(type == 1) {
        	this.score = 20;

        }
        else if(type == 2) {
        	this.score = 10;

        }
        else {
        	this.score = 30;
        }
	}
	
	public int getId() { 
		return id; 
	}
	
    public int getInvaderType() { 
    	return invaderType; 
    }
    
    public int getRow() { 
    	return row; 
    }
    
    public boolean isActivo() { 
    	return activo; 
    }
    
    public int getX() { 
    	return coordenadaX; 
    }
    
    public int getY() { 
    	return coordenadaY; 
    }
    
    public int getAncho() { 
    	return ancho; 
    } 
    
    public int getAlto() { 
    	return alto; 
    }   
    
    public int getScore() {
    	return score;
    }
    
	public int disparar() {
		return this.coordenadaX;
	}
	
	 public void destruir() {
	        this.activo = false;
	}
	
	public void mover(int deltaX, int deltaY) {
        this.coordenadaX += deltaX;
        this.coordenadaY += deltaY;
    }
    
   


}
