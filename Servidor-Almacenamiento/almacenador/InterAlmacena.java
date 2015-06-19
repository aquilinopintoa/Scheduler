package almacenador;

import java.rmi.Remote; 
import java.rmi.RemoteException; 

public interface InterAlmacena extends Remote { 
	
	public boolean almacenar(String id, byte[] matrizA) throws RemoteException;

	public byte[] consultar(String id) throws RemoteException;

	public boolean eliminar(String id) throws RemoteException;	
	
	public boolean estaVivo() throws RemoteException;
}