package mainPackage;

import java.util.Date;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class SimGlobals extends Globals
{
	
	public int maxOrderIndex = 3;

	public int openOrdersIndex = 3;
	
	public int orderOffset = 0;
	public int supplierOffset = 1;
	public int dateOffset = 2;
	public int quantityOffset = 3;
	
	
	public String itemNumberColumn = "����";
	public String descriptionColumn = "����";
	public String shortageColumn = "���� �����";
	public String dateColumn = "����� ";
	public String priceColumn = "4-���� ��� �����";
	public String openOrdersColumn = "���� ��' ������ ������ 1-3";
	public short getDateFormat(XSSFWorkbook workbook) {
		// TODO Auto-generated method stub
		return 0;
	}

}
