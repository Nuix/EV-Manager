package com.nuix.evmanager.controls;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

/***
 * Table model for displaying a table of phone numbers
 * @author Jason Wells
 *
 */
@SuppressWarnings("serial")
public class PhoneNumbersTableModel extends AbstractTableModel {

	private String[] headers = new String[]{
		"Phone Number",	
	};
	
	private List<String> phoneNumbers = new ArrayList<String>();
	
	@Override
	public int getColumnCount() {
		return headers.length;
	}

	@Override
	public int getRowCount() {
		if(phoneNumbers == null){
			return 0;
		} else {
			return phoneNumbers.size();	
		}
	}

	@Override
	public Object getValueAt(int row, int col) {
		return phoneNumbers.get(row);
	}

	@Override
	public String getColumnName(int col) {
		return headers[col];
	}

	public List<String> getPhoneNumbers() {
		return phoneNumbers;
	}

	public void setPhoneNumbers(List<String> phoneNumbers) {
		this.phoneNumbers = phoneNumbers;
		fireTableDataChanged();
	}

	public void clear(){
		setPhoneNumbers(null);
	}
}
