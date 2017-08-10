package MainPackage;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;

public class Globals 
{
	//public static final String con = "C:\\Users\\naordalal\\Desktop\\DB.db";
	public static final String con = "O:\\Purchasing\\PO_FollowUp\\Material Analysis\\DB.db";
	
	public final String shipmentsFilePath = null;
	public final String customerOrdersFilePath = null;
	public final String WOFilePath = null;
	

	public final String woNumberColumn = "���� �����";
	public final String catalogNumberColumn = "���";
	public final String quantityColumn = "����";
	public final String customerColumn = "����";
	public final String dateColumn = "����� �����";
	public final String descriptionColumn = "����";
	
	public final String customerIdColumn = "�� ����";
	public final String orderNumberColumn = "�� �����";
	public final String orderDateColumn = "����� �����";
	public final String priceColumn = "���� �����";
	public final String quantityOrderColumn = "���� ������";
	public final String guaranteedDateColumn = "����� �����";
	
	public final String shipmentIdColumn = "�� �����";
	public final String shipmentDateColumn = "����� �����";
	
	
	
	public static String dateWithoutHourToString(Date date) 
	{
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		
		int year = c.get(Calendar.YEAR);
		int month = c.get(Calendar.MONTH);
		int day = c.get(Calendar.DAY_OF_MONTH);

		String s = String.format("%02d", day) + "/" + String.format("%02d", month) + "/" + year;
		
		return s;
	}
	
	public Date parseDate(String date)
	{
		DateFormat sourceFormat = new SimpleDateFormat("dd/MM/yy");
		DateFormat outsourceFormat = new SimpleDateFormat("dd/MM/yyyy");
		Date parseDate;
		Calendar c = Calendar.getInstance();
		try {
			parseDate = sourceFormat.parse(date);
			String toExp = outsourceFormat.format(parseDate);
			parseDate = outsourceFormat.parse(toExp);
			c.setTime(parseDate);
			c.set(Calendar.HOUR_OF_DAY, 0);
			c.set(Calendar.MINUTE, 0);
			c.set(Calendar.SECOND, 0);
			c.set(Calendar.MILLISECOND, 0);
			parseDate = c.getTime();
		} catch (ParseException e) {

			e.printStackTrace();
			return null;
		}
		
		return parseDate;
	}

	public static int getMonth(Date date) 
	{
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		
		return c.get(Calendar.MONTH);
	}

	public static int getYear(Date date) 
	{
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		
		return c.get(Calendar.YEAR);
	}
	
	
}

