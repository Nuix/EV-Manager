package com.nuix.evmanager.controls;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.table.AbstractTableModel;

import com.nuix.evmanager.data.VaultStore;

@SuppressWarnings("serial")
public class VaultStoreTableModel extends AbstractTableModel {
	private String[] headersWithArchives = new String[]{
		" ",
		"Vault Store Name",
		"Vault Store Description",
		"EV Server",
		"Selected Archives"
	};
	
	private String[] headersWithoutArchives = new String[]{
			" ",
			"Vault Store Name",
			"Vault Store Description",
			"EV Server"
		};
	
	@SuppressWarnings("unused")
	private VaultArchiveTableModel associatedArchiveTableModel = null;
	private List<VaultStore> stores = new ArrayList<VaultStore>();
	private List<Boolean> checkedStores = new ArrayList<Boolean>();
	private List<CheckedOptionsChangedListener> checkListeners = new ArrayList<CheckedOptionsChangedListener>();
	private Map<String,Integer> checkedArchiveCountsByEntryID = new HashMap<String,Integer>();
	
	private boolean showArchiveCounts = false;
	
	private String[] getHeaders(){
		if(showArchiveCounts)
			return headersWithArchives;
		else
			return headersWithoutArchives;
	}
	
	// Need to connect to archive table model to retrieve checked counts for each store
	public VaultStoreTableModel(VaultArchiveTableModel associatedArchiveTableModel){
		super();
		this.associatedArchiveTableModel = associatedArchiveTableModel;
		associatedArchiveTableModel.addCheckedOptionsChangedListener(new CheckedOptionsChangedListener() {
			
			@Override
			public void checkedOptionsChanged() {
				// Retrieve checked archives per store counts when checked archives changes
				checkedArchiveCountsByEntryID = associatedArchiveTableModel.getCheckedCountsByStoreEntryID();
				if(showArchiveCounts){
					for (int row = 0; row < stores.size(); row++) {
						fireTableCellUpdated(row, 4);
					}	
				}
			}
		});
	}
	
	@Override
	public int getColumnCount() {
		return getHeaders().length;
	}

	@Override
	public int getRowCount() {
		if(stores != null){
			return stores.size();	
		} else {
			return 0;
		}
	}

	@Override
	public Object getValueAt(int row, int col) {
		VaultStore store = stores.get(row);
		switch (col) {
		case 0: return checkedStores.get(row);
		case 1: return store.getName();
		case 2: return store.getDescription();
		case 3: return store.getEvApiServer();
		case 4: return checkedArchiveCountsByEntryID.getOrDefault(store.getStoreEntryID(),0);
		default: return "";
		}
	}
	
	@Override
	public String getColumnName(int col) {
		return getHeaders()[col];
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return columnIndex == 0;
	}

	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		if(columnIndex == 0){
			checkedStores.set(rowIndex, (Boolean)aValue);
			notifyCheckListeners();
		}
	}

	@Override
	public Class<?> getColumnClass(int column) {
		if (column == 0){
			return Boolean.class;
		} else if(column == 3){
			return Integer.class;
		} else {
			return String.class;
		}
	}

	public void setStores(List<VaultStore> stores){
		this.stores = stores;
		checkedStores.clear();
		if(this.stores != null){
			for (int i = 0; i < this.stores.size(); i++) {
				checkedStores.add(false);
			}
		}
		this.fireTableDataChanged();
	}
	
	public VaultStore get(int row){
		if(stores != null){
			return stores.get(row);	
		} else {
			return null;
		}
	}
	
	public void addCheckedOptionsChangedListener(CheckedOptionsChangedListener listener){
		checkListeners.add(listener);
	}
	
	public void removeCheckedOptionsChangedListener(CheckedOptionsChangedListener listener){
		checkListeners.remove(listener);
	}
	
	private void notifyCheckListeners(){
		for(CheckedOptionsChangedListener listener : checkListeners){
			listener.checkedOptionsChanged();
		}
	}
	
	public List<VaultStore> getCheckedStores(){
		List<VaultStore> result = new ArrayList<VaultStore>();
		for (int i = 0; i < stores.size(); i++) {
			if(checkedStores.get(i) == true){
				result.add(stores.get(i));
			}
		}
		return result;
	}
	
	public int getCheckedStoreCount(){
		int count = 0;
		for (int i = 0; i < stores.size(); i++) {
			if(checkedStores.get(i) == true){
				count++;
			}
		}
		return count;
	}
	
	public List<VaultStore> getSelectedStoresWithoutSelectedArchives() {
		List<VaultStore> result = new ArrayList<VaultStore>();
		for(VaultStore store : getCheckedStores()){
			int checkedArchives = checkedArchiveCountsByEntryID.getOrDefault(store.getStoreEntryID(),0);
			if(checkedArchives < 1){
				result.add(store);
			}
		}
		return result;
	}

	public boolean getShowArchiveCounts() {
		return showArchiveCounts;
	}

	public void setShowArchiveCounts(boolean showArchiveCounts) {
		this.showArchiveCounts = showArchiveCounts;
		fireTableStructureChanged();
	}
	
}
