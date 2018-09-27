package com.nuix.evmanager.data;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;

/***
 * This class encapsulates a user record in the address book database.  Many values are lazily loaded from the database and cached as
 * they are requested through the various get methods.
 * @author Jason Wells
 *
 */
public class SQLUserRecord extends UserRecord {
	
	private static Logger logger = Logger.getLogger(SQLUserRecord.class);
	
	private SQLUserRecordStore store;
	
	/***
	 * Gets the associated {@link com.nuix.evmanager.data.SQLUserRecordStore} used by this instance to interact with the address book database.
	 * @return The associated {@link com.nuix.evmanager.data.SQLUserRecordStore}
	 */
	public SQLUserRecordStore getStore() {
		return store;
	}
	
	/***
	 * Sets the associated {@link com.nuix.evmanager.data.SQLUserRecordStore} used by this instance to interact with the address book database.
	 * @param store The {@link com.nuix.evmanager.data.SQLUserRecordStore} to associate with this instance
	 */
	public void setStore(SQLUserRecordStore store) {
		this.store = store;
	}
	
	/***
	 * Gets a list of addresses associated with this user.  Same as calling {@link #getAddresses(boolean)} with a value of false.
	 * @return List of addresses associated with this instance 
	 */
	@Override
	public List<UserAddress> getAddresses() throws Exception {
		return getAddresses(false);
	}
	
	/***
	 * Gets a list of addresses associated with this user.  If that list of addresses has not already been obtained and cached, this method will
	 * reach out to the database and obtain that information.  If the data has been previously obtained and cached, depending on the value of
	 * the parameter provided, this method may discard the cached information and obtain fresh data from the database.
	 * @param refresh If a value of true is provided, the data will be refreshed from the database regardless.  A value of false, the data will only be obtained again if needed.
	 */
	@Override
	public List<UserAddress> getAddresses(boolean refresh) throws Exception {
		if(addresses == null || refresh == true){
			try {
				addresses = new ArrayList<UserAddress>();
				List<Object> bindData = new ArrayList<Object>();
				bindData.add(databaseID);
				store.runQuery("SELECT ID,RecordCreated,RecordLastModified,Address FROM UserAddress WHERE UserRecordID = ?", 
						bindData, new Consumer<LinkedHashMap<String,Object>>(){

							@Override
							public void accept(LinkedHashMap<String, Object> record) {
								UserAddress address = new UserAddress();
								address.setAssociatedUserRecord(SQLUserRecord.this);
								address.setDatabaseID((Integer)record.get("ID"));
								address.setAddress((String)record.get("Address"));
								addresses.add(address);
							}
					
				});
			} catch (Exception e) {
				String message = "Error while retrieving addresses for user record";
				logger.error(message,e);
				throw new Exception(message,e);
			}
		}
		return addresses;
	}
	
	/***
	 * Gets list of phone numbers associated with this user.  Data is lazily obtained, meaning the first time this method is called the data is obtained from
	 * the database if necessary, on subsequent calls cached results are returned.
	 * @return List of associated phone numbers 
	 */
	@Override
	public List<String> getPhoneNumbers() throws Exception {
		try {
			if(phoneNumbers == null){
				List<Object> bindData = new ArrayList<Object>();
				bindData.add(databaseID);
				phoneNumbers = store.runQuery("SELECT PhoneNumber FROM UserPhoneNumber WHERE UserRecordID = ?", bindData)
					.stream().map(r -> (String)r.get("PhoneNumber")).collect(Collectors.toList());
			}
		} catch (Exception e) {
			String message = "Error while retrieving phone numbers for user record";
			logger.error(message,e);
			throw new Exception(message,e);
		}
		return phoneNumbers;
	}
	
	/***
	 * Gets list of SIDs associated with this user.  Data is lazily obtained, meaning the first time this method is called the data is obtained from
	 * the database if necessary, on subsequent calls cached results are returned.
	 * @return List of associated SIDs 
	 */
	@Override
	public List<String> getSIDs() throws Exception{
		try {
			if(sids == null){
				List<Object> bindData = new ArrayList<Object>();
				bindData.add(databaseID);
				sids = store.runQuery("SELECT SID FROM UserSID WHERE UserRecordID = ?", bindData)
					.stream().map(r -> (String)r.get("SID")).collect(Collectors.toList());
			}
		} catch (Exception e) {
			String message = "Error while retrieving SIDs for user record";
			logger.error(message,e);
			throw new Exception(message,e);
		}
		return sids;
	}
	
	/***
	 * Records an ingestion event in the database associated with this user.
	 * @param caseName The case name to associate
	 * @param caseLocation The case location to associated
	 * @param dataName A name to associate
	 */
	@Override
	public void recordCustodianIngestionEvent(String caseName, String caseLocation, String dataName) throws Exception{
		store.recordCustodianIngestionEvent(this, caseName, caseLocation, dataName);
	}
	
	/***
	 * Obtains a list of {@link IngestionHistoryEntry} records associated with this user.
	 * @return List of ingestion histor event records associated with this user
	 * @throws Exception Thrown most likely if there are database related exceptions
	 */
	public List<IngestionHistoryEntry> getAllIngestionHistoryEntries() throws Exception{
		List<IngestionHistoryEntry> result = new ArrayList<IngestionHistoryEntry>();
		List<Object> bindData = new ArrayList<Object>();
		bindData.add(databaseID);
		String query = "SELECT * FROM IngestionHistory WHERE UserRecordID = ? ORDER BY DateIngested DESC";
		List<LinkedHashMap<String,Object>> queryResults = store.runQuery(query, bindData);
		for (LinkedHashMap<String, Object> queryResult : queryResults) {
			IngestionHistoryEntry historyEntry = new IngestionHistoryEntry();
			historyEntry.setAssociatedUser(this);
			historyEntry.setCaseLocation((String)queryResult.get("CaseLocation"));
			historyEntry.setCaseName((String)queryResult.get("CaseName"));
			historyEntry.setDateIngested(new DateTime((Timestamp)queryResult.get("DateIngested")));
			
			if(queryResult.containsKey("DataName")){
				historyEntry.setDataName((String)queryResult.get("DataName"));
			} else {
				historyEntry.setDataName("N/A");
			}
			
			if(queryResult.containsKey("DateRangeMin") && queryResult.containsKey("DateRangeMax")){
				Object minValue = queryResult.get("DateRangeMin");
				Object maxValue = queryResult.get("DateRangeMax");
				if(minValue != null && maxValue != null){
					historyEntry.setFilterDateStart(new DateTime((Timestamp)minValue));
					historyEntry.setFilterDateEnd(new DateTime((Timestamp)maxValue));
				}
			}
			
			result.add(historyEntry);
		}
		return result;
	}
	
	/***
	 * Gets the latest Date range max value, based on ingestion history events associated with this user
	 * @return Latest max date range value for this user, based on ingestion history events associated to this user
	 * @throws Exception Thrown most likely if there are database related exceptions
	 */
	public DateTime getLatestDateRangeMax() throws Exception{
		List<Object> bindData = new ArrayList<Object>();
		bindData.add(databaseID);
		String query = "SELECT TOP 1 DateRangeMax FROM IngestionHistory WHERE UserRecordID = ? ORDER BY DateRangeMax DESC";
		List<LinkedHashMap<String,Object>> results = store.runQuery(query, bindData);
		if(results.size() > 0){
			LinkedHashMap<String,Object> record = results.get(0);
			Object dateRangeMax = record.get("DateRangeMax");
			if(dateRangeMax != null){
				return new DateTime((Timestamp)dateRangeMax);	
			} else {
				return null;
			}
		} else {
			return null;
		}
	}
}
