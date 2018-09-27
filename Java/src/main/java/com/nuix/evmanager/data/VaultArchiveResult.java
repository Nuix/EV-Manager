package com.nuix.evmanager.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import org.apache.log4j.Logger;

/***
 * Encapsulates the results of obtaining vault archives.
 * @author Jason Wells
 *
 */
public class VaultArchiveResult extends VirtualizedRecordCollection<VaultArchive> {

	private static Logger logger = Logger.getLogger(VaultArchiveResult.class);
	
	private VaultServer server;
	private Map<String,VaultStore> storesByEntryId;
	private String whereClause;
	private List<Object> bindData;
	private Integer resultSetSize = null;
	private List<VaultStore> stores;
	
	/***
	 * Creates a new instance
	 * @param server The vault server
	 * @param stores A list of vault stores
	 * @param whereClause Where clause used when querying the database
	 * @param bindData The data to bind to the parameters in the queries submit to the database
	 */
	public VaultArchiveResult(VaultServer server, List<VaultStore> stores, String whereClause, List<Object> bindData){
		super();
		this.stores = stores;
		storesByEntryId = new HashMap<String,VaultStore>();
		for(VaultStore store : stores){
			storesByEntryId.put(store.getStoreEntryID(),store);
		}
		this.server = server;
		this.whereClause = whereClause;
		this.bindData = bindData;
		pageSize = 200;
		prefetchedPages = 4;
	}
	
	/***
	 * Creates a new instance
	 * @param server The vault server
	 * @param stores A vault store
	 * @param whereClause Where clause used when querying the database
	 * @param bindData The data to bind to the parameters in the queries submit to the database
	 */
	public VaultArchiveResult(VaultServer server, VaultStore store, String whereClause, List<Object> bindData){
		super();
		this.stores = new ArrayList<VaultStore>();
		this.stores.add(store);
		storesByEntryId = new HashMap<String,VaultStore>();
		storesByEntryId.put(store.getStoreEntryID(),store);
		this.server = server;
		this.whereClause = whereClause;
		this.bindData = bindData;
		pageSize = 200;
		prefetchedPages = 4;
	}
	
	/***
	 * The number of results associated to this instance.
	 * @return The number of results represented by this instance.
	 */
	@Override
	public int size() throws Exception {
		if(resultSetSize == null){
			logger.info("Determining result size...");
			String sql = "SELECT COUNT(1) AS Count FROM dbo.Archive "+whereClause;
			List<LinkedHashMap<String,Object>> records = server.runQuery(sql, bindData);
			resultSetSize = (Integer)records.get(0).get("Count");
			logger.info("Result size determined to be: "+resultSetSize);
		}
		return resultSetSize;
	}

	/***
	 * Fetches the specified page of results.  Invalid page numbers are ignored.
	 * @param pageNumber The page number to fetch, invalid page numbers are ignored
	 */
	@Override
	protected void loadPage(int pageNumber) throws Exception {
		if(!isValidPage(pageNumber)) return;
		
		if(!pages.containsKey(pageNumber)){
			List<VaultArchive> page = new ArrayList<VaultArchive>();
			List<Object> pagingBindData = new ArrayList<Object>(bindData);
			pagingBindData.add((pageNumber * pageSize)+1);
			pagingBindData.add((pageNumber * pageSize)+1+pageSize);
			
			logger.info("Fetching page number: "+pageNumber);
			
			// Materialize page from database
			// This query should work with MSSQL 2008 and up
			String sql = 
				";WITH Results_CTE AS (\n"+
				"SELECT Root.RootIdentity, ArchiveName, ArchiveDescription, VaultStoreEntryId, Root.VaultEntryId,\n"+
				"ROW_NUMBER() OVER (ORDER BY ArchiveName) AS RowNum\n"+
				"FROM Archive\n"+
				"INNER JOIN Root ON Root.RootIdentity = Archive.RootIdentity\n"+
				whereClause + "\n)\n"+
				"SELECT * FROM Results_CTE WHERE RowNum >= ? AND RowNum < ?";
			
			//logger.info("Query: "+sql);
			
			server.runQuery(sql, pagingBindData, new Consumer<LinkedHashMap<String,Object>>(){
				@Override
				public void accept(LinkedHashMap<String, Object> recordData) {
					try {
						int databaseID = (Integer)recordData.get("RootIdentity");
						String archiveName = (String)recordData.get("ArchiveName");
						String archiveDescription = (String)recordData.get("ArchiveDescription");
						String storeEntryId = (String)recordData.get("VaultStoreEntryId");
						String vaultEntryId = (String)recordData.get("VaultEntryId");
						VaultStore store = storesByEntryId.get(storeEntryId);
						VaultArchive archive = VaultArchive.createEVArchive(databaseID,server, store, archiveName, archiveDescription,
								storeEntryId, vaultEntryId);
						page.add(archive);
					} catch (Exception e) {
						logger.error("Error loading page "+pageNumber+" for VaultArchiveResult",e);
						e.printStackTrace();
					}
				}
			});
			pages.put(pageNumber,page);
		}
	}

	/***
	 * Gets the list of associated vault stores.
	 * @return The list of associated vault stores.
	 */
	public List<VaultStore> getStores() {
		return stores;
	}

}
