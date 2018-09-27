package com.nuix.evmanager.data;

import org.joda.time.DateTime;

/***
 * Encapsulates information regarding an address associated to a user
 * @author Jason Wells
 *
 */
public class UserAddress {
	private int databaseID;
	private SQLUserRecord associatedUserRecord;
	private DateTime recordCreated;
	private DateTime recordLastModified;
	private String address;
	
	public UserAddress(){}
	public UserAddress(String emailAddress){
		this.address = emailAddress.trim();
	}
	
	public int getDatabaseID() {
		return databaseID;
	}
	public void setDatabaseID(int databaseID) {
		this.databaseID = databaseID;
	}
	public DateTime getRecordCreated() {
		return recordCreated;
	}
	public void setRecordCreated(DateTime recordCreated) {
		this.recordCreated = recordCreated;
	}
	public DateTime getRecordLastModified() {
		return recordLastModified;
	}
	public void setRecordLastModified(DateTime recordLastModified) {
		this.recordLastModified = recordLastModified;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	
	public void setAssociatedUserRecord(SQLUserRecord record){
		associatedUserRecord = record;
	}
	
	public SQLUserRecord getAssociatedUserRecord(){
		return associatedUserRecord;
	}
}
