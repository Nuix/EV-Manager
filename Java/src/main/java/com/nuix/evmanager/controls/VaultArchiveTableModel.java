package com.nuix.evmanager.controls;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.swing.table.AbstractTableModel;

import org.apache.log4j.Logger;

import com.nuix.evmanager.data.VaultArchive;
import com.nuix.evmanager.data.VaultArchiveResult;

@SuppressWarnings("serial")
public class VaultArchiveTableModel extends AbstractTableModel {
	private static Logger logger = Logger.getLogger(VaultArchiveTableModel.class);
			
	String[] headers = new String[]{
		"",
		"Vault Archive Name",
		"Vault Archive Description",
		"Parent Vault Store Name",
	};
	
	private VaultArchiveResult records = null;
	private Map<Integer,VaultArchive> checkedRecords = new HashMap<Integer,VaultArchive>();
	private List<CheckedOptionsChangedListener> checkListeners = new ArrayList<CheckedOptionsChangedListener>();
	
	@Override
	public int getColumnCount() {
		return headers.length;
	}

	@Override
	public int getRowCount() {
		if(records == null){
			return 0;
		} else {
			try {
				return records.size();
			} catch (Exception e) {
				logger.error("Error while getting row count",e);
				e.printStackTrace();
				return 0;
			}
		}
	}

	@Override
	public Object getValueAt(int row, int col) {
		VaultArchive archive;
		try {
			archive = records.get(row);
		} catch (Exception e) {
			logger.error("Error while getting value at "+row+", "+col,e);
			e.printStackTrace();
			return e.getMessage();
		}
		
		try {
			switch (col) {
			case 0:
				return checkedRecords.containsKey(archive.getRootIdentity());
			case 1:
				return archive.getArchiveName();
			case 2:
				return archive.getArchiveDescription();
			case 3:
				return archive.getEvStore().getName();
			default:
				return "Unknown column";
			}
		} catch (Exception e) {
			logger.error("Error while getting value at "+row+", "+col,e);
			e.printStackTrace();
			return e.getMessage();
		}
	}
	
	@Override
	public void setValueAt(Object aValue, int row, int col) {
		if(col == 0){
			VaultArchive archive;
			try {
				archive = records.get(row);
				boolean value = (Boolean)aValue;
				if(value == true){
					checkedRecords.put(archive.getRootIdentity(),archive);
					notifyCheckListeners();
				} else {
					checkedRecords.remove(archive.getRootIdentity());
					notifyCheckListeners();
				}
			} catch (Exception e) {
				logger.error("Error while setting value at "+row+", "+col,e);
				e.printStackTrace();
			}
		}
	}

	@Override
	public String getColumnName(int col) {
		return headers[col];
	}

	@Override
	public Class<?> getColumnClass(int column) {
		if(column == 0){
			return Boolean.class;
		} else {
			return String.class;
		}
	}

	@Override
	public boolean isCellEditable(int row, int col) {
		return col == 0;
	}
	
	public List<VaultArchive> getCheckedArchives(){
		List<VaultArchive> result = new ArrayList<VaultArchive>();
		result.addAll(checkedRecords.values());
		return result;
	}
	
	public int getCheckedArchiveCount(){
		return checkedRecords.size();
	}
	
	public Map<String,Integer> getCheckedCountsByStoreEntryID(){
		Map<String,Integer> result = new HashMap<String,Integer>();
		for(VaultArchive checkedArchive : getCheckedArchives()){
			String entryID = checkedArchive.getStoreEntryID();
			int currentValue = result.getOrDefault(entryID, 0);
			currentValue++;
			result.put(entryID,currentValue);
		}
		return result;
	}
	
	public void addCheckedOptionsChangedListener(CheckedOptionsChangedListener listener){
		checkListeners.add(listener);
	}
	
	public void removeCheckedOptionsChangedListener(CheckedOptionsChangedListener listener){
		checkListeners.remove(listener);
	}
	
	private void notifyCheckListeners(){
		logger.info("Notifying listeners of checked state change...");
		for(CheckedOptionsChangedListener listener : checkListeners){
			try {
				listener.checkedOptionsChanged();
			} catch (Exception e) {
				logger.error("Error while notify CheckedOptionsChangedListener",e);
				e.printStackTrace();
			}
		}
	}

	public VaultArchive get(int row) throws Exception{
		if(records != null){
			return records.get(row);
		} else {
			return null;
		}
	}
	
	public void setArchives(VaultArchiveResult result){
		// Shut down previous background fetching virtualized result
		if(records != null){
			try {
				records.close();
			} catch (IOException e) {
				logger.error("Error while closing previous archive result set",e);
				e.printStackTrace();
			}
		}
		
		records = result;
		
		// We need to make sure we are only tracking checked state
		// of archives which correspond to selected vault stores!
		try {
			if(records == null){
				checkedRecords.clear();
			} else {
				List<Integer> toRemove = new ArrayList<Integer>();
				Set<String> entryIDs = records.getStores().stream().map(s -> s.getStoreEntryID()).collect(Collectors.toSet());
				for(Map.Entry<Integer,VaultArchive> entry : checkedRecords.entrySet()){
					int id = entry.getKey();
					VaultArchive archive = entry.getValue();
					if(!entryIDs.contains(archive.getStoreEntryID())){
						toRemove.add(id);
					}
				}
				for(Integer i : toRemove){
					checkedRecords.remove(i);
				}
			}
		} catch (Exception e) {
			logger.error("Error while preparing VaultArchiveTableModel with new archives",e);
			e.printStackTrace();
		}
		notifyCheckListeners();
		
		
		try {
			fireTableDataChanged();
		} catch (Exception e) {
			logger.error("Error while notify table of changed data",e);
			e.printStackTrace();
		}
	}
}
