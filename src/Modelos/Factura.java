package Modelos;

import java.util.ArrayList;
import java.util.List;

public class Factura 
{
	private List<Usuario> usuariosSinPagar;

	private long id;
	
	private String nombre;
	
	private String descripcion;
	
	private String fechaCreacion;
	
	private double cantidad;

	public Factura()
	{
		usuariosSinPagar = new ArrayList<Usuario>();
	}
	
	public Factura(String nombre, String descripcion, String fechaCreacion, double cantidad, List<Usuario> usuarios)
	{
		this.nombre = nombre;
		this.descripcion = descripcion;
		this.fechaCreacion = fechaCreacion;
		this.cantidad = cantidad;
		this.usuariosSinPagar = new ArrayList<Usuario> (usuarios);
	}
	
	public List<Usuario> getUsuariosSinPagar() 
	{
		return usuariosSinPagar;
	}

	public void setUsuariosSinPagar(List<Usuario> usuarios) 
	{
		this.usuariosSinPagar = new ArrayList<Usuario> (usuarios);
	}

	public long getId() 
	{
		return id;
	}

	public void setId(long id) 
	{
		this.id = id;
	}

	public String getNombre() 
	{
		return nombre;
	}

	public void setNombre(String nombre) 
	{
		this.nombre = nombre;
	}

	public String getDescripcion() 
	{
		return descripcion;
	}

	public void setDescripcion(String descripcion) 
	{
		this.descripcion = descripcion;
	}

	public String getFechaCreacion() 
	{
		return fechaCreacion;
	}

	public void setFechaCreacion(String fechaCreacion) 
	{
		this.fechaCreacion = fechaCreacion;
	}

	public double getCantidad() 
	{
		return cantidad;
	}

	public void setCantidad(double cantidad) 
	{
		this.cantidad = cantidad;
	}
	
	/**
	 * Elimina un usuario de la lista de usuarios a pagar
	 * a partir de su posicion
	 * @param index Posicion del usuario en la lista
	 */
	public void quitarUsuario(int index)
	{
		usuariosSinPagar.remove(index);
	}
}
