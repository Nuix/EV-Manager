package com.nuix.evmanager.controls;

import java.io.IOException;
//import java.sql.Timestamp;
//import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.table.AbstractTableModel;

import org.apache.log4j.Logger;

import com.nuix.evmanager.data.SQLUserRecord;
import com.nuix.evmanager.data.VirtualizedRecordCollection;

@SuppressWarnings("serial")
public class UserRecordSelectionTableModel extends AbstractTableModel {
	private static Logger logger = Logger.getLogger(UserRecordSelectionTableModel.class);
	
	public String[] headers = new String[] {
		"",
		"Employee ID",
		"Name",
		"Title",
		"Department",
		"Location",
	};
	
	private VirtualizedRecordCollection<SQLUserRecord> resultSet = null;
	private Set<SQLUserRecord> checkedRecords = new HashSet<SQLUserRecord>();
	
	@Override
	public int getColumnCount() {
		return headers.length;
	}

	@Override
	public int getRowCount() {
		if(resultSet == null){
			return 0;
		}
		
		try {
			return resultSet.size();
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}

	@Override
	public Object getValueAt(int row, int col) {
		if(resultSet == null){
			return "";
		}
		
		SQLUserRecord record;
		try {
			record = resultSet.get(row);
		} catch (Exception e) {
			e.printStackTrace();
			return "Error getting result "+row;
		}
		
		if(col == 0){
			return isChecked(record);
		}
		
		switch(col-1){
			case 0: return record.getEmployeeID();
			case 1: return record.getName();
			case 2: return record.getTitle();
			case 3: return record.getDepartment();
			case 4: return record.getLocation();
			case 5: return "temp";
//			case 6:
//				Timestamp escalationDate = record.getEscalationDate();
//				if(escalationDate == null) return "";
//				else return dateFormat.format(escalationDate);
//			case 7:
//				Timestamp hiredDate = record.getHiredDate();
//				if(hiredDate == null) return "";
//				else return dateFormat.format(hiredDate);
//			case 8:
//				Timestamp terminatedDate = record.getTerminatedDate();
//				if(terminatedDate == null) return "";
//				else return dateFormat.format(terminatedDate);
			default:
				return "Unknown column: "+col;
		}
	}

	@Override
	public String getColumnName(int column) {
		return headers[column];
	}

	@Override
	public boolean isCellEditable(int row, int col) {
		return col == 0;
	}

	@Override
	public void setValueAt(Object aValue, int row, int col) {
		if(col == 0){
			SQLUserRecord record = null;
			try {
				record = resultSet.get(row);
				setCheckedState(record, (Boolean)aValue);
			} catch (Exception e) {
				logger.error("Error while updating user checked state");
				logger.error(e);
			}
		}
	}

	@Override
	public Class<?> getColumnClass(int col) {
		if(col == 0){
			return Boolean.class;
		} else {
			return super.getColumnClass(col);	
		}
	}
	
	public boolean isChecked(SQLUserRecord user){
		return checkedRecords.contains(user);
	}
	
	public void setCheckedState(SQLUserRecord user, Boolean value){
		if(value == true)
			checkedRecords.add(user);
		else
			checkedRecords.remove(user);
	}

	public VirtualizedRecordCollection<SQLUserRecord> getResultSet() {
		return resultSet;
	}

	public void setResultSet(VirtualizedRecordCollection<SQLUserRecord> resultSet) throws Exception {
		if(this.resultSet != null){
			try {
				this.resultSet.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		this.resultSet = resultSet;
		this.fireTableDataChanged();
	}
	
	public List<SQLUserRecord> getCheckedUsers() throws Exception{
		return new ArrayList<SQLUserRecord>(checkedRecords);
	}
	
	public void setCheckedUsers(Collection<SQLUserRecord> records){
		checkedRecords.clear();
		for (SQLUserRecord record : records) {
			checkedRecords.add(record);
		}
		this.fireTableDataChanged();
	}
}
