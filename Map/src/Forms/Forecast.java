package Forms;

import java.util.Date;

public class Forecast extends Form
{
	
	private int id;
	private String customer;
	private String description;
	private String notes;

	public Forecast(int id , String customer , String catalogNumber , String quantity , Date initDate , Date requireDate , String description , String notes) 
	{
		super(catalogNumber, quantity, initDate, requireDate);
		
		this.id = id;
		this.customer = customer;
		this.description = description;
		this.notes = notes;
	}

	public String getCustomer() 
	{
		return customer;
	}

	public void setCustomer(String customer) 
	{
		this.customer = customer;
	}

	public String getDescription() 
	{
		return description;
	}

	public void setDescription(String description) 
	{
		this.description = description;
	}

	public String getNotes() 
	{
		return notes;
	}

	public void setNotes(String notes) 
	{
		this.notes = notes;
	}

	public int getId() 
	{
		return id;
	}

	public void setId(int id) 
	{
		this.id = id;
	}

}
