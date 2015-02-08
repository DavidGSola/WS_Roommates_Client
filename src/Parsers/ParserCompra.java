package Parsers;
 
import java.util.ArrayList;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import Modelos.Compra;
import Modelos.Usuario;

public class ParserCompra extends DefaultHandler 
{
	private boolean nombreCompraEncontrado = false;
	private Compra compraTemp;
	private Usuario usuarioTemp;
	private String temp;
	private ArrayList<Compra> listaCompras = new ArrayList<Compra>();
    
	public void characters(char[] buffer, int start, int length) {
		temp = new String(buffer, start, length);
	}
      
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		temp = "";
		if (qName.equalsIgnoreCase("compra"))
		{
			compraTemp = new Compra();
			nombreCompraEncontrado = false;
		}else if (qName.equalsIgnoreCase("usuario"))
			usuarioTemp = new Usuario();	
	}
	
	public void endElement(String uri, String localName, String qName) throws SAXException {

		if (qName.equalsIgnoreCase("compra"))
			listaCompras.add(compraTemp);
		else if (qName.equalsIgnoreCase("descripcion"))
			compraTemp.setDescripcion(temp);
		else if (qName.equalsIgnoreCase("fecha"))
			compraTemp.setFecha(temp);
		else if (qName.equalsIgnoreCase("id"))
			compraTemp.setId(Integer.parseInt(temp));
		else if (qName.equalsIgnoreCase("nombre") && !nombreCompraEncontrado)
		{
			compraTemp.setNombre(temp);
			nombreCompraEncontrado = true;
		}
		else if (qName.equalsIgnoreCase("urgente"))
		{
			if(temp.equals("false"))
				compraTemp.setUrgente(false);
			else
				compraTemp.setUrgente(true);
		}else if (qName.equalsIgnoreCase("usuario"))
			compraTemp.setUsuario(usuarioTemp);
		else if (qName.equalsIgnoreCase("email"))
			usuarioTemp.setEmail(temp);
		else if (qName.equalsIgnoreCase("id"))
			usuarioTemp.setId(Integer.parseInt(temp));
		else if (qName.equalsIgnoreCase("nombre") && nombreCompraEncontrado)
			usuarioTemp.setNombre(temp);
	}
       
	public ArrayList<Compra> getList()
    {
		return listaCompras;
    }
}