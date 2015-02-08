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

import Modelos.Compra;
import Parsers.ParserCompra;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;

public class PanelCompras extends JPanel implements ActionListener
{
	/**
	 * Referencia al frame principal
	 */
	FramePrincipal fPrincipal;
	
	/**
	 * Botón de añadir compra
	 */
	private JButton jbAddCompra;
	
	/**
	 * Botón de eliminar compra
	 */
	private JButton jbEliminar;
	
	/**
	 * Botón de realizar compra
	 */
	private JButton jbComprar;
	
	/**
	 * Botón de salir
	 */
	private JButton jbSalir;
	
	/**
	 * Tabla para mostrar la lista de Compras
	 */
	private JTable jtCompras;
	
	/**
	 * Scrollpane que permite hacer scrollable la tabla {@linkplain jtUsuarios}
	 */
	private	JScrollPane scrollPane;

	/**
	 * Lista de usuarios de la lista de correos
	 */
	private ArrayList<Compra> listaCompras = new ArrayList<Compra>();
	
	public PanelCompras(FramePrincipal fp)
	{
		fPrincipal = fp;
		
		this.setBounds(100, 100, 790, 300);
		this.setLayout(null);
		
		// Modelo de la tabla de los usuarios
		DefaultTableModel tableModel = new DefaultTableModel(new Object[]{"Nombre", "Descripcion", "Fecha", "Urgente", "Usuario"}, 0) 
		{
		    @Override
		    public boolean isCellEditable(int row, int column) 
		    {
		       return false;
		    }
		};
		
		jtCompras = new JTable(tableModel);
		jtCompras.setFont(new Font("Calibri", Font.PLAIN, 16));
		jtCompras.setBounds(20, 10, 540, 180);
		
		scrollPane = new JScrollPane(jtCompras);
		scrollPane.setBounds(20, 10, 540, 240);
		this.add(scrollPane);
	
		// Rellenamos la tabla con los usuarios de la base de datos
		rellenarTabla();
		
		jbAddCompra = new JButton("Añadir Compra");
		jbAddCompra.addActionListener(this);
		jbAddCompra.setActionCommand("addCompra");
		jbAddCompra.setFont(new Font("Calibri", Font.PLAIN, (16)));
		jbAddCompra.setBounds(580, 10, 180, 30);
		this.add(jbAddCompra);	
	
		jbComprar = new JButton("Comprar");
		jbComprar.addActionListener(this);
		jbComprar.setActionCommand("comprar");
		jbComprar.setFont(new Font("Calibri", Font.PLAIN, (16)));
		jbComprar.setBounds(580, 50, 180, 30);
		this.add(jbComprar);
		
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
	 * Obtiene y rellena la tabla con la lista de los usuarios haciendo una llamada al servlet
	 */
	private void rellenarTabla()
	{
        // Creamos una factoria de Parser
		SAXParserFactory spfac = SAXParserFactory.newInstance();

		try {
        	// Utilizamos dicha factoría para crear un objeto SAXParser
        	SAXParser sp = spfac.newSAXParser();
			
        	// Creamoe el handler del Parser para las Compras
			ParserCompra handler = new ParserCompra();
			
			// Hacemos la llamada al servicio web que nos devolverá un XML con las Compras
			ClientConfig config = new DefaultClientConfig();
			Client cliente = Client.create(config);
			WebResource servicio = cliente.resource(FramePrincipal.getBaseURI());
			String comprasXML = servicio.path("rest").path("compras").accept(MediaType.TEXT_XML).get(String.class);
			
			// Parseamos el String
			sp.parse(new InputSource(new StringReader(comprasXML)), handler);
			
			// Leemos las compras a través del handler
			ArrayList<Compra> compras = handler.getList();
			
			// Añadimos la lista a la lista interna de las compras
			listaCompras.addAll(compras);
			
			// Añadimos las compras a la tabla
			DefaultTableModel model = (DefaultTableModel) jtCompras.getModel();
			for(Compra compra: compras)
			{
				System.out.println(compra.getNombre());
				model.addRow(new Object[]{compra.getNombre(), compra.getDescripcion(), compra.getFecha(), compra.getUrgenteString(), compra.getUsuario().getNombre()});
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
	}
	
	/**
	 * Añade un usuario a la tabla que muestra la lista de los usuarios de la lista de correo
	 * @param usuario
	 */
	public void addCompraToTable(Compra compra)
	{
		// Añadimos el usuario a la lista interna
		listaCompras.add(compra);
		
		// Añadimos el usuario al modelo de la tabla para que se muestre
		DefaultTableModel model = (DefaultTableModel) jtCompras.getModel();
		model.addRow(new Object[]{compra.getNombre(), compra.getDescripcion(), compra.getFecha(), compra.getUrgenteString(), compra.getUsuario().getNombre()});
	}
	
	@Override
	public void actionPerformed(ActionEvent event) 
	{
		String actionCommand = event.getActionCommand();
		
		if(actionCommand.equals("addCompra"))
		{
			// Mostramos el frame para registrar usuarios
			FrameAddCompra fAddCompra = new FrameAddCompra(fPrincipal);
			fAddCompra.setVisible(true);
			
		}else if(actionCommand.equals("eliminar"))
		{
			// Obtenemos el indice de la fila seleccionada en la tabla
			int index = jtCompras.getSelectedRow();
			
			// Comprobamos que se hay seleccionado un usuario
			if(index == -1)
				JOptionPane.showMessageDialog(this, "Debe seleccionar un usuario de la lista.", "Advertencia", JOptionPane.WARNING_MESSAGE);
			else
			{
				// Eliminamos el usuario llamado al servlet
				boolean eliminado = eliminarCompra(listaCompras.get(index));
				
				if(eliminado)
				{
					DefaultTableModel model = (DefaultTableModel) jtCompras.getModel();
					
					// Eliminamos el usuario del modelo de la tabla y de la lista interta
					model.removeRow(index);
					listaCompras.remove(index);
				}
			}
		}else if(actionCommand.equals("comprar"))
		{
			// Obtenemos el indice de la fila seleccionada en la tabla
			int index = jtCompras.getSelectedRow();
			
			if(index == -1)
				JOptionPane.showMessageDialog(this, "Debe seleccionar un usuario de la lista.", "Advertencia", JOptionPane.WARNING_MESSAGE);
			
		}else if(actionCommand.equals("salir"))
		{
			fPrincipal.dispatchEvent(new WindowEvent(fPrincipal, WindowEvent.WINDOW_CLOSING));
		}
	}
	
	/**
	 * Elimina un usuario dado de la lista de correo
	 * @param usuario Usuario a eliminar
	 * @return Exito de la operación
	 */
	private boolean eliminarCompra(Compra compra)
	{
		/**
		 * TODO: 
		 */
		return false;
	}
}
