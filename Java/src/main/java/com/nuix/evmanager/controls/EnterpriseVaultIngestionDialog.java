package com.nuix.evmanager.controls;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.border.EmptyBorder;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;

import com.nuix.evmanager.data.EnterpriseVaultIngestionSettings;
import com.nuix.evmanager.data.SQLUserRecord;
import com.nuix.evmanager.data.UserRecordStore;
import com.nuix.evmanager.data.VaultArchive;
import com.nuix.evmanager.data.VaultServer;
import com.nuix.evmanager.data.VaultStore;

/***
 * The main dialog of this script containing a series of tabs which contain the majority of the settings
 * controls for this script.
 * @author Jason Wells
 *
 */
@SuppressWarnings("serial")
public class EnterpriseVaultIngestionDialog extends JDialog {

	private static Logger logger = Logger.getLogger(EnterpriseVaultIngestionDialog.class);
	
	private final JPanel contentPanel = new JPanel();
	private EnterpriseVaultFilterCriteriaControl enterpriseVaultIngestionControl;
	private boolean dialogResult = false;
	private EnterpriseVaultIngestionSettings evIngestionSettings;
	private DataProcessingSettingsControl dataProcessingSettingsControl;
	private EvidenceSettingsControl evidenceSettingsControl;
	private LocalWorkerSettings localWorkerSettings;
	private List<SQLUserRecord> selectedUserRecords = new ArrayList<SQLUserRecord>();

	private VaultEntryBrowser vaultEntryBrowser;

	public EnterpriseVaultIngestionDialog() {
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setIconImage(Toolkit.getDefaultToolkit().getImage(EnterpriseVaultIngestionDialog.class.getResource("/com/nuix/evmanager/controls/nuix_icon.png")));
		setTitle("Add Enterprise Vault Data");
		setSize(1024,850);
		setLocationRelativeTo(null);
		setModal(true);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BorderLayout(0, 0));
		{
			JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
			contentPanel.add(tabbedPane);
			{
				JPanel evidenceSettingsTab = new JPanel();
				tabbedPane.addTab("Evidence Settings", null, evidenceSettingsTab, null);
				evidenceSettingsTab.setLayout(new BorderLayout(0, 0));
				{
					evidenceSettingsControl = new EvidenceSettingsControl();
					evidenceSettingsTab.add(evidenceSettingsControl, BorderLayout.CENTER);
				}
			}
			{
				JPanel storesAndArchivesTab = new JPanel();
				tabbedPane.addTab("Vault Stores & Archives", null, storesAndArchivesTab, null);
				GridBagLayout gbl_storesAndArchivesTab = new GridBagLayout();
				gbl_storesAndArchivesTab.columnWidths = new int[]{0, 0};
				gbl_storesAndArchivesTab.rowHeights = new int[]{0, 0};
				gbl_storesAndArchivesTab.columnWeights = new double[]{1.0, Double.MIN_VALUE};
				gbl_storesAndArchivesTab.rowWeights = new double[]{1.0, Double.MIN_VALUE};
				storesAndArchivesTab.setLayout(gbl_storesAndArchivesTab);
				{
					vaultEntryBrowser = new VaultEntryBrowser();
					GridBagConstraints gbc_vaultEntryBrowser = new GridBagConstraints();
					gbc_vaultEntryBrowser.fill = GridBagConstraints.BOTH;
					gbc_vaultEntryBrowser.gridx = 0;
					gbc_vaultEntryBrowser.gridy = 0;
					storesAndArchivesTab.add(vaultEntryBrowser, gbc_vaultEntryBrowser);
				}
			}
			{
				JPanel evSettingsTab = new JPanel();
				tabbedPane.addTab("Vault Filter Criteria", null, evSettingsTab, null);
				GridBagLayout gbl_evSettingsTab = new GridBagLayout();
				gbl_evSettingsTab.columnWidths = new int[]{0, 0};
				gbl_evSettingsTab.rowHeights = new int[]{0, 0};
				gbl_evSettingsTab.columnWeights = new double[]{1.0, Double.MIN_VALUE};
				gbl_evSettingsTab.rowWeights = new double[]{1.0, Double.MIN_VALUE};
				evSettingsTab.setLayout(gbl_evSettingsTab);
				{
					enterpriseVaultIngestionControl = new EnterpriseVaultFilterCriteriaControl();
					GridBagConstraints gbc_enterpriseVaultIngestionControl = new GridBagConstraints();
					gbc_enterpriseVaultIngestionControl.fill = GridBagConstraints.BOTH;
					gbc_enterpriseVaultIngestionControl.gridx = 0;
					gbc_enterpriseVaultIngestionControl.gridy = 0;
					evSettingsTab.add(enterpriseVaultIngestionControl, gbc_enterpriseVaultIngestionControl);
					enterpriseVaultIngestionControl.addUserRecordsChangedListener(new UserRecordChangedListener() {
						
						@Override
						public void userRecordsChanged(List<SQLUserRecord> updatedValue) {
							evidenceSettingsControl.setSelectedUserRecords(updatedValue);
						}
					});
				}
			}
			{
				JPanel processingSettingsTab = new JPanel();
				tabbedPane.addTab("Data Processing Settings", null, processingSettingsTab, null);
				processingSettingsTab.setLayout(new BorderLayout(0, 0));
				{
					dataProcessingSettingsControl = new DataProcessingSettingsControl();
					processingSettingsTab.add(dataProcessingSettingsControl, BorderLayout.CENTER);
				}
			}
			{
				JPanel workerSettingsTab = new JPanel();
				tabbedPane.addTab("Worker Settings", null, workerSettingsTab, null);
				workerSettingsTab.setLayout(new BorderLayout(0, 0));
				{
					localWorkerSettings = new LocalWorkerSettings();
					workerSettingsTab.add(localWorkerSettings, BorderLayout.CENTER);
				}
			}
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						if(settingsAreValid()){
							buildEvIngestionSettings();
							dialogResult = true;
							dispose();
						}
					}
				});
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
			}
			{
				JButton cancelButton = new JButton("Cancel");
				cancelButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						dialogResult = false;
						dispose();
					}
				});
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
				
			}
		}
		
		logger.info("Preparing dialog...");
	}
	
	private boolean settingsAreValid(){
		if(!evidenceSettingsControl.settingsAreValid()){
			return false;
		}
		
		DateTime fromDate = enterpriseVaultIngestionControl.getFromDate();
		DateTime toDate = enterpriseVaultIngestionControl.getToDate();
		
		if (fromDate == null && toDate != null){
			CommonDialogs.showError("When providing a date value for 'To', you must also provide a value for 'From'.");
			return false;
		}
		
		if (fromDate != null && toDate == null){
			CommonDialogs.showError("When providing a date value for 'From', you must also provide a value for 'To'.");
			return false;
		}
		
		if(fromDate != null && toDate != null){
			if(fromDate.isAfter(toDate)){
				CommonDialogs.showError("'From' date cannot be after 'To' date.");
				return false;
			}
		}
		
		// If any archives are selected each store must have at least one associated archive selected
		// if no archives are selected then allow just stores to be selected
		List<VaultStore> storesWithoutArchives = getSelectedStoresWithoutSelectedArchives();
		List<VaultArchive> selectedArchives = getSelectedArchives();
		
		if(selectedArchives.size() > 0 && storesWithoutArchives.size() > 0){
			StringJoiner message = new StringJoiner("\n");
			message.add("If any vault store has an archive selected, all selected vault stores must have an archive selected.");
			message.add("The following vault stores have no associated archives selected:");
			for(VaultStore store : storesWithoutArchives){
				message.add("    "+store.getName());
			}
			CommonDialogs.showError(message.toString());
			return false;
		}
		
		return true;
	}
	
	private void buildEvIngestionSettings(){
		logger.info("Building ingestion settings from user selections...");
		
		evIngestionSettings = new EnterpriseVaultIngestionSettings();
		
		//Evidence settings
		evIngestionSettings.setEvidenceSettings(evidenceSettingsControl.getEvidenceSettings());
		
		// Set store
		List<VaultStore> selectedStores = getSelectedStores();
		evIngestionSettings.setVaultStores(selectedStores);
		
		// Set archives
		List<VaultArchive> selectedArchives = getSelectedArchives();
		evIngestionSettings.setVaultArchives(selectedArchives);
		
		// Set evidence container custodian name
		String custodianName = evidenceSettingsControl.getEvidenceCustodianName();
		if(custodianName != null && custodianName.trim().length() > 0){
			evIngestionSettings.getEvidenceSettings().setInitialCustodian(custodianName);
		}

		// Set custodian record from address book which will be used to filter EV ingestion
		List<SQLUserRecord> custodianRecords = enterpriseVaultIngestionControl.getSelectedUserRecords();
		evIngestionSettings.setCustodianRecords(custodianRecords);
		
		// Set date range
		evIngestionSettings.setFromDate(enterpriseVaultIngestionControl.getFromDate());
		evIngestionSettings.setToDate(enterpriseVaultIngestionControl.getToDate());
		
		evIngestionSettings.setKeywords(enterpriseVaultIngestionControl.getKeywords());
		evIngestionSettings.setFlag(enterpriseVaultIngestionControl.getKeywordsFlag());
		
		//Processing settings
		evIngestionSettings.setProcessingSettings(dataProcessingSettingsControl.getSettings());
		
		//Parallel processing settings
		evIngestionSettings.setParallelProcessingSettings(localWorkerSettings.getSettingsMap());
		
		evIngestionSettings.setMimeTypeSettings(dataProcessingSettingsControl.getAllMimeTypeSettings());
	}

	public void setUserRecordSource(UserRecordStore recordSource) {
		enterpriseVaultIngestionControl.setUserRecordSource(recordSource);
	}
	
	public boolean getDialogResult() {
		return dialogResult;
	}

	public EnterpriseVaultIngestionSettings getEnterpriseVaultIngestionSettings() {
		return evIngestionSettings;
	}
	
	public void setDefaultProcessingSettingsFromJSONFile(String filePath) throws Exception {
		dataProcessingSettingsControl.setDefaultSettingsFromJSONFile(filePath);
	}
	
	public void setDefaultWorkerSettingsFromJSONFile(String filePath) throws Exception {
		localWorkerSettings.setDefaultSettingsFromJSONFile(filePath);
	}
	
	public void setVaultServers(List<VaultServer> servers){
		logger.info("Setting vault servers...");
		vaultEntryBrowser.setVaultServers(servers);
	}
	
	public VaultServer getSelectedVaultServer(){
		return vaultEntryBrowser.getSelectedVaultServer();
	}
	
	public List<VaultStore> getSelectedStores() {
		return vaultEntryBrowser.getSelectedStores();
	}

	public List<VaultArchive> getSelectedArchives() {
		return vaultEntryBrowser.getSelectedArchives();
	}
	
	public List<VaultStore> getSelectedStoresWithoutSelectedArchives() {
		return vaultEntryBrowser.getSelectedStoresWithoutSelectedArchives();
	}
	
	public List<SQLUserRecord> getSelectedUserRecords(){
		return selectedUserRecords;
	}

	public void setMimeTypeSettings(String mimeType, Map<String, Boolean> settings) {
		dataProcessingSettingsControl.setMimeTypeSettings(mimeType, settings);
	}
}
