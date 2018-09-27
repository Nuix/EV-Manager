package com.nuix.evmanager.data;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.function.Consumer;

/***
 * Encapsulates a set of user record results in response to a query against the address book database.  Provides methods
 * to page through chunks of user record results.
 * @author Jason Wells
 *
 */
public class SQLUserRecordResult extends VirtualizedRecordCollection<SQLUserRecord> {
	private SQLUserRecordStore store;
	private String whereClause;
	private List<Object> bindData;
	
	private Integer resultSetSize = null;
	
	/***
	 * Creates a new instance
	 * @param store The record store instance data will be obtained from
	 * @param whereClause The where clause used in queries to the database
	 * @param bindData A list of values which will be bound to query 
	 */
	public SQLUserRecordResult(SQLUserRecordStore store, String whereClause, List<Object> bindData){
		super();
		this.whereClause = whereClause;
		this.store = store;
		this.bindData = bindData;
	}
	
	/***
	 * Gets the number of resulting user records represented by this instance.
	 * @return The number of records represented by this instance
	 */
	@Override
	public int size() throws Exception{
		if(resultSetSize == null){
			List<LinkedHashMap<String,Object>> records = store.runQuery("SELECT COUNT(1) AS Count FROM UserRecord "+whereClause, bindData);
			resultSetSize = (Integer)records.get(0).get("Count");
		}
		return resultSetSize;
	}
	
	/***
	 * Loads the specified page of user record results from the database.
	 * @param pageNumber The page number to load.  Invalid page numbers are ignored.
	 */
	@Override
	protected void loadPage(int pageNumber) throws Exception{
		if(!isValidPage(pageNumber)) return;
		
		if(!pages.containsKey(pageNumber)){
			List<SQLUserRecord> page = new ArrayList<SQLUserRecord>();
			List<Object> pagingBindData = new ArrayList<Object>(bindData);
			pagingBindData.add((pageNumber * pageSize)+1);
			pagingBindData.add((pageNumber * pageSize)+1+pageSize);
			
			// Materialize page from database
			// This query should work with MSSQL 2008 and up
			String sql = ";WITH Results_CTE AS (\n"+
				"SELECT *,\n"+
				"ROW_NUMBER() OVER (ORDER BY EmployeeID) as RowNum\n"+
				"FROM UserRecord\n"+
				whereClause+"\n)\n"+
				"SELECT * FROM Results_CTE WHERE RowNum >= ? AND RowNum < ?";
			
			//Materialize page from database
			store.runQuery(sql,
					pagingBindData, new Consumer<LinkedHashMap<String,Object>>(){
				@Override
				public void accept(LinkedHashMap<String, Object> recordData) {
					SQLUserRecord record = new SQLUserRecord();
					record.setStore(SQLUserRecordResult.this.store);
					record.setDatabaseID((Integer)recordData.get("ID"));
					record.setName((String)recordData.get("Name"));
					record.setDepartment((String)recordData.get("Department"));
					record.setLocation((String)recordData.get("Location"));
					record.setTitle((String)recordData.get("Title"));
					record.setEmployeeID((String)recordData.get("EmployeeID"));
					page.add(record);
				}
			});
			pages.put(pageNumber,page);
		}
	}

	/***
	 * Gets the SQL where clause used by this instance.
	 * @return The SQL qhere clause used by this instance.
	 */
	public String getWhereClause() {
		return whereClause;
	}
}
