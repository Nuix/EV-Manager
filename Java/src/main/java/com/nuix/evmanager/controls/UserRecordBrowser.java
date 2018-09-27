package com.nuix.evmanager.controls;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.apache.log4j.Logger;

import com.nuix.evmanager.data.AddressChangeOrder;
import com.nuix.evmanager.data.SQLUserRecord;
import com.nuix.evmanager.data.UserRecordCriteria;
import com.nuix.evmanager.data.UserRecordStore;
import com.nuix.evmanager.data.VirtualizedRecordCollection;

/***
 * A control for browsing and filtering user record entries present in the address book database
 * @author Jason Wells
 *
 */
@SuppressWarnings("serial")
public class UserRecordBrowser extends JPanel {
	private static Logger logger = Logger.getLogger(UserRecordBrowser.class);
	
	private UserRecordStore recordSource;
	
	private JTable userRecordTable;
	private UserRecordSelectionTableModel userRecordTableModel = new UserRecordSelectionTableModel();
	
	private Timer fetchDelayTimer;

	private JLabel lblUserRecords;
	private JLabel lblSelectUserEmails;
	private JScrollPane scrollPane_1;
	private JLabel lblLocation;
	private JTextField txtLocationCriteria;
	private JButton btnClearCriteria;
	private JProgressBar busyProgressBar;
	private JLabel lblDepartment;
	private JTextField txtDepartmentCriteria;
	private JLabel lblEmployeeId;
	private JTextField txtEmployeeIdCriteria;
	private JTextField txtNameCriteria;
	private JPanel panel;
	private JLabel lblName_1;
	private JLabel lblTitle;
	private JTextField txtTitleCriteria;
	
	private List<UserSelectionListener> userSelectionListeners = new ArrayList<UserSelectionListener>();
	private SQLUserRecord selectedUserRecord;
	
	private JTable addressListingTable;
	private SimpleAddressTableModel addressesTableModel = new SimpleAddressTableModel();
	private JButton btnEditAddresses;
	private JPanel userRecordSection;
	private JPanel detailsSection;
	private JScrollPane scrollPane_2;
	private JTable phoneNumbersTable;
	private JLabel lblPhoneNumbers;
	private PhoneNumbersTableModel phoneNumbersTableModel = new PhoneNumbersTableModel();
	private JLabel lblSids;
	private JScrollPane scrollPane_3;
	private JTable sidTable;
	private SIDTableModel sidTableModel = new SIDTableModel();

	public UserRecordBrowser() {
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{450};
		gridBagLayout.rowHeights = new int[]{0, 0, 0, 0};
		gridBagLayout.columnWeights = new double[]{1.0};
		gridBagLayout.rowWeights = new double[]{0.0, 2.0, 1.0, Double.MIN_VALUE};
		setLayout(gridBagLayout);
		
		panel = new JPanel();
		panel.setBorder(new CompoundBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Filter Criteria", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)), new EmptyBorder(5, 5, 5, 5)));
		GridBagConstraints gbc_panel = new GridBagConstraints();
		gbc_panel.insets = new Insets(0, 0, 5, 0);
		gbc_panel.fill = GridBagConstraints.BOTH;
		gbc_panel.gridx = 0;
		gbc_panel.gridy = 0;
		add(panel, gbc_panel);
		GridBagLayout gbl_panel = new GridBagLayout();
		gbl_panel.columnWidths = new int[]{75, 250, 75, 250, 75, 250, 0, 0};
		gbl_panel.rowHeights = new int[]{0, 0, 0};
		gbl_panel.columnWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, Double.MIN_VALUE};
		gbl_panel.rowWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
		panel.setLayout(gbl_panel);
		
		lblName_1 = new JLabel("Name");
		GridBagConstraints gbc_lblName_1 = new GridBagConstraints();
		gbc_lblName_1.insets = new Insets(0, 0, 5, 5);
		gbc_lblName_1.anchor = GridBagConstraints.EAST;
		gbc_lblName_1.gridx = 0;
		gbc_lblName_1.gridy = 0;
		panel.add(lblName_1, gbc_lblName_1);
		
		txtNameCriteria = new JTextField();
		GridBagConstraints gbc_txtNameCriteria = new GridBagConstraints();
		gbc_txtNameCriteria.insets = new Insets(0, 0, 5, 5);
		gbc_txtNameCriteria.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtNameCriteria.gridx = 1;
		gbc_txtNameCriteria.gridy = 0;
		panel.add(txtNameCriteria, gbc_txtNameCriteria);
		txtNameCriteria.setColumns(10);
		
		lblLocation = new JLabel("Location");
		GridBagConstraints gbc_lblLocation = new GridBagConstraints();
		gbc_lblLocation.anchor = GridBagConstraints.EAST;
		gbc_lblLocation.insets = new Insets(0, 0, 5, 5);
		gbc_lblLocation.gridx = 2;
		gbc_lblLocation.gridy = 0;
		panel.add(lblLocation, gbc_lblLocation);
		
		txtLocationCriteria = new JTextField();
		GridBagConstraints gbc_txtLocationCriteria = new GridBagConstraints();
		gbc_txtLocationCriteria.insets = new Insets(0, 0, 5, 5);
		gbc_txtLocationCriteria.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtLocationCriteria.gridx = 3;
		gbc_txtLocationCriteria.gridy = 0;
		panel.add(txtLocationCriteria, gbc_txtLocationCriteria);
		txtLocationCriteria.setColumns(10);
		
		lblTitle = new JLabel("Title");
		GridBagConstraints gbc_lblTitle = new GridBagConstraints();
		gbc_lblTitle.anchor = GridBagConstraints.EAST;
		gbc_lblTitle.insets = new Insets(0, 0, 5, 5);
		gbc_lblTitle.gridx = 4;
		gbc_lblTitle.gridy = 0;
		panel.add(lblTitle, gbc_lblTitle);
		
		txtTitleCriteria = new JTextField();
		GridBagConstraints gbc_txtTitleCriteria = new GridBagConstraints();
		gbc_txtTitleCriteria.insets = new Insets(0, 0, 5, 5);
		gbc_txtTitleCriteria.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtTitleCriteria.gridx = 5;
		gbc_txtTitleCriteria.gridy = 0;
		panel.add(txtTitleCriteria, gbc_txtTitleCriteria);
		txtTitleCriteria.setColumns(10);
		
		lblEmployeeId = new JLabel("Employee ID");
		GridBagConstraints gbc_lblEmployeeId = new GridBagConstraints();
		gbc_lblEmployeeId.anchor = GridBagConstraints.EAST;
		gbc_lblEmployeeId.insets = new Insets(0, 0, 0, 5);
		gbc_lblEmployeeId.gridx = 0;
		gbc_lblEmployeeId.gridy = 1;
		panel.add(lblEmployeeId, gbc_lblEmployeeId);
		
		txtEmployeeIdCriteria = new JTextField();
		GridBagConstraints gbc_txtEmployeeIdCriteria = new GridBagConstraints();
		gbc_txtEmployeeIdCriteria.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtEmployeeIdCriteria.insets = new Insets(0, 0, 0, 5);
		gbc_txtEmployeeIdCriteria.gridx = 1;
		gbc_txtEmployeeIdCriteria.gridy = 1;
		panel.add(txtEmployeeIdCriteria, gbc_txtEmployeeIdCriteria);
		txtEmployeeIdCriteria.setColumns(10);
		
		lblDepartment = new JLabel("Department");
		GridBagConstraints gbc_lblDepartment = new GridBagConstraints();
		gbc_lblDepartment.anchor = GridBagConstraints.EAST;
		gbc_lblDepartment.insets = new Insets(0, 0, 0, 5);
		gbc_lblDepartment.gridx = 2;
		gbc_lblDepartment.gridy = 1;
		panel.add(lblDepartment, gbc_lblDepartment);
		
		txtDepartmentCriteria = new JTextField();
		GridBagConstraints gbc_txtDepartmentCriteria = new GridBagConstraints();
		gbc_txtDepartmentCriteria.insets = new Insets(0, 0, 0, 5);
		gbc_txtDepartmentCriteria.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtDepartmentCriteria.gridx = 3;
		gbc_txtDepartmentCriteria.gridy = 1;
		panel.add(txtDepartmentCriteria, gbc_txtDepartmentCriteria);
		txtDepartmentCriteria.setColumns(10);
		
		btnClearCriteria = new JButton("Clear All Criteria");
		GridBagConstraints gbc_btnClearCriteria = new GridBagConstraints();
		gbc_btnClearCriteria.insets = new Insets(0, 0, 0, 5);
		gbc_btnClearCriteria.anchor = GridBagConstraints.WEST;
		gbc_btnClearCriteria.gridx = 5;
		gbc_btnClearCriteria.gridy = 1;
		panel.add(btnClearCriteria, gbc_btnClearCriteria);
		btnClearCriteria.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				clearCriteria();
			}
		});
		btnClearCriteria.setIcon(new ImageIcon(UserRecordBrowser.class.getResource("/com/nuix/evmanager/controls/cancel.png")));
		
		userRecordSection = new JPanel();
		userRecordSection.setBorder(new CompoundBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "User Records", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)), new EmptyBorder(5, 5, 5, 5)));
		GridBagConstraints gbc_userRecordSection = new GridBagConstraints();
		gbc_userRecordSection.insets = new Insets(0, 0, 5, 0);
		gbc_userRecordSection.fill = GridBagConstraints.BOTH;
		gbc_userRecordSection.gridx = 0;
		gbc_userRecordSection.gridy = 1;
		add(userRecordSection, gbc_userRecordSection);
		GridBagLayout gbl_userRecordSection = new GridBagLayout();
		gbl_userRecordSection.columnWidths = new int[]{0, 0, 0};
		gbl_userRecordSection.rowHeights = new int[]{0, 0, 0};
		gbl_userRecordSection.columnWeights = new double[]{1.0, 0.0, Double.MIN_VALUE};
		gbl_userRecordSection.rowWeights = new double[]{1.0, 0.0, Double.MIN_VALUE};
		userRecordSection.setLayout(gbl_userRecordSection);
		
		JScrollPane scrollPane = new JScrollPane();
		GridBagConstraints gbc_scrollPane = new GridBagConstraints();
		gbc_scrollPane.gridwidth = 2;
		gbc_scrollPane.insets = new Insets(0, 0, 5, 0);
		gbc_scrollPane.fill = GridBagConstraints.BOTH;
		gbc_scrollPane.gridx = 0;
		gbc_scrollPane.gridy = 0;
		userRecordSection.add(scrollPane, gbc_scrollPane);
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		userRecordTable = new JTable(userRecordTableModel);
		userRecordTable.setFillsViewportHeight(true);
		scrollPane.setViewportView(userRecordTable);
		
		busyProgressBar = new JProgressBar();
		GridBagConstraints gbc_busyProgressBar = new GridBagConstraints();
		gbc_busyProgressBar.fill = GridBagConstraints.HORIZONTAL;
		gbc_busyProgressBar.insets = new Insets(0, 0, 0, 5);
		gbc_busyProgressBar.gridx = 0;
		gbc_busyProgressBar.gridy = 1;
		userRecordSection.add(busyProgressBar, gbc_busyProgressBar);
		busyProgressBar.setIndeterminate(true);
		
		lblUserRecords = new JLabel("0 Records");
		GridBagConstraints gbc_lblUserRecords = new GridBagConstraints();
		gbc_lblUserRecords.gridx = 1;
		gbc_lblUserRecords.gridy = 1;
		userRecordSection.add(lblUserRecords, gbc_lblUserRecords);
		
		detailsSection = new JPanel();
		detailsSection.setBorder(new CompoundBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Additional User Information", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)), new EmptyBorder(5, 5, 5, 5)));
		GridBagConstraints gbc_detailsSection = new GridBagConstraints();
		gbc_detailsSection.fill = GridBagConstraints.BOTH;
		gbc_detailsSection.gridx = 0;
		gbc_detailsSection.gridy = 2;
		add(detailsSection, gbc_detailsSection);
		GridBagLayout gbl_detailsSection = new GridBagLayout();
		gbl_detailsSection.columnWidths = new int[]{0, 0, 20, 150, 20, 300, 0};
		gbl_detailsSection.rowHeights = new int[]{0, 0, 0};
		gbl_detailsSection.columnWeights = new double[]{1.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		gbl_detailsSection.rowWeights = new double[]{0.0, 1.0, Double.MIN_VALUE};
		detailsSection.setLayout(gbl_detailsSection);
		
		lblSelectUserEmails = new JLabel("Email Addresses");
		GridBagConstraints gbc_lblSelectUserEmails = new GridBagConstraints();
		gbc_lblSelectUserEmails.anchor = GridBagConstraints.WEST;
		gbc_lblSelectUserEmails.insets = new Insets(0, 0, 5, 5);
		gbc_lblSelectUserEmails.gridx = 0;
		gbc_lblSelectUserEmails.gridy = 0;
		detailsSection.add(lblSelectUserEmails, gbc_lblSelectUserEmails);
		lblSelectUserEmails.setFont(new Font("Tahoma", Font.BOLD, 11));
		
		btnEditAddresses = new JButton("Edit Addresses");
		GridBagConstraints gbc_btnEditAddresses = new GridBagConstraints();
		gbc_btnEditAddresses.insets = new Insets(0, 0, 5, 5);
		gbc_btnEditAddresses.gridx = 1;
		gbc_btnEditAddresses.gridy = 0;
		detailsSection.add(btnEditAddresses, gbc_btnEditAddresses);
		btnEditAddresses.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					beginEditingSelectedUserRecord();
				} catch (Exception e) {
					logger.error("Error editing selected user record",e);
				}
			}
		});
		btnEditAddresses.setEnabled(false);
		btnEditAddresses.setIcon(new ImageIcon(UserRecordBrowser.class.getResource("/com/nuix/evmanager/controls/pencil.png")));
		
		lblPhoneNumbers = new JLabel("Phone Numbers");
		lblPhoneNumbers.setFont(new Font("Tahoma", Font.BOLD, 11));
		GridBagConstraints gbc_lblPhoneNumbers = new GridBagConstraints();
		gbc_lblPhoneNumbers.anchor = GridBagConstraints.WEST;
		gbc_lblPhoneNumbers.insets = new Insets(0, 0, 5, 5);
		gbc_lblPhoneNumbers.gridx = 3;
		gbc_lblPhoneNumbers.gridy = 0;
		detailsSection.add(lblPhoneNumbers, gbc_lblPhoneNumbers);
		
		lblSids = new JLabel("SID");
		lblSids.setFont(new Font("Tahoma", Font.BOLD, 11));
		GridBagConstraints gbc_lblSids = new GridBagConstraints();
		gbc_lblSids.anchor = GridBagConstraints.WEST;
		gbc_lblSids.insets = new Insets(0, 0, 5, 0);
		gbc_lblSids.gridx = 5;
		gbc_lblSids.gridy = 0;
		detailsSection.add(lblSids, gbc_lblSids);
		
		scrollPane_1 = new JScrollPane();
		GridBagConstraints gbc_scrollPane_1 = new GridBagConstraints();
		gbc_scrollPane_1.insets = new Insets(0, 0, 0, 5);
		gbc_scrollPane_1.gridwidth = 2;
		gbc_scrollPane_1.fill = GridBagConstraints.BOTH;
		gbc_scrollPane_1.gridx = 0;
		gbc_scrollPane_1.gridy = 1;
		detailsSection.add(scrollPane_1, gbc_scrollPane_1);
		scrollPane_1.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		
		addressListingTable = new JTable(addressesTableModel);
		addressListingTable.setFillsViewportHeight(true);
		scrollPane_1.setViewportView(addressListingTable);
		
		scrollPane_2 = new JScrollPane();
		scrollPane_2.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		GridBagConstraints gbc_scrollPane_2 = new GridBagConstraints();
		gbc_scrollPane_2.insets = new Insets(0, 0, 0, 5);
		gbc_scrollPane_2.fill = GridBagConstraints.BOTH;
		gbc_scrollPane_2.gridx = 3;
		gbc_scrollPane_2.gridy = 1;
		detailsSection.add(scrollPane_2, gbc_scrollPane_2);
		
		phoneNumbersTable = new JTable(phoneNumbersTableModel);
		phoneNumbersTable.setFillsViewportHeight(true);
		scrollPane_2.setViewportView(phoneNumbersTable);
		
		scrollPane_3 = new JScrollPane();
		scrollPane_3.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		GridBagConstraints gbc_scrollPane_3 = new GridBagConstraints();
		gbc_scrollPane_3.fill = GridBagConstraints.BOTH;
		gbc_scrollPane_3.gridx = 5;
		gbc_scrollPane_3.gridy = 1;
		detailsSection.add(scrollPane_3, gbc_scrollPane_3);
		
		sidTable = new JTable(sidTableModel);
		sidTable.setFillsViewportHeight(true);
		scrollPane_3.setViewportView(sidTable);
		
		setupCriteriaListeners();
		setupSelectionListener();
	}
	
	private void signalFetchRequest(){
		fetchDelayTimer.stop();
		fetchDelayTimer.start();
	}
	
	private void setupCriteriaListeners(){
		fetchDelayTimer = new Timer(750,new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				fetchResults();
			}
		});
		fetchDelayTimer.setRepeats(false);
		
		DocumentListener textCriteriaListener = new DocumentListener(){
			@Override
			public void changedUpdate(DocumentEvent arg0) { signalFetchRequest(); }
			@Override
			public void insertUpdate(DocumentEvent arg0) { signalFetchRequest(); }
			@Override
			public void removeUpdate(DocumentEvent arg0) { signalFetchRequest(); }
		};
		txtLocationCriteria.getDocument().addDocumentListener(textCriteriaListener);
		txtDepartmentCriteria.getDocument().addDocumentListener(textCriteriaListener);
		txtEmployeeIdCriteria.getDocument().addDocumentListener(textCriteriaListener);
		txtNameCriteria.getDocument().addDocumentListener(textCriteriaListener);
		txtTitleCriteria.getDocument().addDocumentListener(textCriteriaListener);
	}
	
	private void clearCriteria(){
		txtNameCriteria.setText("");
		txtLocationCriteria.setText("");
		txtDepartmentCriteria.setText("");
		txtEmployeeIdCriteria.setText("");
		txtTitleCriteria.setText("");
	}
	
	private void setupSelectionListener(){
		userRecordTable.getSelectionModel().addListSelectionListener(new ListSelectionListener(){
			@Override
			public void valueChanged(ListSelectionEvent event) {
				if(!event.getValueIsAdjusting()){
					int selectedRow = userRecordTable.getSelectedRow();
					if(selectedRow == -1) return;
					try {
						selectedUserRecord = userRecordTableModel.getResultSet().get(selectedRow);
						fireUserSelectionChanged(selectedUserRecord);
					} catch (Exception e) {
						logger.error("Error in user record table selection changed handler",e);
					}
				}
			}
		});
	}
	
	private void fetchResults(){
		if(recordSource != null){
			busyProgressBar.setVisible(true);
			try {
				new Thread(()->{
					UserRecordCriteria criteria = new UserRecordCriteria();
					criteria.setNameCriteria(txtNameCriteria.getText());
					criteria.setLocationCriteria(txtLocationCriteria.getText());
					criteria.setDepartmentCriteria(txtDepartmentCriteria.getText());
					criteria.setEmployeeIdCriteria(txtEmployeeIdCriteria.getText());
					criteria.setTitleCriteria(txtTitleCriteria.getText());
					try {
						VirtualizedRecordCollection<SQLUserRecord> result = recordSource.findRecords(criteria);
						String countString = NumberFormat.getNumberInstance(Locale.US).format(result.size())+" Records";
						SwingUtilities.invokeLater(()->{
							lblUserRecords.setText(countString);			
							setUserRecordResult(result);
							addressesTableModel.clear();
							phoneNumbersTableModel.clear();
							sidTableModel.clear();
							btnEditAddresses.setEnabled(false);
							lblSelectUserEmails.setText("Email Addresses for:");
							TableHelpers.resizeColumnWidth(userRecordTable);
							TableHelpers.scrollToTop(userRecordTable);
						});
					} catch (Exception e) {
						logger.error("Error in fetchResults method",e);
					}
					
					SwingUtilities.invokeLater(()->{
						busyProgressBar.setVisible(false);
					});
				}).start();
			} catch (Exception e1) {
				logger.error("Error in fetchResults method thread",e1);
			}
		}
	}
	
	public void setUserRecordResult(VirtualizedRecordCollection<SQLUserRecord> result){
		try {
			userRecordTableModel.setResultSet(result);
		} catch (Exception e) {
			logger.error("Error while retrieving user record result set size");
			logger.error(e);
		}
	}

	public UserRecordStore getRecordSource() {
		return recordSource;
	}

	public void setRecordSource(UserRecordStore recordSource) {
		this.recordSource = recordSource;
		fetchResults();
	}
	
	private void fireUserSelectionChanged(SQLUserRecord userRecord){
		for(UserSelectionListener listener : userSelectionListeners){
			listener.userSelected(userRecord);
		}
		try {
			addressesTableModel.setAddresses(userRecord.getAddresses());
			phoneNumbersTableModel.setPhoneNumbers(userRecord.getPhoneNumbers());
			sidTableModel.setRecords(userRecord.getSIDs());
			btnEditAddresses.setEnabled(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void addUserSelectionListener(UserSelectionListener listener){
		userSelectionListeners.add(listener);
	}
	
	public void removeUserSelectionListener(UserSelectionListener listener){
		userSelectionListeners.remove(listener);
	}
	
	private void beginEditingSelectedUserRecord() throws Exception {
		if(selectedUserRecord != null){
			UserRecordEditorDialog editorDialog = new UserRecordEditorDialog();
			editorDialog.setUserRecord(selectedUserRecord);
			editorDialog.setVisible(true);
			
			if(editorDialog.isDirty() && editorDialog.getDialogResult() == true){
				AddressChangeOrder changeOrder = editorDialog.getChangeOrder();
				recordSource.processAddressChangeOrder(changeOrder);
				addressesTableModel.setAddresses(selectedUserRecord.getAddresses(true));
			}
		}
	}
	
	public List<SQLUserRecord> getCheckedUsers() throws Exception{
		return userRecordTableModel.getCheckedUsers();
	}
	
	public void setCheckedUsers(Collection<SQLUserRecord> records){
		userRecordTableModel.setCheckedUsers(records);
	}
}
