package Parsers;
 
import java.util.ArrayList;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import Modelos.Usuario;

public class ParserUsuario extends DefaultHandler 
{
	private Usuario usuarioTemp;
	private String temp;
	private ArrayList<Usuario> listaUsuarios = new ArrayList<Usuario>();
    
	public void characters(char[] buffer, int start, int length) {
		temp = new String(buffer, start, length);
	}
      
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		temp = "";
		if (qName.equalsIgnoreCase("usuario"))
			usuarioTemp = new Usuario();
	}
	
	public void endElement(String uri, String localName, String qName) throws SAXException {
		if (qName.equalsIgnoreCase("usuario"))
			listaUsuarios.add(usuarioTemp);
		else if (qName.equalsIgnoreCase("email"))
			usuarioTemp.setEmail(temp);
		else if (qName.equalsIgnoreCase("id"))
			usuarioTemp.setId(Integer.parseInt(temp));
		else if (qName.equalsIgnoreCase("nombre"))
			usuarioTemp.setNombre(temp);
       }
       
       public ArrayList<Usuario> getList()
       {
    	   return listaUsuarios;
       }
}