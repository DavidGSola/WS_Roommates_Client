package Modelos;


public class Usuario 
{
	private long id;
	
	private String nombre;
	
	private String email;
	
	public Usuario()
	{
	
	}
  
	public Usuario (String nombre, String email)
	{
		this.nombre = nombre;
		this.email = email;
	}
 
	public long getId() {
		return id;
	}

	public void setId(long id) {
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
  
	public String getEmail() 
	{
		return email;
	}
  
	public void setEmail(String email) 
	{
		this.email = email;
	}

}