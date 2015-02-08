package Parsers;
 
import java.util.ArrayList;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import Modelos.Factura;
import Modelos.Usuario;

public class ParserFactura extends DefaultHandler 
{
	private boolean nombreFacturaEncontrado = false;
	private boolean idFacturaEncontrado = false;
	private Factura facturaTemp;
	private Usuario usuarioTemp;
	private String temp;
	private ArrayList<Factura> listafacturas = new ArrayList<Factura>();
	private ArrayList<Usuario> listaUsuariosTemp;
    
	public void characters(char[] buffer, int start, int length) {
		temp = new String(buffer, start, length);
	}
      
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		temp = "";
		if (qName.equalsIgnoreCase("factura")) {
			facturaTemp = new Factura();
			idFacturaEncontrado = false;
			nombreFacturaEncontrado = false;
			listaUsuariosTemp = new ArrayList<Usuario>();
		} else if (qName.equalsIgnoreCase("usuariosSinPagar"))
			usuarioTemp = new Usuario();	
	}
	
	public void endElement(String uri, String localName, String qName) throws SAXException {
		if (qName.equalsIgnoreCase("factura")) {
			facturaTemp.setUsuariosSinPagar(listaUsuariosTemp);
			listafacturas.add(facturaTemp);
		} 
		else if (qName.equalsIgnoreCase("cantidad"))
			facturaTemp.setCantidad(Double.parseDouble(temp));
		
		else if (qName.equalsIgnoreCase("descripcion"))
			facturaTemp.setDescripcion(temp);
		
		else if (qName.equalsIgnoreCase("fechaCreacion"))
			facturaTemp.setFechaCreacion(temp);
		
		else if (qName.equalsIgnoreCase("id") && !idFacturaEncontrado) {
			facturaTemp.setId(Integer.parseInt(temp));
			idFacturaEncontrado = true;
		} 
		else if (qName.equalsIgnoreCase("nombre") && !nombreFacturaEncontrado) {
			facturaTemp.setNombre(temp);
			nombreFacturaEncontrado = true;
		} 
		else if (qName.equalsIgnoreCase("usuariosSinPagar"))
			listaUsuariosTemp.add(usuarioTemp);
		
		else if (qName.equalsIgnoreCase("email"))
			usuarioTemp.setEmail(temp);
		
		else if (qName.equalsIgnoreCase("id")  && idFacturaEncontrado)
			usuarioTemp.setId(Integer.parseInt(temp));
		
		else if (qName.equalsIgnoreCase("nombre") && nombreFacturaEncontrado)
			usuarioTemp.setNombre(temp);
	}
       
	public ArrayList<Factura> getList() {
		return listafacturas;
    }
}