package coordinador;

public class Servidor implements java.io.Serializable, Comparable<Servidor>{

    private String nombre;      // Nombre del servidor
    private int puerto;		    // Puerto del servidor
    private String direccion;   // Direccion del servidor
    private String tipo;		// Tipo del servidor. Puede ser calculo o almacenamiento
    private int carga;         	// (APLICA SOLO PARA SERV. DE ALAMACENAMIENTO) Si es un servidor de almacenamiento 
	                            // representa el nro de matrices que tiene almacenadas.
	
    public Servidor( String nombre, String direccion, int puerto, String tipo){
        this.nombre = nombre;
        this.direccion = direccion;
        this.tipo = tipo;
        this.puerto = puerto;
        this.carga = 0;
    }

    public String getNombre(){
        return this.nombre;
    }

    public String getDireccion(){
        return this.direccion;
    }

    public String getTipo(){
        return this.tipo;
    }

    public int getCarga(){
        return this.carga;
    }

    public int getPuerto(){
        return this.puerto;
    }

    public void setNombre(String nombre){
        this.nombre = nombre;
    }

    public void setDireccion(String direccion){
        this.direccion = direccion;
    }

    public void setTipo(String tipo){
        this.tipo = tipo;
    }

    public void setCarga(int car){
        this.carga = car;
    }

    public void setPuerto(int puerto){
        this.puerto = puerto;
    }
	
	public int compareTo(Servidor otro) { 
        if (this.carga == otro.getCarga())
			return 0;
		else if (this.carga < otro.getCarga())
			return -1;
		else
			return 1;
    }

    public boolean igualServidor(Servidor otro){
        if (this.nombre.equals(otro.getNombre()) && this.direccion.equals(otro.getDireccion())
            && this.tipo.equals(otro.getTipo()) && this.puerto == otro.getPuerto()){
            return true;
        }
        else{
            return false;
        }

    }

    public String toString(){
        return "\n\t" + this.nombre + "\n\t" + this.direccion + "\n\t" 
            + this.tipo  + "\n\t" +  this.puerto + "\n\t" + this.carga + "\n";
    }
}
