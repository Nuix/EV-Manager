package com.nuix.evmanager.controls;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.Toolkit;
import java.awt.event.ActionListener;
import java.util.Map;
import java.awt.event.ActionEvent;

/***
 * A dialog which hosts a {@link DataProcessingSettingsControl}.
 * @author Jason Wells
 *
 */
@SuppressWarnings("serial")
public class DataProcessingSettingsDialog extends JDialog {

	private final JPanel contentPanel = new JPanel();
	private boolean dialogResult = false;
	private DataProcessingSettingsControl dataProcessingSettingsControl;

	public DataProcessingSettingsDialog() {
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setIconImage(Toolkit.getDefaultToolkit().getImage(DataProcessingSettingsDialog.class.getResource("/com/nuix/evmanager/controls/nuix_icon.png")));
		setTitle("Data Processing Settings");
		setSize(600,800);
		setLocationRelativeTo(null);
		setModal(true);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BorderLayout(0, 0));
		{
			dataProcessingSettingsControl = new DataProcessingSettingsControl();
			contentPanel.add(dataProcessingSettingsControl, BorderLayout.CENTER);
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						System.out.println("Clicked OK");
						dialogResult = true;
						System.out.println("dialogResult: "+dialogResult);
						dispose();
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
	}

	public boolean getDialogResult() {
		return dialogResult;
	}

	public Map<String,Object> getSettings(){
		return dataProcessingSettingsControl.getSettings();
	}
	
	public String getSettingsJSON(){
		return dataProcessingSettingsControl.getSettingsJSON();
	}

	public void setMimeTypeSettings(String mimeType, Map<String, Boolean> settings) {
		dataProcessingSettingsControl.setMimeTypeSettings(mimeType, settings);
	}
}
