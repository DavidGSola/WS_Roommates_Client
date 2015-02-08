package Parsers;
 
import java.util.ArrayList;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import Modelos.Respuesta;

public class ParserRespuesta extends DefaultHandler 
{
	private Respuesta respuestaTemp;
	private String temp;
	private ArrayList<Respuesta> listaRespuestas = new ArrayList<Respuesta>();
    
	public void characters(char[] buffer, int start, int length) {
		temp = new String(buffer, start, length);
	}
      
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		temp = "";
		if (qName.equalsIgnoreCase("respuesta"))
			respuestaTemp = new Respuesta();
	}
	
	public void endElement(String uri, String localName, String qName) throws SAXException {
		if (qName.equalsIgnoreCase("respuesta"))
			listaRespuestas.add(respuestaTemp);
		else if (qName.equalsIgnoreCase("error"))
		{
			if(temp.equals("false"))
				respuestaTemp.setError(false);
			else
				respuestaTemp.setError(true);
		}
		else if (qName.equalsIgnoreCase("mensaje"))
			respuestaTemp.setMensaje(temp);
       }
       
       public ArrayList<Respuesta> getList()
       {
    	   return listaRespuestas;
       }
       
       public Respuesta getRespuesta()
       {
    	   return listaRespuestas.get(0);
       }
}