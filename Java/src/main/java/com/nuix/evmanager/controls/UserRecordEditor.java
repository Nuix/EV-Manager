package com.nuix.evmanager.controls;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.nuix.evmanager.data.AddressChangeOrder;
import com.nuix.evmanager.data.SQLUserRecord;
import com.nuix.evmanager.data.UserAddress;

@SuppressWarnings("serial")
public class UserRecordEditor extends JPanel {
	private SQLUserRecord currentRecord;
	private JTextField txtEmployeeId;
	private JTextField txtName;
	private JTextField txtTitle;
	private JTextField txtDepartment;
	private JTextField txtLocation;
	private JTable addressesTable;
	
	AddressEditorTableModel addressesTableModel = new AddressEditorTableModel();
	private JTextField txtNewAddress;
	private List<IsDirtyChangeListener> isDirtyListeners = new ArrayList<IsDirtyChangeListener>();
	private JTextField txtDatabaseId;
	private JButton btnAdd;
	
	public UserRecordEditor() {
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{0, 0, 0, 0, 0};
		gridBagLayout.rowHeights = new int[]{0, 0, 0};
		gridBagLayout.columnWeights = new double[]{1.0, 1.0, 0.0, 1.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, 1.0, Double.MIN_VALUE};
		setLayout(gridBagLayout);
		
		JPanel userInfoSection = new JPanel();
		userInfoSection.setBorder(new CompoundBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "User Information", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)), new EmptyBorder(5, 5, 5, 5)));
		GridBagConstraints gbc_userInfoSection = new GridBagConstraints();
		gbc_userInfoSection.gridwidth = 4;
		gbc_userInfoSection.insets = new Insets(0, 0, 5, 0);
		gbc_userInfoSection.fill = GridBagConstraints.BOTH;
		gbc_userInfoSection.gridx = 0;
		gbc_userInfoSection.gridy = 0;
		add(userInfoSection, gbc_userInfoSection);
		GridBagLayout gbl_userInfoSection = new GridBagLayout();
		gbl_userInfoSection.columnWidths = new int[]{0, 0, 25, 0, 0, 0};
		gbl_userInfoSection.rowHeights = new int[]{0, 0, 0, 0};
		gbl_userInfoSection.columnWeights = new double[]{0.0, 1.0, 0.0, 0.0, 1.0, Double.MIN_VALUE};
		gbl_userInfoSection.rowWeights = new double[]{0.0, 0.0, 0.0, Double.MIN_VALUE};
		userInfoSection.setLayout(gbl_userInfoSection);
		
		JLabel lblEmployeeId = new JLabel("Employee ID");
		GridBagConstraints gbc_lblEmployeeId = new GridBagConstraints();
		gbc_lblEmployeeId.anchor = GridBagConstraints.EAST;
		gbc_lblEmployeeId.insets = new Insets(0, 0, 5, 5);
		gbc_lblEmployeeId.gridx = 0;
		gbc_lblEmployeeId.gridy = 0;
		userInfoSection.add(lblEmployeeId, gbc_lblEmployeeId);
		
		txtEmployeeId = new JTextField();
		GridBagConstraints gbc_txtEmployeeId = new GridBagConstraints();
		gbc_txtEmployeeId.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtEmployeeId.insets = new Insets(0, 0, 5, 5);
		gbc_txtEmployeeId.gridx = 1;
		gbc_txtEmployeeId.gridy = 0;
		userInfoSection.add(txtEmployeeId, gbc_txtEmployeeId);
		txtEmployeeId.setEditable(false);
		txtEmployeeId.setColumns(10);
		
		JLabel lblDatabaseId = new JLabel("Database ID");
		GridBagConstraints gbc_lblDatabaseId = new GridBagConstraints();
		gbc_lblDatabaseId.anchor = GridBagConstraints.EAST;
		gbc_lblDatabaseId.insets = new Insets(0, 0, 5, 5);
		gbc_lblDatabaseId.gridx = 3;
		gbc_lblDatabaseId.gridy = 0;
		userInfoSection.add(lblDatabaseId, gbc_lblDatabaseId);
		
		txtDatabaseId = new JTextField();
		GridBagConstraints gbc_txtDatabaseId = new GridBagConstraints();
		gbc_txtDatabaseId.insets = new Insets(0, 0, 5, 0);
		gbc_txtDatabaseId.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtDatabaseId.gridx = 4;
		gbc_txtDatabaseId.gridy = 0;
		userInfoSection.add(txtDatabaseId, gbc_txtDatabaseId);
		txtDatabaseId.setEditable(false);
		txtDatabaseId.setColumns(10);
		
		JLabel lblName = new JLabel("Name");
		GridBagConstraints gbc_lblName = new GridBagConstraints();
		gbc_lblName.anchor = GridBagConstraints.EAST;
		gbc_lblName.insets = new Insets(0, 0, 5, 5);
		gbc_lblName.gridx = 0;
		gbc_lblName.gridy = 1;
		userInfoSection.add(lblName, gbc_lblName);
		
		txtName = new JTextField();
		GridBagConstraints gbc_txtName = new GridBagConstraints();
		gbc_txtName.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtName.insets = new Insets(0, 0, 5, 5);
		gbc_txtName.gridx = 1;
		gbc_txtName.gridy = 1;
		userInfoSection.add(txtName, gbc_txtName);
		txtName.setEditable(false);
		txtName.setColumns(10);
		
		JLabel lblDepartment = new JLabel("Department");
		GridBagConstraints gbc_lblDepartment = new GridBagConstraints();
		gbc_lblDepartment.anchor = GridBagConstraints.EAST;
		gbc_lblDepartment.insets = new Insets(0, 0, 5, 5);
		gbc_lblDepartment.gridx = 3;
		gbc_lblDepartment.gridy = 1;
		userInfoSection.add(lblDepartment, gbc_lblDepartment);
		
		txtDepartment = new JTextField();
		GridBagConstraints gbc_txtDepartment = new GridBagConstraints();
		gbc_txtDepartment.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtDepartment.insets = new Insets(0, 0, 5, 0);
		gbc_txtDepartment.gridx = 4;
		gbc_txtDepartment.gridy = 1;
		userInfoSection.add(txtDepartment, gbc_txtDepartment);
		txtDepartment.setEditable(false);
		txtDepartment.setColumns(10);
		
		JLabel lblLocation = new JLabel("Location");
		GridBagConstraints gbc_lblLocation = new GridBagConstraints();
		gbc_lblLocation.anchor = GridBagConstraints.EAST;
		gbc_lblLocation.insets = new Insets(0, 0, 0, 5);
		gbc_lblLocation.gridx = 0;
		gbc_lblLocation.gridy = 2;
		userInfoSection.add(lblLocation, gbc_lblLocation);
		
		txtLocation = new JTextField();
		GridBagConstraints gbc_txtLocation = new GridBagConstraints();
		gbc_txtLocation.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtLocation.insets = new Insets(0, 0, 0, 5);
		gbc_txtLocation.gridx = 1;
		gbc_txtLocation.gridy = 2;
		userInfoSection.add(txtLocation, gbc_txtLocation);
		txtLocation.setEditable(false);
		txtLocation.setColumns(10);
		
		JLabel lblTitle = new JLabel("Title");
		GridBagConstraints gbc_lblTitle = new GridBagConstraints();
		gbc_lblTitle.anchor = GridBagConstraints.EAST;
		gbc_lblTitle.insets = new Insets(0, 0, 0, 5);
		gbc_lblTitle.gridx = 3;
		gbc_lblTitle.gridy = 2;
		userInfoSection.add(lblTitle, gbc_lblTitle);
		
		txtTitle = new JTextField();
		GridBagConstraints gbc_txtTitle = new GridBagConstraints();
		gbc_txtTitle.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtTitle.gridx = 4;
		gbc_txtTitle.gridy = 2;
		userInfoSection.add(txtTitle, gbc_txtTitle);
		txtTitle.setEditable(false);
		txtTitle.setColumns(10);
		
		JPanel adressesSection = new JPanel();
		adressesSection.setBorder(new CompoundBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "User Addresses", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)), new EmptyBorder(5, 5, 5, 5)));
		GridBagConstraints gbc_adressesSection = new GridBagConstraints();
		gbc_adressesSection.gridwidth = 4;
		gbc_adressesSection.fill = GridBagConstraints.BOTH;
		gbc_adressesSection.gridx = 0;
		gbc_adressesSection.gridy = 1;
		add(adressesSection, gbc_adressesSection);
		GridBagLayout gbl_adressesSection = new GridBagLayout();
		gbl_adressesSection.columnWidths = new int[]{0, 0, 0};
		gbl_adressesSection.rowHeights = new int[]{0, 0, 0};
		gbl_adressesSection.columnWeights = new double[]{1.0, 0.0, Double.MIN_VALUE};
		gbl_adressesSection.rowWeights = new double[]{0.0, 1.0, Double.MIN_VALUE};
		adressesSection.setLayout(gbl_adressesSection);
		
		txtNewAddress = new JTextField();
		GridBagConstraints gbc_txtNewAddress = new GridBagConstraints();
		gbc_txtNewAddress.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtNewAddress.insets = new Insets(0, 0, 5, 5);
		gbc_txtNewAddress.gridx = 0;
		gbc_txtNewAddress.gridy = 0;
		adressesSection.add(txtNewAddress, gbc_txtNewAddress);
		txtNewAddress.setColumns(10);
		
		JToolBar toolBar = new JToolBar();
		toolBar.setFloatable(false);
		GridBagConstraints gbc_toolBar = new GridBagConstraints();
		gbc_toolBar.insets = new Insets(0, 0, 5, 0);
		gbc_toolBar.gridx = 1;
		gbc_toolBar.gridy = 0;
		adressesSection.add(toolBar, gbc_toolBar);
		
		btnAdd = new JButton("");
		btnAdd.setEnabled(false);
		btnAdd.setToolTipText("Add email address");
		toolBar.add(btnAdd);
		btnAdd.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				performAddNewAddress();
			}
		});
		btnAdd.setIcon(new ImageIcon(UserRecordEditor.class.getResource("/com/nuix/evmanager/controls/add.png")));
		
		JSeparator separator = new JSeparator();
		separator.setOrientation(SwingConstants.VERTICAL);
		toolBar.add(separator);
		
		JButton btnDeleteSelected = new JButton("");
		toolBar.add(btnDeleteSelected);
		btnDeleteSelected.setToolTipText("Mark selected addresses for deletion");
		btnDeleteSelected.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				performDeleteSelected();
			}
		});
		btnDeleteSelected.setIcon(new ImageIcon(UserRecordEditor.class.getResource("/com/nuix/evmanager/controls/delete.png")));
		
		JButton btnUndoAllChanges = new JButton("");
		toolBar.add(btnUndoAllChanges);
		btnUndoAllChanges.setToolTipText("Undo all modifications");
		btnUndoAllChanges.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					if(CommonDialogs.getConfirmation("Undo all modifications?", "Undo All?")){
						undoAllChanges();	
					}
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		});
		btnUndoAllChanges.setIcon(new ImageIcon(UserRecordEditor.class.getResource("/com/nuix/evmanager/controls/arrow_turn_left.png")));
		
		JScrollPane scrollPane = new JScrollPane();
		GridBagConstraints gbc_scrollPane = new GridBagConstraints();
		gbc_scrollPane.gridwidth = 2;
		gbc_scrollPane.fill = GridBagConstraints.BOTH;
		gbc_scrollPane.gridx = 0;
		gbc_scrollPane.gridy = 1;
		adressesSection.add(scrollPane, gbc_scrollPane);
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		
		addressesTable = new JTable(addressesTableModel);
		addressesTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		addressesTable.setFillsViewportHeight(true);
		scrollPane.setViewportView(addressesTable);
		addressesTable.getColumnModel().getColumn(0).setMaxWidth(60);
		
		addressesTableModel.addIsDirtyChangeListener(new IsDirtyChangeListener() {
			
			@Override
			public void statusChanged(boolean isDirty) {
				notifyIsDirtyListeners(isDirty);
			}
		});
		
		txtNewAddress.getDocument().addDocumentListener(new DocumentListener() {
			
			@Override
			public void removeUpdate(DocumentEvent e){ handleChange(); }
			@Override
			public void insertUpdate(DocumentEvent e){ handleChange(); }
			@Override
			public void changedUpdate(DocumentEvent e){ handleChange(); }
			
			public void handleChange(){
				String updatedValue = txtNewAddress.getText();
				if(updatedValue != null && updatedValue.trim().length() > 0){
					btnAdd.setEnabled(true);
				} else {
					btnAdd.setEnabled(false);
				}
			}
		});
	}

	public void setUserRecord(SQLUserRecord userRecord) throws Exception{
		currentRecord = userRecord;
		txtEmployeeId.setText(currentRecord.getEmployeeID());
		txtDatabaseId.setText(""+currentRecord.getDatabaseID());
		txtName.setText(currentRecord.getName());
		txtTitle.setText(currentRecord.getTitle());
		txtDepartment.setText(currentRecord.getDepartment());
		txtLocation.setText(currentRecord.getLocation());
		addressesTableModel.setAddresses(currentRecord,currentRecord.getAddresses());
	}
	
	private void performAddNewAddress() {
		String newAddressString = txtNewAddress.getText().trim();
		if(newAddressString.length() > 0){
			UserAddress newAddress = new UserAddress();
			newAddress.setAddress(newAddressString);
			newAddress.setAssociatedUserRecord(currentRecord);
			addressesTableModel.addNewAddress(newAddress);
			txtNewAddress.setText("");
		}
	}

	private void performDeleteSelected() {
		int[] selectedRows = addressesTable.getSelectedRows();
		for (int i = 0; i < selectedRows.length; i++) {
			addressesTableModel.deleteRow(selectedRows[i]);
		}
	}
	
	public void undoAllChanges() throws Exception{
		addressesTableModel.setAddresses(currentRecord,currentRecord.getAddresses(true));
	}
	
	public void addIsDirtyChangeListener(IsDirtyChangeListener listener){
		isDirtyListeners.add(listener);
	}
	
	public void removeIsDirtyListener(IsDirtyChangeListener listener){
		isDirtyListeners.remove(listener);
	}
	
	private void notifyIsDirtyListeners(boolean isDirty){
		for(IsDirtyChangeListener listener : isDirtyListeners){
			listener.statusChanged(isDirty);
		}
	}
	
	public AddressChangeOrder getChangeOrder(){
		return addressesTableModel.getChangeOrder();
	}
	
	public boolean isDirty() {
		return addressesTableModel.isDirty();
	}
}
