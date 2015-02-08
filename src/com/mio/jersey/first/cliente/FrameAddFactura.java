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

import Modelos.Factura;
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
 * Frame para crear una nueva factura.
 * @author Antonio Toro
 *
 */
@SuppressWarnings("serial")
public class FrameAddFactura extends JFrame implements ActionListener
{
	/**
	 * Textfield donde escribir el nombre
	 */
	private JTextField jtfNombre;
	
	/**
	 * Textfield donde escribir la descripcion
	 */
	private JTextField jtfDescripcion;
	
	/**
	 * Textfield donde escribir la cantidad
	 */
	private JTextField jtfCantidad;
	
	/**
	 * Boton de aniadir factura
	 */
	private JButton jbAdd;
	
	/**
	 * Boton de cancelar el registro
	 */
	private JButton jbCancelar;
	
	/**
	 * Referencia al frame principal de la interfaz de usuario
	 */
	private FramePrincipal fPrincipal = null;

	/**
	 * Crea la aplicacion
	 */
	public FrameAddFactura(FramePrincipal principal) {
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
		
		JLabel jlDescripcion = new JLabel("Descripcion:");
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
		
		JLabel jlCantidad = new JLabel("Cantidad:");
		jlCantidad.setFont(new Font("Calibri", Font.PLAIN, (24)));
		jlCantidad.setBounds(20, 110, 500, 90);
		this.add(jlCantidad);
		
		jtfCantidad = new JTextField();
		jtfCantidad.setForeground(Color.GRAY);
		jtfCantidad.setBounds(140, 130, 400, 45);
		jtfCantidad.setMargin(new Insets((5),(10),(5),(5)));
		jtfCantidad.setFont(new Font("Calibri", Font.PLAIN, (24)));
		jtfCantidad.setColumns(10);
		this.add(jtfCantidad);
		
		jbAdd = new JButton("Aceptar");
		jbAdd.addActionListener(this);
		jbAdd.setActionCommand("registrar");
		jbAdd.setFont(new Font("Calibri", Font.PLAIN, (24)));
		jbAdd.setBounds(380, 200, 160, 45);
		this.add(jbAdd);	
		
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
			Factura factura= null;
			if(jtfNombre.getText().length()!=0 && jtfDescripcion.getText().length()!=0 && jtfCantidad.getText().length()!=0)
			{
				double cantidad;
				try{
					cantidad = Double.parseDouble(jtfCantidad.getText());
				} catch (java.lang.NumberFormatException ex) {
					cantidad = Double.parseDouble(jtfCantidad.getText().replace(',','.'));
				}
				factura = registrarFactura(new Factura(jtfNombre.getText(), jtfDescripcion.getText(), new Date().getTime()+"", cantidad, fPrincipal.getListaUsuarios()));
			}
			else
				JOptionPane.showMessageDialog(this, "Debe rellenar todos los campos.", "Advertencia", JOptionPane.WARNING_MESSAGE);
			
			// Si se registra con exito lo aniadimos a la tabla del frame principal
			if(factura != null)
			{
				if(fPrincipal != null)
					fPrincipal.actualizaTablaFacturas();
				
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
	private Factura registrarFactura(Factura factura)
	{
		ClientConfig config = new DefaultClientConfig();
		Client cliente = Client.create(config);
		WebResource servicio = cliente.resource(FramePrincipal.getBaseURI());
		
		Form f = new Form();
		f.add("email", fPrincipal.getUsuarioSesion().getEmail());
		f.add("descripcion", factura.getDescripcion());
		f.add("nombre", factura.getNombre());
		f.add("cantidad", factura.getCantidad());
		String respuestaXML = servicio.path("rest").path("facturas").accept(MediaType.TEXT_XML).post(String.class, f);

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
				JOptionPane.showMessageDialog(this, "Factura " + factura.getNombre() + " registrado correctamente.");
				return factura;
			}
			else 
			{
				JOptionPane.showMessageDialog(this,
					    "No se ha podido a\u00F1adir la compra porque no existe el usuario.",
					    "Error",
					    JOptionPane.ERROR_MESSAGE);
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
