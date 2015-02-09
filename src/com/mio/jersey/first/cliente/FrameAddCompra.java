package com.mio.jersey.first.cliente;

import java.awt.Color;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.StringReader;
import java.util.Date;

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

import Modelos.Compra;
import Modelos.Respuesta;
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
public class FrameAddCompra extends JFrame implements ActionListener
{
	/**
	 * Textfield donde escribir el nombre
	 */
	private JTextField jtfNombre;
	
	/**
	 * Textfield donde escribir la descripción
	 */
	private JTextField jtfDescripcion;
	
	/**
	 * Botón de añadir compra
	 */
	private JButton jbAddCompra;
	
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
	public FrameAddCompra(FramePrincipal principal) 
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
		
		JLabel jlDescripcion = new JLabel("Descripci\u00F3n:");
		jlDescripcion.setFont(new Font("Calibri", Font.PLAIN, (24)));
		jlDescripcion.setBounds(20, 60, 500, 90);
		this.add(jlDescripcion);
		
		jtfDescripcion = new JTextField();
		jtfDescripcion.setForeground(Color.GRAY);
		jtfDescripcion.setBounds(140, 80, 400, 45);
		jtfDescripcion.setMargin(new Insets((5),(10),(5),(5)));
		jtfDescripcion.setFont(new Font("Calibri", Font.PLAIN, (24)));
		jtfDescripcion.setColumns(10);
		this.add(jtfDescripcion);
		
		jbAddCompra = new JButton("Aceptar");
		jbAddCompra.addActionListener(this);
		jbAddCompra.setActionCommand("registrar");
		jbAddCompra.setFont(new Font("Calibri", Font.PLAIN, (24)));
		jbAddCompra.setBounds(380, 200, 160, 45);
		this.add(jbAddCompra);	
		
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
			Compra compra= null;
			if(jtfNombre.getText().length()!=0 && jtfDescripcion.getText().length()!=0)
			{
				compra = registrarCompra(new Compra(fPrincipal.getUsuarioSesion(), jtfNombre.getText(), jtfDescripcion.getText(), new Date().getTime()+""));
			}
			else
				JOptionPane.showMessageDialog(this, "Debe rellenar todos los campos.", "Advertencia", JOptionPane.WARNING_MESSAGE);
			
			// Si se registra con exito lo anidimos a la tabla del frame principal
			if(compra != null)
			{
				if(fPrincipal != null)
					fPrincipal.actualizaTablaCompra();
				
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
	private Compra registrarCompra(Compra compra)
	{
		System.out.println(compra.getFecha());
		ClientConfig config = new DefaultClientConfig();
		Client cliente = Client.create(config);
		WebResource servicio = cliente.resource(FramePrincipal.getBaseURI());
		
		Form f = new Form();
		f.add("email", compra.getUsuario().getEmail());
		f.add("descripcion", compra.getDescripcion());
		f.add("nombre", compra.getNombre());
		String respuestaXML = servicio.path("rest").path("compras").accept(MediaType.TEXT_XML).post(String.class, f);

		SAXParserFactory spfac = SAXParserFactory.newInstance();

		try {
        	// Utilizamos dicha factoria para crear un objeto SAXParser
        	SAXParser sp = spfac.newSAXParser();
			
        	// Creamoe el handler del Parser para la Respuesta
			ParserRespuesta handler = new ParserRespuesta();
			
			// Parseamos el String
			sp.parse(new InputSource(new StringReader(respuestaXML)), handler);
			
			Respuesta respuesta = handler.getRespuesta();
			if(!respuesta.isError())
			{
				JOptionPane.showMessageDialog(this, "Compra " + compra.getNombre() + " registrado correctamente.");
				return compra;
			}
			else 
			{
				JOptionPane.showMessageDialog(this,
					    "No se ha podido a\u00F1adir la compra porque no existe el usuario.",
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
