package com.nuix.evmanager.controls;

import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.table.AbstractTableModel;

import com.nuix.evmanager.data.AddressChangeOrder;
import com.nuix.evmanager.data.SQLUserRecord;
import com.nuix.evmanager.data.UserAddress;

/***
 * A table model allowing for modifying the list of addresses associated with a {@link SQLUserRecord instance}.
 * @author Jason Wells
 *
 */
@SuppressWarnings("serial")
public class AddressEditorTableModel extends AbstractTableModel {
	
	private ImageIcon addIcon = new ImageIcon(Toolkit.getDefaultToolkit().getImage(UserRecordEditorDialog.class.getResource("/com/nuix/evmanager/controls/add.png")));
	private ImageIcon editIcon = new ImageIcon(Toolkit.getDefaultToolkit().getImage(UserRecordEditorDialog.class.getResource("/com/nuix/evmanager/controls/pencil.png")));
	private ImageIcon deleteIcon = new ImageIcon(Toolkit.getDefaultToolkit().getImage(UserRecordEditorDialog.class.getResource("/com/nuix/evmanager/controls/delete.png")));

	private List<IsDirtyChangeListener> isDirtyListeners = new ArrayList<IsDirtyChangeListener>();
	
	enum ModificationStatus {
		NoChange,
		Add,
		Update,
		Delete,
	}

	private String[] headers = new String[]{
			"Status",
			"Address"
	};
	
	private SQLUserRecord userRecord;
	private List<UserAddress> addresses = new ArrayList<UserAddress>();
	private List<ModificationStatus> addressModStatuses = new ArrayList<ModificationStatus>();
	
	@Override
	public int getColumnCount() {
		return headers.length;
	}

	@Override
	public int getRowCount() {
		return addresses.size();
	}

	@Override
	public Object getValueAt(int row, int col) {
		UserAddress userAddress = addresses.get(row);
		ModificationStatus modStatus = addressModStatuses.get(row);
		if(col == 0){
			switch (modStatus) {
				case Add:
					return addIcon;
				case Update:
					return editIcon;
				case Delete:
					return deleteIcon;
				case NoChange:
				default:
						return null;
			}
		} else {
			return userAddress.getAddress();	
		}
	}
	
	@Override
	public String getColumnName(int column) {
		return headers[column];
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		if(columnIndex == 0){
			return ImageIcon.class;
		} else {
			return super.getColumnClass(columnIndex);	
		}
	}

	@Override
	public boolean isCellEditable(int row, int col) {
		return col == 1;
	}

	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		if(columnIndex == 1){
			UserAddress userAddress = addresses.get(rowIndex);
			String updatedValue = (String)aValue;
			if(!userAddress.getAddress().equalsIgnoreCase(updatedValue)){
				userAddress.setAddress(updatedValue);
				markUpdated(rowIndex);
				fireTableDataChanged();
			}
		}
	}
	
	private void markUpdated(int row){
		addressModStatuses.set(row, ModificationStatus.Update);
		notifyIsDirtyListeners();
	}

	/***
	 * Marks a row to be deleted.
	 * @param row The row index to mark as deleted.
	 */
	public void deleteRow(int row){
		ModificationStatus currentModStatus = addressModStatuses.get(row);
		switch (currentModStatus) {
			case NoChange:
				addressModStatuses.set(row, ModificationStatus.Delete);
				fireTableDataChanged();
				break;
			case Add:
				addresses.remove(row);
				addressModStatuses.remove(row);
				fireTableDataChanged();
				break;
			default:
				break;
		}
		notifyIsDirtyListeners();
	}
	
	/***
	 * Adds a new address
	 * @param address The new address to add
	 */
	public void addNewAddress(UserAddress address){
		addresses.add(address);
		addressModStatuses.add(ModificationStatus.Add);
		fireTableDataChanged();
		notifyIsDirtyListeners();
	}

	/***
	 * Sets the list of addresses represented by this table model
	 * @param userRecord The {@link SQLUserRecord} instance which owns the addresses provided
	 * @param addresses The list of email addresses
	 */
	public void setAddresses(SQLUserRecord userRecord, List<UserAddress> addresses){
		this.userRecord = userRecord;
		this.addresses = addresses;
		addressModStatuses.clear();
		for (int i = 0; i < this.addresses.size(); i++) {
			addressModStatuses.add(ModificationStatus.NoChange);
		}
		fireTableDataChanged();
		notifyIsDirtyListeners();
	}

	/***
	 * Gets whether this table model currently contains user modifications
	 * @return True if modifications are pending
	 */
	public boolean isDirty() {
		int updateCount = 0;
		int addCount = 0;
		int deleteCount = 0;
		for (int i = 0; i < addressModStatuses.size(); i++) {
			switch (addressModStatuses.get(i)) {
				case Add:
					addCount++;
					break;
				case Delete:
					deleteCount++;
					break;
				case Update:
					updateCount++;
					break;
				default:
					break;
			}
		}
		
		return updateCount > 0 || addCount > 0 || deleteCount > 0;
	}
	
	/***
	 * Adds a callback which will be notified when the {@link #isDirty()} state changes
	 * @param listener The callback to add
	 */
	public void addIsDirtyChangeListener(IsDirtyChangeListener listener){
		isDirtyListeners.add(listener);
	}
	
	/***
	 * Removes a previously added {@link #isDirty()} change listener
	 * @param listener The callback to remove
	 */
	public void removeIsDirtyListener(IsDirtyChangeListener listener){
		isDirtyListeners.remove(listener);
	}
	
	private void notifyIsDirtyListeners(){
		boolean isDirty = isDirty();
		for(IsDirtyChangeListener listener : isDirtyListeners){
			listener.statusChanged(isDirty);
		}
	}
	
	/***
	 * Gets a {@link AddressChangeOrder} object which represents modifications made by the user
	 * @return a {@link AddressChangeOrder} object which represents modifications made by the user
	 */
	public AddressChangeOrder getChangeOrder(){
		List<UserAddress> newAddresses = new ArrayList<UserAddress>();
		List<UserAddress> updatedAddresses = new ArrayList<UserAddress>();
		List<UserAddress> deletedAddresses = new ArrayList<UserAddress>();
		
		for (int i = 0; i < addressModStatuses.size(); i++) {
			switch (addressModStatuses.get(i)) {
				case Add:
					newAddresses.add(addresses.get(i));
					break;
				case Delete:
					deletedAddresses.add(addresses.get(i));
					break;
				case Update:
					updatedAddresses.add(addresses.get(i));
					break;
				default:
					break;
			}
		}
		
		return new AddressChangeOrder(userRecord,newAddresses,updatedAddresses,deletedAddresses);
	}
}
