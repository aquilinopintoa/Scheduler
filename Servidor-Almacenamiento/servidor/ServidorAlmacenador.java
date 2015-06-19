package servidor;

import java.io.*;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import coordinador.*;
import almacenador.*;


public class ServidorAlmacenador implements InterAlmacena{ 

	public ServidorAlmacenador(){
		super(); 
	}


	/*
	** Las siguientes funciones permiten implementar la interfaz "InterAlmacena".
	*/
	private byte[] archivoABytes(String archivo){
		try { 
			File file = new File(archivo);
			if (file.exists()){
				byte buffer[] = new byte[(int)file.length()]; 
				BufferedInputStream input = new BufferedInputStream(new FileInputStream(archivo)); 
				input.read(buffer,0,buffer.length); 
				input.close(); 
				return buffer;
			}
			else{
				String s= "El cliente no tiene ninguna matriz almacenada.";
				return s.getBytes("UTF-8");
			}
		} 
		catch(Exception e){
			System.out.println("Fallo funcion: \"archivoABytes\", no se pudo convertir el archivo.");
			return null; 
		}
	}
	
	public boolean almacenar(String id, byte[] matrizA) throws RemoteException{
		try {
			File carpeta = new File("almacen");
			if(!carpeta.isDirectory()){
				carpeta.mkdir();
			}
			String nombreArch = "./almacen/MatrizCliente"+id+".txt";
			File file = new File(nombreArch);
			BufferedOutputStream output = new BufferedOutputStream(new FileOutputStream(nombreArch)); 
			output.write(matrizA,0,matrizA.length); 
			output.flush();
			output.close(); 		
		} 
		catch(Exception e) {
			return false;
		}
		return true;
	}

	public byte[] consultar(String id) throws RemoteException{
		String nombreArch = "./almacen/MatrizCliente"+id+".txt";
		byte[] byteMatriz = archivoABytes(nombreArch);
		return byteMatriz;
	}

	public boolean eliminar(String id) throws RemoteException{
		try {
			String nombreArch = "./almacen/MatrizCliente"+id+".txt";
			File file = new File(nombreArch);
			if (file.exists()){
				if(!file.delete()){
					return false;
				}
			}
		} 
		catch(Exception e) {
			return false;
		}
		return true;
	}
	
	public boolean estaVivo() throws RemoteException{
		return true;
	}


	/*
	** Las siguientes funciones permiten configurar y arrancar el servidor.
	*/
	private static void configurarSistema(){
		FileWriter fichero = null;
        PrintWriter pw = null;
		String directorio = "file:" + System.getProperty("user.dir");
		String codigoBase = directorio +"/public_objects/classes/rmi.jar";
		System.setProperty("java.rmi.server.codebase", codigoBase);
		String politicaDir = directorio.replace('\\', '/');
        String politica = "grant codeBase \"" + politicaDir + "/\" {\n\tpermission java.security.AllPermission;\n};";
		try{
            fichero = new FileWriter("server.policy");
            pw = new PrintWriter(fichero);
			pw.println(politica);
			fichero.close();
        }
		catch (Exception e) {
            System.out.println("Error: No se ha podido configurar el servidor.");
			System.exit(-1);
        }
		System.setProperty("java.security.policy", "server.policy");
	}
	
	private static void registrarServidor(String maquina, int port ,String nombre, String nomServidor,int portServidor){
		if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
		}
        try {
           	Registry registro = LocateRegistry.getRegistry(maquina,port);
            InterDespachador desp = (InterDespachador) registro.lookup(nombre);
            boolean s = desp.registrarse(nomServidor,portServidor ,"almacenamiento");

			if (s){
				System.out.println("El registro del servidor fue exitoso.");
			}
			else{
				System.out.println("El Despachador NO ha permitido el registro del servidor.");
				System.exit(-1);
			}
		}
		catch (Exception e) {
            System.err.println("Error: Se ha producido un error de conexion.");
            System.exit(-1);
        }
	}
	
	public static void main(String[] args) {
		String maqDespachador;
		String nomDespachador;
		String nomServidor;
		int portDespachador;
		int portServidor;
		
		// Se verifica el numero de argumentos.
		if(args.length!=5){
			System.out.println("Error: Debe introducir el nombre de la maquina despachador, el puerto y el nombre "+
				"del servidor despachador, el nombre que desea para su servidor y el puerto");
		}
		maqDespachador = args[0];
		portDespachador = Integer.parseInt(args[1]);
		nomDespachador = args[2];
		nomServidor = args[3];
		portServidor = Integer.parseInt(args[4]);
		
		// Se configura el servidor.
		configurarSistema();
		
		// registrarse.
		registrarServidor(maqDespachador, portDespachador, nomDespachador, nomServidor,portServidor);
		
		// Se inicia el servidor.
		if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }
        try {
            InterAlmacena motor = new ServidorAlmacenador();
            InterAlmacena stub = (InterAlmacena) UnicastRemoteObject.exportObject(motor, 0);
            Registry registro = LocateRegistry.createRegistry(portServidor);
            registro.rebind(nomServidor, stub);
            System.out.println("Se ha iniciado correctamente el servidor de almacenamiento.");
        } catch (Exception e) {
            System.err.println("Error: El servidor no ha podido ser iniciado correctamente.");
			System.exit(-1);
        }
    }

}