package Modelos;


public class Usuario 
{
	private long id;
	
	private String nombre;
	
	private String email;
	
	//private List<Compra> compras = new ArrayList<>();

//	private List<Factura> mFacturas = new ArrayList<>();
	
	public Usuario()
	{
	
	}
  
	public Usuario (String nombre, String email)
	{
//		this.id = id;
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

//	public List<Compra> getCompras() 
//	{
//		return compras;
//	}
//
//	public void setCompras(List<Compra> compras) 
//	{
//		this.compras = compras;
//	}

//	public List<Factura> getFacturas() 
//	{
//		return mFacturas;
//	}
//
//	public void setFacturas(List<Factura> facturas) 
//	{
//		this.mFacturas = facturas;
//	}
}