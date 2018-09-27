package com.nuix.evmanager.controls;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JToolBar;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.TableColumnModel;

import org.apache.log4j.Logger;
import org.jdesktop.swingx.JXButton;
import org.jdesktop.swingx.JXDatePicker;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import com.nuix.evmanager.data.IngestionHistoryEntry;
import com.nuix.evmanager.data.SQLUserRecord;
import com.nuix.evmanager.data.SQLUserRecordStore;
import com.nuix.evmanager.data.UserRecordStore;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;

/***
 * A control allowing a user to specify criteria for filtering
 * @author Jason Wells
 *
 */
@SuppressWarnings("serial")
public class EnterpriseVaultFilterCriteriaControl extends JPanel {
	private static Logger logger = Logger.getLogger(EnterpriseVaultFilterCriteriaControl.class);
	UserRecordStore userRecordStore = null;
	private JTextArea txtrKeywords;
	private JComboBox<String> comboKeywordsFlag;
	private JButton btnClearCustodians;
	private JXDatePicker fromDatePicker;
	private JXDatePicker toDatePicker;
	private JXButton btnSetRangeFromCustodian;
	private String datePickerFormat = "yyyy/MM/dd";
	private List<SQLUserRecord> selectedUserRecords = new ArrayList<SQLUserRecord>();
	private IngestionHistoryTableModel selectedUserHistoryTableModel = new IngestionHistoryTableModel();
	private SimpleUserRecordTableModel selectedUserRecordTableModel = new SimpleUserRecordTableModel();
	private List<UserRecordChangedListener> userRecordChangedListeners = new ArrayList<UserRecordChangedListener>();
	private JTable selectedUsersHistoryTable;
	private JTable selectedUsersTable;
	
	public void addUserRecordsChangedListener(UserRecordChangedListener listener){
		userRecordChangedListeners.add(listener);
	}
	public void removeUserRecordsChangedListener(UserRecordChangedListener listener){
		userRecordChangedListeners.remove(listener);
	}
	protected void fireUserRecordsChanged(List<SQLUserRecord> updatedValue){
		for (int i = 0; i < userRecordChangedListeners.size(); i++) {
			userRecordChangedListeners.get(i).userRecordsChanged(updatedValue);
		}
	}

	public EnterpriseVaultFilterCriteriaControl() {
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{0, 0};
		gridBagLayout.rowHeights = new int[]{0, 0, 125, 0};
		gridBagLayout.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{1.0, 0.0, 0.0, Double.MIN_VALUE};
		setLayout(gridBagLayout);
		
		JPanel panel = new JPanel();
		panel.setBorder(new CompoundBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Address Book Users & History", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)), new EmptyBorder(5, 5, 5, 5)));
		GridBagConstraints gbc_panel = new GridBagConstraints();
		gbc_panel.insets = new Insets(0, 0, 5, 0);
		gbc_panel.fill = GridBagConstraints.BOTH;
		gbc_panel.gridx = 0;
		gbc_panel.gridy = 0;
		add(panel, gbc_panel);
		GridBagLayout gbl_panel = new GridBagLayout();
		gbl_panel.columnWidths = new int[]{0, 0, 0};
		gbl_panel.rowHeights = new int[]{0, 100, 0, 100, 0};
		gbl_panel.columnWeights = new double[]{1.0, 1.0, Double.MIN_VALUE};
		gbl_panel.rowWeights = new double[]{0.0, 1.0, 0.0, 1.0, Double.MIN_VALUE};
		panel.setLayout(gbl_panel);
		
		JToolBar toolBar = new JToolBar();
		GridBagConstraints gbc_toolBar = new GridBagConstraints();
		gbc_toolBar.anchor = GridBagConstraints.WEST;
		gbc_toolBar.insets = new Insets(0, 0, 5, 5);
		gbc_toolBar.gridx = 0;
		gbc_toolBar.gridy = 0;
		panel.add(toolBar, gbc_toolBar);
		toolBar.setFloatable(false);
		
		JButton btnFindCustodian = new JButton("Find Users");
		toolBar.add(btnFindCustodian);
		btnFindCustodian.setToolTipText("Find users in address book");
		btnFindCustodian.setIcon(new ImageIcon(EnterpriseVaultFilterCriteriaControl.class.getResource("/com/nuix/evmanager/controls/magnifier.png")));
		btnFindCustodian.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				findCustodianInAddressBook();
			}
		});
		
		btnClearCustodians = new JButton("Clear Selected Users");
		btnClearCustodians.setEnabled(false);
		btnClearCustodians.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				clearSelectedCustodian();
			}
		});
		toolBar.add(btnClearCustodians);
		btnClearCustodians.setToolTipText("Clear selected users");
		btnClearCustodians.setIcon(new ImageIcon(EnterpriseVaultFilterCriteriaControl.class.getResource("/com/nuix/evmanager/controls/cancel.png")));
		
		JButton btnAddUser = new JButton("Add New User to Database");
		btnAddUser.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				addNewUser();
			}
		});
		
		JSeparator separator = new JSeparator();
		separator.setOrientation(SwingConstants.VERTICAL);
		toolBar.add(separator);
		btnAddUser.setIcon(new ImageIcon(EnterpriseVaultFilterCriteriaControl.class.getResource("/com/nuix/evmanager/controls/add.png")));
		toolBar.add(btnAddUser);
		
		JScrollPane scrollPane_2 = new JScrollPane();
		scrollPane_2.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		GridBagConstraints gbc_scrollPane_2 = new GridBagConstraints();
		gbc_scrollPane_2.gridwidth = 2;
		gbc_scrollPane_2.insets = new Insets(0, 0, 5, 5);
		gbc_scrollPane_2.fill = GridBagConstraints.BOTH;
		gbc_scrollPane_2.gridx = 0;
		gbc_scrollPane_2.gridy = 1;
		panel.add(scrollPane_2, gbc_scrollPane_2);
		
		selectedUsersTable = new JTable(selectedUserRecordTableModel);
		selectedUsersTable.setFillsViewportHeight(true);
		scrollPane_2.setViewportView(selectedUsersTable);
		
		JScrollPane scrollPane_1 = new JScrollPane();
		scrollPane_1.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		GridBagConstraints gbc_scrollPane_1 = new GridBagConstraints();
		gbc_scrollPane_1.gridheight = 2;
		gbc_scrollPane_1.gridwidth = 2;
		gbc_scrollPane_1.fill = GridBagConstraints.BOTH;
		gbc_scrollPane_1.gridx = 0;
		gbc_scrollPane_1.gridy = 2;
		panel.add(scrollPane_1, gbc_scrollPane_1);
		
		selectedUsersHistoryTable = new JTable(selectedUserHistoryTableModel);
		selectedUsersHistoryTable.setFillsViewportHeight(true);
		scrollPane_1.setViewportView(selectedUsersHistoryTable);
		adjustCustodiansTableColumnWidths();
		
		JPanel dateRangeSection = new JPanel();
		dateRangeSection.setBorder(new CompoundBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Date Range", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)), new EmptyBorder(5, 5, 5, 5)));
		GridBagConstraints gbc_dateRangeSection = new GridBagConstraints();
		gbc_dateRangeSection.insets = new Insets(0, 0, 5, 0);
		gbc_dateRangeSection.fill = GridBagConstraints.BOTH;
		gbc_dateRangeSection.gridx = 0;
		gbc_dateRangeSection.gridy = 1;
		add(dateRangeSection, gbc_dateRangeSection);
		GridBagLayout gbl_dateRangeSection = new GridBagLayout();
		gbl_dateRangeSection.columnWidths = new int[]{0, 150, 25, 0, 150, 25, 0, 0};
		gbl_dateRangeSection.rowHeights = new int[]{0, 0};
		gbl_dateRangeSection.columnWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		gbl_dateRangeSection.rowWeights = new double[]{0.0, Double.MIN_VALUE};
		dateRangeSection.setLayout(gbl_dateRangeSection);
		
		JLabel lblFrom = new JLabel("From");
		GridBagConstraints gbc_lblFrom = new GridBagConstraints();
		gbc_lblFrom.insets = new Insets(0, 0, 0, 5);
		gbc_lblFrom.gridx = 0;
		gbc_lblFrom.gridy = 0;
		dateRangeSection.add(lblFrom, gbc_lblFrom);
		
		fromDatePicker = new JXDatePicker();
		GridBagConstraints gbc_fromDatePicker = new GridBagConstraints();
		gbc_fromDatePicker.insets = new Insets(0, 0, 0, 5);
		gbc_fromDatePicker.fill = GridBagConstraints.HORIZONTAL;
		gbc_fromDatePicker.gridx = 1;
		gbc_fromDatePicker.gridy = 0;
		dateRangeSection.add(fromDatePicker, gbc_fromDatePicker);
		fromDatePicker.setFormats(new String[] {datePickerFormat});
		
		JLabel lblTo = new JLabel("To");
		GridBagConstraints gbc_lblTo = new GridBagConstraints();
		gbc_lblTo.insets = new Insets(0, 0, 0, 5);
		gbc_lblTo.gridx = 3;
		gbc_lblTo.gridy = 0;
		dateRangeSection.add(lblTo, gbc_lblTo);
		
		toDatePicker = new JXDatePicker();
		GridBagConstraints gbc_toDatePicker = new GridBagConstraints();
		gbc_toDatePicker.insets = new Insets(0, 0, 0, 5);
		gbc_toDatePicker.fill = GridBagConstraints.HORIZONTAL;
		gbc_toDatePicker.gridx = 4;
		gbc_toDatePicker.gridy = 0;
		dateRangeSection.add(toDatePicker, gbc_toDatePicker);
		toDatePicker.setFormats(new String[] {datePickerFormat});
		
		JToolBar toolBar_1 = new JToolBar();
		GridBagConstraints gbc_toolBar_1 = new GridBagConstraints();
		gbc_toolBar_1.gridx = 6;
		gbc_toolBar_1.gridy = 0;
		dateRangeSection.add(toolBar_1, gbc_toolBar_1);
		toolBar_1.setFloatable(false);
		
		JXButton btnClearDates = new JXButton();
		btnClearDates.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				fromDatePicker.setDate(null);
				toDatePicker.setDate(null);
			}
		});
		
		btnSetRangeFromCustodian = new JXButton();
		btnSetRangeFromCustodian.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(selectedUserRecords.size() > 1){
					String message = "Unable to determine appropriate date range when more than one address book user record is selected";
					String title = "Unable to determine an appropriate date range";
					CommonDialogs.showInformation(message, title);
				} else {
					try {
						SQLUserRecord record = selectedUserRecords.get(0);
						DateTime latestDateRangeMax = record.getLatestDateRangeMax();
						if(latestDateRangeMax != null){
							fromDatePicker.setDate(latestDateRangeMax.toDate());
							if(toDatePicker.getDate() == null){
								toDatePicker.setDate(new DateTime().toDate());
							}
						} else {
							String message = "Unable to determine appropriate date range for selected address book user record";
							String title = "Unable to determine an appropriate date range";
							CommonDialogs.showInformation(message, title);
						}
					} catch (Exception e) {
						logger.error("Error while determining appropriate date range for user record",e);
					}
				}
			}
		});
		btnSetRangeFromCustodian.setEnabled(false);
		toolBar_1.add(btnSetRangeFromCustodian);
		btnSetRangeFromCustodian.setToolTipText("Set range based on custodian last ingestion");
		btnSetRangeFromCustodian.setIcon(new ImageIcon(EnterpriseVaultFilterCriteriaControl.class.getResource("/com/nuix/evmanager/controls/user.png")));
		btnClearDates.setToolTipText("Clear dates");
		btnClearDates.setIcon(new ImageIcon(EnterpriseVaultFilterCriteriaControl.class.getResource("/com/nuix/evmanager/controls/cancel.png")));
		toolBar_1.add(btnClearDates);
		
		JPanel keywordsSection = new JPanel();
		keywordsSection.setBorder(new CompoundBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Keywords", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)), new EmptyBorder(5, 5, 5, 5)));
		GridBagConstraints gbc_keywordsSection = new GridBagConstraints();
		gbc_keywordsSection.fill = GridBagConstraints.BOTH;
		gbc_keywordsSection.gridx = 0;
		gbc_keywordsSection.gridy = 2;
		add(keywordsSection, gbc_keywordsSection);
		GridBagLayout gbl_keywordsSection = new GridBagLayout();
		gbl_keywordsSection.columnWidths = new int[]{0, 150, 0, 0};
		gbl_keywordsSection.rowHeights = new int[]{0, 0, 0};
		gbl_keywordsSection.columnWeights = new double[]{0.0, 0.0, 1.0, Double.MIN_VALUE};
		gbl_keywordsSection.rowWeights = new double[]{0.0, 1.0, Double.MIN_VALUE};
		keywordsSection.setLayout(gbl_keywordsSection);
		
		JLabel lblFlag = new JLabel("Flag");
		GridBagConstraints gbc_lblFlag = new GridBagConstraints();
		gbc_lblFlag.insets = new Insets(0, 0, 5, 5);
		gbc_lblFlag.anchor = GridBagConstraints.EAST;
		gbc_lblFlag.gridx = 0;
		gbc_lblFlag.gridy = 0;
		keywordsSection.add(lblFlag, gbc_lblFlag);
		
		comboKeywordsFlag = new JComboBox<String>();
		GridBagConstraints gbc_comboKeywordsFlag = new GridBagConstraints();
		gbc_comboKeywordsFlag.fill = GridBagConstraints.HORIZONTAL;
		gbc_comboKeywordsFlag.insets = new Insets(0, 0, 5, 5);
		gbc_comboKeywordsFlag.gridx = 1;
		gbc_comboKeywordsFlag.gridy = 0;
		keywordsSection.add(comboKeywordsFlag, gbc_comboKeywordsFlag);
		comboKeywordsFlag.setModel(new DefaultComboBoxModel<String>(new String[] {"ANY", "ALL", "ALLNEAR", "PHRASE", "BEGINS", "BEGINANY", "EXACT", "EXACTANY", "ENDS", "ENDSANY"}));
		
		JScrollPane scrollPane = new JScrollPane();
		GridBagConstraints gbc_scrollPane = new GridBagConstraints();
		gbc_scrollPane.fill = GridBagConstraints.BOTH;
		gbc_scrollPane.gridwidth = 3;
		gbc_scrollPane.gridx = 0;
		gbc_scrollPane.gridy = 1;
		keywordsSection.add(scrollPane, gbc_scrollPane);
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		
		txtrKeywords = new JTextArea();
		scrollPane.setViewportView(txtrKeywords);
	}

	private void adjustCustodiansTableColumnWidths() {
		TableColumnModel cm = selectedUsersHistoryTable.getColumnModel();
		cm.getColumn(0).setMaxWidth(150);
		cm.getColumn(0).setWidth(150);
		cm.getColumn(0).setPreferredWidth(150);
		
		cm.getColumn(1).setMaxWidth(150);
		cm.getColumn(1).setWidth(150);
		cm.getColumn(1).setPreferredWidth(150);
	}
	private void findCustodianInAddressBook(){
		if(!userRecordStore.canConnect()){
			CommonDialogs.showError("Unable to connect to user record database.  Please ensure your settings are correct.");
		} else {
			AddressBookDialog dialog = new AddressBookDialog();
			dialog.setUserRecordSource(userRecordStore);
			dialog.setCheckedUsers(selectedUserRecords);
			dialog.setVisible(true);
			if(dialog.getDialogResult() == true){
				try {
					selectedUserRecords = dialog.getSelectedRecords();
					selectedUserRecordTableModel.setRecords(selectedUserRecords);
					
					List<IngestionHistoryEntry> selectedUserHistory = new ArrayList<IngestionHistoryEntry>();
					for (SQLUserRecord userRecord : selectedUserRecords) {
						selectedUserHistory.addAll(userRecord.getAllIngestionHistoryEntries());
					}
					Collections.sort(selectedUserHistory);
					
					selectedUserHistoryTableModel.setRecords(selectedUserHistory);
					fireUserRecordsChanged(selectedUserRecords);
				} catch (Exception e) {
					logger.error("Error while retrieving selected user records");
					logger.error(e);
				}
				
				// Enable buttons that require a custodian to be selected
				btnClearCustodians.setEnabled(true);
				if(selectedUserRecords.size() > 0){
					btnSetRangeFromCustodian.setEnabled(true);
				}
			}
		}
	}
	
	private void clearSelectedCustodian(){
		if(CommonDialogs.getConfirmation("Remove all selected users?", "Remove All?")){
			selectedUserRecords = new ArrayList<SQLUserRecord>();
			List<IngestionHistoryEntry> selectedUserHistory = new ArrayList<IngestionHistoryEntry>();
			selectedUserHistoryTableModel.setRecords(selectedUserHistory);
			selectedUserRecordTableModel.clear();
			// Disable buttons that require a custodian to be selected
			btnClearCustodians.setEnabled(false);
			btnSetRangeFromCustodian.setEnabled(false);
		}
	}
	
	public void setUserRecordSource(UserRecordStore recordSource) {
		logger.info("Setting record source...");
		userRecordStore = recordSource;
	}

	public List<SQLUserRecord> getSelectedUserRecords() {
		return selectedUserRecords;
	}
	
	public String getKeywords(){
		return txtrKeywords.getText();
	}
	
	public String getKeywordsFlag(){
		return (String)comboKeywordsFlag.getSelectedItem();
	}
	
	public DateTime getFromDate(){
		Date fromDate = fromDatePicker.getDate();
		if(fromDate != null){
			DateTime result = new DateTime(fromDate).withZone(DateTimeZone.UTC);
			result = result.millisOfDay().withMinimumValue();
			return result;
		} else {
			return null;
		}
	}
	
	public DateTime getToDate(){
		Date toDate = toDatePicker.getDate();
		if(toDate != null){
			DateTime result = new DateTime(toDate).withZone(DateTimeZone.UTC);
			result = result.millisOfDay().withMaximumValue();
			return result;
		} else {
			return null;
		}
	}
	
	public void addNewUser(){
		if(userRecordStore.canConnect()){
			AddUserDialog dialog = new AddUserDialog(SwingUtilities.windowForComponent(this),(SQLUserRecordStore) userRecordStore);
			dialog.setVisible(true);	
		} else {
			CommonDialogs.showError("Database is currently unreachable, new users cannot currently be added.","Cannot Reach Database");
		}
		
	}
}
