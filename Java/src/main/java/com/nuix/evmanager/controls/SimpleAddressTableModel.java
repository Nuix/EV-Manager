package com.nuix.evmanager.controls;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import com.nuix.evmanager.data.UserAddress;

/***
 * A table model for displaying a table listing of addresses
 * @author Jason Wells
 *
 */
@SuppressWarnings("serial")
public class SimpleAddressTableModel extends AbstractTableModel {
	private String[] headers = new String[]{
			"Address"
	};
	
	private List<UserAddress> addresses = new ArrayList<UserAddress>(); 

	@Override
	public int getColumnCount() {
		return headers.length;
	}

	@Override
	public int getRowCount() {
		if(addresses == null){
			return 0;
		} else {
			return addresses.size();	
		}
	}

	@Override
	public Object getValueAt(int row, int col) {
		return addresses.get(row).getAddress();
	}
	
	@Override
	public String getColumnName(int column) {
		return headers[column];
	}

	public void setAddresses(List<UserAddress> addresses){
		this.addresses = addresses;
		fireTableDataChanged();
	}
	
	public void clear(){
		setAddresses(null);
		fireTableDataChanged();
	}
}
