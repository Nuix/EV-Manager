package com.nuix.evmanager.controls;

import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;

import org.apache.log4j.Logger;
import org.jdesktop.swingx.JXButton;

import com.nuix.evmanager.data.VaultArchive;
import com.nuix.evmanager.data.VaultArchiveCriteria;
import com.nuix.evmanager.data.VaultArchiveResult;
import com.nuix.evmanager.data.VaultServer;
import com.nuix.evmanager.data.VaultStore;

@SuppressWarnings("serial")
public class VaultEntryBrowser extends JPanel {
	private static Logger logger = Logger.getLogger(VaultEntryBrowser.class);
			
	private JTable storeTable;
	
	private JComboBox<VaultServer> evServerComboBox;
	private JTable archiveTable;
	private VaultArchiveTableModel archiveTableModel = new VaultArchiveTableModel();
	private VaultStoreTableModel storeTableModel = new VaultStoreTableModel(archiveTableModel);
	private VaultServer selectedVaultServer;
	private JPanel vaultArchiveSection = null;
	
	List<VaultStore> checkedVaultStores = new ArrayList<VaultStore>();
	
	private JLabel lblStoreRecordCount;
	private JTextField txtNameCriteria;
	
	private Timer fetchDelayTimer;
	private JLabel lblArchiveRecordCount;

	private JCheckBox chckbxSpecificVaultArchives;
	
	public VaultEntryBrowser() {
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{0, 0};
		gridBagLayout.rowHeights = new int[]{0, 0, 0, 0, 0};
		gridBagLayout.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, 1.0, 0.0, 1.0, Double.MIN_VALUE};
		setLayout(gridBagLayout);
		
		JPanel serverSection = new JPanel();
		serverSection.setBorder(new CompoundBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Enterprise Vault Server", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)), new EmptyBorder(5, 5, 5, 5)));
		GridBagConstraints gbc_serverSection = new GridBagConstraints();
		gbc_serverSection.insets = new Insets(0, 0, 5, 0);
		gbc_serverSection.fill = GridBagConstraints.BOTH;
		gbc_serverSection.gridx = 0;
		gbc_serverSection.gridy = 0;
		add(serverSection, gbc_serverSection);
		GridBagLayout gbl_serverSection = new GridBagLayout();
		gbl_serverSection.columnWidths = new int[]{400, 0, 0};
		gbl_serverSection.rowHeights = new int[]{0, 0};
		gbl_serverSection.columnWeights = new double[]{0.0, 1.0, Double.MIN_VALUE};
		gbl_serverSection.rowWeights = new double[]{0.0, Double.MIN_VALUE};
		serverSection.setLayout(gbl_serverSection);
		
		evServerComboBox = new JComboBox<VaultServer>();
		GridBagConstraints gbc_evServerComboBox = new GridBagConstraints();
		gbc_evServerComboBox.insets = new Insets(0, 0, 0, 5);
		gbc_evServerComboBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_evServerComboBox.gridx = 0;
		gbc_evServerComboBox.gridy = 0;
		serverSection.add(evServerComboBox, gbc_evServerComboBox);
		
		JPanel vaultStoreSection = new JPanel();
		vaultStoreSection.setBorder(new CompoundBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Vault Stores", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)), new EmptyBorder(5, 5, 5, 5)));
		GridBagConstraints gbc_vaultStoreSection = new GridBagConstraints();
		gbc_vaultStoreSection.insets = new Insets(0, 0, 5, 0);
		gbc_vaultStoreSection.fill = GridBagConstraints.BOTH;
		gbc_vaultStoreSection.gridx = 0;
		gbc_vaultStoreSection.gridy = 1;
		add(vaultStoreSection, gbc_vaultStoreSection);
		GridBagLayout gbl_vaultStoreSection = new GridBagLayout();
		gbl_vaultStoreSection.columnWidths = new int[]{0, 0, 0};
		gbl_vaultStoreSection.rowHeights = new int[]{0, 0, 0};
		gbl_vaultStoreSection.columnWeights = new double[]{1.0, 0.0, Double.MIN_VALUE};
		gbl_vaultStoreSection.rowWeights = new double[]{1.0, 0.0, Double.MIN_VALUE};
		vaultStoreSection.setLayout(gbl_vaultStoreSection);
		
		JScrollPane storeTableScrollPane = new JScrollPane();
		GridBagConstraints gbc_storeTableScrollPane = new GridBagConstraints();
		gbc_storeTableScrollPane.gridwidth = 2;
		gbc_storeTableScrollPane.insets = new Insets(0, 0, 5, 0);
		gbc_storeTableScrollPane.fill = GridBagConstraints.BOTH;
		gbc_storeTableScrollPane.gridx = 0;
		gbc_storeTableScrollPane.gridy = 0;
		vaultStoreSection.add(storeTableScrollPane, gbc_storeTableScrollPane);
		storeTableScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		
		storeTable = new JTable(storeTableModel);
		storeTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		storeTable.setFillsViewportHeight(true);
		storeTableScrollPane.setViewportView(storeTable);
		storeTable.getColumnModel().getColumn(0).setMaxWidth(40);
		storeTable.getColumnModel().getColumn(0).setWidth(40);
		
		lblStoreRecordCount = new JLabel("0 Records");
		GridBagConstraints gbc_lblStoreRecordCount = new GridBagConstraints();
		gbc_lblStoreRecordCount.gridx = 1;
		gbc_lblStoreRecordCount.gridy = 1;
		vaultStoreSection.add(lblStoreRecordCount, gbc_lblStoreRecordCount);
		
		chckbxSpecificVaultArchives = new JCheckBox("Process Specific Vault Archives");
		chckbxSpecificVaultArchives.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				setArchiveSelectionEnabled(chckbxSpecificVaultArchives.isSelected());
			}
		});
		GridBagConstraints gbc_chckbxSpecificVaultArchives = new GridBagConstraints();
		gbc_chckbxSpecificVaultArchives.anchor = GridBagConstraints.WEST;
		gbc_chckbxSpecificVaultArchives.insets = new Insets(0, 0, 5, 0);
		gbc_chckbxSpecificVaultArchives.gridx = 0;
		gbc_chckbxSpecificVaultArchives.gridy = 2;
		add(chckbxSpecificVaultArchives, gbc_chckbxSpecificVaultArchives);
		
		vaultArchiveSection = new JPanel(){
			@Override
			public void setEnabled(boolean value) {
				super.setEnabled(value);
				for (Component c : getComponents()) {
					c.setEnabled(value);
				}
				setVisible(value);
				storeTableModel.setShowArchiveCounts(value);
				gridBagLayout.rowWeights[3] = value ? 1.0 : 0.0;
				getParent().validate();
			}
		};
		vaultArchiveSection.setBorder(new CompoundBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Vault Archives", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)), new EmptyBorder(5, 5, 5, 5)));
		GridBagConstraints gbc_vaultArchiveSection = new GridBagConstraints();
		gbc_vaultArchiveSection.fill = GridBagConstraints.BOTH;
		gbc_vaultArchiveSection.gridx = 0;
		gbc_vaultArchiveSection.gridy = 3;
		add(vaultArchiveSection, gbc_vaultArchiveSection);
		GridBagLayout gbl_vaultArchiveSection = new GridBagLayout();
		gbl_vaultArchiveSection.columnWidths = new int[]{0, 250, 0, 0, 0, 0};
		gbl_vaultArchiveSection.rowHeights = new int[]{0, 0, 0, 0};
		gbl_vaultArchiveSection.columnWeights = new double[]{0.0, 0.0, 0.0, 1.0, 0.0, Double.MIN_VALUE};
		gbl_vaultArchiveSection.rowWeights = new double[]{0.0, 1.0, 0.0, Double.MIN_VALUE};
		vaultArchiveSection.setLayout(gbl_vaultArchiveSection);
		
		JLabel lblName = new JLabel("Archive Name");
		GridBagConstraints gbc_lblName = new GridBagConstraints();
		gbc_lblName.insets = new Insets(0, 0, 5, 5);
		gbc_lblName.gridx = 0;
		gbc_lblName.gridy = 0;
		vaultArchiveSection.add(lblName, gbc_lblName);
		
		txtNameCriteria = new JTextField();
		GridBagConstraints gbc_txtNameCriteria = new GridBagConstraints();
		gbc_txtNameCriteria.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtNameCriteria.insets = new Insets(0, 0, 5, 5);
		gbc_txtNameCriteria.gridx = 1;
		gbc_txtNameCriteria.gridy = 0;
		vaultArchiveSection.add(txtNameCriteria, gbc_txtNameCriteria);
		txtNameCriteria.setColumns(10);
		
		JToolBar toolBar = new JToolBar();
		GridBagConstraints gbc_toolBar = new GridBagConstraints();
		gbc_toolBar.insets = new Insets(0, 0, 5, 5);
		gbc_toolBar.gridx = 2;
		gbc_toolBar.gridy = 0;
		vaultArchiveSection.add(toolBar, gbc_toolBar);
		toolBar.setFloatable(false);
		
		JXButton btnClearArchiveName = new JXButton();
		btnClearArchiveName.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				txtNameCriteria.setText("");
			}
		});
		btnClearArchiveName.setToolTipText("Clear archive name criteria");
		btnClearArchiveName.setIcon(new ImageIcon(VaultEntryBrowser.class.getResource("/com/nuix/evmanager/controls/cancel.png")));
		toolBar.add(btnClearArchiveName);
		
		JScrollPane archiveTableScrollPane = new JScrollPane(){

			@Override
			public void setEnabled(boolean value) {
				getHorizontalScrollBar().setEnabled(value);
				getVerticalScrollBar().setEnabled(value);
				getViewport().getView().setEnabled(value);
				super.setEnabled(value);
			}
			
		};
		GridBagConstraints gbc_archiveTableScrollPane = new GridBagConstraints();
		gbc_archiveTableScrollPane.gridwidth = 5;
		gbc_archiveTableScrollPane.insets = new Insets(0, 0, 5, 0);
		gbc_archiveTableScrollPane.fill = GridBagConstraints.BOTH;
		gbc_archiveTableScrollPane.gridx = 0;
		gbc_archiveTableScrollPane.gridy = 1;
		vaultArchiveSection.add(archiveTableScrollPane, gbc_archiveTableScrollPane);
		archiveTableScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		
		archiveTable = new JTable(archiveTableModel);
		archiveTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		archiveTable.setFillsViewportHeight(true);
		archiveTableScrollPane.setViewportView(archiveTable);
		archiveTable.getColumnModel().getColumn(0).setMaxWidth(60);
		archiveTable.getColumnModel().getColumn(0).setWidth(60);
		
		lblArchiveRecordCount = new JLabel("0 Records");
		GridBagConstraints gbc_lblArchiveRecordCount = new GridBagConstraints();
		gbc_lblArchiveRecordCount.gridx = 4;
		gbc_lblArchiveRecordCount.gridy = 2;
		vaultArchiveSection.add(lblArchiveRecordCount, gbc_lblArchiveRecordCount);

		DefaultTableCellRenderer leftRenderer = new DefaultTableCellRenderer();
		leftRenderer.setHorizontalAlignment(JLabel.LEFT);
		for (int i = 1; i < storeTableModel.getColumnCount(); i++) {
			storeTable.getColumnModel().getColumn(i).setCellRenderer(leftRenderer);	
		}
		
		setupCriteriaListeners();
		setupSelectionListeners();
		setArchiveSelectionEnabled(chckbxSpecificVaultArchives.isSelected());
	}
	
	private void updateStoreCountLabel() {
		String totalCount = NumberFormat.getNumberInstance(Locale.US).format(storeTableModel.getRowCount());
		String recordsCountString = totalCount+" Vault Stores";
		String checkedCount = NumberFormat.getNumberInstance(Locale.US).format(storeTableModel.getCheckedStoreCount());
		String checkedCountString = checkedCount+" Checked";
		lblStoreRecordCount.setText(recordsCountString+", "+checkedCountString);
	}
	
	private void updateArchiveCountLabel(){
		String totalCount = NumberFormat.getNumberInstance(Locale.US).format(archiveTableModel.getRowCount());
		String recordCountString = totalCount+" Vault Archives";
		String checkedCount = NumberFormat.getNumberInstance(Locale.US).format(archiveTableModel.getCheckedArchiveCount());
		String checkedCountString = checkedCount+" Checked";
		lblArchiveRecordCount.setText(recordCountString+", "+checkedCountString);
	}

	private void setupSelectionListeners(){
		// Respond to selected server changing
		evServerComboBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				VaultServer selectedServer = (VaultServer)evServerComboBox.getSelectedItem();
				if(selectedServer != null){
					logger.info("Selected server changed to: "+selectedServer.getDisplayName());
					try {
						selectedVaultServer = selectedServer;
						archiveTableModel.setArchives(null);
						storeTableModel.setStores(null);
						List<VaultStore> vaultStores = selectedServer.getStores();
						storeTableModel.setStores(vaultStores);
						updateStoreCountLabel();
						TableHelpers.resizeColumnWidth(storeTable);
					} catch (Exception e) {
						e.printStackTrace();
					}	
				}
			}
		});
		
		//Respond to checked vault stores changing
		storeTableModel.addCheckedOptionsChangedListener(new CheckedOptionsChangedListener() {
			@Override
			public void checkedOptionsChanged() {
				checkedVaultStores = storeTableModel.getCheckedStores();
				try {
					updateStoreCountLabel();
					signalUpdateArchiveListing();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		
		archiveTableModel.addCheckedOptionsChangedListener(new CheckedOptionsChangedListener() {
			
			@Override
			public void checkedOptionsChanged() {
				updateArchiveCountLabel();
			}
		});
	}
	
	private void signalUpdateArchiveListing(){
		fetchDelayTimer.stop();
		fetchDelayTimer.start();
	}
	
	private void setupCriteriaListeners(){
		fetchDelayTimer = new Timer(750,new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					updateArchiveListing();
				} catch (Exception e) {
				}
			}
		});
		fetchDelayTimer.setRepeats(false);
		
		DocumentListener textCriteriaListener = new DocumentListener(){
			@Override
			public void changedUpdate(DocumentEvent arg0) { signalUpdateArchiveListing(); }
			@Override
			public void insertUpdate(DocumentEvent arg0) { signalUpdateArchiveListing(); }
			@Override
			public void removeUpdate(DocumentEvent arg0) { signalUpdateArchiveListing(); }
		};
		txtNameCriteria.getDocument().addDocumentListener(textCriteriaListener);
	}
	
	private void updateArchiveListing() throws Exception{
		logger.info("Updating archive listing...");
		
		VaultArchiveCriteria archiveCirteria = new VaultArchiveCriteria();
		archiveCirteria.setArchiveNameCriteria(txtNameCriteria.getText());
		
		VaultArchiveResult archives = null;
		if(checkedVaultStores.size() > 0){
			archives = selectedVaultServer.getArchives(checkedVaultStores, archiveCirteria);
		}
		archiveTableModel.setArchives(archives);
		updateArchiveCountLabel();
		logger.info("Tweaking archive table column sizes...");
		TableHelpers.resizeColumnWidth(archiveTable);
		logger.info("Scrolling archives table to top...");
		TableHelpers.scrollToTop(archiveTable);
	}
	
	private void setArchiveSelectionEnabled(boolean enabled) {
		vaultArchiveSection.setEnabled(enabled);
		storeTable.getColumnModel().getColumn(0).setMaxWidth(40);
		storeTable.getColumnModel().getColumn(0).setWidth(40);
		TableHelpers.resizeColumnWidth(storeTable);
		DefaultTableCellRenderer leftRenderer = new DefaultTableCellRenderer();
		leftRenderer.setHorizontalAlignment(JLabel.LEFT);
		for (int i = 1; i < storeTableModel.getColumnCount(); i++) {
			storeTable.getColumnModel().getColumn(i).setCellRenderer(leftRenderer);	
		}
	}
	
	public void setVaultServers(List<VaultServer> servers){
		for(VaultServer server : servers){
			evServerComboBox.addItem(server);	
		}
	}
	
	public VaultServer getSelectedVaultServer(){
		return selectedVaultServer;
	}
	
	public List<VaultStore> getSelectedStores() {
		return storeTableModel.getCheckedStores();
	}

	public List<VaultArchive> getSelectedArchives() {
		if(chckbxSpecificVaultArchives.isSelected()){
			return archiveTableModel.getCheckedArchives();	
		} else {
			return new ArrayList<VaultArchive>();
		}
	}
	
	public List<VaultStore> getSelectedStoresWithoutSelectedArchives() {
		return storeTableModel.getSelectedStoresWithoutSelectedArchives();
	}
}
