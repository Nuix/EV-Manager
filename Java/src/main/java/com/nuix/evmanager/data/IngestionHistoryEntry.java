package com.nuix.evmanager.data;

import org.joda.time.DateTime;

public class IngestionHistoryEntry implements Comparable<IngestionHistoryEntry> {
	private DateTime dateIngested = null;
	private String caseName = null;
	private String caseLocation = null;
	
	private String dataName = "Unknown";
	private DateTime filterDateStart = null;
	private DateTime filterDateEnd = null;
	private SQLUserRecord associatedUser = null;
	
	public DateTime getDateIngested() {
		return dateIngested;
	}
	public void setDateIngested(DateTime dateIngested) {
		this.dateIngested = dateIngested;
	}
	public String getCaseName() {
		return caseName;
	}
	public void setCaseName(String caseName) {
		this.caseName = caseName;
	}
	public String getCaseLocation() {
		return caseLocation;
	}
	public void setCaseLocation(String caseLocation) {
		this.caseLocation = caseLocation;
	}
	public String getDataName() {
		return dataName;
	}
	public void setDataName(String dataName) {
		this.dataName = dataName;
	}
	public DateTime getFilterDateStart() {
		return filterDateStart;
	}
	public void setFilterDateStart(DateTime filterDateStart) {
		this.filterDateStart = filterDateStart;
	}
	public DateTime getFilterDateEnd() {
		return filterDateEnd;
	}
	public void setFilterDateEnd(DateTime filterDateEnd) {
		this.filterDateEnd = filterDateEnd;
	}
	public SQLUserRecord getAssociatedUser() {
		return associatedUser;
	}
	public void setAssociatedUser(SQLUserRecord associatedUser) {
		this.associatedUser = associatedUser;
	}
	
	@Override
	public int compareTo(IngestionHistoryEntry other) {
		return -1 * getDateIngested().compareTo(other.getDateIngested());
	}
}
