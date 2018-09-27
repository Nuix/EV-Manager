package com.nuix.evmanager.controls;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;

import com.nuix.evmanager.data.IngestionHistoryEntry;

/***
 * Table model for displaying a table listing of ingestion history events.
 * @author Jason Wells
 *
 */
@SuppressWarnings("serial")
public class IngestionHistoryTableModel extends AbstractTableModel {

	private static Logger logger = Logger.getLogger(IngestionHistoryTableModel.class);
	private static String dateFormatString = "YYYY/MM/dd";
	private static String dateTimeFormatString = "YYYY/MM/dd HH:mm:ss";
	
	String[] headers = new String[]{
		"Employee ID",
		"User Name",
		"Ingestion Date",
		"Data",
		"Date Range Filter",
		"Case Name",
	};
	
	private List<IngestionHistoryEntry> records = new ArrayList<IngestionHistoryEntry>();

	@Override
	public Object getValueAt(int row, int col) {
		try {
			IngestionHistoryEntry record = records.get(row);
			switch (col) {
			case 0: return record.getAssociatedUser().getEmployeeID();
			case 1: return record.getAssociatedUser().getName();
			case 2: return record.getDateIngested().toString(dateTimeFormatString);
			case 3: return record.getDataName();
			case 4:
				DateTime rangeMin = record.getFilterDateStart();
				DateTime rangeMax = record.getFilterDateEnd();
				if(rangeMin != null && rangeMax != null){
					return rangeMin.toString(dateFormatString) + " - " + rangeMax.toString(dateFormatString);
				} else {
					return "N/A";
				}
			case 5: return record.getCaseName();
			default: return "???";
			}
		} catch (Exception e) {
			logger.error("Error while retrieving display value for user at ("+row+","+col+")");
			logger.error(e);
			return "Error";
		}
	}
	
	@Override
	public int getColumnCount() {
		return headers.length;
	}

	@Override
	public int getRowCount() {
		return records.size();
	}

	@Override
	public String getColumnName(int col) {
		return headers[col];
	}

	public void setRecords(List<IngestionHistoryEntry> records){
		this.records = records;
		fireTableDataChanged();
	}
}
