package Modelos;

public class Compra 
{
	private Usuario usuario;

	private long id;
	
	private String nombre;
	
	private String descripcion;
	
	private String fecha;
	
	private boolean urgente;
	
	public Compra()
	{
		
	}
	
	public Compra(Usuario usuario, String nombre, String descripcion, String fecha)
	{
		this.usuario = usuario;
		this.nombre = nombre;
		this.descripcion = descripcion;
		this.fecha = fecha;
		this.urgente = false;
	}

	public Usuario getUsuario() 
	{
		return usuario;
	}

	public void setUsuario(Usuario usuario) 
	{
		this.usuario = usuario;
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

	public String getFecha() 
	{
		return fecha;
	}

	public void setFecha(String fecha) 
	{
		this.fecha = fecha;
	}

	public boolean isUrgente() 
	{
		return urgente;
	}

	public void setUrgente(boolean urgente) 
	{
		this.urgente = urgente;
	}
	
	public String getUrgenteString()
	{
		return urgente ? "Si" : "No";
	}
}