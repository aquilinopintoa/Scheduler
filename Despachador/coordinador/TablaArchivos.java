package coordinador;

public class TablaArchivos implements java.io.Serializable{

    private String usuario;          // Nombre de usuario del cliente.
    private Servidor respaldoUno;    // Datos del primer respaldo.
    private Servidor respaldoDos;    // Datos del segundo respaldo.
	

    public TablaArchivos(String usuario, Servidor respaldoUno, Servidor respaldoDos){
        this.usuario = usuario;
        this.respaldoUno = respaldoUno;
        this.respaldoDos = respaldoDos;
    }

    public String getUsuario(){
        return this.usuario;
    }

    public Servidor getRespaldo(int numero){
        if (numero== 1)
            return this.respaldoUno;
        else
            return this.respaldoDos;
    }

    public void setRespaldo(int numero, Servidor resp){
        if (numero== 1)
            this.respaldoUno = resp;
        else
            this.respaldoDos = resp;
    }

    public String toString(){
        String res= "\n\t" + this.usuario + "\n\tRespaldos: \n"; 
        if(this.respaldoUno!=null)
            res = res + this.respaldoUno.toString();
        else
            res = res + "El primer respaldo: nulo\n";
        if(this.respaldoDos!=null)
            res = res + this.respaldoDos.toString();
        else
            res = res + "El segundo respaldo: nulo\n";
        return res;
    }
    
}