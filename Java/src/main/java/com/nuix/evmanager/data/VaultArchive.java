package com.nuix.evmanager.data;

/***
 * Encapsulates information about a vault archive
 * @author Jason Wells
 *
 */
public class VaultArchive {
	private int rootIdentity;
	private String archiveName;
	private String archiveDescription;
	private String storeEntryID;
	private String vaultEntryID;
	
	private VaultStore evStore;
	private VaultServer server;
	
	private VaultArchive(){}
	
	public static VaultArchive createEVArchive(int databaseID, VaultServer server, VaultStore store, String archiveName,
			String archiveDescription, String storeEntryID, String vaultEntryID){
		VaultArchive result = new VaultArchive();
		result.rootIdentity = databaseID;
		result.evStore = store;
		result.server = server;
		result.archiveName = archiveName;
		result.archiveDescription = archiveDescription;
		result.storeEntryID = storeEntryID;
		result.vaultEntryID = vaultEntryID;
		return result;
	}

	public String getArchiveName() {
		return archiveName;
	}

	public String getArchiveDescription() {
		return archiveDescription;
	}

	public String getStoreEntryID() {
		return storeEntryID;
	}

	public VaultStore getEvStore() {
		return evStore;
	}

	public VaultServer getServer() {
		return server;
	}

	public int getRootIdentity() {
		return rootIdentity;
	}

	public String getVaultEntryID() {
		return vaultEntryID;
	}
}
