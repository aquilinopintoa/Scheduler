package client;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;

import javax.swing.table.DefaultTableModel;
import java.io.*; 
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import calculadora.*;
import coordinador.*;


public class ClienteCalculo extends JFrame implements ActionListener {
	
	JFrame frame_inicio, frame_calculadora;
	JButton btnAcep, btnBuscar1, btnBuscar2,btnAceptar, btnVolver;
	JLabel  LblNombre, LblDireccion, LblPuerto, LblArchivo1, LblArchivo2, LblOperacion, LblUsuario;
	JTextField TxtNombre, TxtDireccion, TxtPuerto, TxtArchivo1, TxtArchivo2, TxtUsuario;
	JComboBox comboOperacion;
	JList lista1, lista2, lista3;
	JScrollPane barraDesplazamiento1, barraDesplazamiento2, barraDesplazamiento3;
	Object[] nombreColumnas, nombreColumnasAux;
	JTable table1, table2, table3;
	JScrollPane scrollPane1, scrollPane2, scrollPane3;
	Object [][] matriz1, matriz2, matriz3;
	String nombreDespachador, dirDespachador, nomDespPasivo, dirDespPasivo, usuario;
	int puertoDespachador, puertoDespPasivo;
	String Regexpnumeros="[0-9]*";
	
	
	public void Inicio(){
		frame_inicio = new JFrame();
		frame_inicio.setTitle("Calculadora de Matrices");
		frame_inicio.setLayout(null);
		frame_inicio.setSize(360,205);
		
		LblNombre=new JLabel("Nombre Despachador:");
		LblNombre.setBounds(20,15,210,20);
		frame_inicio.add(LblNombre);

	    	TxtNombre=new JTextField("");
	   	TxtNombre.setBounds(185,15,160,20);
	   	frame_inicio.add(TxtNombre);
	    	TxtNombre.addActionListener(this);
		
	   	LblDireccion=new JLabel("Direccion Despachador:");
	   	LblDireccion.setBounds(20,41,200,20);
	   	frame_inicio.add(LblDireccion);
	    
		TxtDireccion=new JTextField("");
		TxtDireccion.setBounds(195,41,150,20);
		frame_inicio.add(TxtDireccion);
	   	TxtDireccion.addActionListener(this);
		
		LblPuerto=new JLabel("Puerto Despachador:");
	   	LblPuerto.setBounds(20,67,210,20);
	    	frame_inicio.add(LblPuerto);
	    
		TxtPuerto=new JTextField("");
		TxtPuerto.setBounds(175,67,80,20);
		frame_inicio.add(TxtPuerto);
	   	TxtPuerto.addActionListener(this);
		
		LblUsuario=new JLabel("Usuario:");
		LblUsuario.setBounds(20,93,210,20);
	    	frame_inicio.add(LblUsuario);
	    
		TxtUsuario=new JTextField("");
		TxtUsuario.setBounds(85,93,140,20);
		frame_inicio.add(TxtUsuario);
		TxtUsuario.addActionListener(this);

		btnAcep=new JButton("Aceptar");
		btnAcep.setBounds(230,110,110,30);
		frame_inicio.add(btnAcep);
		btnAcep.addActionListener(this);
		
		frame_inicio.setVisible(true);
		frame_inicio.setLocationRelativeTo(null);
		frame_inicio.setDefaultCloseOperation(EXIT_ON_CLOSE);
	}
	
	public void Calculadora(){
		frame_calculadora=new JFrame();
		frame_calculadora.setTitle("Calculadora de Matrices");
		frame_calculadora.setLayout(null);
		frame_calculadora.setSize(830,410);
		
		LblArchivo1=new JLabel("Archivo1:");
		LblArchivo1.setBounds(10,15,150,20);
		frame_calculadora.add(LblArchivo1);

	    TxtArchivo1=new JTextField("");
	   	TxtArchivo1.setBounds(10,41,150,20);
	   	frame_calculadora.add(TxtArchivo1);
	    TxtArchivo1.addActionListener(this);

		btnBuscar1=new JButton("Buscar");
		btnBuscar1.setBounds(170,35,90,25);
		frame_calculadora.add(btnBuscar1);
		btnBuscar1.addActionListener(this);
		
	   	LblArchivo2=new JLabel("Archivo2:");
	    LblArchivo2.setBounds(300,15,150,20);
	    frame_calculadora.add(LblArchivo2);
	    
		TxtArchivo2=new JTextField("");
		TxtArchivo2.setBounds(300,41,150,20);
		frame_calculadora.add(TxtArchivo2);
	    TxtArchivo2.addActionListener(this);
		
		btnBuscar2=new JButton("Buscar");
		btnBuscar2.setBounds(460,35,90,25);
		frame_calculadora.add(btnBuscar2);
		btnBuscar2.addActionListener(this);


	    LblOperacion=new JLabel("Operaciones:");
	    LblOperacion.setBounds(580,15,150,20);
	    frame_calculadora.add(LblOperacion);
	    
		String[] opciones = {"Suma","Resta","Multiplicacion","Determinante","Traspuesta","Inversa","Almacenar","Consultar","Eliminar"};
		comboOperacion = new JComboBox(opciones);
		comboOperacion.setEditable(false);
		comboOperacion.setBounds(580,40,140,20);
		frame_calculadora.add(comboOperacion);	
		
		//1ERA TABLA CON LA 1ERA MATRIZ
		table1 = new JTable();
		table1.setEnabled(false);
		table1.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		table1.doLayout();
		scrollPane1 = new JScrollPane(table1);
		scrollPane1.setBounds(25,80,200,190);
		scrollPane1.setVisible(false);
		frame_calculadora.add(scrollPane1);
		
		//2DA TABLA CON LA 2DA MATRIZ
		table2 = new JTable();
		table2.setEnabled(false);
		table2.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		table2.doLayout();
		scrollPane2 = new JScrollPane(table2);
		scrollPane2.setBounds(315,80,200,190);
		scrollPane2.setVisible(false);
		frame_calculadora.add(scrollPane2);
		
		//3ERA TABLA CON LA 3RA MATRIZ
		table3 = new JTable();
		table3.setEnabled(false);
		table3.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		table3.doLayout();
		scrollPane3 = new JScrollPane(table3);
		scrollPane3.setBounds(605,80,200,190);
		scrollPane3.setVisible(false);
		frame_calculadora.add(scrollPane3);
		
		btnVolver=new JButton("Volver");
		btnVolver.setBounds(580,330,110,30);
		frame_calculadora.add(btnVolver);
		btnVolver.addActionListener(this);
		
		btnAceptar=new JButton("Aceptar");
		btnAceptar.setBounds(700,330,110,30);
		frame_calculadora.add(btnAceptar);
		btnAceptar.addActionListener(this);
		
		frame_calculadora.setVisible(true);
		frame_calculadora.setLocationRelativeTo(null);
		frame_calculadora.setDefaultCloseOperation(EXIT_ON_CLOSE);
	}
	
	// Lee un archivo y lo retorna como arreglo de bytes.
	private static byte[] archivoABytes(String archivo){
		try { 
			File file = new File(archivo); 
			byte buffer[] = new byte[(int)file.length()]; 
			BufferedInputStream input = new BufferedInputStream(new FileInputStream(archivo));
			input.read(buffer,0,buffer.length); 
			input.close();
				
			String s = new String(buffer, "UTF-8");
			System.out.println("El archivo "+archivo+ "Su contenido es \n "+s + "\n");
			return buffer; 
		}catch(Exception e){
			JOptionPane.showMessageDialog(null,"Error al abrir el archivo "+archivo, "Validaciones",JOptionPane.ERROR_MESSAGE);
			return null; 
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
            fichero = new FileWriter("client.policy");
            pw = new PrintWriter(fichero);
			pw.println(politica);
			fichero.close();
        }
        catch (Exception e) {
    	   	System.out.println("Error: No se ha podido configurar el Cliente.");
			System.exit(-1);
        }
		System.setProperty("java.security.policy", "client.policy");
	}

	// Retorna null en caso de que la matriz no sea valida.
	public Object [][] parserMatriz(byte[] matriz, String archivo){
		try {
			String s = new String(matriz, "UTF-8");
			String [] lineas = s.trim().split("\n");
			String [] aux;
			Object [][] resultado = null;
			int nroColumnas=-1;
			for(int i=0; i<lineas.length; i++){
				aux = lineas[i].trim().split(",");
				if(i!=0 && nroColumnas!=aux.length){
					JOptionPane.showMessageDialog(null,"El archivo "+archivo+" no esta bien formado", 
							"Validaciones",JOptionPane.ERROR_MESSAGE);
					return null;
				}
				if(i==0){
					nroColumnas = aux.length;
					resultado = new Object[lineas.length][nroColumnas];
				}
				for(int j=0; j<nroColumnas; j++){
					resultado[i][j]= aux[j];
				}
			}
			nombreColumnas = new Object[nroColumnas];
			for (int i=0; i<nroColumnas; i++)
				nombreColumnas[i] = i;
				
			return resultado;
				
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null,"Error al leer el archivo "+archivo, 
					"Validaciones",JOptionPane.ERROR_MESSAGE);
			return null;
		}
	}
	
	public boolean sonConsistente(Object[][] mat1, Object [][] mat2, String operacion){

		if (operacion.equals("Suma") || operacion.equals("Resta")){
			if (mat1.length != mat2.length || mat1[0].length != mat2[0].length){
				JOptionPane.showMessageDialog(null,"Error: Las matrices deben ser de igual dimension", 
						"Validaciones",JOptionPane.ERROR_MESSAGE);
				return false;
			}
			
		}else if (operacion.equals("Multiplicacion")){
			if (mat1[0].length != mat2.length){
				JOptionPane.showMessageDialog(null,"Error: El numero de columnas de la 1era matriz debe ser igual al numero de filas de la 2da matriz", 
						"Validaciones",JOptionPane.ERROR_MESSAGE);
				return false;
			}
			
		
		}else if (operacion.equals("Determinante") || operacion.equals("Inversa")){
			if (mat1.length != mat1[0].length){
				JOptionPane.showMessageDialog(null,"Error: La matriz debe ser cuadrada", 
						"Validaciones",JOptionPane.ERROR_MESSAGE);
					return false;
			}
			
		}

		return true;		
	}




	public boolean conectarDespachador(){
		Servidor despPasivo = null;
		int intentos = 0;
		
		while (intentos < 3) {
			despPasivo = obtenerDespachadorPasivo(this.nombreDespachador,this.dirDespachador,this.puertoDespachador);
			if (despPasivo != null){
				this.nomDespPasivo = despPasivo.getNombre();
				this.dirDespPasivo = despPasivo.getDireccion();
				this.puertoDespPasivo = despPasivo.getPuerto();
				return true;
			}
			intentos++;
		}

		JOptionPane.showMessageDialog(null,"Error: No se pudo conectar con el servidor", 
						"Validaciones",JOptionPane.ERROR_MESSAGE);
		return false;		
	}


	private static Servidor obtenerDespachadorPasivo(String nombre, String direccion, int puerto){
		Servidor serv = null;
		try {    
         		Registry registro = LocateRegistry.getRegistry(direccion,puerto);
           		InterDespachador desp = (InterDespachador) registro.lookup(nombre);
			serv = desp.getDespachadorPasivo();
			if (serv == null){
				System.out.println("El Despachador NO ha respondido la solicitud de obtener el despachador pasivo.");
				return null;
			}	
			System.out.println("El despachador me retorno el despachador pasivo: "+serv.getDireccion()+ " puerto: "+serv.getPuerto());

        	}catch (Exception e) {
            		System.out.println("Error: Se ha producido un error de conexion durante la solicitud de obtener el despachador pasivo.");
			return null;
       		}
		return serv;
	}

	private static Servidor obtenerServidorCalculo(String nombre, String direccion, int puerto){
		Servidor serv = null;
		try {    
         		Registry registro = LocateRegistry.getRegistry(direccion,puerto);
           		InterDespachador desp = (InterDespachador) registro.lookup(nombre);
			serv = desp.solicitarServidorCalculo();
			if (serv == null){
				System.out.println("El Despachador NO ha respondido.");
				return null;
			}
			System.out.println("El despachador me retorno el servidor con direccion: "+serv.getDireccion()+ " puerto: "+serv.getPuerto());
        	}catch (Exception e) {
            		System.out.println("Error: Se ha producido un error de conexion.");
			return null;
       		}
		return serv;
	}


	public byte[] operarMatrices(byte[] matrizA, byte[] matrizB, String operacion){
		
		byte[] respuesta = null;
		int intentos = 0;
		int aux = 0;
		Servidor serv = null;
		
		// Se inicia el cliente.
        	if (System.getSecurityManager() == null) 
           		System.setSecurityManager(new SecurityManager());
       	 	
		
        	//Se contacta al despachador al menos 3 veces si los servidores no responden
        	while (respuesta == null && aux < 3 ){
        	
        		// Se establece contacto con el Despachador
        		// Se reintenta establecer conexion con el despachador hasta 3 veces
        		while ( serv == null && intentos < 3 ) {
        			serv = obtenerServidorCalculo(this.nombreDespachador,this.dirDespachador,this.puertoDespachador);
        			intentos++;
        		}
    	
        		//Si el despachador no responde se contacta al despachador pasivo
        	 	if (serv == null){
        	 	 	//Se intercambia al despachador activo con el pasivo
        	  		String nombre = this.nombreDespachador;
        	 	 	String dir = this.dirDespachador;
        	  		int puerto = this.puertoDespachador;
        	  
        	  		this.nombreDespachador = this.nomDespPasivo;
        	  		this.dirDespachador = this.dirDespPasivo;
        	  		this.puertoDespachador = this.puertoDespPasivo;
        	  
        	  		this.nomDespPasivo = nombre;
        	  		this.dirDespPasivo = dir;
        	  		this.puertoDespPasivo = puerto;
        	  		intentos = 0;

        	  		while ( serv == null && intentos < 3 ) {
        	  			serv = obtenerServidorCalculo(this.nombreDespachador,this.dirDespachador,this.puertoDespachador);
        				intentos++;
        			}
			}
 
        
        		if (serv != null ){
        			// Se establece conexion con el servidor de calculo
				respuesta = Operar(serv, matrizA, matrizB, operacion);
	      		} else {
    		
        			JOptionPane.showMessageDialog(null,"Error al conectarse con el servidor", 
					"Validaciones",JOptionPane.ERROR_MESSAGE);
        			return null;
        		}
        	
        		aux++;
       		}
        
        	if (respuesta == null) {
    			JOptionPane.showMessageDialog(null,"Error al conectarse con el servidor", 
				"Validaciones",JOptionPane.ERROR_MESSAGE);
    			return null;
        	}
    	
		return respuesta;
	}
	
	public byte[] Operar(Servidor serv, byte[] matrizA, byte[] matrizB, String operacion) {
		byte[] respuesta = null;
		int intentos = 0 ;
		
		//Intenta conectarse con el servidor al menos 3 veces
		while ( intentos < 3 && respuesta == null ) { 
			try {
				Registry registro = LocateRegistry.getRegistry(serv.getDireccion(),serv.getPuerto());
				InterfaceCalculadora calc = (InterfaceCalculadora) registro.lookup(serv.getNombre());
				if (operacion.equals("Suma"))
        				respuesta = calc.suma(matrizA, matrizB);
    		
        			else if (operacion.equals("Resta"))
        				respuesta = calc.resta(matrizA, matrizB);
    		
        			else if (operacion.equals("Multiplicacion"))
        				respuesta = calc.multiplicar(matrizA, matrizB);

				else if (operacion.equals("Determinante"))
        				respuesta = calc.calDeterminante(matrizA);

				else if (operacion.equals("Traspuesta"))
        				respuesta = calc.calTraspuesta(matrizA);

				else if (operacion.equals("Inversa"))
        				respuesta = calc.calInversa(matrizA);

				if(respuesta== null){
					System.out.println("Error: Ha ocurrido un error en el Servidor de Calculo.");
					intentos++;
				} else {
					System.out.println("El servidor de calculo retorno la operacion de las matrices");
					return respuesta;
				}

			} catch (Exception e) {
				System.out.println("Error: El cliente no ha podido realizar sus operaciones correctamente.");
				intentos++;
			}
		}
		return respuesta;	
	}
	

	public boolean opAlmacenElim(byte[] matrizA, String operacion){
		boolean result = false;
		int intentos = 0;
		int aux = 0;
		
		// Se inicia el cliente.
        	if (System.getSecurityManager() == null) 
            		System.setSecurityManager(new SecurityManager());
     
   	
		//Ciclo en caso de que el despachador principal no responda se contacta al despachador pasivo
		while (aux < 2){

        		// Se establece contacto con el Despachador
        		// Se reintenta establecer conexion con el despachador hasta 3 veces
        		while (intentos < 3 ) {
        			try {    
         				Registry registro = LocateRegistry.getRegistry(this.dirDespachador,this.puertoDespachador);
           				InterDespachador desp = (InterDespachador) registro.lookup(this.nombreDespachador);

					if (operacion.equals("Almacenar"))
						result = desp.almacenarMatriz(this.usuario, matrizA);

					else if (operacion.equals("Eliminar"))
						result = desp.eliminarMatriz(this.usuario);

					if (result == false)
						System.out.println("El Despachador NO ha respondido a la solicitud de "+operacion);
					else{
						System.out.println("El despachador me respondio a la solicitud de "+operacion);
						return true;
					}

        			} catch (Exception e) {
            				System.out.println("Error: Se ha producido un error de conexion.");
       				}
        			intentos++;
        		}
    	
        		//Si el despachador no responde se contacta al despachador pasivo
        		if (result == false){
        	 	 	 //Se intercambia al despachador activo con el pasivo
        	 	 	String nombre = this.nombreDespachador;
        	 		String dir = this.dirDespachador;
        		  	int puerto = this.puertoDespachador;
        	 	 
        	 	 	this.nombreDespachador = this.nomDespPasivo;
        	 	 	this.dirDespachador = this.dirDespPasivo;
        	 		this.puertoDespachador = this.puertoDespPasivo;
        		  
        	 	 	this.nomDespPasivo = nombre;
        	 	 	this.dirDespPasivo = dir;
        	 	 	this.puertoDespPasivo = puerto;
        	 	 	intentos = 0;
        	 	 }		
        	 	
			aux++;
		}
    	
		JOptionPane.showMessageDialog(null,"Error al conectarse con el servidor.", 
				"Validaciones",JOptionPane.ERROR_MESSAGE);
		return false; 

	}
	
	public byte[] opConsultar(){
		byte[] respuesta = null;
		int intentos = 0;
		int aux = 0;
		
		// Se inicia el cliente.
        	if (System.getSecurityManager() == null) 
            		System.setSecurityManager(new SecurityManager());
        	
   	
		//Ciclo en caso de que el despachador principal no responda se contacta al despachador pasivo
		while ( aux < 2){

        		// Se establece contacto con el Despachador
        		// Se reintenta establecer conexion con el despachador hasta 3 veces
        		while ( intentos < 3 ) {
        			try {    
         				Registry registro = LocateRegistry.getRegistry(this.dirDespachador,this.puertoDespachador);
           				InterDespachador desp = (InterDespachador) registro.lookup(this.nombreDespachador);
					respuesta = desp.consultarMatriz(this.usuario);
					if (respuesta == null){
						System.out.println("El Despachador NO ha respondido a la solicitud de consulta");

					}else{
						System.out.println("El Despachador respondio la solicitud de consulta");
						return respuesta;
					}
        			}
        			catch (Exception e) {
            				System.out.println("Error: Se ha producido un error de conexion.");
       				}
        			intentos++;
        		}
    	
        		//Si el despachador no responde se contacta al despachador pasivo
        		if (respuesta == null){
        	 	 	 //Se intercambia al despachador activo con el pasivo
        	 	 	String nombre = this.nombreDespachador;
        	 		String dir = this.dirDespachador;
        			int puerto = this.puertoDespachador;
        	 	
        	 		this.nombreDespachador = this.nomDespPasivo;
        	 		this.dirDespachador = this.dirDespPasivo;
        	 		this.puertoDespachador = this.puertoDespPasivo;
        		  
        	 		this.nomDespPasivo = nombre;
        	 		this.dirDespPasivo = dir;
        	 		this.puertoDespPasivo = puerto;
        	 		intentos = 0;
        	 	 }		
        	 	 
			aux++;
		}
    	
		JOptionPane.showMessageDialog(null,"Error al conectarse con el servidor.", 
				"Validaciones",JOptionPane.ERROR_MESSAGE);
		return null; 

	}
	
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() ==btnAcep){
			if ( TxtNombre.getText().equals("")  || TxtDireccion.getText().equals("") 
			     || TxtPuerto.getText().equals("") || TxtUsuario.getText().equals("")){

				JOptionPane.showMessageDialog(null,"Debe llenar todos los campos", "Validaciones",JOptionPane.WARNING_MESSAGE);

			} else if(!TxtPuerto.getText().trim().matches(Regexpnumeros)){

				JOptionPane.showMessageDialog(null,"Puerto Despachador es un campo numerico", "Validaciones",JOptionPane.WARNING_MESSAGE);
				TxtPuerto.setText("");
			}else{
				
				this.nombreDespachador = TxtNombre.getText();
				this.dirDespachador = TxtDireccion.getText();
				this.puertoDespachador = Integer.parseInt(TxtPuerto.getText());
				this.usuario = TxtUsuario.getText();
				
				//Se configura el sistema
				configurarSistema();
				
				//Si logra conectarse con el despachador y obtiene la direccion del despachador pasivo
				if (conectarDespachador()){
					frame_inicio.setVisible(false);
					this.Calculadora();
				}
				
			}
			
		}else if (e.getSource() ==btnVolver){	
			frame_calculadora.setVisible(false);
			this.Inicio();
			
		}else if (e.getSource() ==btnBuscar1){
			
			JFileChooser fc = new JFileChooser();
		    	int respuesta = fc.showOpenDialog(this);
		    	if (respuesta == JFileChooser.APPROVE_OPTION) {
		    		//Crear un objeto File con el archivo elegido
		    		File archivoElegido = fc.getSelectedFile();
		    		//Mostrar el nombre del archvivo en un campo de texto
		    		TxtArchivo1.setText(archivoElegido.getAbsolutePath());
		    	}
			
		}else if (e.getSource() ==btnBuscar2){
			
			JFileChooser fc = new JFileChooser();
		    	int respuesta = fc.showOpenDialog(this);
		    	//Comprobar si se ha pulsado Aceptar
		    	if (respuesta == JFileChooser.APPROVE_OPTION) {
		    		//Crear un objeto File con el archivo elegido
		    		File archivoElegido = fc.getSelectedFile();
		    		//Mostrar el nombre del archvivo en un campo de texto
		    		TxtArchivo2.setText(archivoElegido.getAbsolutePath());
		    	}
			
		}else if (e.getSource() ==btnAceptar){
			//Se limpia la pantalla
			scrollPane1.setVisible(false);
			scrollPane2.setVisible(false);
			scrollPane3.setVisible(false);
			
			//Dependiendo de la operacion seleccionada se hacen las validaciones de los campos
			String operacion = String.valueOf(comboOperacion.getSelectedItem());
			//Caso: operacion seleccionada es suma, resta o multiplicacion
			if (operacion.equals("Suma") || operacion.equals("Resta") || operacion.equals("Multiplicacion")) {
				if ( TxtArchivo1.getText().equals("")  || TxtArchivo2.getText().equals("") ){
					JOptionPane.showMessageDialog(null,"Debe llenar los dos campos correspondientes a los archivos", 
							"Validaciones",JOptionPane.WARNING_MESSAGE);
				}else{
					//Se parsean los dos archivos
					byte[] arch1 = archivoABytes(TxtArchivo1.getText());
					byte[] arch2 = archivoABytes(TxtArchivo2.getText());
					
					if (arch1 != null && arch2 != null) {
						matriz1 = parserMatriz(arch1,TxtArchivo1.getText());
						nombreColumnasAux = nombreColumnas;
						matriz2 = parserMatriz(arch2,TxtArchivo2.getText());
		
						//Se verifica que los archivos esten bien formados
						if (matriz1 != null && matriz2 != null && sonConsistente(matriz1,matriz2,operacion)) {
							//Se muestra la 1era matriz en pantalla
							DefaultTableModel dtm1= new DefaultTableModel(matriz1,nombreColumnasAux);
							table1.setModel(dtm1);
							scrollPane1.setVisible(true);

							//Se muestra la 2da matriz en pantalla
							DefaultTableModel dtm2= new DefaultTableModel(matriz2,nombreColumnas);
							table2.setModel(dtm2);
							scrollPane2.setVisible(true);
							
							//Se llama al procedimiento remoto
							byte[] result = operarMatrices(arch1,arch2,operacion);
							
							//Si el servidor respondio exitosamente
							if (result != null) {
								//Se muestra la matriz resultado
								matriz3 = parserMatriz(result,"MatrizResultado");
								DefaultTableModel dtm3= new DefaultTableModel(matriz3,nombreColumnas);
								table3.setModel(dtm3);
								scrollPane3.setVisible(true);


							}
						}
					}
				}
				
			//Caso: Operacion seleccionada es determinante, traspuesta o inversa
			}else if (operacion.equals("Determinante") || operacion.equals("Traspuesta") || operacion.equals("Inversa")){
				if ( TxtArchivo1.getText().equals("") ){
					JOptionPane.showMessageDialog(null,"Debe llenar el 1er campo correspondiente al archivo", 
							"Validaciones",JOptionPane.WARNING_MESSAGE);
				}else{
					//Se parsea el archivo
					byte[] arch1 = archivoABytes(TxtArchivo1.getText());
					
					if (arch1 != null) {
						matriz1 = parserMatriz(arch1,TxtArchivo1.getText());
		
						//Se verifica que el archivo este bien formados
						if (matriz1 != null && sonConsistente(matriz1,null,operacion)) {
							//Se muestra la 1era matriz en pantalla
							DefaultTableModel dtm1= new DefaultTableModel(matriz1,nombreColumnas);
							table1.setModel(dtm1);
							scrollPane1.setVisible(true);
				    	
							//Se llama al procedimiento remoto
							byte[] result = operarMatrices(arch1,null,operacion);
							
							
							if (result != null) {
								try {    
         								String s = new String(result, "UTF-8");
									String error = "Error:\n Determinante de la matriz igual a 0. NO se puede calcular la matriz inversa.\n";
									if (operacion.equals("Inversa") && s.equals(error)){
										JOptionPane.showMessageDialog(null,"Error: Determinante de la matriz igual a 0. NO se puede calcular la matriz inversa.", 
										"Validaciones",JOptionPane.ERROR_MESSAGE);
									}else {
									
										//Se muestra la matriz resultado
										matriz3 = parserMatriz(result,"MatrizResultado");
										DefaultTableModel dtm3= new DefaultTableModel(matriz3,nombreColumnas);
										table3.setModel(dtm3);
										scrollPane3.setVisible(true);
								}

        							} catch (Exception excep) {
            								JOptionPane.showMessageDialog(null,"Error: Ocurrio un fallo durante la operacion", 
										"Validaciones",JOptionPane.ERROR_MESSAGE);
       								}

							}
						}
					}
				}

			//Caso: Operacion seleccionada Almacenar
			}else if ( operacion.equals("Almacenar")){
				if ( TxtArchivo1.getText().equals("") ){
					JOptionPane.showMessageDialog(null,"Debe llenar el 1er campo correspondiente al archivo", 
							"Validaciones",JOptionPane.WARNING_MESSAGE);
				}else{

					//Se parsea el archivo
					byte[] arch1 = archivoABytes(TxtArchivo1.getText());
					
					if (arch1 != null) {
						matriz1 = parserMatriz(arch1,TxtArchivo1.getText());
		
						//Se verifica que el archivo este bien formados
						if (matriz1 != null && sonConsistente(matriz1,null,operacion)) {
							//Se muestra la 1era matriz en pantalla
							DefaultTableModel dtm1= new DefaultTableModel(matriz1,nombreColumnas);
							table1.setModel(dtm1);
							scrollPane1.setVisible(true);
				    	
							//Se llama al procedimiento remoto
							boolean result = opAlmacenElim(arch1,"Almacenar");
							
							
							if (result != false) {
								JOptionPane.showMessageDialog(null,"La matriz se almaceno correctamente", 
								"Validaciones",JOptionPane.INFORMATION_MESSAGE);
							}
						}
					}


				}
			//Caso: Operacion seleccionada Consultar
			}else if ( operacion.equals("Consultar") ){
				//Se llama al procedimiento remoto
				byte[] result = opConsultar();
				
				if (result != null) {
					//Se muestra la matriz consultada
					matriz3 = parserMatriz(result,"MatrizResultado");
					DefaultTableModel dtm3= new DefaultTableModel(matriz3,nombreColumnas);
					table3.setModel(dtm3);
					scrollPane3.setVisible(true);
				}


			//Caso: Operacion seleccionada Eliminar
			}else if (operacion.equals("Eliminar") ){
				//Se llama al procedimiento remoto
				boolean result = opAlmacenElim(null,"Eliminar");
				
				if (result != false) {
					JOptionPane.showMessageDialog(null,"La matriz se elimino correctamente", 
						"Validaciones",JOptionPane.INFORMATION_MESSAGE);
				}
			}
			
		}
	}

	public static void main(String[] args) {
		ClienteCalculo inter = new ClienteCalculo();
		inter.Inicio();

	}

}
