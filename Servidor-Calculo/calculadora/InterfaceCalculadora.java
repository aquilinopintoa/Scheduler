package calculadora;

import java.rmi.Remote; 
import java.rmi.RemoteException; 

public interface InterfaceCalculadora extends Remote { 
	
	public byte[] multiplicar(byte[] matrizA, byte[] matrizB) throws RemoteException;

    public byte[] suma(byte[] matrizA, byte[] matrizB) throws RemoteException;

    public byte[] resta(byte[] matrizA, byte[] matrizB) throws RemoteException;

    public byte[] calTraspuesta(byte[] matrizA) throws RemoteException;
  
    public byte[] calDeterminante(byte[] matrizA) throws RemoteException;
  
    public byte[] calInversa(byte[] matrizA) throws RemoteException;
	
	public boolean estaVivo() throws RemoteException;
}