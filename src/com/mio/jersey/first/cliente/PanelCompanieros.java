package com.mio.jersey.first.cliente;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.ws.rs.core.MediaType;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import Modelos.Respuesta;
import Modelos.Usuario;
import Parsers.ParserRespuesta;
import Parsers.ParserUsuario;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;

@SuppressWarnings("serial")
public class PanelCompanieros extends JPanel implements ActionListener
{
	/**
	 * Referencia al frame principal
	 */
	FramePrincipal fPrincipal;
	
	/**
	 * Boton de registrar un usuario
	 */
	private JButton jbRegistrarse;
	
	/**
	 * Boton de eliminar un usuario
	 */
	private JButton jbEliminar;
	
	/**
	 * Boton de salir
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
	
	public PanelCompanieros(FramePrincipal fp)
	{
		fPrincipal = fp;
		
		this.setBounds(100, 100, 790, 300);
		this.setLayout(null);
		
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
		this.add(scrollPane);
	
		// Rellenamos la tabla con los usuarios del servicio web
		rellenarTabla();
		
		jbRegistrarse = new JButton("Registrar");
		jbRegistrarse.addActionListener(this);
		jbRegistrarse.setActionCommand("registrar");
		jbRegistrarse.setFont(new Font("Calibri", Font.PLAIN, (16)));
		jbRegistrarse.setBounds(580, 10, 180, 30);
		this.add(jbRegistrarse);	
		
		jbEliminar = new JButton("Eliminar");
		jbEliminar.addActionListener(this);
		jbEliminar.setActionCommand("eliminar");
		jbEliminar.setFont(new Font("Calibri", Font.PLAIN, (16)));
		jbEliminar.setBounds(580, 90, 180, 30);
		this.add(jbEliminar);		
		
		jbSalir = new JButton("Salir");
		jbSalir.addActionListener(this);
		jbSalir.setActionCommand("salir");
		jbSalir.setFont(new Font("Calibri", Font.PLAIN, (16)));
		jbSalir.setBounds(580, 215, 180, 30);
		this.add(jbSalir);
	}
	
	/**
	 * Obtiene y rellena la tabla con la lista de los usuarios haciendo una llamada al servicio web
	 */
	private void rellenarTabla()
	{
        // Creamos una factoria de Parser
		SAXParserFactory spfac = SAXParserFactory.newInstance();

		try {
        	// Utilizamos dicha factoría para crear un objeto SAXParser
        	SAXParser sp = spfac.newSAXParser();
			
        	// Creamos el handler del Parser para los Usuarios
			ParserUsuario handler = new ParserUsuario();
			
			// Hacemos la llamada al servicio web que nos devolverá un XML con los Usuarios
			ClientConfig config = new DefaultClientConfig();
			Client cliente = Client.create(config);
			WebResource servicio = cliente.resource(FramePrincipal.getBaseURI());
			String usuariosXML = servicio.path("rest").path("usuarios").accept(MediaType.TEXT_XML).get(String.class);
			
			// Parseamos el String
			sp.parse(new InputSource(new StringReader(usuariosXML)), handler);
			
			// Leemos los usuarios a traves del handler
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
	 * Actualiza la tabla de usuarios y la lista interna
	 */
	public void actualizaTablaUsuarios()
	{
		listaUsuarios.clear();
		DefaultTableModel model = (DefaultTableModel) jtUsuarios.getModel();
	
		while(model.getRowCount() != 0)
			model.removeRow(0);
		
		rellenarTabla();
	}
	
	@Override
	public void actionPerformed(ActionEvent event) 
	{
		String actionCommand = event.getActionCommand();
		
		if(actionCommand.equals("registrar"))
		{
			// Mostramos el frame para registrar usuarios
			FrameAddUsuario fRegistrar = new FrameAddUsuario(fPrincipal);
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
				boolean eliminado = eliminarUsuario(listaUsuarios.get(index));
				
				if(eliminado)
				{
					actualizaTablaUsuarios();
					fPrincipal.actualizaTablaCompra();
					fPrincipal.actualizaTablaFacturas();
				}
			}
		}else if(actionCommand.equals("salir"))
		{
			fPrincipal.dispatchEvent(new WindowEvent(fPrincipal, WindowEvent.WINDOW_CLOSING));
		}
	}
	
	/**
	 * Elimina un usuario a traves del servicio web
	 * @param usuario Usuario a eliminar
	 * @return Exito de la operacion
	 */
	private boolean eliminarUsuario(Usuario usuario)
	{
		ClientConfig config = new DefaultClientConfig();
		
		Client cliente = Client.create(config);
		WebResource servicio = cliente.resource(FramePrincipal.getBaseURI());
		String respuestaXML = servicio.path("rest").path("usuarios/"+usuario.getEmail()).accept(MediaType.TEXT_XML).delete(String.class);
		
		SAXParserFactory spfac = SAXParserFactory.newInstance();
	
		try {
	    	SAXParser sp = spfac.newSAXParser();
			
	    	// Creamos el handler del Parser para la Respuesta
			ParserRespuesta handler = new ParserRespuesta();
			
			// Parseamos el String
			sp.parse(new InputSource(new StringReader(respuestaXML)), handler);
			
			Respuesta respuesta = handler.getRespuesta();
			
			if(!respuesta.isError())
			{
				JOptionPane.showMessageDialog(this, "Usuario " + usuario.getNombre() + " eliminada correctamente.");

				return true;
			}
			else 
			{
				JOptionPane.showMessageDialog(this,
						"No se ha podido elimnar el usuario " + usuario.getNombre() + ".",
					    "Advertencia",
					    JOptionPane.WARNING_MESSAGE);
				
				return false;
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
		
		return false;
	}

	/**
	 * Devuelve la lista de usuarios.
	 * @return Lista de los usuarios
	 */
	public ArrayList<Usuario> getListaUsuarios() {
		return listaUsuarios;
	}
}
