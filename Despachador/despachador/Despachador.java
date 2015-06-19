package despachador;

import java.io.*; 
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.server.RemoteServer;
import java.rmi.server.ServerNotActiveException;
import java.util.Collections;
import java.util.ArrayList;
import coordinador.*;
import almacenador.*;
import calculadora.*;

public class Despachador implements InterDespachador { 
	
	private String nombrePasivo;					 // Nombre del despachador pasivo.
	private String dirPasivo;						 // Direccion del despachador pasivo.
	private int puertoPasivo;						 // Puerto del despachador pasivo.
	private ArrayList<Servidor> ListServCalculo;     // Representa la lista de servidores de calculo activos
	private ArrayList<Servidor> ListServAlmacen;     // Representa la lista de servidores de almacenamiento activos
	private ArrayList<TablaArchivos> matrices;   	 // Indicara la direccion de los servidores donde se almacenan las matrices
	private int mantenimiento;              // Indica si existen archivos que les falte un respaldo.
	private int indice;                              /* El balanceo de carga del servidor de calculo se hace con round-robin.
													 ** por lo tanto "indice", muestra quien es al que le corresponde atenter la peticion. 
													 */
	     
	public Despachador() throws RemoteException{ 
		super();
		this.nombrePasivo = "";
		this.dirPasivo = "";
		this.puertoPasivo = 0;
		this.mantenimiento = 0;
		this.ListServCalculo = new ArrayList<Servidor>();
		this.ListServAlmacen = new ArrayList<Servidor>();
		this.matrices = new ArrayList<TablaArchivos>();
		this.indice = 0;
	}

/*
**  Metodo solicitado por los servidores para registrarse ante el despachador cuando comienzan a trabajar o se
**  recuperan de un fallo. Retorna true si el servidor se registro correctamente.
*/
	public boolean registrarse(String nomServidor ,int puerto, String tipo) throws RemoteException {
		boolean resultado = false;
		try {
			//Almacena el nuevo servidor en la lista de servidores dependiendo de su tipo.
			String aux = "";
			Servidor serv = new Servidor(nomServidor, RemoteServer.getClientHost(), puerto , tipo);
			if (!estaRegistrado(serv)){
				if (serv.getTipo().equals("calculo")){
					ListServCalculo.add(serv);
				}
				else{
					ListServAlmacen.add(serv);
				}
				resultado = true;
				aux = "Se ha registrado el servidor:\n\tnombre: " + serv.getNombre() 
				+ "\n\tmaquina: " + serv.getDireccion() + "\n\tpuerto: "+serv.getPuerto()+"\n\ttipo: " + serv.getTipo();
				System.out.println(aux);
			}
			return resultado;
		}
		catch(Exception e) {
			return resultado;
		}
	}


	private boolean estaRegistrado(Servidor serv){
		boolean resultado = false;
		ArrayList<Servidor> aux = (serv.getTipo()=="calculo")?ListServCalculo:ListServAlmacen;
		int tam = aux.size();
		Servidor temp = null;
		for(int i=0; i<tam; i++){
			temp = aux.get(i);
			if(serv.getTipo().equals(temp.getTipo()) && serv.getNombre().equals(temp.getNombre()) && serv.getDireccion().equals(temp.getDireccion())){
				resultado = true;
				break;
			}
		}
		return resultado;
	}
	
/*
**  Metodo solicitado por los clientes. Retorna la dirección del servidor de calculo que se encuentre
**  mas desocupado. En caso de que no haya ningun servidor de calculo activo se retorna "null".
*/
	public Servidor solicitarServidorCalculo() throws RemoteException {
		Servidor ser = null;

        //Verifica que exista al menos un servidor de calculo.
        if (!this.ListServCalculo.isEmpty()){
        	ser = ListServCalculo.get(this.indice);

        	// Se verifica que el servidor este vivo.
        	if(verificarServidorCalculo(ser)){
        		if (this.indice + 1 == ListServCalculo.size()){
        			this.indice = 0;
        		}
        		else{
        			this.indice = this.indice + 1;
        		}
        	}
        	else{
        		// Se declara muerto al servidor, se elimina de las lista.
        		ListServCalculo.remove(indice);

        		// Se busca un servidor disponible.
        		int tam= ListServCalculo.size();
        		this.indice=0;
        		while(tam!=0){
        			ser = ListServCalculo.get(this.indice);
		        	if(verificarServidorCalculo(ser)){
		        		if (this.indice + 1 == ListServCalculo.size()){
		        			this.indice = 0;
		        		}
		        		else{
		        			this.indice = this.indice + 1;
		        		}
		        		break;
		        	}
		        	else{
		        		ListServCalculo.remove(indice);
		        		tam = ListServCalculo.size();
		        	}
        		} 

        	}
        }
        // Se imprime el estado del despachador.
        imprimirEstadoCalc();
        return ser;
    }

    private boolean verificarServidorCalculo(Servidor serv){
		Registry registro;
		InterfaceCalculadora calc;
		boolean estaVivo = false;
		if (System.getSecurityManager() == null) {
			System.setSecurityManager(new SecurityManager());
		}
		for (int i=0; i<3; i++){
			try {
	           	registro = LocateRegistry.getRegistry(serv.getDireccion(), serv.getPuerto());
	            calc = (InterfaceCalculadora) registro.lookup(serv.getNombre());
	            estaVivo = calc.estaVivo();
	            if(estaVivo){
	            	break;
	            }
			}
			catch (Exception e) {}	
		}
		return estaVivo;
	}


/*
**  Metodo solicitado por el despachador pasivo. Retorna la informacion de control del despachador
**  principal.
*/
    public EstadoDespachador obtenerEstado(String nombre, int puerto) throws RemoteException, ServerNotActiveException{
    	this.nombrePasivo = nombre;
		this.puertoPasivo = puerto;
		this.dirPasivo = RemoteServer.getClientHost();

		// Se realiza el mantenimiento.
		if (this.mantenimiento == 2){
			gestorMantenimiento();
			this.mantenimiento = 0;	
		}
		else{
			this.mantenimiento = this.mantenimiento+1;
		}
		

		return new EstadoDespachador(this.ListServCalculo, this.ListServAlmacen, 
			this.matrices, this.indice);
	}


/*
**  Metodo solicitado por el cliente para almacenar un archivo. Retorna true si el archivo fue
**  almacenado. False en otro caso.
*/
	public boolean almacenarMatriz(String usuario, byte[] archivo) throws RemoteException{
		
		// Se verifica que existan al menos dos servidores.
		if(ListServAlmacen.size()<2){
			return false;
		}
		TablaArchivos tabla = buscarUsuario(usuario);

		// Se envia el archivo a dos servidores.
		int nroRespaldos=0;
		Servidor respaldo;
		boolean esEnviado;
		for(int i=1; i<=2; i++){
			respaldo = tabla.getRespaldo(i);
			if (respaldo!= null){
				esEnviado = enviarArchivo(respaldo, usuario, archivo);
				if (esEnviado){
					nroRespaldos = nroRespaldos + 1;
					continue;
				}	
			}

			// Se trata de enviar el archivo al servidor de menor carga.
			Collections.sort(ListServAlmacen);
			for (int j=0; j<ListServAlmacen.size(); j++) {
				respaldo = ListServAlmacen.get(j);
				esEnviado = enviarArchivo(respaldo, usuario, archivo);
				if (esEnviado) {
					respaldo.setCarga(respaldo.getCarga() + 1);
					tabla.setRespaldo(i, respaldo);
					nroRespaldos = nroRespaldos + 1;
					break;	
				}
				else{
					// Se declara muerto al servidor, por lo tanto se elimina de las listas.
					eliminarServidor(respaldo);
				}
			}
		}
		// Se muestra el estado del despachador.
		imprimirEstadoAlm();

		if (nroRespaldos>0){
			return true;
		}
		else{
			return false;
		}
	}

	private TablaArchivos buscarUsuario(String usuario){
		int tam = matrices.size();
		TablaArchivos aux;
		for(int i=0; i<tam; i++){
			aux = matrices.get(i);
			if(aux.getUsuario().equals(usuario)){
				return aux;
			}
		}
		aux = new TablaArchivos(usuario, null, null);
		matrices.add(aux);
		return aux;
	}

	private void eliminarServidor(Servidor elemento){
		
		// Se elimina de la lista de servidores.
		this.ListServAlmacen.remove(elemento);

		// Se elimina de la lista de respaldos.
		int tam = matrices.size();
		TablaArchivos aux;
		Servidor temp;
		for(int i=0; i<tam; i++){
			aux = matrices.get(i);
			for (int j=1; j<=2; j++){
				temp = aux.getRespaldo(j);
				if(temp.igualServidor(elemento)){
					aux.setRespaldo(j, null);
				}
			}
		}
	}

	private boolean enviarArchivo(Servidor serv, String usuario, byte[] archivo){
		Registry registro;
		InterAlmacena almacen;
		boolean esGuardado = false;
		if (System.getSecurityManager() == null) {
			System.setSecurityManager(new SecurityManager());
		}
		for (int i=0; i<3; i++) {
			try {
	           	registro = LocateRegistry.getRegistry(serv.getDireccion(), serv.getPuerto());
	            almacen = (InterAlmacena) registro.lookup(serv.getNombre());
	            esGuardado = almacen.almacenar(usuario, archivo);
	            if(esGuardado){
	            	break;
	            }
			}
			catch (Exception e) {}	
		}
		return esGuardado;
	}


/*
**  Metodo solicitado por el cliente para eliminar un archivo. Retorna true si el archivo fue
**  eliminado. False en otro caso.
*/
	public boolean eliminarMatriz(String usuario) throws RemoteException{
		TablaArchivos tabla = buscarUsuario(usuario);

		// Se elimina el archivo de los dos servidores.
		int nroRespaldos=0;
		Servidor respaldo;
		boolean esEliminado;
		int indice;
		for(int i=1; i<=2; i++){
			respaldo = tabla.getRespaldo(i);
			if (respaldo!= null){
				esEliminado = eliminarArchivo(respaldo, usuario);
				if (esEliminado){
					nroRespaldos = nroRespaldos + 1;
					respaldo.setCarga(respaldo.getCarga() - 1);
					tabla.setRespaldo(i, null);
				}
				else{
					// Se declara muerto al servidor, por lo tanto se elimina de las listas. 
					eliminarServidor(respaldo);
				}
			}
			else{
				nroRespaldos = nroRespaldos +1;
			}
		}
		// Se muestra el estado del despachador.
		imprimirEstadoAlm();
		
		if (nroRespaldos>0){
			return true;
		}
		else{
			return false;
		}
	}
	
	private boolean eliminarArchivo(Servidor serv, String usuario){
		Registry registro;
		InterAlmacena almacen;
		boolean esEliminado = false;

		if (System.getSecurityManager() == null) {
			System.setSecurityManager(new SecurityManager());
		}

		for (int i=0; i<3; i++) {
			try {
	           	registro = LocateRegistry.getRegistry(serv.getDireccion(), serv.getPuerto());
	            almacen = (InterAlmacena) registro.lookup(serv.getNombre());
	            esEliminado = almacen.eliminar(usuario);
	            if(esEliminado){
	            	break;
	            }
			}
			catch (Exception e) {}	
		}
		return esEliminado;
	}


/*
**  Metodo solicitado por el cliente para consultar un archivo. Retorna el archivo si es capaz de obtenerlo.
**  Null en otro caso.
*/
	public byte[] consultarMatriz(String usuario) throws RemoteException{
		TablaArchivos tabla = buscarUsuario(usuario);

		// Se consulta el archivo de alguno de los servidores.
		Servidor respaldo;
		byte[] respuesta = null;
		int indice;
		for(int i=1; i<=2; i++){
			respaldo = tabla.getRespaldo(i);
			if (respaldo!= null){
				respuesta = consultarArchivo(respaldo, usuario);
				if (respuesta != null){

					// Se muestra el estado del despachador.
					imprimirEstadoAlm();
					return respuesta;
				}
				else{
					// Se declara muerto al servidor, por lo tanto se elimina de las listas. 
					eliminarServidor(respaldo);
				}
			}
		}
		// Se muestra el estado del despachador.
		imprimirEstadoAlm();

		return respuesta;
	}	

	private byte[] consultarArchivo(Servidor serv, String usuario){
		Registry registro;
		InterAlmacena almacen;
		byte[] archivo = null;

		if (System.getSecurityManager() == null) {
			System.setSecurityManager(new SecurityManager());
		}

		for (int i=0; i<3; i++) {
			try {
	           	registro = LocateRegistry.getRegistry(serv.getDireccion(), serv.getPuerto());
	            almacen = (InterAlmacena) registro.lookup(serv.getNombre());
	            archivo = almacen.consultar(usuario);
	            if(archivo != null){
	            	break;
	            }
			}
			catch (Exception e) {}	
		}
		return archivo;
	}


/*
**  Retorna la direccion del despachador de respaldo.
*/
	public Servidor getDespachadorPasivo() throws RemoteException{
		if (!this.dirPasivo.equals("")){
			return new Servidor(this.nombrePasivo, this.dirPasivo, this.puertoPasivo, "");
		}
		else{
			return null;
		}
	}


/*
**  Funciones para mostrar el estado del servidor.
*/
	private void imprimirEstadoAlm(){
		System.out.println("Servidores de almacenamiento: ");
		for (int i=0; i< this.ListServAlmacen.size(); i++) {
			System.out.println(this.ListServAlmacen.get(i).toString());
		}
		System.out.println("Tabla de archivos: ");
		for (int i=0; i< this.matrices.size(); i++) {
			System.out.println(this.matrices.get(i).toString());
		}
	}

	private void imprimirEstadoCalc(){
		System.out.println("Servidores de calculo: ");
		for (int i=0; i< this.ListServCalculo.size(); i++) {
			System.out.println(this.ListServCalculo.get(i).toString());
		}
		System.out.println("El indice de los ser. de calculo es " + this.indice);
	}


/*
**  Los siguientes metodos son usados para iniciar el despachador.
*/
	public static void main(String[] args) {
		boolean esActivo = Boolean.parseBoolean(args[0]);
		String nomDespachador = args[1];
		int puerto = Integer.parseInt(args[2]);
		Despachador motor;
		try {
			motor = new Despachador();

			// Se configura el despachador.
			configurarSistema();

			// Se verifica si el despachador es pasivo o activo.
			if(!esActivo){
				String dirActivo = args[3];
				String nomDespachadorPasivo = args[4];
				int puertoPasivo = Integer.parseInt(args[5]);
				int pollingTime = Integer.parseInt(args[6]);
				motor.funcionarPasivamente(dirActivo, nomDespachador, puerto, pollingTime, nomDespachadorPasivo, puertoPasivo);
				nomDespachador = nomDespachadorPasivo;
				puerto = puertoPasivo;
			}
			
			// Se inicia el despachador.
			if (System.getSecurityManager() == null) {
				System.setSecurityManager(new SecurityManager());
	    	}
	    	InterDespachador motorReal = motor;
	    	InterDespachador stub = (InterDespachador) UnicastRemoteObject.exportObject(motorReal, 0);
	    	Registry registro = LocateRegistry.createRegistry(puerto);
	    	registro.rebind(nomDespachador, stub);
	    	System.out.println("Se ha iniciado correctamente el Despachador.");
	    } 
	    catch (Exception e) {
	    	System.err.println("Error: El Despachador no ha podido ser iniciado correctamente.");
	    	e.printStackTrace();
	    	System.exit(-1);
	    }
	}

	private void gestorMantenimiento(){
		TablaArchivos tabla;
		Servidor serv = null;
		Servidor respaldo;
		byte[] copia;
		boolean esEnviado;
		String usuario;
		int pos=0;
		for (int i=0; i< this.matrices.size(); i++) {
			tabla = this.matrices.get(i);
			if (tabla.getRespaldo(1)!=null && tabla.getRespaldo(2)==null){
				serv = tabla.getRespaldo(1);
				pos = 2;
			}
			else if (tabla.getRespaldo(1)==null && tabla.getRespaldo(2)!=null){
				serv = tabla.getRespaldo(2);
				pos = 1;
			}
			if (serv!=null){
				usuario = tabla.getUsuario();
				copia = consultarArchivo(serv, usuario);
				if (copia != null){

					// Se trata de enviar el archivo al servidor de menor carga.
					Collections.sort(this.ListServAlmacen);
					for (int j=0; j<this.ListServAlmacen.size(); j++) {
						respaldo = this.ListServAlmacen.get(j);
						if (!serv.igualServidor(respaldo)){
							esEnviado = enviarArchivo(respaldo, usuario, copia);
							if (esEnviado) {
								respaldo.setCarga(respaldo.getCarga() + 1);
								tabla.setRespaldo(pos, respaldo);
								break;	
							}
							else{
								// Se declara muerto al servidor, por lo tanto se elimina de las listas.
								eliminarServidor(respaldo);
							}
						}
					}
				}
				else{
					// Se declara muerto al servidor, por lo tanto se elimina de las listas. 
					eliminarServidor(serv);
				}
			}
			serv = null;
		}
		imprimirEstadoAlm();
	}
	
	private void establecerEstado(EstadoDespachador estado){
		this.ListServCalculo = estado.getListServCalculo();
		this.ListServAlmacen = estado.getListServAlmacen();
		this.matrices = estado.getMatrices();
		this.indice = estado.getIndice(); 
	}

	private void funcionarPasivamente(String dirActivo, String nomDespachador, int puerto, int pollingTime,
		String nomDespachadorPasivo, int puertoPasivo){

		Registry registro;
		InterDespachador desp;
		EstadoDespachador estado;
		int intentos = 3;
		if (System.getSecurityManager() == null) {
			System.setSecurityManager(new SecurityManager());
		}
		System.out.println("Se inicia el despachador pasivo.");
		while(true){
	        try {
	        	// Se espera que se cumpla el tiempo de espera.
            	Thread.sleep(pollingTime);
	           	registro = LocateRegistry.getRegistry(dirActivo, puerto);
	            desp = (InterDespachador) registro.lookup(nomDespachador);
	            estado = desp.obtenerEstado(nomDespachadorPasivo, puertoPasivo);
	        	this.establecerEstado(estado);
	        	intentos=3;
			}
			catch (Exception e) {
				intentos= intentos-1;
				if(intentos==0){
					System.out.println("El despachador principal ha fallado. Se inicia el despachador de respaldo.");
					break;
				}
				else{
					System.out.println("Fallo conexion con despachador, restan " + intentos + " intentos.");
				}
	        }
		}
	}

	private static void configurarSistema(){
		FileWriter fichero = null;
		PrintWriter pw = null;
		String directorio = "file:" + System.getProperty("user.dir");
		String codigoBase = directorio +"/public_objects/classes/rmi.jar";
		System.setProperty("java.rmi.server.codebase", codigoBase);
		String politicaDir = directorio.replace('\\', '/');
        String politica = "grant codeBase \"" + politicaDir + "/\" {\n\tpermission java.security.AllPermission;\n};";
		try{
            fichero = new FileWriter("despachador.policy");
            pw = new PrintWriter(fichero);
			pw.println(politica);
			fichero.close();
        }
		catch (Exception e) {
            System.out.println("Error: No se ha podido configurar el despachador.");
			System.exit(-1);
        }
		System.setProperty("java.security.policy", "despachador.policy");
	}
	
}
