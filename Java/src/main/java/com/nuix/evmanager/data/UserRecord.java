package com.nuix.evmanager.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/***
 * Encapsulates information regarding a user
 * @author Jason Wells
 *
 */
public class UserRecord {
	protected int databaseID = 0;
	protected String employeeId;
	protected String name;
	protected String title;
	protected String department;
	protected String location;
	protected List<UserAddress> addresses;
	protected List<String> phoneNumbers;
	protected List<String> sids;
		
	public UserRecord(){
		name = "";
		title = "";
		department = "";
		employeeId = "";
		location = "";
		addresses = null;
		phoneNumbers = null;
	}
	
	public int getDatabaseID() {
		return databaseID;
	}
	public void setDatabaseID(int databaseID) {
		this.databaseID = databaseID;
	}
		
	public String getTitle() {
		return title;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getDepartment() {
		return department;
	}
	
	public void setDepartment(String department) {
		this.department = department;
	}
	
	public String getEmployeeID() {
		return employeeId;
	}
	
	public void setEmployeeID(String employeeId) {
		this.employeeId = employeeId;
	}
	
	public String getLocation() {
		return location;
	}
	
	public void setLocation(String location) {
		this.location = location;
	}
	
	public List<UserAddress> getAddresses() throws Exception {
		return getAddresses(false);
	}
	
	public List<UserAddress> getAddresses(boolean refresh) throws Exception {
		return addresses;
	}
	
	public static String getDelimitedAddressesString(Collection<SQLUserRecord> users) throws Exception{
		List<String> addressesForNuix = new ArrayList<String>();
		for (SQLUserRecord user : users) {
			addressesForNuix.addAll(user.getAddresses()
			.stream()
			.map(a -> a.getAddress().replace(";", ";;").replace(" ","."))
			.sorted()
			.collect(Collectors.toList()));
		}
				 
		return String.join(" ", addressesForNuix);
	}
	
	public void setAddresses(List<UserAddress> addresses) {
		this.addresses = addresses;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<String> getPhoneNumbers() throws Exception {
		return phoneNumbers;
	}

	public void setPhoneNumbers(List<String> phoneNumbers) {
		this.phoneNumbers = phoneNumbers;
	}

	public List<String> getSIDs() throws Exception {
		return sids;
	}

	public void setSIDs(List<String> sids) {
		this.sids = sids;
	}
	
	public void recordCustodianIngestionEvent(String caseName, String caseLocation, String dataName) throws Exception{
	}
	
	public IngestionHistoryEntry getLastIngestionHistoryEntry() throws Exception{
		return null;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + databaseID;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		UserRecord other = (UserRecord) obj;
		if (databaseID != other.databaseID)
			return false;
		return true;
	}
}
