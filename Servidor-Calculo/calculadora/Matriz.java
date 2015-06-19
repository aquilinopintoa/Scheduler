package calculadora;

public class Matriz {

	// Retorna null en caso de que la matriz no sea valida.
	public double [][] parserMatriz(byte[] matriz) throws ExcepcionDeFormato{
		try {
			String s = new String(matriz, "UTF-8");
			String [] lineas = s.trim().split("\n");
			String [] aux;
			double [][] resultado = null;
			int nroColumnas=-1;
			for(int i=0; i<lineas.length; i++){
				aux = lineas[i].trim().split(",");
				if(i!=0 && nroColumnas!=aux.length){
					throw new ExcepcionDeFormato("Error:\nEl formato de la matriz NO es correcto.");
				}
				if(i==0){
					nroColumnas = aux.length;
					resultado = new double[lineas.length][nroColumnas];
				}
				for(int j=0; j<nroColumnas; j++){
					resultado[i][j]= Double.parseDouble(aux[j]);
				}
			}
			return resultado;
		} 
		catch (Exception e) {
			throw new ExcepcionDeFormato("Error:\nEl formato de la matriz NO es correcto.");
		}
	}

	// Recibe las dos matrices, y un booleano. Si el booleano es "true", indica que es suma.
	// Si es "false" indica resta.
	public byte[] sumaYResta(double [][] a, double [][] b, boolean indicador)throws ExcepcionMatematica{
		double suma = 0;
		String resultado= "";
		int parametro = indicador?1:-1;
		int nroFilas;
		int nroColumnas;
		if (a.length != b.length || a[0].length != b[0].length){
			String msg = indicador?"Suma":"Resta"; 
			throw new ExcepcionMatematica("Error:\nLas Dimensiones de las matrices (" 
				+ a.length + "x" + a[0].length + ") y (" + b.length + "x" + b[0].length 
				+ ") NO permiten la " + msg + ".\n");
		}
		nroFilas = a.length;
		nroColumnas	= a[0].length;	
		for(int i=0; i<nroFilas; i++){
			for(int j=0; j<nroColumnas; j++){
				resultado = resultado + (a[i][j] + (parametro * b[i][j]));
				resultado = (j!=nroColumnas-1)?(resultado + ","):(resultado + "\n");
			}
		}
		return stringAByte(resultado);
	}

	// Retorna un arreglo de bytes con la matriz resultado en el formato establecido.
	public byte[] producto(double [][] a, double [][] b) throws ExcepcionMatematica{
		double suma = 0;
		String resultado= "";
		if (a[0].length != b.length){
			throw new ExcepcionMatematica("Error:\nLas Dimensiones de las matrices (" 
				+ a.length + "x" + a[0].length + ") y (" + b.length + "x" + b[0].length 
				+ ") NO permiten la Multiplicacion.\n");
		}
		for(int i=0; i<a.length; i++){
			for(int j=0; j<b[0].length; j++){
				for(int k=0; k<b.length; k++){
					suma+= a[i][k] * b[k][j];
				}
				resultado = resultado + suma;
				resultado = (j!=b[0].length -1)?(resultado + ","):(resultado + "\n");
				suma = 0;
			}
		}
		return stringAByte(resultado);
	}
	
	// Calcula la traspuesta de una matriz.
	public byte[] traspuesta(double[][] m){
		double[][] resultado = matrizTranspuesta(m);
		return stringAByte(matrizAString(resultado));
	}
	
	// Calcula el determinante de una matriz.
	public byte[] determinante(double[][] m) throws ExcepcionMatematica{
		if (m[0].length != m.length){
			throw new ExcepcionMatematica("Error:\nLa matriz de dimension" 
				+ m.length + "x" + m[0].length + " NO es CUADRADA y no se puede calcular el determinante.\n");
		}
		double resultado = calDeterminante(m);
		return stringAByte(Double.toString(resultado));
	}
	
	// Calcula la inversa de una matriz.
	public byte[] inversa(double[][] m) throws ExcepcionMatematica{
		if (m[0].length != m.length){
			throw new ExcepcionMatematica("Error:\nLa matriz de dimension" 
				+ m.length + "x" + m[0].length + " NO es CUADRADA y no se puede calcular la inversa.\n");
		}
		double determ = calDeterminante(m);
		if (determ == 0){
			throw new ExcepcionMatematica("Error:\n Determinante de la matriz igual a 0. NO se puede calcular la matriz inversa.\n");
		}
		double det=1/determ;
		double[][] nmatriz=matrizAdjunta(m);
		multiplicarMatriz(det,nmatriz);
		return stringAByte(matrizAString(nmatriz));
	}

	
/*
** Las siguientes son funciones auxiliares.
*/
	private byte[] stringAByte(String s){
		try{
			return s.getBytes("UTF-8");
		}
		catch (Exception e){
			return null;
		}
	}

	private String matrizAString(double[][] m){
		String resultado="";
		for(int i=0; i< m.length; i++){
			for(int j=0; j<m[0].length; j++){
				resultado = resultado + m[i][j];
				resultado = (j!=m[0].length-1)?(resultado + ","):(resultado + "\n");
			}
		}
		return resultado;
	}
	
	private double calDeterminante (double [][] matriz){
        double determinante = 0.0;
        int filas = matriz.length;
        int columnas = matriz[0].length;
		
        // En caso de que sea una matriz 1x1
        if ((filas==1) && (columnas==1))
			return matriz[0][0];
		
        int signo=1;
        for (int col=0;col<columnas;col++){
		
			// Obtiene el adjunto de fila=0, columna=columna, pero sin el signo.
             double[][] submatriz = getSubmatriz(matriz, filas, columnas, col);
             determinante = determinante + signo*matriz[0][col]*calDeterminante(submatriz);
			 signo*=-1;
        }
		return determinante;
    }
 
    private double[][] getSubmatriz(double[][] matriz,int filas,int col,int columna) {
         double [][] submatriz = new double[filas-1][col-1];
         int contador=0;
   
         for (int x=0;x<col;x++)
         {
             if (x==columna) continue;         
             for (int y=1;y<filas;y++){
                 submatriz[y-1][contador]=matriz[y][x];
             }
			contador++;
         }
		return submatriz;
	}

	private void multiplicarMatriz(double n, double[][] matriz) {
		for(int i=0;i<matriz.length;i++)
			for(int j=0;j<matriz.length;j++)
				matriz[i][j]*=n;
	}

	private double[][] matrizAdjunta(double [][] matriz){
		return matrizTranspuesta(matrizCofactores(matriz));
	}
	
	private double[][] matrizCofactores(double[][] matriz){
		double[][] nm=new double[matriz.length][matriz.length];
		for(int i=0;i<matriz.length;i++) {
			for(int j=0;j<matriz.length;j++) {
				double[][] det=new double[matriz.length-1][matriz.length-1];
				double detValor;
				for(int k=0;k<matriz.length;k++) {
					if(k!=i) {
						for(int l=0;l<matriz.length;l++) {
							if(l!=j) {
							int indice1=k<i ? k : k-1 ;
							int indice2=l<j ? l : l-1 ;
							det[indice1][indice2]=matriz[k][l];
							}
						}
					}
				}
				detValor=calDeterminante(det);
				nm[i][j]=detValor * (double)Math.pow(-1, i+j+2);
			}
		}
		return nm;
	}
 
	private double[][] matrizTranspuesta(double [][] matriz){
		double[][]nuevam =new double[matriz[0].length][matriz.length];
		for(int i=0; i<matriz[0].length; i++){
			for(int j=0; j<matriz.length; j++){
				nuevam[i][j]=matriz[j][i];
			}
		}
		return nuevam;
	}
	
}


