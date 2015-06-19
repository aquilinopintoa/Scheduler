package servidor;

import java.io.*;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import calculadora.*; 
import coordinador.*; 

public class ServidorCalculo implements InterfaceCalculadora{ 

	public ServidorCalculo() throws RemoteException{ 
		super(); 
	}
	
	// Calcula la multiplicacion de matrices.
	// Si retorna null, ha ocurrido un error en el despachador.
	public byte[] multiplicar(byte[] matrizA, byte[] matrizB) throws RemoteException{
		Matriz m = new Matriz();
		double [][] a = null;
		double [][] b = null;
		byte[] respuesta;
		try{
			a = m.parserMatriz(matrizA);
			b = m.parserMatriz(matrizB);
		}
		catch(ExcepcionDeFormato ef){
			// Se le indica al cliente que el archivo tiene errores de formato.
			try{
				respuesta= ef.getMessage().getBytes("UTF-8");
			}
			catch(Exception exp){
				return null;
			}
		}
		try{
			respuesta = m.producto(a, b);
		}
		catch (ExcepcionMatematica e){
			// Se le indica al cliente que las matrices no son matematicamente "multiplicables".
			try{
				respuesta= e.getMessage().getBytes("UTF-8");
			}
			catch(Exception ex){
				return null;
			}
		}
		return respuesta;
	}

	// Calcula la suma de matrices.
	// Si retorna null, ha ocurrido un error en el despachador.
	public byte[] suma(byte[] matrizA, byte[] matrizB) throws RemoteException{
		Matriz m = new Matriz();
		double [][] a = null;
		double [][] b = null;
		byte[] respuesta;
		try{
			a = m.parserMatriz(matrizA);
			b = m.parserMatriz(matrizB);
		}
		catch(ExcepcionDeFormato ef){
			// Se le indica al cliente que el archivo tiene errores de formato.
			try{
				respuesta= ef.getMessage().getBytes("UTF-8");
			}
			catch(Exception exp){
				return null;
			}
		}
		try{
			respuesta = m.sumaYResta(a, b, true);
		}
		catch (ExcepcionMatematica e){
			// Se le indica al cliente que las matrices no son matematicamente "Sumables".
			try{
				respuesta= e.getMessage().getBytes("UTF-8");
			}
			catch(Exception ex){
				return null;
			}
		}
		return respuesta;
	}

	// Calcula la resta de matrices.
	// Si retorna null, ha ocurrido un error en el despachador.
	public byte[] resta(byte[] matrizA, byte[] matrizB) throws RemoteException{
		Matriz m = new Matriz();
		double [][] a = null;
		double [][] b = null;
		byte[] respuesta;
		try{
			a = m.parserMatriz(matrizA);
			b = m.parserMatriz(matrizB);
		}
		catch(ExcepcionDeFormato ef){
			// Se le indica al cliente que el archivo tiene errores de formato.
			try{
				respuesta= ef.getMessage().getBytes("UTF-8");
			}
			catch(Exception exp){
				return null;
			}
		}
		try{
			respuesta = m.sumaYResta(a, b, false);
		}
		catch (ExcepcionMatematica e){
			// Se le indica al cliente que las matrices no son matematicamente "Restables".
			try{
				respuesta= e.getMessage().getBytes("UTF-8");
			}
			catch(Exception ex){
				return null;
			}
		}
		return respuesta;
	}
	
	// Calcula la traspuesta de una matriz.
	// Si retorna null, ha ocurrido un error en el despachador.
	public byte[] calTraspuesta(byte[] matrizA) throws RemoteException{
		Matriz m = new Matriz();
		double [][] a = null;
		byte[] respuesta;
		try{
			a = m.parserMatriz(matrizA);
		}
		catch(ExcepcionDeFormato ef){
			// Se le indica al cliente que el archivo tiene errores de formato.
			try{
				respuesta= ef.getMessage().getBytes("UTF-8");
			}
			catch(Exception exp){
				return null;
			}
		}
		respuesta = m.traspuesta(a);
		return respuesta;
	}
	
	// Calcula el determiante de matriz.
	// Si retorna null, ha ocurrido un error en el despachador.
	public byte[] calDeterminante(byte[] matrizA) throws RemoteException{
		Matriz m = new Matriz();
		double [][] a= null;
		byte[] respuesta;
		try{
			a = m.parserMatriz(matrizA);
		}
		catch(ExcepcionDeFormato ef){
			// Se le indica al cliente que el archivo tiene errores de formato.
			try{
				respuesta= ef.getMessage().getBytes("UTF-8");
			}
			catch(Exception exp){
				return null;
			}
		}
		try{
			respuesta = m.determinante(a);
		}
		catch (ExcepcionMatematica e){
			// Se le indica al cliente que la matriz debe ser cuadrada para poder calcular determinante.
			try{
				respuesta= e.getMessage().getBytes("UTF-8");
			}
			catch(Exception ex){
				return null;
			}
		}
		return respuesta;
	}
	
	// Calcula la inversa de la matriz.
	// Si retorna null, ha ocurrido un error en el despachador.
	public byte[] calInversa(byte[] matrizA) throws RemoteException{
		Matriz m = new Matriz();
		double [][] a = null;
		byte[] respuesta;
		try{
			a = m.parserMatriz(matrizA);
		}
		catch(ExcepcionDeFormato ef){
			// Se le indica al cliente que el archivo tiene errores de formato.
			try{
				respuesta= ef.getMessage().getBytes("UTF-8");
			}
			catch(Exception exp){
				return null;
			}
		}
		try{
			respuesta = m.inversa(a);
		}
		catch (ExcepcionMatematica e){
			// Se le indica al cliente que la matriz debe ser cuadrada
			// y el determinante diferente de cero para poder calcular inversa.
			try{
				respuesta = e.getMessage().getBytes("UTF-8");
			}
			catch(Exception ex){
				return null;
			}
		}
		return respuesta;
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
            boolean s = desp.registrarse(nomServidor,portServidor ,"calculo");

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
            InterfaceCalculadora motor = new ServidorCalculo();
            InterfaceCalculadora stub = (InterfaceCalculadora) UnicastRemoteObject.exportObject(motor, 0);
            Registry registro = LocateRegistry.createRegistry(portServidor);
            registro.rebind(nomServidor, stub);
            System.out.println("Se ha iniciado correctamente el servidor de calculo.");
        } catch (Exception e) {
            System.err.println("Error: El servidor no ha podido ser iniciado correctamente.");
			System.exit(-1);
        }
    }
	
}

