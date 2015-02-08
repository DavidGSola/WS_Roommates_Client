package com.mio.jersey.first.cliente;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Date;

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

import Modelos.Factura;
import Modelos.Respuesta;
import Modelos.Usuario;
import Parsers.ParserFactura;
import Parsers.ParserRespuesta;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;

@SuppressWarnings("serial")
public class PanelFacturas extends JPanel implements ActionListener
{
	/**
	 * Referencia al frame principal
	 */
	FramePrincipal fPrincipal;
	
	/**
	 * Boton de aniadir factura
	 */
	private JButton jbAddFactura;
	
	/**
	 * Boton de eliminar factura
	 */
	private JButton jbEliminar;
	
	/**
	 * Boton de pagar factura
	 */
	private JButton jbPagar;
	
	/**
	 * Boton de salir
	 */
	private JButton jbSalir;
	
	/**
	 * Tabla para mostrar la lista de Facturas
	 */
	private JTable jtFacturas;
	
	/**
	 * Scrollpane que permite hacer scrollable la tabla {@linkplain jtUsuarios}
	 */
	private	JScrollPane scrollPane;

	/**
	 * Lista de usuarios de la lista de correos
	 */
	private ArrayList<Factura> listaFacturas = new ArrayList<Factura>();

	/**
	 * Constructor del Panel
	 * @param fp Referencia al FramePrincipal
	 */
	public PanelFacturas(FramePrincipal fp)
	{
		fPrincipal = fp;
		
		this.setBounds(100, 100, 790, 300);
		this.setLayout(null);
		
		// Modelo de la tabla de los usuarios
		DefaultTableModel tableModel = new DefaultTableModel(new Object[]{"Nombre", "Descripcion", "Cantidad", "Fecha", "Restantes por pagar"}, 0) 
		{
		    @Override
		    public boolean isCellEditable(int row, int column) 
		    {
		       return false;
		    }
		};
		
		jtFacturas = new JTable(tableModel);
		jtFacturas.setFont(new Font("Calibri", Font.PLAIN, 16));
		jtFacturas.setBounds(20, 10, 540, 180);
		
		scrollPane = new JScrollPane(jtFacturas);
		scrollPane.setBounds(20, 10, 540, 240);
		this.add(scrollPane);
	
		// Rellenamos la tabla con las compras que hay en el servicio web
		rellenarTabla();
		
		jbAddFactura = new JButton("A\u00F1adir Factura");
		jbAddFactura.addActionListener(this);
		jbAddFactura.setActionCommand("addFactura");
		jbAddFactura.setFont(new Font("Calibri", Font.PLAIN, (16)));
		jbAddFactura.setBounds(580, 10, 180, 30);
		this.add(jbAddFactura);	
	
		jbPagar = new JButton("Pagar");
		jbPagar.addActionListener(this);
		jbPagar.setActionCommand("pagar");
		jbPagar.setFont(new Font("Calibri", Font.PLAIN, (16)));
		jbPagar.setBounds(580, 50, 180, 30);
		this.add(jbPagar);
		
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
	 * Obtiene y rellena la tabla con la lista de las compras haciendo una llamada al servicio web
	 */
	private void rellenarTabla()
	{
        // Creamos una factoria de Parser
		SAXParserFactory spfac = SAXParserFactory.newInstance();

		try {
        	// Utilizamos dicha factoria para crear un objeto SAXParser
        	SAXParser sp = spfac.newSAXParser();
			
        	// Creamos el handler del Parser para las Compras
			ParserFactura handler = new ParserFactura();
			
			// Hacemos la llamada al servicio web que nos devolvera un XML con las Compras
			ClientConfig config = new DefaultClientConfig();
			Client cliente = Client.create(config);
			WebResource servicio = cliente.resource(FramePrincipal.getBaseURI());
			String comprasXML = servicio.path("rest").path("facturas").accept(MediaType.TEXT_XML).get(String.class);
			
			// Parseamos el String
			sp.parse(new InputSource(new StringReader(comprasXML)), handler);
			
			// Leemos las compras a traves del handler
			ArrayList<Factura> facturas = handler.getList();
			
			// Aniadimos la lista a la lista interna de las compras
			listaFacturas.addAll(facturas);
			
			// Aniadimos las compras a la tabla
			DefaultTableModel model = (DefaultTableModel) jtFacturas.getModel();
			for(Factura factura: facturas) {
				String restantes = "";
				for (int i = 0; i < factura.getUsuariosSinPagar().size(); i++) {
					Usuario u = factura.getUsuariosSinPagar().get(i);
					restantes += u.getNombre();
					if (i < factura.getUsuariosSinPagar().size()-1) {
						restantes += ", ";
					}
				}
				Date fecha = new Date(Long.parseLong(factura.getFechaCreacion()));
				model.addRow(new Object[]{factura.getNombre(), factura.getDescripcion(), factura.getCantidad(), fecha, restantes});
				
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
	 * Actualiza la tabla de compras y la lista interna
	 */
	public void actualizaTablaFacturas()
	{
		listaFacturas.clear();
		DefaultTableModel model = (DefaultTableModel) jtFacturas.getModel();
	
		while(model.getRowCount() != 0)
			model.removeRow(0);
		
		rellenarTabla();
	}
	
	@Override
	public void actionPerformed(ActionEvent event) 
	{
		String actionCommand = event.getActionCommand();
		
		if(actionCommand.equals("addFactura"))
		{
			// Mostramos el frame para aniadir una compra
			FrameAddCompra fAddCompra = new FrameAddCompra(fPrincipal);
			fAddCompra.setVisible(true);
			
		}else if(actionCommand.equals("eliminar"))
		{
			// Obtenemos el indice de la fila seleccionada en la tabla
			int index = jtFacturas.getSelectedRow();
			
			// Comprobamos que hay seleccionada una compra en la tabla
			if(index == -1)
				JOptionPane.showMessageDialog(this, "Debe seleccionar una factura de la lista.", "Advertencia", JOptionPane.WARNING_MESSAGE);
			else
			{
				// Eliminamos la compra llamando al servicio webs
				boolean eliminado = eliminarFactura(listaFacturas.get(index));
				
				// Actualizamos la tabla
				if(eliminado)
					actualizaTablaFacturas();
			}
		}else if(actionCommand.equals("pagar"))
		{
			// Obtenemos el indice de la fila seleccionada en la tabla
			int index = jtFacturas.getSelectedRow();
			
			if(index == -1)
				JOptionPane.showMessageDialog(this, "Debe seleccionar una factura de la lista.", "Advertencia", JOptionPane.WARNING_MESSAGE);
			else
				marcarPagada(listaFacturas.get(index));
		}else if(actionCommand.equals("salir"))
		{
			fPrincipal.dispatchEvent(new WindowEvent(fPrincipal, WindowEvent.WINDOW_CLOSING));
		}
	}
	
	/**
	 * Marca como realizada una compra haciendo uso del servicio web
	 * @param factura Compra a marcar como realizada
	 */
	private void marcarPagada(Factura factura)
	{
		ClientConfig config = new DefaultClientConfig();
		Client cliente = Client.create(config);
		WebResource servicio = cliente.resource(FramePrincipal.getBaseURI());
		String respuestaXML = servicio.path("rest").path("factura/"+factura.getId()+"/"+fPrincipal.getUsuarioSesion().getEmail()).accept(MediaType.TEXT_XML).put(String.class);
		
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
				JOptionPane.showMessageDialog(this, "Factura " + factura.getNombre() + " pagada correctamente.");

				actualizaTablaFacturas();
			}
			else 
			{
				JOptionPane.showMessageDialog(this,
						"No se ha podido pagar la factura " + factura.getNombre() + ".",
					    "Error",
					    JOptionPane.ERROR_MESSAGE);
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
	 * Elimina una factura a traves del servicio web
	 * @param factura Factura a eliminar
	 * @return Exito de la operacion
	 */
	private boolean eliminarFactura(Factura factura)
	{
		ClientConfig config = new DefaultClientConfig();
	
		Client cliente = Client.create(config);
		WebResource servicio = cliente.resource(FramePrincipal.getBaseURI());
		String respuestaXML = servicio.path("rest").path("facturas/"+factura.getId()).accept(MediaType.TEXT_XML).delete(String.class);
		
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
				JOptionPane.showMessageDialog(this, "Factura " + factura.getNombre() + " eliminada correctamente.");

				return true;
			}
			else 
			{
				JOptionPane.showMessageDialog(this,
						"No se ha podido elimnar la factura " + factura.getNombre() + ".",
					    "Error",
					    JOptionPane.ERROR_MESSAGE);
				
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
}
