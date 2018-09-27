package com.nuix.evmanager.controls;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.StringJoiner;
import java.util.stream.Collectors;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.EmptyBorder;

import org.apache.log4j.Logger;

import com.jidesoft.grid.JideTable;
import com.nuix.evmanager.data.SQLUserRecordStore;
import com.nuix.evmanager.data.UserAddress;
import com.nuix.evmanager.data.UserRecord;

import java.awt.Toolkit;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.ImageIcon;
import javax.swing.JToolBar;

/*
 * - Needs to validate employeed id does not already exist
 * - Needs to allows input of one or more:
 *     - Email
 *     - Phone
 *     - SID
 * - Needs to have at least 1 email provided
 * 
 * - Employee ID cannot be empty
 * - Name cannot be empty
 * - Title, Department and Location may be empty
 * - Should have at least 1 email address
 * 
 */

/***
 * A dialog containing controls allowing a user to build a new user record
 * @author Jason Wells
 *
 */
@SuppressWarnings("serial")
public class AddUserDialog extends JDialog {

	private static Logger logger = Logger.getLogger(AddUserDialog.class);
	
	private final JPanel contentPanel = new JPanel();
	private JTextField txtEmployeeId;
	private JTextField txtName;
	private JTextField txtTitle;
	private JTextField txtDepartment;
	private JTextField txtLocation;
	private JTextField txtNewEmailAddress;
	private JTextField txtNewPhoneNumber;
	private JTextField txtNewSid;
	
	private StringTableModel addresses = new StringTableModel("Email Address");
	private StringTableModel phoneNumbers = new StringTableModel("Phone Number");
	private StringTableModel sids = new StringTableModel("SID");
	
	private SQLUserRecordStore userRecordStore = null;

	private JideTable addressesTable;

	private JideTable phoneNumbersTable;

	private JideTable sidsTable;

	public AddUserDialog(Window parent, SQLUserRecordStore store) {
		super(parent);
		setTitle("Add New User");
		setIconImage(Toolkit.getDefaultToolkit().getImage(AddUserDialog.class.getResource("/com/nuix/evmanager/controls/nuix_icon.png")));
		userRecordStore = store;
		setModal(true);
		setSize(800, 600);
		setLocationRelativeTo(null);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		GridBagLayout gbl_contentPanel = new GridBagLayout();
		gbl_contentPanel.columnWidths = new int[]{0, 0, 0, 0};
		gbl_contentPanel.rowHeights = new int[]{0, 0, 0, 0, 0, 0, 0};
		gbl_contentPanel.columnWeights = new double[]{0.0, 1.0, 1.0, Double.MIN_VALUE};
		gbl_contentPanel.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 1.0, Double.MIN_VALUE};
		contentPanel.setLayout(gbl_contentPanel);
		{
			JLabel lblEmployeeId = new JLabel("Employee ID:");
			GridBagConstraints gbc_lblEmployeeId = new GridBagConstraints();
			gbc_lblEmployeeId.anchor = GridBagConstraints.EAST;
			gbc_lblEmployeeId.insets = new Insets(0, 0, 5, 5);
			gbc_lblEmployeeId.gridx = 0;
			gbc_lblEmployeeId.gridy = 0;
			contentPanel.add(lblEmployeeId, gbc_lblEmployeeId);
		}
		{
			txtEmployeeId = new JTextField();
			GridBagConstraints gbc_txtEmployeeId = new GridBagConstraints();
			gbc_txtEmployeeId.insets = new Insets(0, 0, 5, 5);
			gbc_txtEmployeeId.fill = GridBagConstraints.HORIZONTAL;
			gbc_txtEmployeeId.gridx = 1;
			gbc_txtEmployeeId.gridy = 0;
			contentPanel.add(txtEmployeeId, gbc_txtEmployeeId);
			txtEmployeeId.setColumns(10);
		}
		{
			JLabel lblName = new JLabel("Name:");
			GridBagConstraints gbc_lblName = new GridBagConstraints();
			gbc_lblName.anchor = GridBagConstraints.EAST;
			gbc_lblName.insets = new Insets(0, 0, 5, 5);
			gbc_lblName.gridx = 0;
			gbc_lblName.gridy = 1;
			contentPanel.add(lblName, gbc_lblName);
		}
		{
			txtName = new JTextField();
			GridBagConstraints gbc_txtName = new GridBagConstraints();
			gbc_txtName.insets = new Insets(0, 0, 5, 5);
			gbc_txtName.fill = GridBagConstraints.HORIZONTAL;
			gbc_txtName.gridx = 1;
			gbc_txtName.gridy = 1;
			contentPanel.add(txtName, gbc_txtName);
			txtName.setColumns(10);
		}
		{
			JLabel lblTitle = new JLabel("Title:");
			GridBagConstraints gbc_lblTitle = new GridBagConstraints();
			gbc_lblTitle.anchor = GridBagConstraints.EAST;
			gbc_lblTitle.insets = new Insets(0, 0, 5, 5);
			gbc_lblTitle.gridx = 0;
			gbc_lblTitle.gridy = 2;
			contentPanel.add(lblTitle, gbc_lblTitle);
		}
		{
			txtTitle = new JTextField();
			GridBagConstraints gbc_txtTitle = new GridBagConstraints();
			gbc_txtTitle.insets = new Insets(0, 0, 5, 5);
			gbc_txtTitle.fill = GridBagConstraints.HORIZONTAL;
			gbc_txtTitle.gridx = 1;
			gbc_txtTitle.gridy = 2;
			contentPanel.add(txtTitle, gbc_txtTitle);
			txtTitle.setColumns(10);
		}
		{
			JLabel lblDepartment = new JLabel("Department:");
			GridBagConstraints gbc_lblDepartment = new GridBagConstraints();
			gbc_lblDepartment.anchor = GridBagConstraints.EAST;
			gbc_lblDepartment.insets = new Insets(0, 0, 5, 5);
			gbc_lblDepartment.gridx = 0;
			gbc_lblDepartment.gridy = 3;
			contentPanel.add(lblDepartment, gbc_lblDepartment);
		}
		{
			txtDepartment = new JTextField();
			GridBagConstraints gbc_txtDepartment = new GridBagConstraints();
			gbc_txtDepartment.insets = new Insets(0, 0, 5, 5);
			gbc_txtDepartment.fill = GridBagConstraints.HORIZONTAL;
			gbc_txtDepartment.gridx = 1;
			gbc_txtDepartment.gridy = 3;
			contentPanel.add(txtDepartment, gbc_txtDepartment);
			txtDepartment.setColumns(10);
		}
		{
			JLabel lblLocation = new JLabel("Location:");
			GridBagConstraints gbc_lblLocation = new GridBagConstraints();
			gbc_lblLocation.insets = new Insets(0, 0, 5, 5);
			gbc_lblLocation.anchor = GridBagConstraints.EAST;
			gbc_lblLocation.gridx = 0;
			gbc_lblLocation.gridy = 4;
			contentPanel.add(lblLocation, gbc_lblLocation);
		}
		{
			txtLocation = new JTextField();
			GridBagConstraints gbc_txtLocation = new GridBagConstraints();
			gbc_txtLocation.insets = new Insets(0, 0, 5, 5);
			gbc_txtLocation.fill = GridBagConstraints.HORIZONTAL;
			gbc_txtLocation.gridx = 1;
			gbc_txtLocation.gridy = 4;
			contentPanel.add(txtLocation, gbc_txtLocation);
			txtLocation.setColumns(10);
		}
		{
			JPanel panel = new JPanel();
			GridBagConstraints gbc_panel = new GridBagConstraints();
			gbc_panel.gridwidth = 3;
			gbc_panel.insets = new Insets(0, 0, 0, 5);
			gbc_panel.fill = GridBagConstraints.BOTH;
			gbc_panel.gridx = 0;
			gbc_panel.gridy = 5;
			contentPanel.add(panel, gbc_panel);
			GridBagLayout gbl_panel = new GridBagLayout();
			gbl_panel.columnWidths = new int[]{0, 0, 0, 10, 0, 0, 0};
			gbl_panel.rowHeights = new int[]{10, 0, 0, 10, 0, 0, 0};
			gbl_panel.columnWeights = new double[]{0.0, 1.0, 0.0, 0.0, 0.0, 1.0, 0.0};
			gbl_panel.rowWeights = new double[]{0.0, 0.0, 1.0, 0.0, 0.0, 1.0, Double.MIN_VALUE};
			panel.setLayout(gbl_panel);
			{
				JButton btnAddEmailAddress = new JButton("Add Email Address");
				btnAddEmailAddress.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						String value = txtNewEmailAddress.getText().trim();
						if(value.length() > 1000){
							String message = "Email address value exceeds limit of 1000 characters";
							CommonDialogs.showWarning(message, "Value Exceeds Max Length");
						} else {
							addresses.addValue(value);
							txtNewEmailAddress.setText("");	
						}
					}
				});
				GridBagConstraints gbc_btnAddEmailAddress = new GridBagConstraints();
				gbc_btnAddEmailAddress.fill = GridBagConstraints.HORIZONTAL;
				gbc_btnAddEmailAddress.insets = new Insets(0, 0, 5, 5);
				gbc_btnAddEmailAddress.gridx = 0;
				gbc_btnAddEmailAddress.gridy = 1;
				panel.add(btnAddEmailAddress, gbc_btnAddEmailAddress);
			}
			{
				txtNewEmailAddress = new JTextField();
				txtNewEmailAddress.addKeyListener(new KeyAdapter() {
					@Override
					public void keyReleased(KeyEvent event) {
						if(event.getKeyCode() == KeyEvent.VK_ENTER){
							String value = txtNewEmailAddress.getText().trim();
							if(value.length() > 1000){
								String message = "Email address value exceeds limit of 1000 characters";
								CommonDialogs.showWarning(message, "Value Exceeds Max Length");
							} else {
								addresses.addValue(value);
								txtNewEmailAddress.setText("");	
							}
						}
					}
				});
				GridBagConstraints gbc_txtNewEmailAddress = new GridBagConstraints();
				gbc_txtNewEmailAddress.gridwidth = 5;
				gbc_txtNewEmailAddress.insets = new Insets(0, 0, 5, 5);
				gbc_txtNewEmailAddress.fill = GridBagConstraints.HORIZONTAL;
				gbc_txtNewEmailAddress.gridx = 1;
				gbc_txtNewEmailAddress.gridy = 1;
				panel.add(txtNewEmailAddress, gbc_txtNewEmailAddress);
				txtNewEmailAddress.setColumns(10);
			}
			{
				JToolBar toolBar = new JToolBar();
				toolBar.setFloatable(false);
				GridBagConstraints gbc_toolBar = new GridBagConstraints();
				gbc_toolBar.insets = new Insets(0, 0, 5, 0);
				gbc_toolBar.gridx = 6;
				gbc_toolBar.gridy = 1;
				panel.add(toolBar, gbc_toolBar);
				{
					JButton btnDeleteSelectedEmail = new JButton("");
					btnDeleteSelectedEmail.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							int[] indices = addressesTable.getSelectedRows();
							if(indices.length > 0){
								String message = "Are you sure you want to remove the selected "+indices.length+" addresses?";
								String title = "Delete Selected Addresses?";
								if(CommonDialogs.getConfirmation(message, title) == true){
									addresses.removeIndices(indices);	
								}
							}
						}
					});
					toolBar.add(btnDeleteSelectedEmail);
					btnDeleteSelectedEmail.setToolTipText("Deleted selected email addresses");
					btnDeleteSelectedEmail.setIcon(new ImageIcon(AddUserDialog.class.getResource("/com/nuix/evmanager/controls/bin_closed.png")));
				}
			}
			{
				JScrollPane scrollPane = new JScrollPane();
				scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
				GridBagConstraints gbc_scrollPane = new GridBagConstraints();
				gbc_scrollPane.gridwidth = 7;
				gbc_scrollPane.insets = new Insets(0, 0, 5, 0);
				gbc_scrollPane.fill = GridBagConstraints.BOTH;
				gbc_scrollPane.gridx = 0;
				gbc_scrollPane.gridy = 2;
				panel.add(scrollPane, gbc_scrollPane);
				{
					addressesTable = new JideTable(addresses);
					addressesTable.addKeyListener(new KeyAdapter() {
						@Override
						public void keyReleased(KeyEvent event) {
							if(event.getKeyCode() == KeyEvent.VK_DELETE){
								int[] indices = addressesTable.getSelectedRows();
								if(indices.length > 0){
									String message = "Are you sure you want to remove the selected "+indices.length+" addresses?";
									String title = "Delete Selected Addresses?";
									if(CommonDialogs.getConfirmation(message, title) == true){
										addresses.removeIndices(indices);	
									}
								}
							}
						}
					});
					addressesTable.setFillsViewportHeight(true);
					scrollPane.setViewportView(addressesTable);
				}
			}
			{
				JButton btnAddPhoneNumber = new JButton("Add Phone Number");
				btnAddPhoneNumber.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						String value = txtNewPhoneNumber.getText().trim();
						
						if(value.length() > 30){
							String message = "Phone number value exceeds limit of 30 characters";
							CommonDialogs.showWarning(message, "Value Exceeds Max Length");	
						} else {
							phoneNumbers.addValue(value);
							txtNewPhoneNumber.setText("");
						}
					}
				});
				GridBagConstraints gbc_btnAddPhoneNumber = new GridBagConstraints();
				gbc_btnAddPhoneNumber.fill = GridBagConstraints.HORIZONTAL;
				gbc_btnAddPhoneNumber.insets = new Insets(0, 0, 5, 5);
				gbc_btnAddPhoneNumber.gridx = 0;
				gbc_btnAddPhoneNumber.gridy = 4;
				panel.add(btnAddPhoneNumber, gbc_btnAddPhoneNumber);
			}
			{
				txtNewPhoneNumber = new JTextField();
				txtNewPhoneNumber.addKeyListener(new KeyAdapter() {
					@Override
					public void keyReleased(KeyEvent event) {
						if(event.getKeyCode() == KeyEvent.VK_ENTER){
							String value = txtNewPhoneNumber.getText().trim();
							
							if(value.length() > 30){
								String message = "Phone number value exceeds limit of 30 characters";
								CommonDialogs.showWarning(message, "Value Exceeds Max Length");	
							} else {
								phoneNumbers.addValue(value);
								txtNewPhoneNumber.setText("");
							}
						}
					}
				});
				GridBagConstraints gbc_txtNewPhoneNumber = new GridBagConstraints();
				gbc_txtNewPhoneNumber.insets = new Insets(0, 0, 5, 5);
				gbc_txtNewPhoneNumber.fill = GridBagConstraints.HORIZONTAL;
				gbc_txtNewPhoneNumber.gridx = 1;
				gbc_txtNewPhoneNumber.gridy = 4;
				panel.add(txtNewPhoneNumber, gbc_txtNewPhoneNumber);
				txtNewPhoneNumber.setColumns(10);
			}
			{
				JButton btnAddSid = new JButton("Add SID");
				btnAddSid.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						String value = txtNewSid.getText().trim();
						
						if(value.length() > 190){
							String message = "SID value exceeds limit of 190 characters";
							CommonDialogs.showWarning(message, "Value Exceeds Max Length");
						} else {
							sids.addValue(value);
							txtNewSid.setText("");
						}
					}
				});
				{
					JToolBar toolBar = new JToolBar();
					toolBar.setFloatable(false);
					GridBagConstraints gbc_toolBar = new GridBagConstraints();
					gbc_toolBar.insets = new Insets(0, 0, 5, 5);
					gbc_toolBar.gridx = 2;
					gbc_toolBar.gridy = 4;
					panel.add(toolBar, gbc_toolBar);
					{
						JButton btnDeleteSelectedPhone = new JButton("");
						btnDeleteSelectedPhone.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent e) {
								int[] indices = phoneNumbersTable.getSelectedRows();
								if(indices.length > 0){
									String message = "Are you sure you want to remove the selected "+indices.length+" phone numbers?";
									String title = "Delete Selected Phone Numbers?";
									if(CommonDialogs.getConfirmation(message, title) == true){
										phoneNumbers.removeIndices(indices);
									}
								}
							}
						});
						btnDeleteSelectedPhone.setIcon(new ImageIcon(AddUserDialog.class.getResource("/com/nuix/evmanager/controls/bin_closed.png")));
						btnDeleteSelectedPhone.setToolTipText("Delete selected phone numbers");
						toolBar.add(btnDeleteSelectedPhone);
					}
				}
				GridBagConstraints gbc_btnAddSid = new GridBagConstraints();
				gbc_btnAddSid.fill = GridBagConstraints.HORIZONTAL;
				gbc_btnAddSid.insets = new Insets(0, 0, 5, 5);
				gbc_btnAddSid.gridx = 4;
				gbc_btnAddSid.gridy = 4;
				panel.add(btnAddSid, gbc_btnAddSid);
			}
			{
				txtNewSid = new JTextField();
				txtNewSid.addKeyListener(new KeyAdapter() {
					@Override
					public void keyReleased(KeyEvent event) {
						if(event.getKeyCode() == KeyEvent.VK_ENTER){
							String value = txtNewSid.getText().trim();
							
							if(value.length() > 190){
								String message = "SID value exceeds limit of 190 characters";
								CommonDialogs.showWarning(message, "Value Exceeds Max Length");
							} else {
								sids.addValue(value);
								txtNewSid.setText("");
							}
						}
					}
				});
				GridBagConstraints gbc_txtNewSid = new GridBagConstraints();
				gbc_txtNewSid.insets = new Insets(0, 0, 5, 5);
				gbc_txtNewSid.fill = GridBagConstraints.HORIZONTAL;
				gbc_txtNewSid.gridx = 5;
				gbc_txtNewSid.gridy = 4;
				panel.add(txtNewSid, gbc_txtNewSid);
				txtNewSid.setColumns(10);
			}
			{
				JToolBar toolBar = new JToolBar();
				toolBar.setFloatable(false);
				GridBagConstraints gbc_toolBar = new GridBagConstraints();
				gbc_toolBar.insets = new Insets(0, 0, 5, 0);
				gbc_toolBar.gridx = 6;
				gbc_toolBar.gridy = 4;
				panel.add(toolBar, gbc_toolBar);
				{
					JButton btnDeleteSelectedSids = new JButton("");
					btnDeleteSelectedSids.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							int[] indices = sidsTable.getSelectedRows();
							if(indices.length > 0){
								String message = "Are you sure you want to remove the selected "+indices.length+" SIDs?";
								String title = "Delete Selected SIDs?";
								if(CommonDialogs.getConfirmation(message, title) == true){
									sids.removeIndices(indices);
								}
							}
						}
					});
					btnDeleteSelectedSids.setIcon(new ImageIcon(AddUserDialog.class.getResource("/com/nuix/evmanager/controls/bin_closed.png")));
					btnDeleteSelectedSids.setToolTipText("Delete selected SIDs");
					toolBar.add(btnDeleteSelectedSids);
				}
			}
			{
				JScrollPane scrollPane = new JScrollPane();
				scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
				GridBagConstraints gbc_scrollPane = new GridBagConstraints();
				gbc_scrollPane.gridwidth = 3;
				gbc_scrollPane.insets = new Insets(0, 0, 0, 5);
				gbc_scrollPane.fill = GridBagConstraints.BOTH;
				gbc_scrollPane.gridx = 0;
				gbc_scrollPane.gridy = 5;
				panel.add(scrollPane, gbc_scrollPane);
				{
					phoneNumbersTable = new JideTable(phoneNumbers);
					phoneNumbersTable.addKeyListener(new KeyAdapter() {
						@Override
						public void keyReleased(KeyEvent event) {
							if(event.getKeyCode() == KeyEvent.VK_DELETE){
								int[] indices = phoneNumbersTable.getSelectedRows();
								if(indices.length > 0){
									String message = "Are you sure you want to remove the selected "+indices.length+" phone numbers?";
									String title = "Delete Selected Phone Numbers?";
									if(CommonDialogs.getConfirmation(message, title) == true){
										phoneNumbers.removeIndices(indices);
									}
								}
							}
						}
					});
					phoneNumbersTable.setFillsViewportHeight(true);
					scrollPane.setViewportView(phoneNumbersTable);
				}
			}
			{
				JScrollPane scrollPane = new JScrollPane();
				scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
				GridBagConstraints gbc_scrollPane = new GridBagConstraints();
				gbc_scrollPane.gridwidth = 3;
				gbc_scrollPane.fill = GridBagConstraints.BOTH;
				gbc_scrollPane.gridx = 4;
				gbc_scrollPane.gridy = 5;
				panel.add(scrollPane, gbc_scrollPane);
				{
					sidsTable = new JideTable(sids);
					sidsTable.addKeyListener(new KeyAdapter() {
						@Override
						public void keyReleased(KeyEvent event) {
							if(event.getKeyCode() == KeyEvent.VK_DELETE){
								int[] indices = sidsTable.getSelectedRows();
								if(indices.length > 0){
									String message = "Are you sure you want to remove the selected "+indices.length+" SIDs?";
									String title = "Delete Selected SIDs?";
									if(CommonDialogs.getConfirmation(message, title) == true){
										sids.removeIndices(indices);
									}
								}
							}
						}
					});
					sidsTable.setFillsViewportHeight(true);
					scrollPane.setViewportView(sidsTable);
				}
			}
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("Add User");
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						saveUser();
					}
				});
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
			}
			{
				JButton cancelButton = new JButton("Cancel");
				cancelButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						if(CommonDialogs.getConfirmation("Are you sure you want to discard any changes you may have made?", "Discard?")){
							dispose();	
						}
					}
				});
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
	}
	
	protected void saveUser(){
		if(userDataIsValid()){
			String employeeId = txtEmployeeId.getText().trim();
			String name = txtName.getText().trim();
			String title = txtTitle.getText().trim();
			String department = txtDepartment.getText().trim();
			String location = txtLocation.getText().trim();
			
			List<String> emailAddressValues = addresses.getValues();
			List<String> phoneNumberValues = phoneNumbers.getValues();
			List<String> sidValues = sids.getValues();
			
			UserRecord record = new UserRecord();
			record.setEmployeeID(employeeId);
			record.setName(name);
			record.setTitle(title);
			record.setDepartment(department);
			record.setLocation(location);
			record.setAddresses(emailAddressValues.stream().map(v -> new UserAddress(v)).collect(Collectors.toList()));
			record.setPhoneNumbers(phoneNumberValues);
			record.setSIDs(sidValues);
			
			boolean success = false;
			try {
				success = userRecordStore.addUserRecord(record);
			} catch (Exception e) {
				CommonDialogs.showError("Error while attempting to add new user to database: \n"+e.getMessage());
				logger.error("Error while attempting to add new user to database",e);
				e.printStackTrace();
			}
			
			if(success){
				this.dispose();
			}
		}
	}

	protected boolean userDataIsValid(){
		/*
		 * - Employee ID cannot be empty
		 * - Name cannot be empty
		 * - Title, Department and Location may be empty
		 * - Should have at least 1 email address
		 */
		int maxLength = 100;
		
		String employeeId = txtEmployeeId.getText().trim();
		String name = txtName.getText().trim();
		String title = txtTitle.getText().trim();
		String department = txtDepartment.getText().trim();
		String location = txtLocation.getText().trim();
		
		List<String> emailAddressValues = addresses.getValues();
		
		if(employeeId.length() > maxLength){
			String message = "Value for Employee ID exceeds max length of "+maxLength;
			CommonDialogs.showWarning(message, "Value Exceeds Max Length");
			return false;
		}
		
		if(name.length() > maxLength){
			String message = "Value for Name exceeds max length of "+maxLength;
			CommonDialogs.showWarning(message, "Value Exceeds Max Length");
			return false;
		}
		
		if(title.length() > maxLength){
			String message = "Value for Title exceeds max length of "+maxLength;
			CommonDialogs.showWarning(message, "Value Exceeds Max Length");
			return false;
		}
		
		if(department.length() > maxLength){
			String message = "Value for Department exceeds max length of "+maxLength;
			CommonDialogs.showWarning(message, "Value Exceeds Max Length");
			return false;
		}
		
		if(location.length() > maxLength){
			String message = "Value for Location exceeds max length of "+maxLength;
			CommonDialogs.showWarning(message, "Value Exceeds Max Length");
			return false;
		}
		
		if(employeeId.isEmpty()){
			CommonDialogs.showWarning("Please provide a value for Employee ID.", "No Employee ID Provided");
			return false;
		}
		
		try {
			if(userRecordStore.employeedIdExists(employeeId)){
				CommonDialogs.showWarning("The Employee ID you provided already exists in the database.", "Employee ID Already Exists");
				return false;
			}
		} catch (Exception e) {
			CommonDialogs.showError("Error while checking user record database for employee ID existence: "+e.getMessage());
			logger.error("Error while checking user record database for employee ID existence",e);
			e.printStackTrace();
			return false;
		}
		
		if(name.isEmpty()){
			CommonDialogs.showWarning("Please provide a value for Name.", "No Name Provided");
			return false;
		}
		
		if(emailAddressValues.size() < 1){
			CommonDialogs.showWarning("Please provide at least 1 email address.", "No Email Addresses Provided");
			return false;
		}
		
		boolean titleEmpty = title.isEmpty();
		boolean departmentEmpty = department.isEmpty();
		boolean locationEmpty = location.isEmpty();
		boolean zeroPhoneNumbers = phoneNumbers.getValues().size() < 1;
		boolean zeroSids = sids.getValues().size() < 1;
		
		if(titleEmpty || departmentEmpty || locationEmpty || zeroPhoneNumbers || zeroSids){
			StringJoiner message = new StringJoiner("\n");
			message.add("The following have no value provided:");
			if(titleEmpty){ message.add("- Title"); }
			if(departmentEmpty){ message.add("- Department"); }
			if(locationEmpty){ message.add("- Location"); }
			if(zeroPhoneNumbers){ message.add("- Phone Numbers"); }
			if(zeroSids){ message.add("- SIDs"); }
			message.add("While a user may be added using this data as is, please confirm you wish to proceed.");
			if(CommonDialogs.getConfirmation(message.toString(), "Optional Values Not Provided") == false){
				return false;
			}
		}
		
		return true;
	}
}
