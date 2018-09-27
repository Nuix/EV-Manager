package com.nuix.evmanager.controls;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import com.nuix.evmanager.data.SQLUserRecord;

/***
 * A table model for displaying a listing of user records in a table
 * @author Jason Wells
 *
 */
@SuppressWarnings("serial")
public class SimpleUserRecordTableModel extends AbstractTableModel {

	private List<SQLUserRecord> records = new ArrayList<SQLUserRecord>(); 
	
	private String[] headers = new String[]{
			"Employee ID",
			"Name",
			"Title",
			"Department",
			"Location",	
	};
	
	@Override
	public int getColumnCount() {
		return headers.length;
	}

	@Override
	public int getRowCount() {
		return records.size();
	}

	@Override
	public Object getValueAt(int row, int col) {
		SQLUserRecord record = records.get(row);
		switch (col) {
			case 0: return record.getEmployeeID();
			case 1: return record.getName();
			case 2: return record.getTitle();
			case 3: return record.getDepartment();
			case 4: return record.getLocation();
			default:
				return "Unknown column: "+col;
		}
	}

	@Override
	public String getColumnName(int column) {
		return headers[column];
	}
	
	public void setRecords(List<SQLUserRecord> records){
		this.records = records;
		this.fireTableDataChanged();
	}
	
	public void clear(){
		records.clear();
		this.fireTableDataChanged();
	}
}
