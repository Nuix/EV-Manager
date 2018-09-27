package com.nuix.evmanager.controls;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

/***
 * A table model for displaying a list of SIDs in a table
 * @author Jason Wells
 *
 */
@SuppressWarnings("serial")
public class SIDTableModel extends AbstractTableModel {

	private String[] headers = new String[]{
		"SID",	
	};
	
	private List<String> records = new ArrayList<String>();
	
	@Override
	public int getColumnCount() {
		return headers.length;
	}

	@Override
	public int getRowCount() {
		if(records == null){
			return 0;
		} else {
			return records.size();	
		}
	}

	@Override
	public Object getValueAt(int row, int col) {
		return records.get(row);
	}

	@Override
	public String getColumnName(int col) {
		return headers[col];
	}

	public List<String> getRecords() {
		return records;
	}

	public void setRecords(List<String> phoneNumbers) {
		this.records = phoneNumbers;
		fireTableDataChanged();
	}

	public void clear(){
		setRecords(null);
	}
}
