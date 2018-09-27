/**
 * NUIX MAKES NO EXPRESS OR IMPLIED REPRESENTATIONS OR WARRANTIES WITH RESPECT TO THIS CODE (INCLUDING BUT NOT LIMITED
 * TO ANY WARRANTIES OF MERCHANTABILITY, SATISFACTORY QUALITY, FITNESS FOR A PARTICULAR PURPOSE, OR SUITABILITY FOR
 * CUSTOMER’S REQUIREMENTS). WITHOUT LIMITING THE FOREGOING, NUIX DOES NOT WARRANT THAT THIS CODE WILL MEET CUSTOMER’S
 * REQUIREMENTS OR THAT ANY USE OF THIS CODE WILL BE ERROR-FREE OR THAT ANY ERRORS OR DEFECTS IN THIS CODE WILL BE CORRECTED.
 * THIS CODE IS PROVIDED TO CUSTOMER ON AN “AS IS” AND “AS AVAILABLE” BASIS AND FOR COMMERCIAL USE ONLY. CUSTOMER IS RESPONSIBLE
 * FOR DETERMINING WHETHER ANY INFORMATION GENERATED FROM USE OF THIS CODE IS ACCURATE AND SUFFICIENT FOR CUSTOMER’S PURPOSES.
 */
package com.nuix.evmanager.controls;

import javax.swing.JPanel;

import java.awt.GridBagLayout;

import javax.swing.JTextField;

import java.awt.GridBagConstraints;
import java.awt.Insets;

import javax.swing.JButton;

import com.nuix.evmanager.controls.CommonDialogs;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.File;

/***
 * A control which provides a user a way to select paths
 * @author JWells01
 *
 */
@SuppressWarnings("serial")
public class PathSelectionControl extends JPanel {
	public enum ChooserType {
		DIRECTORY,
		OPEN_FILE,
		SAVE_FILE
	}
	private JTextField txtFilePath;
	private String dialogTitle = "Choose";
	private JButton btnChoose;
	private PathSelectedCallback pathSelectedCallback;
	
	public PathSelectionControl(ChooserType type, String fileTypeName, String fileExtension, String openFileDialogTitle) {
		if(openFileDialogTitle != null)
			dialogTitle = openFileDialogTitle;
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{0, 0, 0};
		gridBagLayout.rowHeights = new int[]{0, 0};
		gridBagLayout.columnWeights = new double[]{1.0, 0.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, Double.MIN_VALUE};
		setLayout(gridBagLayout);
		
		txtFilePath = new JTextField();
		GridBagConstraints gbc_txtFilePath = new GridBagConstraints();
		gbc_txtFilePath.insets = new Insets(0, 0, 0, 5);
		gbc_txtFilePath.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtFilePath.gridx = 0;
		gbc_txtFilePath.gridy = 0;
		add(txtFilePath, gbc_txtFilePath);
		txtFilePath.setColumns(10);
		
		btnChoose = new JButton("Choose");
		btnChoose.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				File selectedFile = null;
				switch(type){
				case DIRECTORY:
					selectedFile = CommonDialogs.getDirectory(txtFilePath.getText(), dialogTitle);
					firePathSelected();
					break;
				case OPEN_FILE:
					selectedFile = CommonDialogs.openFileDialog(txtFilePath.getText(), fileTypeName, fileExtension, dialogTitle);
					firePathSelected();
					break;
				case SAVE_FILE:
					selectedFile = CommonDialogs.saveFileDialog(txtFilePath.getText(), fileTypeName, fileExtension, dialogTitle);
					firePathSelected();
					break;
				default:
					break;
				}
				
				if(selectedFile != null)
					txtFilePath.setText(selectedFile.toString());
			}
		});
		GridBagConstraints gbc_btnChoose = new GridBagConstraints();
		gbc_btnChoose.gridx = 1;
		gbc_btnChoose.gridy = 0;
		add(btnChoose, gbc_btnChoose);

	}
	
	protected void firePathSelected(){
		if(pathSelectedCallback != null){
			pathSelectedCallback.pathSelected(getPath());
		}
	}
	
	public void whenPathSelected(PathSelectedCallback callback){
		pathSelectedCallback = callback;
	}

	public void setPath(String path){
		txtFilePath.setText(path);
	}
	
	public String getPath(){
		return txtFilePath.getText();
	}
	
	public File getPathFile(){
		return new File(getPath());
	}
	
	public void setEnabled(boolean value){
		txtFilePath.setEnabled(value);
		btnChoose.setEnabled(value);
	}
	
	public void setPathFieldEditable(boolean value){
		txtFilePath.setEditable(value);
	}
}
