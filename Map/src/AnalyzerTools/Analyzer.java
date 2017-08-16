package AnalyzerTools;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import Forms.CustomerOrder;
import Forms.Forecast;
import Forms.Form;
import Forms.Shipment;
import Forms.WorkOrder;
import mainPackage.DataBase;
import mainPackage.Globals;
import mainPackage.Globals.FormType;
import mainPackage.Pair;

public class Analyzer 
{
	private DataBase db;

	public Analyzer() 
	{
		new Globals();
		db = new DataBase();
	}
	
	public void addNewFC(String customer , String catalogNumber , String quantity , String initDate , String requireDate , String description , String notes)
	{
		db.addFC(customer, catalogNumber, quantity, initDate, requireDate, description , notes);
		updateProductQuantities(catalogNumber);
	}
	
	public void updateFC(int id , String customer , String catalogNumber , String quantity , String initDate , String requireDate , String description , String notes)
	{
		db.updateFC(id,customer, catalogNumber, quantity, initDate, requireDate, description , notes);
		int remainder = Integer.parseInt(getForecast(id).getQuantity()) - Integer.parseInt(quantity);
		if(remainder != 0)
			updateProductQuantities(catalogNumber);
	}
	
	public void removeFC(int id)
	{
		db.removeFC(id);
	}
	
	public Forecast getForecast(int id)
	{
		return db.getForecast(id);
	}
	
	public List<Forecast> getAllForecastOnMonth(String catalogNumber , MonthDate date)
	{
		List<Forecast> allForecastOnMonth = new ArrayList<Forecast>();
		List<String> familyCatalogNumber = db.getAllPatriarchsCatalogNumber(catalogNumber);
		familyCatalogNumber.stream().forEach(cn -> allForecastOnMonth.addAll(db.getAllFCOnMonth(cn , date)));
		return allForecastOnMonth;
	}
	
	public List<Shipment> getAllShipmentsOnMonth(String catalogNumber , MonthDate date)
	{
		List<Shipment> allShipmentsOnMonth = new ArrayList<Shipment>();
		List<String> familyCatalogNumber = db.getAllPatriarchsCatalogNumber(catalogNumber);
		familyCatalogNumber.stream().forEach(cn -> allShipmentsOnMonth.addAll(db.getAllShipmentsOnMonth(cn , date)));
		return allShipmentsOnMonth;
	}
	
	public List<WorkOrder> getAllWorkOrderOnMonth(String catalogNumber , MonthDate date)
	{
		List<WorkOrder> allWorkOrdersOnMonth = new ArrayList<WorkOrder>();
		List<String> familyCatalogNumber = db.getAllPatriarchsCatalogNumber(catalogNumber);
		familyCatalogNumber.stream().forEach(cn -> allWorkOrdersOnMonth.addAll(db.getAllWOOnMonth(cn , date)));
		return allWorkOrdersOnMonth;
	}
	
	public List<CustomerOrder> getAllCustomerOrdersOnMonth(String catalogNumber , MonthDate date)
	{
		List<CustomerOrder> allCustomerOrdersOnMonth = new ArrayList<CustomerOrder>();
		List<String> familyCatalogNumber = db.getAllPatriarchsCatalogNumber(catalogNumber);
		familyCatalogNumber.stream().forEach(cn -> allCustomerOrdersOnMonth.addAll(db.getAllPOOnMonth(cn , date)));
		return allCustomerOrdersOnMonth;
	}
	
	public void updateAlias(String catalogNumber , String alias)
	{
		db.updateAlias(catalogNumber, alias);
	}
	
	public void cleanProductQuantityPerDate(String catalogNumber)
	{
		db.cleanProductQuantityPerDate(catalogNumber , FormType.WO);
		db.cleanProductQuantityPerDate(catalogNumber , FormType.PO);
		db.cleanProductQuantityPerDate(catalogNumber , FormType.SHIPMENT);
		db.cleanProductQuantityPerDate(catalogNumber , FormType.FC);
	}
	
	public void updateProductQuantities(String catalogNumber)
	{
		updateProductQuantities(db.getAllFC(catalogNumber), db.getAllProductsFCQuantityPerDate(catalogNumber),db.getInitProductsFCQuantityPerDate(catalogNumber),db.getInitProductsFCDates(catalogNumber) , FormType.FC);
	}

	private void updateProductQuantities(List<? extends Form> forms ,  Map<String, List<QuantityPerDate>> productsQuantityPerDate 
			, Map<String , List<QuantityPerDate>> initProductsQuantityPerDate, Map<String , Date> productsInitDates ,  FormType type)
	{
		Map<MonthDate,List<Form>> newFormsPerDate = new HashMap<>();
		
		for (Form form : forms) 
		{
			if(productsInitDates.containsKey(form.getCatalogNumber()))
				if(form.getCreateDate().before(productsInitDates.get(form.getCatalogNumber())))
					continue;
			MonthDate monthDate = new MonthDate(form.getRequestDate());
			if(newFormsPerDate.containsKey(monthDate))
				newFormsPerDate.get(monthDate).add(form);
			else
			{
				List<Form> formOfMonth = new ArrayList<Form>();
				formOfMonth.add(form);
				newFormsPerDate.put(monthDate , formOfMonth);
			}
		}

		
		Iterator<Entry<MonthDate, List<Form>>> it = newFormsPerDate.entrySet().iterator();
	    while (it.hasNext()) 
	    {
	        Map.Entry<MonthDate,List<Form>> entry = (Map.Entry<MonthDate,List<Form>>)it.next();
	        for (Form form : entry.getValue()) 
	        {
	        	QuantityPerDate quantityPerDate = new QuantityPerDate(entry.getKey(), new Integer(form.getQuantity()));
	        	if(initProductsQuantityPerDate.containsKey(form.getCatalogNumber()))
	        	{
	        		List<QuantityPerDate> quantityPerDateList = initProductsQuantityPerDate.get(form.getCatalogNumber());
	        		List<MonthDate> datesList = quantityPerDateList.stream().map(el -> el.getDate()).collect(Collectors.toList());
	        		
	        		int indexOfQuantity = datesList.indexOf(quantityPerDate.getDate());
	        		if(indexOfQuantity != -1)
	        			quantityPerDateList.get(indexOfQuantity).addQuantity(quantityPerDate.getQuantity());
	        		else
	        			quantityPerDateList.add(quantityPerDate);
	        			
	        	}
	        	else
	        	{
	        		List<QuantityPerDate> quantityPerDateList = new ArrayList<>();
	        		quantityPerDateList.add(quantityPerDate);
	        		initProductsQuantityPerDate.put(form.getCatalogNumber(), quantityPerDateList);
	        	}
			}
	    }
	    
    
	    Iterator<Entry<String, List<QuantityPerDate>>> productsQuantityIterator = initProductsQuantityPerDate.entrySet().iterator();
	    while (productsQuantityIterator.hasNext()) 
	    {
	        Map.Entry<String,List<QuantityPerDate>> entry = (Map.Entry<String,List<QuantityPerDate>>)productsQuantityIterator.next();
	        if(!productsQuantityPerDate.containsKey(entry.getKey()))
	        	entry.getValue().stream().forEach(date -> db.addNewProductFormQuantityPerDate(entry.getKey() , date , type));
	        else
	        {
	        	List<QuantityPerDate> currentQuantityPerDateList = productsQuantityPerDate.get(entry.getKey());
	        	List<QuantityPerDate> changedQuantityPerDateList = entry.getValue().stream().filter(el -> !currentQuantityPerDateList.contains(el)).collect(Collectors.toList());
	        	
        		List<MonthDate> currentDateList = currentQuantityPerDateList.stream().map(el -> el.getDate()).collect(Collectors.toList());
        		List<MonthDate> newDateList = entry.getValue().stream().map(el -> el.getDate()).collect(Collectors.toList());
        		
	        	for (QuantityPerDate quantityPerDate : changedQuantityPerDateList) 
	        	{
					if(currentDateList.contains(quantityPerDate.getDate()))
						db.updateNewProductFormQuantityPerDate(entry.getKey() , quantityPerDate , type);
					else
						db.addNewProductFormQuantityPerDate(entry.getKey() , quantityPerDate , type);
				}
	        	
        		List<MonthDate> removedDateList = currentDateList.stream().filter(date -> !newDateList.contains(date)).collect(Collectors.toList());
        		removedDateList.stream().forEach(date -> db.removeProductQuantity(entry.getKey() , date));
	        }
	    }
	    
	    productsQuantityIterator = productsQuantityPerDate.entrySet().iterator();
	    while (productsQuantityIterator.hasNext()) 
	    {
	        Map.Entry<String,List<QuantityPerDate>> entry = (Map.Entry<String,List<QuantityPerDate>>)productsQuantityIterator.next();
	        if(!initProductsQuantityPerDate.containsKey(entry.getKey()))
	        	db.removeProductQuantity(entry.getKey() , null);
	    }
	    
	}
	
	public Map<MonthDate,Map<String,ProductColumn>> calculateMap()
	{
		
		Map<MonthDate,Map<String,ProductColumn>> map = new HashMap<MonthDate,Map<String,ProductColumn>>();
		Map<String,String> catalogNumbers = db.getAllCatalogNumbers();
		
		MonthDate maximumDate = db.getMaximumForecastDate();
		List<MonthDate> monthToCalculate = createDates(new MonthDate(Globals.addMonths(Globals.getTodayDate() , -6)) , maximumDate);
		
		for (String catalogNumber : catalogNumbers.keySet()) 
		{
			String descendantCatalogNumber = db.getDescendantCatalogNumber(catalogNumber);
			
			for (MonthDate monthDate : monthToCalculate) 
			{
				QuantityPerDate supplied = db.getProductShipmentQuantityOnDate(catalogNumber , monthDate);
				QuantityPerDate customerOrders = db.getProductPOQuantityOnDate(catalogNumber , monthDate);
				QuantityPerDate workOrder = db.getProductWOQuantityOnDate(catalogNumber , monthDate);
				QuantityPerDate forecast = db.getProductFCQuantityOnDate(catalogNumber , monthDate);
				
				double materialAvailability = 0 ,workOrderAfterSupplied = 0 , openCustomerOrder = 0;
				
				double previousOpenCustomerOrder = 0 , previousWorkOrderAfterSupplied = 0 , previousMaterialAvailability = 0;
				int indexOfCurrentMonth = monthToCalculate.indexOf(monthDate);
				if(indexOfCurrentMonth != 0)
				{
					ProductColumn previousProductColumn = map.get(monthToCalculate.get(indexOfCurrentMonth - 1)).get(catalogNumber);
					previousOpenCustomerOrder = previousProductColumn.getOpenCustomerOrder();
					previousWorkOrderAfterSupplied = previousProductColumn.getWorkOrderAfterSupplied();
					previousMaterialAvailability = previousProductColumn.getMaterialAvailability();
				}

				Pair<String,Integer> fatherCatalogNumberAndQuantityToAssociate = db.getFather(catalogNumber);
				double materialAvailabilityFix = 0;
				if(fatherCatalogNumberAndQuantityToAssociate.getLeft() != null)
				{
					String fatherCatalogNumber = fatherCatalogNumberAndQuantityToAssociate.getLeft();
					QuantityPerDate fatherSupplied = db.getProductShipmentQuantityOnDate(fatherCatalogNumber , monthDate);
					QuantityPerDate fatherWorkOrder = db.getProductWOQuantityOnDate(fatherCatalogNumber , monthDate);
					
					int quantityToAssociate = fatherCatalogNumberAndQuantityToAssociate.getRight();
					customerOrders.setQuantity(customerOrders.getQuantity() + quantityToAssociate * fatherWorkOrder.getQuantity());
					supplied.setQuantity(supplied.getQuantity() + quantityToAssociate * fatherSupplied.getQuantity());
					materialAvailabilityFix = quantityToAssociate * fatherWorkOrder.getQuantity();
				}
				
				materialAvailability = forecast.getQuantity() + previousMaterialAvailability - workOrder.getQuantity() + materialAvailabilityFix;
				workOrderAfterSupplied = workOrder.getQuantity() - supplied.getQuantity() + previousWorkOrderAfterSupplied;
				openCustomerOrder = customerOrders.getQuantity() - supplied.getQuantity() + previousOpenCustomerOrder;
				
				ProductColumn productColumn = new ProductColumn(descendantCatalogNumber, catalogNumbers.get(descendantCatalogNumber), forecast.getQuantity(), materialAvailability, workOrder.getQuantity()
						, workOrderAfterSupplied, customerOrders.getQuantity(), supplied.getQuantity(), openCustomerOrder);
				
				if(map.containsKey(monthDate))
				{
					if(map.get(monthDate).containsKey(descendantCatalogNumber))
						map.get(monthDate).get(descendantCatalogNumber).addProductColumn(productColumn);
					else
						map.get(monthDate).put(descendantCatalogNumber, productColumn);
				}
					
				else
				{
					Map<String,ProductColumn> productPerProductColumn = new HashMap<String,ProductColumn>();
					productPerProductColumn.put(descendantCatalogNumber, productColumn);
					map.put(monthDate, productPerProductColumn);
				}
			}
		}
		
		
		return map;
		
	}

	private List<MonthDate> createDates(MonthDate fromDate, MonthDate toDate) 
	{
		List<MonthDate> dates = new ArrayList<>();
		MonthDate currentDate = fromDate;
		
		while(!currentDate.after(toDate))
		{
			dates.add(currentDate);
			currentDate = new MonthDate(Globals.addMonths(currentDate, 1));
		}

		return dates;
	}
}
