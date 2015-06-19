package coordinador;

import java.util.ArrayList;

public class EstadoDespachador implements java.io.Serializable{

	private ArrayList<Servidor> ListServCalculo;     // Representa la lista de servidores de calculo activos
	private ArrayList<Servidor> ListServAlmacen;     // Representa la lista de servidores de almacenamiento activos
	private ArrayList<TablaArchivos> matrices;   // Indicara la direccion de los servidores donde se almacenan las matrices
	private int indice;   							 /* El balanceo de carga del servidor de calculo se hace con round-robin.
													 ** por lo tanto "indice", muestra quien es al que le corresponde atenter la peticion. 
													 */

	public EstadoDespachador(ArrayList<Servidor> ListServCalculo, 
		ArrayList<Servidor> ListServAlmacen, ArrayList<TablaArchivos> matrices, int indice){

		this.ListServCalculo = ListServCalculo;
		this.ListServAlmacen = ListServAlmacen;
		this.matrices = matrices;
		this.indice = indice; 
	}

	public ArrayList<Servidor> getListServCalculo(){
		return this.ListServCalculo;
	}

	public ArrayList<Servidor> getListServAlmacen(){
		return this.ListServAlmacen;
	}

	public ArrayList<TablaArchivos> getMatrices(){
		return this.matrices;
	}

	public int getIndice(){
		return this.indice;
	}

}