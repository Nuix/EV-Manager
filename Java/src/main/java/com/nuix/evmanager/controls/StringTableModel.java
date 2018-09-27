package com.nuix.evmanager.controls;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.table.DefaultTableModel;

/***
 * A table model for listing in a table a collection of string values
 * @author Jason Wells
 *
 */
@SuppressWarnings("serial")
public class StringTableModel extends DefaultTableModel {
	private String header = "No Header Provided";
	private List<String> values = new ArrayList<String>();
	
	public StringTableModel(String header){
		super();
		this.header = header;
	}

	@Override
	public int getColumnCount() {
		return 1;
	}

	@Override
	public String getColumnName(int column) {
		return header;
	}

	@Override
	public int getRowCount() {
		if(values == null){ return 0; }
		else { return values.size(); }
	}

	@Override
	public Object getValueAt(int row, int column) {
		return values.get(row);
	}

	@Override
	public boolean isCellEditable(int row, int column) {
		return false;
	}
	
	public void addValue(String value){
		value = value.trim();
		if(!values.contains(value)){
			values.add(value);
			int lastIndex = values.size() - 1;
			this.fireTableRowsInserted(lastIndex,lastIndex);
		}
	}
	
	public void removeValueAt(int row){
		values.remove(row);
		this.fireTableRowsDeleted(row, row);
	}
	
	public void removeIndices(int[] indices){
		Arrays.sort(indices);
		for(int i=indices.length-1;i>=0;i--){
			int rowIndex = indices[i];
			values.remove(rowIndex);
		}
		this.fireTableDataChanged();
	}
	
	public List<String> getValues(){
		return values;
	}
}
