package com.mio.jersey.first.cliente;

import java.awt.Color;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.StringReader;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.ws.rs.core.MediaType;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import Modelos.Respuesta;
import Modelos.Usuario;
import Parsers.ParserRespuesta;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.representation.Form;

/**
 * Frame registrar de la interfaz de usuario. Permite registrar un usuario
 * en la lista de correo.
 * @author DavidGSola
 *
 */
@SuppressWarnings("serial")
public class FrameAddUsuario extends JFrame implements ActionListener
{
	/**
	 * Textfield donde escribir el nombre
	 */
	private JTextField jtfNombre;
	
	/**
	 * Textfield donde escribir el email
	 */
	private JTextField jtfEmail;
	
	/**
	 * Botón de registrar
	 */
	private JButton jbRegistrarse;
	
	/**
	 * Botón de cancelar el registro
	 */
	private JButton jbCancelar;
	
	/**
	 * Referencia al frame principal de la interfaz de usuario
	 */
	private FramePrincipal fPrincipal = null;

	/**
	 * Crea la aplicación
	 */
	public FrameAddUsuario(FramePrincipal principal) 
	{
		fPrincipal = principal;
		initialize();
	}

	/**
	 * Inicializa el panel principal
	 */
	private void initialize() {
		this.setBounds(100, 100, 580, 300);
		this.setLayout(null);
		
		JLabel jlNombre = new JLabel("Nombre:");
		jlNombre.setFont(new Font("Calibri", Font.PLAIN, (24)));
		jlNombre.setBounds(20, 10, 500, 90);
		this.add(jlNombre);
		
		jtfNombre = new JTextField();
		jtfNombre.setForeground(Color.GRAY);
		jtfNombre.setBounds(140, 30, 400, 45);
		jtfNombre.setMargin(new Insets((5),(10),(5),(5)));
		jtfNombre.setFont(new Font("Calibri", Font.PLAIN, (24)));
		jtfNombre.setColumns(10);
		this.add(jtfNombre);
		
		JLabel jlEmail = new JLabel("Email:");
		jlEmail.setFont(new Font("Calibri", Font.PLAIN, (24)));
		jlEmail.setBounds(20, 60, 500, 90);
		this.add(jlEmail);
		
		jtfEmail = new JTextField();
		jtfEmail.setForeground(Color.GRAY);
		jtfEmail.setBounds(140, 80, 400, 45);
		jtfEmail.setMargin(new Insets((5),(10),(5),(5)));
		jtfEmail.setFont(new Font("Calibri", Font.PLAIN, (24)));
		jtfEmail.setColumns(10);
		this.add(jtfEmail);
		
		jbRegistrarse = new JButton("Aceptar");
		jbRegistrarse.addActionListener(this);
		jbRegistrarse.setActionCommand("registrar");
		jbRegistrarse.setFont(new Font("Calibri", Font.PLAIN, (24)));
		jbRegistrarse.setBounds(380, 200, 160, 45);
		this.add(jbRegistrarse);	
		
		jbCancelar = new JButton("Cancelar");
		jbCancelar.addActionListener(this);
		jbCancelar.setActionCommand("cancelar");
		jbCancelar.setFont(new Font("Calibri", Font.PLAIN, (24)));
		jbCancelar.setBounds(200, 200, 160, 45);
		this.add(jbCancelar);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		String actionCommand = e.getActionCommand();
		if(actionCommand == "registrar")
		{
			Usuario usuario = null;
			if(jtfNombre.getText().length()!=0 && jtfEmail.getText().length()!=0)
				usuario = registrarUsuario(new Usuario(jtfNombre.getText(), jtfEmail.getText()) );
			else
				JOptionPane.showMessageDialog(this, "Debe rellenar todos los campos.", "Advertencia", JOptionPane.WARNING_MESSAGE);
			
			// Si se registra con exito lo añadimos a la tabla del frame principal
			if(usuario != null)
			{
				if(fPrincipal != null)
					fPrincipal.addUsuarioToTable(usuario);
				this.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
			}
		}
		else if(actionCommand == "cancelar")
		{
			this.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
		}
	}
	
	/**
	 * Registra un usuario en la lista de correo haciendo uso del servlet
	 * @param usuario
	 * @return
	 */
	private Usuario registrarUsuario(Usuario usuario)
	{
		ClientConfig config = new DefaultClientConfig();
		Client cliente = Client.create(config);
		WebResource servicio = cliente.resource(FramePrincipal.getBaseURI());
		
		Form f = new Form();
		f.add("email", usuario.getEmail());
		f.add("nombre", usuario.getNombre());
		String respuestaXML = servicio.path("rest").path("usuarios").accept(MediaType.TEXT_XML).post(String.class, f);

		SAXParserFactory spfac = SAXParserFactory.newInstance();

		try {
        	// Utilizamos dicha factoría para crear un objeto SAXParser
        	SAXParser sp = spfac.newSAXParser();
			
        	// Creamoe el handler del Parser para la Respuesta
			ParserRespuesta handler = new ParserRespuesta();
			
			// Parseamos el String
			sp.parse(new InputSource(new StringReader(respuestaXML)), handler);
			
			Respuesta respuesta = handler.getRespuesta();
			if(!respuesta.isError())
			{
				JOptionPane.showMessageDialog(this, "Usuario " + usuario.getNombre() + " registrado correctamente.");
				return usuario;
			}
			else if(respuesta.getMensaje().equalsIgnoreCase("Usuario existente"))
			{
				JOptionPane.showMessageDialog(this,
					    "No se ha podido registrar el usuario " + usuario.getNombre() + " debido a que ya existe el email.",
					    "Advertencia",
					    JOptionPane.WARNING_MESSAGE);
				return null;
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
		
		return null;
	}
}
