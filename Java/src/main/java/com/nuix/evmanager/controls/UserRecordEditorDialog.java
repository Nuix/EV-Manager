package com.nuix.evmanager.controls;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import com.nuix.evmanager.data.AddressChangeOrder;
import com.nuix.evmanager.data.SQLUserRecord;

@SuppressWarnings("serial")
public class UserRecordEditorDialog extends JDialog {

	private final JPanel contentPanel = new JPanel();
	private UserRecordEditor userRecordEditor;
	private JButton saveButton;
	private boolean dialogResult = false;

	public UserRecordEditorDialog() {
		setTitle("Edit User Record");
		setIconImage(Toolkit.getDefaultToolkit().getImage(UserRecordEditorDialog.class.getResource("/com/nuix/evmanager/controls/nuix_icon.png")));
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setSize(800,600);
		setLocationRelativeTo(null);
		setModal(true);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BorderLayout(0, 0));
		{
			userRecordEditor = new UserRecordEditor();
			contentPanel.add(userRecordEditor, BorderLayout.CENTER);
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				saveButton = new JButton("Save Changes");
				saveButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						if(isDirty()){
							String message = "You are about to save the following changes: \n\n"+getChangeOrder().getSummary()+"\n\nProceed?";
							int result = JOptionPane.showConfirmDialog(null, message, "Proceed with changes?", JOptionPane.YES_NO_OPTION);
							if (result == JOptionPane.YES_OPTION){
								dialogResult = true;
								dispose();
							}
						}
					}
				});
				saveButton.setEnabled(false);
				buttonPane.add(saveButton);
			}
			{
				JButton cancelButton = new JButton("Cancel");
				cancelButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						if(isDirty()){
							String message = "You have unsaved changes, proceed and discard changes?";
							int result = JOptionPane.showConfirmDialog(null, message, "Proceed with changes?", JOptionPane.YES_NO_OPTION);
							if (result == JOptionPane.YES_OPTION){
								try {
									userRecordEditor.undoAllChanges();
								} catch (Exception e1) {
									e1.printStackTrace();
								}
								dialogResult = false;
								dispose();
							}
						}
						else {
							dispose();
						}
					}
				});
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
		
		userRecordEditor.addIsDirtyChangeListener(new IsDirtyChangeListener() {
			
			@Override
			public void statusChanged(boolean isDirty) {
				saveButton.setEnabled(isDirty);
			}
		});
	}

	public void setUserRecord(SQLUserRecord userRecord) throws Exception{
		userRecordEditor.setUserRecord(userRecord);
	}
	
	public AddressChangeOrder getChangeOrder(){
		return userRecordEditor.getChangeOrder();
	}
	
	public boolean isDirty() {
		return userRecordEditor.isDirty();
	}
	
	public boolean getDialogResult(){
		return dialogResult;
	}
}
