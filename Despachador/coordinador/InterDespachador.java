package coordinador;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.net.*;
import java.rmi.server.ServerNotActiveException;

public interface InterDespachador extends Remote { 
	
	public boolean registrarse(String nomServidor, int puerto, String tipo) throws RemoteException;

	public Servidor solicitarServidorCalculo() throws RemoteException;

	public EstadoDespachador obtenerEstado(String nombre, int puerto) throws RemoteException, ServerNotActiveException;	

	public boolean almacenarMatriz(String usuario, byte[] archivo) throws RemoteException;

	public boolean eliminarMatriz(String usuario) throws RemoteException;

	public byte[] consultarMatriz(String usuario) throws RemoteException;

	public Servidor getDespachadorPasivo() throws RemoteException;

}
