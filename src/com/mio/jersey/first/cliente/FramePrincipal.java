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
import javax.swing.JTabbedPane;
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
	
	/**
	 * Panel de Login
	 */
	private JPanel panelLogin;
	
	/**
	 * Panel de pestañas
	 */
	private JTabbedPane panelPestañas;
	
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
	
	/**
	 * Referencia al panel de compañeros
	 */
	private PanelCompañeros panelCompañeros;

	/**
	 * Referencia al panel de compras
	 */
	private PanelCompras panelCompras;
	
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
	private void crearPanelesPestañas() {
		this.setBounds(100, 100, 790, 320);
		
		panelPestañas = new JTabbedPane();
		
		panelCompañeros = new PanelCompañeros(this);
		panelPestañas.addTab("Compañeros", panelCompañeros);
		
		panelCompras = new PanelCompras(this);
		panelPestañas.addTab("Compras", panelCompras);
		
		getContentPane().removeAll();
		getContentPane().add(panelPestañas);
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
					crearPanelesPestañas();
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
		}
	}

	/**
	 * Añade un usuario a la tabla que muestra la lista de los usuarios de la lista de correo
	 * @param usuario
	 */
	public void addUsuarioToTable(Usuario usuario)
	{
		panelCompañeros.addUsuarioToTable(usuario);
	}
	
	public static URI getBaseURI(){
		return UriBuilder.fromUri("http://localhost:8080/DSBCS_Tutorial_Jersey").build();
	}
}
