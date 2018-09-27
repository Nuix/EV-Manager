package com.nuix.evmanager.controls;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import com.nuix.evmanager.data.EvidenceSettings;
import com.nuix.evmanager.data.SQLUserRecord;

import javax.swing.JButton;
import javax.swing.ImageIcon;
import javax.swing.JToolBar;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

/***
 * A control allowing a user to specify evidence container settings.
 * @author Jason Wells
 *
 */
@SuppressWarnings("serial")
public class EvidenceSettingsControl extends JPanel {
	private JTextField txtEvidenceName;
	
	class Zone{
		public DateTimeZone timeZone;

		public Zone(DateTimeZone timeZone){
			this.timeZone = timeZone;
		}
		
		@Override
		public String toString() {
			return "(GTM"+timeZone.getName(new DateTime().getMillis())+
					") "+timeZone.toTimeZone().getDisplayName() + " ("+
					timeZone.getID()+")";
		}
	}
	private List<SQLUserRecord> selectedUserRecords = null;
	private List<Zone> zones = new ArrayList<Zone>();
	private JComboBox<Zone> comboTimeZone;
	private JComboBox<Charset> comboEncoding;
	private JLabel lblCustodianName;
	private JTextField txtEvidenceCustodian;
	private JButton btnUseSelectedCustodian;
	private JToolBar toolBar;
	private JButton btnClearCustodianName;

	public EvidenceSettingsControl() {
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{0, 100, 200, 0, 0, 0};
		gridBagLayout.rowHeights = new int[]{0, 0, 0, 0, 0, 0};
		gridBagLayout.columnWeights = new double[]{0.0, 0.0, 0.0, 0.0, 1.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 1.0, Double.MIN_VALUE};
		setLayout(gridBagLayout);
		
		JLabel lblEvidenceName = new JLabel("Evidence name:");
		GridBagConstraints gbc_lblEvidenceName = new GridBagConstraints();
		gbc_lblEvidenceName.insets = new Insets(0, 0, 5, 5);
		gbc_lblEvidenceName.anchor = GridBagConstraints.EAST;
		gbc_lblEvidenceName.gridx = 0;
		gbc_lblEvidenceName.gridy = 0;
		add(lblEvidenceName, gbc_lblEvidenceName);
		
		txtEvidenceName = new JTextField();
		GridBagConstraints gbc_txtEvidenceName = new GridBagConstraints();
		gbc_txtEvidenceName.gridwidth = 2;
		gbc_txtEvidenceName.insets = new Insets(0, 0, 5, 5);
		gbc_txtEvidenceName.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtEvidenceName.gridx = 1;
		gbc_txtEvidenceName.gridy = 0;
		add(txtEvidenceName, gbc_txtEvidenceName);
		txtEvidenceName.setColumns(10);
		
		lblCustodianName = new JLabel("Custodian Name:");
		GridBagConstraints gbc_lblCustodianName = new GridBagConstraints();
		gbc_lblCustodianName.anchor = GridBagConstraints.EAST;
		gbc_lblCustodianName.insets = new Insets(0, 0, 5, 5);
		gbc_lblCustodianName.gridx = 0;
		gbc_lblCustodianName.gridy = 1;
		add(lblCustodianName, gbc_lblCustodianName);
		
		txtEvidenceCustodian = new JTextField();
		txtEvidenceCustodian.setToolTipText("The custodian name to assign to the data ingested");
		GridBagConstraints gbc_txtEvidenceCustodian = new GridBagConstraints();
		gbc_txtEvidenceCustodian.gridwidth = 2;
		gbc_txtEvidenceCustodian.insets = new Insets(0, 0, 5, 5);
		gbc_txtEvidenceCustodian.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtEvidenceCustodian.gridx = 1;
		gbc_txtEvidenceCustodian.gridy = 1;
		add(txtEvidenceCustodian, gbc_txtEvidenceCustodian);
		txtEvidenceCustodian.setColumns(10);
		
		toolBar = new JToolBar();
		toolBar.setFloatable(false);
		GridBagConstraints gbc_toolBar = new GridBagConstraints();
		gbc_toolBar.insets = new Insets(0, 0, 5, 5);
		gbc_toolBar.gridx = 3;
		gbc_toolBar.gridy = 1;
		add(toolBar, gbc_toolBar);
		
		btnUseSelectedCustodian = new JButton("");
		btnUseSelectedCustodian.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(selectedUserRecords != null){
					List<String> names = selectedUserRecords.stream().map(u -> u.getName()).collect(Collectors.toList());
					txtEvidenceCustodian.setText(String.join("; ",names));
				}
			}
		});
		toolBar.add(btnUseSelectedCustodian);
		btnUseSelectedCustodian.setToolTipText("Use selected custodian from Address Book");
		btnUseSelectedCustodian.setIcon(new ImageIcon(EvidenceSettingsControl.class.getResource("/com/nuix/evmanager/controls/user.png")));
		
		btnClearCustodianName = new JButton("");
		btnClearCustodianName.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				txtEvidenceCustodian.setText("");
			}
		});
		btnClearCustodianName.setToolTipText("Clear custodian name");
		btnClearCustodianName.setIcon(new ImageIcon(EvidenceSettingsControl.class.getResource("/com/nuix/evmanager/controls/cancel.png")));
		toolBar.add(btnClearCustodianName);
		
		JLabel lblSourceTimeZone = new JLabel("Source time zone:");
		GridBagConstraints gbc_lblSourceTimeZone = new GridBagConstraints();
		gbc_lblSourceTimeZone.anchor = GridBagConstraints.EAST;
		gbc_lblSourceTimeZone.insets = new Insets(0, 0, 5, 5);
		gbc_lblSourceTimeZone.gridx = 0;
		gbc_lblSourceTimeZone.gridy = 2;
		add(lblSourceTimeZone, gbc_lblSourceTimeZone);
		
		comboTimeZone = new JComboBox<Zone>();
		GridBagConstraints gbc_comboTimeZone = new GridBagConstraints();
		gbc_comboTimeZone.gridwidth = 2;
		gbc_comboTimeZone.insets = new Insets(0, 0, 5, 5);
		gbc_comboTimeZone.fill = GridBagConstraints.HORIZONTAL;
		gbc_comboTimeZone.gridx = 1;
		gbc_comboTimeZone.gridy = 2;
		add(comboTimeZone, gbc_comboTimeZone);
		
		JLabel lblSourceEncoding = new JLabel("Source encoding:");
		GridBagConstraints gbc_lblSourceEncoding = new GridBagConstraints();
		gbc_lblSourceEncoding.anchor = GridBagConstraints.EAST;
		gbc_lblSourceEncoding.insets = new Insets(0, 0, 5, 5);
		gbc_lblSourceEncoding.gridx = 0;
		gbc_lblSourceEncoding.gridy = 3;
		add(lblSourceEncoding, gbc_lblSourceEncoding);
		
		comboEncoding = new JComboBox<Charset>();
		GridBagConstraints gbc_comboEncoding = new GridBagConstraints();
		gbc_comboEncoding.insets = new Insets(0, 0, 5, 5);
		gbc_comboEncoding.fill = GridBagConstraints.HORIZONTAL;
		gbc_comboEncoding.gridx = 1;
		gbc_comboEncoding.gridy = 3;
		add(comboEncoding, gbc_comboEncoding);

		initialize();
	}

	private void initialize() {
		for(String zoneID : DateTimeZone.getAvailableIDs()){
			zones.add(new Zone(DateTimeZone.forID(zoneID)));
		}
		
		int defaultZoneIndex = 0;
		for (int i = 0; i < zones.size(); i++) {
			comboTimeZone.addItem(zones.get(i));
			if(zones.get(i).timeZone == DateTimeZone.getDefault()){
				defaultZoneIndex = i;
			}
		}
		comboTimeZone.setSelectedIndex(defaultZoneIndex);
		
		for(Map.Entry<String,Charset> charsetEntry : Charset.availableCharsets().entrySet()){
			comboEncoding.addItem(charsetEntry.getValue());
		}
		comboEncoding.setSelectedItem(Charset.forName("UTF-8"));
	}

	public EvidenceSettings getEvidenceSettings(){
		EvidenceSettings result = new EvidenceSettings();
		result.setEvidenceName(txtEvidenceName.getText());
		result.setSourceTimeZoneID(((Zone)comboTimeZone.getSelectedItem()).timeZone.getID());
		result.setSourceEncoding(((Charset)comboEncoding.getSelectedItem()).name());
		return result;
	}
	
	public boolean settingsAreValid(){
		if(txtEvidenceName.getText().trim().length() < 1){
			CommonDialogs.showError("Please provide an evidence name");
			return false;
		}
		return true;
	}
	
	public void setSelectedUserRecords(List<SQLUserRecord> updatedValue){
		selectedUserRecords = updatedValue;
	}
	
	public String getEvidenceCustodianName(){
		return txtEvidenceCustodian.getText();
	}
}
