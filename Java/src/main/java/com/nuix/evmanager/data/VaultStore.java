package com.nuix.evmanager.data;

/***
 * Encapsulates information regarding a vault store
 * @author Jason Wells
 *
 */
public class VaultStore {
	private String name;
	private String description;
	private String storeEntryID;
	private int storeIdentity;
	private VaultServer server;
	private String evApiServer = null;
	
	private VaultStore(){}
	
	public static VaultStore createEVStore(VaultServer server, String name, String description,
			String storeEntryID, int storeIdentity, String evApiServer){
		VaultStore result = new VaultStore();
		result.server = server;
		result.name = name;
		result.description = description;
		result.storeEntryID = storeEntryID;
		result.storeIdentity = storeIdentity;
		result.evApiServer = evApiServer;
		return result;
	}
	
	public VaultArchiveResult getArchives() throws Exception{
		return server.getArchives(this);
	}
	
	public VaultArchiveResult getArchives(VaultArchiveCriteria criteria) throws Exception{
		return server.getArchives(this,criteria);
	}
	
	public String getName() {
		return name;
	}
	
	public String getDescription() {
		return description;
	}
	
	public String getStoreEntryID() {
		return storeEntryID;
	}
	
	public int getStoreIdentity() {
		return storeIdentity;
	}

	public VaultServer getServer() {
		return server;
	}
	
	public String getEvApiServer(){
		return evApiServer;
	}
}
