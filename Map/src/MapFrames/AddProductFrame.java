package MapFrames;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.AbstractAction;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

import org.apache.poi.ss.usermodel.Font;

import Components.FilterCombo;
import Components.MultiSelectionComboBox;
import Forms.Tree;
import MainPackage.DataBase;
import MainPackage.Globals;

public class AddProductFrame implements ActionListener 
{
	private Globals globals;
	private DataBase db;
	private JFrame frame;
	private JPanel panel;
	private String userName;
	private JLabel catalogNumberLabel;
	private FilterCombo catalogNumberComboBox;
	private JLabel customerLabel;
	private JComboBox<String> customerComboBox;
	private JLabel descriptionLabel;
	private JTextArea descriptionText;
	private JLabel fatherLabel;
	private MultiSelectionComboBox<String> fatherComboBox;
	private JButton addProductButton;
	private JLabel copyRight;
	
	public AddProductFrame(String userName) 
	{
		this.userName = userName;
		db = new DataBase();
		initialize();
	}

	private void initialize() 
	{
		globals = new Globals();
		
		frame = new JFrame("New Product");
		frame.setVisible(true);
		frame.setLayout(null);
		frame.getRootPane().setFocusable(true);
		frame.setBounds(300, 100, 500, 500);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setResizable(false);
		frame.setIconImage(globals.frameImage);
		
		frame.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "Cancel"); 
		frame.getRootPane().getActionMap().put("Cancel", new AbstractAction(){
			private static final long serialVersionUID = 1L;

				public void actionPerformed(ActionEvent e)
	            {
					frame.dispose();
	            }
	        });
		
		panel = new JPanel();
		panel.setLocation(0 , 0);
		panel.setSize(500, 500);
		panel.setLayout(null);
		frame.add(panel);
		
		catalogNumberLabel = new JLabel("<html><u>Catalog Number:</u></html>");
		catalogNumberLabel.setLocation(30,20);
		catalogNumberLabel.setSize(100,100);
		panel.add(catalogNumberLabel);
		
		DefaultComboBoxModel<String> model = new DefaultComboBoxModel<String>();
		List<String> catalogNumbers = db.getAllCatalogNumbers(userName);
		boolean clearWhenFocusLost = false;
		catalogNumberComboBox = new FilterCombo(catalogNumbers, model, clearWhenFocusLost);
		catalogNumberComboBox.setLocation(150, 60);
		catalogNumberComboBox.setSize(150, 20);
		catalogNumberComboBox.addActionListener(this);
		panel.add(catalogNumberComboBox);
		
		customerLabel = new JLabel("<html><u>Customer:</u></html>");
		customerLabel.setLocation(30,90);
		customerLabel.setSize(100,100);
		panel.add(customerLabel);
		
		model = new DefaultComboBoxModel<String>();
		List<String> customers = db.getCustomersOfUser(userName);
		for (String customer : customers) 
		{
			model.addElement(customer);
		}
		
		customerComboBox = new JComboBox<String>(model);
		customerComboBox.setLocation(150, 130);
		customerComboBox.setSize(150, 20);
		customerComboBox.addActionListener(this);
		panel.add(customerComboBox);
		
		descriptionLabel = new JLabel("<html><u>Description:</u></html>");
		descriptionLabel.setLocation(30, 160);
		descriptionLabel.setSize(100,100);
		panel.add(descriptionLabel);
		
		descriptionText = new JTextArea();
		descriptionText.setLocation(150, 200);
		descriptionText.setSize(200, 70);
		descriptionText.setFont(new java.awt.Font("Ariel", Font.ANSI_CHARSET , 12));
		descriptionText.setLineWrap(true);
		descriptionText.setWrapStyleWord(true);
		panel.add(descriptionText);
		
		fatherLabel = new JLabel("<html><u>Father Catalog Number:</u></html>");
		fatherLabel.setLocation(30, 270);
		fatherLabel.setSize(120,100);
		panel.add(fatherLabel);
		
		model = new DefaultComboBoxModel<String>();
		catalogNumbers = db.getAllCatlogNumberOfCustomer((String) customerComboBox.getModel().getSelectedItem());
		for (String catalogNumber : catalogNumbers) 
			model.addElement(catalogNumber);
		
		model.setSelectedItem(null);
		
		fatherComboBox = new MultiSelectionComboBox<String>(model);
		fatherComboBox.setLocation(150, 310);
		fatherComboBox.setSize(150, 20);
		panel.add(fatherComboBox);
			
		addProductButton = new JButton();
		addProductButton.setLocation(200, 420);
		addProductButton.setSize(80 , 40);
		addProductButton.addActionListener(this);
		addProductButton.setIcon(globals.okIcon);
		addProductButton.setFocusable(false);
		addProductButton.setContentAreaFilled(false);
		addProductButton.setPressedIcon(globals.clickOkIcon);
		addProductButton.setToolTipText("OK");
		panel.add(addProductButton);
			
		copyRight = new JLabel("<html><b>\u00a9 Naor Dalal</b></html>");
		copyRight.setLocation(30 , 430);
		copyRight.setSize(100,30);
		panel.add(copyRight);
		

	}

	@Override
	public void actionPerformed(ActionEvent event) 
	{
		if(event.getSource() == catalogNumberComboBox)
		{
			DefaultComboBoxModel<String> catalogNumberComboBoxModel = (DefaultComboBoxModel<String>) catalogNumberComboBox.getModel();
			DefaultComboBoxModel<String> customerComboBoxModel = (DefaultComboBoxModel<String>) customerComboBox.getModel();
			customerComboBox.removeAllItems();
			String catalogNumber =  (catalogNumberComboBoxModel.getSelectedItem() == null) ? "" : (String) catalogNumberComboBoxModel.getSelectedItem();
			String customerName = db.getCustomerOfCatalogNumber(catalogNumber);
			
			if(customerName != null && !customerName.equals(""))
			{
				
				customerComboBoxModel.addElement(customerName);
				
				List<Tree> trees = db.getAllTrees(userName, catalogNumber);
				if(trees.isEmpty())
					return;
				descriptionText.setText(trees.get(0).getDescription());
				descriptionText.setEnabled(false);
				
				List<String> fathers = trees.stream().map(tree -> tree.getFatherCN()).collect(Collectors.toList());
				List<String> fatherCatalogNumbers = db.getAllCatalogNumbers(userName);
				fatherCatalogNumbers.removeAll(fathers);
				fatherCatalogNumbers.remove(catalogNumber);
				DefaultComboBoxModel<String> fatherComboBoxModel = (DefaultComboBoxModel<String>) fatherComboBox.getModel();
				fatherComboBox.removeAllItems();				
				for (String fatherCatalogNumber : fatherCatalogNumbers) 
				{
					fatherComboBoxModel.addElement(fatherCatalogNumber);
				}
				
				customerComboBox.setEnabled(false);
			}
			else
			{
				List<String> customers = db.getCustomersOfUser(userName);
				for (String customer : customers) 
				{
					customerComboBoxModel.addElement(customer);
				}
				
				customerComboBox.setEnabled(true);
				
				descriptionText.setText("");
				descriptionText.setEnabled(true);
				DefaultComboBoxModel<String> fatherComboBoxModel = (DefaultComboBoxModel<String>) fatherComboBox.getModel();
				fatherComboBox.removeAllItems();
				List<String> fatherCatalogNumbers = db.getAllCatalogNumbers(userName);
				for (String fatherCatalogNumber : fatherCatalogNumbers) 
				{
					fatherComboBoxModel.addElement(fatherCatalogNumber);
				}
			}
		}
		else if(event.getSource() == customerComboBox)
		{
			DefaultComboBoxModel<String> customerComboBoxModel = (DefaultComboBoxModel<String>) customerComboBox.getModel();
			if(customerComboBoxModel.getSelectedItem() != null)
			{
				List<String> catalogNumbers = db.getAllCatlogNumberOfCustomer((String) customerComboBoxModel.getSelectedItem());
				String catalogNumber = catalogNumberComboBox.getText();
				
				catalogNumbers.remove(catalogNumber.trim());
				DefaultComboBoxModel<String> fatherComboBoxModel = (DefaultComboBoxModel<String>) fatherComboBox.getModel();
				fatherComboBox.removeAllItems();
				for (String cn : catalogNumbers) 
					fatherComboBoxModel.addElement(cn);
				
				fatherComboBoxModel.setSelectedItem(null);
			}
		}
		else if(event.getSource() == addProductButton)
		{			
			if(catalogNumberComboBox.getText().trim().equals(""))
			{
				JOptionPane.showConfirmDialog(null, "Please enter a catalog number","",JOptionPane.PLAIN_MESSAGE);
				return;
			}
			
			if(descriptionText.getText().trim().equals(""))
			{
				JOptionPane.showConfirmDialog(null, "Please enter a description","",JOptionPane.PLAIN_MESSAGE);
				return;
			}
			
			String catalogNumber = catalogNumberComboBox.getText().trim();
			DefaultComboBoxModel<String> customerComboBoxModel = (DefaultComboBoxModel<String>) customerComboBox.getModel();
			String customer = (String) customerComboBoxModel.getSelectedItem();
			String description = descriptionText.getText().trim();
			
			List<String> fathers = fatherComboBox.getSelectedItems();
			
			if(fathers.size() > 0)
			{				
				for (String father : fatherComboBox.getSelectedItems()) 
				{
					boolean validInput = false;
					String quantity = "";
					while(!validInput)
					{
						quantity = JOptionPane.showInputDialog(null , "Enter quantity for " + father , "Quantity To Associate" , JOptionPane.OK_OPTION);
						if(quantity == null || !org.apache.commons.lang3.StringUtils.isNumeric(quantity.trim()))
						{
							JOptionPane.showConfirmDialog(null, "Please enter a valid quantity","",JOptionPane.PLAIN_MESSAGE);
							continue;	
						}
						
						validInput = true;
					}
					
					db.addNewProduct(catalogNumber, customer, description, father, quantity.trim());
				}
			}
			else
				db.addNewProduct(catalogNumber, customer, description, "", "0");

			
			JOptionPane.showConfirmDialog(null, "Added successfully","",JOptionPane.PLAIN_MESSAGE);
			
			catalogNumberComboBox.clear();
			fatherComboBox.removeAllSelectedItem();
			fatherComboBox.getModel().setSelectedItem(null);
			descriptionText.setText("");
			descriptionText.setEnabled(true);
			customerComboBox.setEnabled(true);
			
			catalogNumberComboBox.requestFocusInWindow();
			
		}
		
	}
}