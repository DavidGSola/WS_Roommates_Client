package com.mio.jersey.first.cliente;
 
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import Modelos.Usuario;
import Parsers.ParserUsuario;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;

/**
 * Frame principal de la interfaz de usuario. Mantiene una lista con todos los
 * usuarios de la lista de correo. Permite eliminar, registrar y actualizar 
 * los usuarios de la lista de correo.
 * @author DavidGSola
 *
 */
public class FramePrincipal extends JFrame implements ActionListener
{
	
	private JPanel panelLogin;
	
	/**
	 * Area de texto para introducir el nombre
	 * del usuario
	 */
	private JTextArea areaNick;
	
	/**
	 * Boton para login
	 */
	private JButton jbLogin;

	/**
	 * Boton para registrarse en la pantalla de inicio
	 */
	private JButton jbRegistrarInicio;
	
	
	private JPanel panelPrincipal;
	
	/**
	 * Referencia al frame que permite registrar un usuario
	 */
//	private FrameRegistrar fRegistrar;
	
	/**
	 * Botón de registrar un usuario
	 */
	private JButton jbRegistrarse;
	
	/**
	 * Botón de eliminar un usuario
	 */
	private JButton jbEliminar;
	
	/**
	 * Botón de actualizar un usuario
	 */
	private JButton jbActualizar;
	
	/**
	 * Botón de salir
	 */
	private JButton jbSalir;
	
	/**
	 * Tabla para mostrar la lista de usuarios
	 */
	private JTable jtUsuarios;
	
	/**
	 * Scrollpane que permite hacer scrollable la tabla {@linkplain jtUsuarios}
	 */
	private	JScrollPane scrollPane;
	
	/**
	 * Lista de usuarios de la lista de correos
	 */
	private ArrayList<Usuario> listaUsuarios = new ArrayList<Usuario>();
	
	/**
	 * Usuario con la sesión activa
	 */
	private Usuario usuarioSesion;
	
	public static void main(String[] args) 
	{
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try 
				{
					// Inicia el frame principal
					FramePrincipal frame = new FramePrincipal();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Crea la aplicación
	 */
	public FramePrincipal() 
	{
		crearPanelLogin();
	}


	/**
	 * Inicializa el panel principal
	 */
	private void crearPanelLogin() {
		this.setBounds(300,300,245,450);
		
		panelLogin = new JPanel();
		panelLogin.setBounds(300, 300, 245, 450);
		panelLogin.setLayout(null);
		
		areaNick = new JTextArea();
		areaNick.setBounds(10, 57, 204, 65);
		areaNick.setFont(new Font("Tahoma", Font.PLAIN, 24));
		areaNick.setMargin(new Insets(10, 10, 10, 10));
		areaNick.addKeyListener(new KeyAdapter()
		{
			@Override
			public void keyPressed(KeyEvent key)
			{
				if(KeyEvent.VK_ENTER == key.getKeyCode())
				{
					key.consume();
					jbLogin.doClick();
				}
			}
		});
		panelLogin.add(areaNick);
	
		jbLogin = new JButton("Conectar");
		jbLogin.setBounds(10, 133, 204, 65);
		jbLogin.setActionCommand("iniciar");
		jbLogin.setFont(new Font("Tahoma", Font.BOLD, 18));
		jbLogin.addActionListener(this);
		panelLogin.add(jbLogin);
		
		JLabel labelEscribaSuEmail = new JLabel("Email:");
		labelEscribaSuEmail.setVerticalAlignment(SwingConstants.BOTTOM);
		labelEscribaSuEmail.setFont(new Font("Tahoma", Font.ITALIC, 20));
		labelEscribaSuEmail.setHorizontalAlignment(SwingConstants.LEFT);
		labelEscribaSuEmail.setBounds(10, 11, 204, 35);
		panelLogin.add(labelEscribaSuEmail);
		
		jbRegistrarInicio = new JButton("Registrarse");
		jbRegistrarInicio.setBounds(10, 215, 204, 65);
		jbRegistrarInicio.setActionCommand("registrarInicio");
		jbRegistrarInicio.setFont(new Font("Tahoma", Font.BOLD, 18));
		jbRegistrarInicio.addActionListener(this);
		panelLogin.add(jbRegistrarInicio);

		getContentPane().add(panelLogin);
	}
	
	/**
	 * Inicializa el panel principal
	 */
	private void crearPanelPrincipal() {
		this.setBounds(100, 100, 790, 300);
		
		panelPrincipal = new JPanel();
		panelPrincipal.setBounds(100, 100, 790, 300);
		panelPrincipal.setLayout(null);
		
		// Modelo de la tabla de los usuarios
		DefaultTableModel tableModel = new DefaultTableModel(new Object[]{"Nombre", "Email"}, 0) 
		{
		    @Override
		    public boolean isCellEditable(int row, int column) 
		    {
		       return false;
		    }
		};
		
		jtUsuarios = new JTable(tableModel);
		jtUsuarios.setFont(new Font("Calibri", Font.PLAIN, 16));
		jtUsuarios.setBounds(20, 10, 540, 180);
		
		scrollPane = new JScrollPane(jtUsuarios);
		scrollPane.setBounds(20, 10, 540, 240);
		panelPrincipal.add(scrollPane);
	
		// Rellenamos la tabla con los usuarios de la base de datos
		rellenarTabla();
		
		jbRegistrarse = new JButton("Registrar");
		jbRegistrarse.addActionListener(this);
		jbRegistrarse.setActionCommand("registrar");
		jbRegistrarse.setFont(new Font("Calibri", Font.PLAIN, (16)));
		jbRegistrarse.setBounds(580, 10, 180, 30);
		panelPrincipal.add(jbRegistrarse);	
	
		jbActualizar = new JButton("Actualizar");
		jbActualizar.addActionListener(this);
		jbActualizar.setActionCommand("actualizar");
		jbActualizar.setFont(new Font("Calibri", Font.PLAIN, (16)));
		jbActualizar.setBounds(580, 50, 180, 30);
		panelPrincipal.add(jbActualizar);
		
		jbEliminar = new JButton("Eliminar");
		jbEliminar.addActionListener(this);
		jbEliminar.setActionCommand("eliminar");
		jbEliminar.setFont(new Font("Calibri", Font.PLAIN, (16)));
		jbEliminar.setBounds(580, 90, 180, 30);
		panelPrincipal.add(jbEliminar);		
		
		jbSalir = new JButton("Salir");
		jbSalir.addActionListener(this);
		jbSalir.setActionCommand("salir");
		jbSalir.setFont(new Font("Calibri", Font.PLAIN, (16)));
		jbSalir.setBounds(580, 215, 180, 30);
		panelPrincipal.add(jbSalir);

		getContentPane().removeAll();
		getContentPane().add(panelPrincipal);
	}
	
	@Override
	public void actionPerformed(ActionEvent event) 
	{
		String actionCommand = event.getActionCommand();
		
		if(actionCommand.equals("iniciar"))
		{
			//Create a "parser factory" for creating SAX parsers
	        // Creamos una factoria de Parser
			SAXParserFactory spfac = SAXParserFactory.newInstance();

			try {
	        	// Utilizamos dicha factoría para crear un objeto SAXParser
	        	SAXParser sp = spfac.newSAXParser();
				
	        	// Creamoe el handler del Parser para los Usuarios
				ParserUsuario handler = new ParserUsuario();
				
				// Hacemos la llamada al servicio web que nos devolverá un XML con los Usuarios
				ClientConfig config = new DefaultClientConfig();
				Client cliente = Client.create(config);
				WebResource servicio = cliente.resource(getBaseURI());
				String usuarioXML = servicio.path("rest").path("usuarios/"+areaNick.getText()).accept(MediaType.TEXT_XML).get(String.class);
				
				// Parseamos el String
				sp.parse(new InputSource(new StringReader(usuarioXML)), handler);
				ArrayList<Usuario> usuarios = handler.getList();
				
				if(usuarios.size() == 1)
				{
					usuarioSesion = usuarios.get(0);
					crearPanelPrincipal();
				}
			
			} catch (ParserConfigurationException e) {
				e.printStackTrace();
			} catch (SAXException e) {
				e.printStackTrace();
			} catch (UniformInterfaceException e) {
				e.printStackTrace();
			} catch (ClientHandlerException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}else if(actionCommand.equals("registrarInicio"))
		{
			// Mostramos el frame para registrar usuarios
			FrameRegistrar fRegistrar = new FrameRegistrar(null);
			fRegistrar.setVisible(true);
		}else if(actionCommand.equals("registrar"))
		{
			// Mostramos el frame para registrar usuarios
			FrameRegistrar fRegistrar = new FrameRegistrar(this);
			fRegistrar.setVisible(true);
			
		}else if(actionCommand.equals("eliminar"))
		{
			// Obtenemos el indice de la fila seleccionada en la tabla
			int index = jtUsuarios.getSelectedRow();
			
			// Comprobamos que se hay seleccionado un usuario
			if(index == -1)
				JOptionPane.showMessageDialog(this, "Debe seleccionar un usuario de la lista.", "Advertencia", JOptionPane.WARNING_MESSAGE);
			else
			{
				// Eliminamos el usuario llamado al servlet
				boolean eliminado = eliminarUsuario(listaUsuarios.get(index));
				
				if(eliminado)
				{
					DefaultTableModel model = (DefaultTableModel) jtUsuarios.getModel();
					
					// Eliminamos el usuario del modelo de la tabla y de la lista interta
					model.removeRow(index);
					listaUsuarios.remove(index);
				}
			}
		}else if(actionCommand.equals("actualizar"))
		{
			// Obtenemos el indice de la fila seleccionada en la tabla
			int index = jtUsuarios.getSelectedRow();
			
			if(index == -1)
				JOptionPane.showMessageDialog(this, "Debe seleccionar un usuario de la lista.", "Advertencia", JOptionPane.WARNING_MESSAGE);
			else
			{
//				FrameActualizar fActualizar = new FrameActualizar(this,  listaUsuarios.get(index));
//				fActualizar.setVisible(true);
			}
			
		}else if(actionCommand.equals("salir"))
		{
			this.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
		}
	}
	
	/**
	 * Obtiene y rellena la tabla con la lista de los usuarios haciendo una llamada al servlet
	 */
	private void rellenarTabla()
	{
		//Create a "parser factory" for creating SAX parsers
        // Creamos una factoria de Parser
		SAXParserFactory spfac = SAXParserFactory.newInstance();

		try {
        	// Utilizamos dicha factoría para crear un objeto SAXParser
        	SAXParser sp = spfac.newSAXParser();
			
        	// Creamoe el handler del Parser para los Usuarios
			ParserUsuario handler = new ParserUsuario();
			
			// Hacemos la llamada al servicio web que nos devolverá un XML con los Usuarios
			ClientConfig config = new DefaultClientConfig();
			Client cliente = Client.create(config);
			WebResource servicio = cliente.resource(getBaseURI());
			String usuariosXML = servicio.path("rest").path("usuarios").accept(MediaType.TEXT_XML).get(String.class);
			
			// Parseamos el String
			sp.parse(new InputSource(new StringReader(usuariosXML)), handler);
			
			// Leemos los usuarios a través del handler
			ArrayList<Usuario> usuarios = handler.getList();
			
			// Añadimos la lista a la lista interna de los usuarios
			listaUsuarios.addAll(usuarios);
			
			// Añadimos los usuarios a la tabla
			DefaultTableModel model = (DefaultTableModel) jtUsuarios.getModel();
			for(Usuario usuario : usuarios)
				model.addRow(new Object[]{usuario.getNombre(), usuario.getEmail()});
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (UniformInterfaceException e) {
			e.printStackTrace();
		} catch (ClientHandlerException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Elimina un usuario dado de la lista de correo
	 * @param usuario Usuario a eliminar
	 * @return Exito de la operación
	 */
	private boolean eliminarUsuario(Usuario usuario)
	{
//		try
//		{
//			// Seleccionamos la acción a realizar
//			String accion = "accion=eliminar&";
//			// Añadimos el id del usuario para que lo sepa el Servlet
//			String id = "id=" + usuario.getId() + "&";
//			
//			URL gwtServlet = new URL("http://localhost:8080/DSBCS_Practica2/ListaCorreoServlet");
//			
//			HttpURLConnection servletConnection = (HttpURLConnection) gwtServlet
//					.openConnection();
//			servletConnection.setUseCaches(false);
//			servletConnection.setRequestMethod("POST");
//			servletConnection.setDoOutput(true);
//			OutputStream output = servletConnection.getOutputStream();
//			
//			output.write(accion.getBytes());
//			output.write(id.getBytes());
//			
//			output.flush();
//			output.close();
//			
//			// Leemos la respuesta
//			String answer = servletConnection.getContentType();
//			
//			if(answer.equalsIgnoreCase("Correcto")) 
//			{
//				JOptionPane.showMessageDialog(this, "Usuario " + usuario.getNombre() + " eliminado correctamente.");
//				
//				return true;
//			}
//		} catch (MalformedURLException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
		
		return false;
	}
	
	/**
	 * Añade un usuario a la tabla que muestra la lista de los usuarios de la lista de correo
	 * @param usuario
	 */
	public void addUsuarioToTable(Usuario usuario)
	{
		// Añadimos el usuario a la lista interna
		listaUsuarios.add(usuario);
		
		// Añadimos el usuario al modelo de la tabla para que se muestre
		DefaultTableModel model = (DefaultTableModel) jtUsuarios.getModel();
		model.addRow(new Object[]{usuario.getNombre(), usuario.getEmail()});
	}
		
	/**
	 * Actualiza el email de un usuario en la tabla
	 * @param usuario
	 * @param email
	 */
	public void actualizarUsuarioEmailTable(Usuario usuario, String email)
	{
		int index = listaUsuarios.indexOf(usuario);
		
		// Actualizamos el usuario del modelo de la tabla
		DefaultTableModel model = (DefaultTableModel) jtUsuarios.getModel();
		model.setValueAt(email, index, 2);
	}
	
	public static URI getBaseURI(){
		return UriBuilder.fromUri("http://localhost:8080/DSBCS_Tutorial_Jersey").build();
	}
}
