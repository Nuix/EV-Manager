package com.nuix.evmanager.data;

import java.util.List;

/***
 * Interface representing a source of user records
 * @author Jason Wells
 *
 */
public interface UserRecordStore {
	public VirtualizedRecordCollection<SQLUserRecord> findRecords(UserRecordCriteria criteria) throws Exception;
	public List<String> getAllLocations() throws Exception;
	public List<String> getAllTitles() throws Exception;
	public List<String> getAllDepartments() throws Exception;
	public void processAddressChangeOrder(AddressChangeOrder changeOrder) throws Exception;
	public boolean canConnect();
}
