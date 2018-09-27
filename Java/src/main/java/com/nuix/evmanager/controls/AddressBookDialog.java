 package com.nuix.evmanager.controls;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import com.nuix.evmanager.data.SQLUserRecord;
import com.nuix.evmanager.data.SQLUserRecordStore;
import com.nuix.evmanager.data.UserRecordStore;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

/***
 * A dialog which hosts controls allowing you to browse and select custodians from the address book database.
 * @author Jason Wells
 *
 */
@SuppressWarnings("serial")
public class AddressBookDialog extends JDialog {

	private final JPanel contentPanel = new JPanel();
	private JButton okButton;
	private UserRecordBrowser userRecordBrowser;
	private SQLUserRecord selectedRecord = null;
	private boolean dialogResult = false;

	public AddressBookDialog() {
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setIconImage(Toolkit.getDefaultToolkit().getImage(AddressBookDialog.class.getResource("/com/nuix/evmanager/controls/nuix_icon.png")));
		setTitle("User Address Book");
		setSize(1024, 768);
		setLocationRelativeTo(null);
		setModal(true);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BorderLayout(0, 0));
		{
			userRecordBrowser = new UserRecordBrowser();
			contentPanel.add(userRecordBrowser, BorderLayout.CENTER);
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				okButton = new JButton("OK");
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						dialogResult = true;
						dispose();
					}
				});
				okButton.setEnabled(false);
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
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
		{
			JMenuBar menuBar = new JMenuBar();
			setJMenuBar(menuBar);
			{
				JMenu mnUser = new JMenu("Addtional Actions");
				menuBar.add(mnUser);
				{
					JMenuItem mntmAddNewUser = new JMenuItem("Add New User...");
					mnUser.add(mntmAddNewUser);
				}
			}
		}
		
		userRecordBrowser.addUserSelectionListener(new UserSelectionListener() {
			@Override
			public void userSelected(SQLUserRecord userRecord) {
				selectedRecord = userRecord;
				okButton.setEnabled(true);
			}
		});
	}

	/***
	 * Gets the currently selected user record, or null if no record is selected
	 * @return
	 */
	public SQLUserRecord getSelectedRecord() {
		return selectedRecord;
	}
	
	public List<SQLUserRecord> getSelectedRecords() throws Exception{
		return userRecordBrowser.getCheckedUsers();
	}

	/***
	 * Gets the dialog result
	 * @return Returns true if the dialog was closed via the "Ok" button, false otherwise
	 */
	public boolean getDialogResult() {
		return dialogResult;
	}
	
	/***
	 * Sets the current user record data source, which is likely going to be an instance of {@link SQLUserRecordStore}.
	 * @param recordSource The user record data source
	 */
	public void setUserRecordSource(UserRecordStore recordSource) {
		userRecordBrowser.setRecordSource(recordSource);
	}
	
	public void setCheckedUsers(Collection<SQLUserRecord> records){
		userRecordBrowser.setCheckedUsers(records);
	}
}
