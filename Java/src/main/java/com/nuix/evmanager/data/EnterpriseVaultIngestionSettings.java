package com.nuix.evmanager.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;

import nuix.EvidenceContainer;
import nuix.Processor;

/***
 * A class representing the collection of settings which will be used to ingest the EV data.
 * @author Jason Wells
 *
 */
public class EnterpriseVaultIngestionSettings {
	private static Logger logger = Logger.getLogger(EnterpriseVaultIngestionSettings.class);
	private static boolean includeStoreWithArchive = true;
	public static void setIncludeStoreWithArchive(boolean value){ includeStoreWithArchive = value; }
	
	private EvidenceSettings evidenceSettings;
	private Map<String,Object> processingSettings;
	private Map<String,Object> parallelProcessingSettings;
	private List<MimeTypeSetting> mimeTypeSettings = new ArrayList<MimeTypeSetting>();
	
	private List<VaultStore> vaultStores = null;
	private List<VaultArchive> vaultArchives = null;
	private List<SQLUserRecord> custodians = null;
	private DateTime fromDate = null;
	private DateTime toDate = null;
	private String keywords = null;
	private String flag = null;
	
	private String workerTimeoutProperty = "nuix.processing.worker.timeout";
	private String originalWorkerTimeoutValue = null;

	/***
	 * Gets the list of associated vault store objects
	 * @return A list of the associated vault store objects
	 */
	public List<VaultStore> getVaultStores() {
		return vaultStores;
	}
	
	/***
	 * Sets the associated vault store objects
	 * @param vaultStores The new list of vault store objects to associate with this instance
	 */
	public void setVaultStores(List<VaultStore> vaultStores) {
		this.vaultStores = vaultStores;
	}
	
	/***
	 * Gets the list of associated vault archive objects
	 * @return A list of the associated vault archive objects
	 */
	public List<VaultArchive> getVaultArchives() {
		return vaultArchives;
	}
	
	/***
	 * Sets the vault stored objects associated with this instance
	 * @param vaultArchives The new list of vault store objects to associate with this instance
	 */
	public void setVaultArchives(List<VaultArchive> vaultArchives) {
		this.vaultArchives = vaultArchives;
	}
	
	/***
	 * Gets the list of user records associated with this instance
	 * @return The list of user records associated with this instance
	 */
	public List<SQLUserRecord> getCustodianRecords() {
		return custodians;
	}
	
	/***
	 * Sets the list of user records associated with this instance
	 * @param custodians The new list of user records to associate with this instance
	 */
	public void setCustodianRecords(List<SQLUserRecord> custodians) {
		this.custodians = custodians;
	}
	
	/***
	 * Gets the "from" portion of the date range
	 * @return The "from" portion of the date range
	 */
	public DateTime getFromDate() {
		return fromDate;
	}
	
	/***
	 * Sets the "from" portion of the date range
	 * @param fromDate The new "from" portion of the date range
	 */
	public void setFromDate(DateTime fromDate) {
		this.fromDate = fromDate;
	}
	
	/***
	 * Gets the "to" portion of the date range
	 * @return The "to" portion of the date range
	 */
	public DateTime getToDate() {
		return toDate;
	}
	
	/***
	 * Sets the "to" portion of the date range
	 * @param toDate The new "to" portion of the date range
	 */
	public void setToDate(DateTime toDate) {
		this.toDate = toDate;
	}
	
	/***
	 * Gets the associated filtering keywords string
	 * @return The associated filtering keywords string
	 */
	public String getKeywords() {
		return keywords;
	}
	
	/***
	 * Sets the associated filtering keywords string
	 * @param keywords The new filtering keywords string
	 */
	public void setKeywords(String keywords) {
		this.keywords = keywords;
	}
	
	/***
	 * Gets the associated flag string
	 * @return The associated flag string
	 */
	public String getFlag() {
		return flag;
	}
	
	/***
	 * Sets the associated flag string
	 * @param flag The new flag string
	 */
	public void setFlag(String flag) {
		this.flag = flag;
	}
	
	/***
	 * Gets the evidence settings object representing how the Nuix evidence container will be created
	 * @return The Nuix evidence container settings
	 */
	public EvidenceSettings getEvidenceSettings() {
		return evidenceSettings;
	}
	
	/***
	 * Sets the evidence settings object representing how the Nuix evidence container will be created
	 * @param evidenceSettings The new evidence container settings
	 */
	public void setEvidenceSettings(EvidenceSettings evidenceSettings) {
		this.evidenceSettings = evidenceSettings;
	}
	
	/***
	 * Convenience method to check if a string is null or only whitespace
	 * @param input The string to check
	 * @return True if the string is null or only whitespace characters
	 */
	private boolean hasValue(String input){
		return input != null && input.trim().length() > 0;
	}
	
	/***
	 * Builds settings map which is later passed on to Nuix via EvidenceContainer.addNetworkLocation
	 * @param store The vault store object, can be null
	 * @param archive The vault archive object, can be null
	 * @return A map of settings which can be provided to Nuix method EvidenceContainer.addNetworkLocation
	 * @throws Exception If things go wrong, exceptions will be thrown
	 */
	public Map<Object, Object> buildNetworkLocationSettingsMap(VaultStore store, VaultArchive archive) throws Exception{
		Map<Object,Object> settings = new HashMap<Object,Object>();
		
		settings.put("type","enterprise_vault");
		settings.put("computer",store.getEvApiServer());
		
		if(includeStoreWithArchive){
			if(store != null){
				settings.put("vault",store.getStoreEntryID());
			}
			
			if(archive != null){
				settings.put("archive",archive.getVaultEntryID());
			}
		} else {
			if(archive != null){
				settings.put("archive",archive.getVaultEntryID());
			} else if(store != null){
				settings.put("vault",store.getStoreEntryID());
			}
		}

		if(custodians != null && custodians.size() > 0){
			settings.put("custodian",SQLUserRecord.getDelimitedAddressesString(custodians));
		}
		
		if(fromDate != null && toDate != null){
			// Turns out EV query API expects UTC date ranges, we need to make sure we
			// coerce the values to Nuix as UTC or you will get funky search ranges
			settings.put("from",fromDate);
			settings.put("to",toDate);
		}
		
		if(hasValue(keywords) && hasValue(flag)){
			settings.put("keywords",keywords);
			settings.put("flag",flag);
		}
		
		return settings;
	}
	
	/***
	 * Similar to {@link #buildNetworkLocationSettingsMap(VaultStore, VaultArchive)}, but builds a list of settings maps
	 * for multiple vault stores.
	 * @return A list of multiple settings map for use in calls to Nuix method EvidenceContainer.addNetworkLocation
	 * @throws Exception If things go wrong, exceptions will be thrown
	 */
	private List<Map<Object, Object>> buildEvidenceSettingsMaps() throws Exception{
		List<Map<Object,Object>> result = new ArrayList<Map<Object,Object>>();
		if(vaultStores.size() > 0){
			for(VaultStore store : vaultStores){
				//Determine provided associated archives (if any)
				List<VaultArchive> associatedArchives =
						vaultArchives.stream()
						.filter(a -> a.getStoreEntryID().equals(store.getStoreEntryID())).collect(Collectors.toList());
				// No associated archives so just add the store
				if(associatedArchives.size() < 1){
					Map<Object, Object> networkLocationSettings = buildNetworkLocationSettingsMap(store,null);
					result.add(networkLocationSettings);
				}
				// There are archives associated so we need to make a call for
				// each archive/store pair
				else {
					for(VaultArchive archive : associatedArchives){
						Map<Object, Object> networkLocationSettings = buildNetworkLocationSettingsMap(store,archive);
						result.add(networkLocationSettings);
					}
				}
			}
		} else {
			// Looks like were just adding the entire server!
			Map<Object, Object> networkLocationSettings = buildNetworkLocationSettingsMap(null,null);
			result.add(networkLocationSettings);
		}
		return result;
	}
	
	/***
	 * Take all the Processor settings and applies them to a Nuix Processor objects.  Note that this also reconfigures the
	 * worker timeout by altering the Java system property in referenced in {@link #workerTimeoutProperty}.  Original worker timeout
	 * property is recorded and can be restored later by calling {@link #restoreWorkerTimeout()}.
	 * @param processor The Nuix Processor object to configure.
	 * @throws Exception If things go wrong, exceptions will be thrown
	 */
	public void configureProcessorAndEnqueueData(Processor processor) throws Exception{
		if(processingSettings != null){
			logger.info("Configuring processing settings...");
			for(Map.Entry<String,Object> entry : processingSettings.entrySet()){
				logger.info(entry.getKey()+": "+entry.getValue());
			}
			processor.setProcessingSettings(processingSettings);
		}
		
		if (parallelProcessingSettings != null){
			logger.info("Configuring parallel processing settings...");
			for(Map.Entry<String,Object> entry : parallelProcessingSettings.entrySet()){
				logger.info(entry.getKey()+": "+entry.getValue());
			}
			processor.setParallelProcessingSettings(parallelProcessingSettings);
			
			if (parallelProcessingSettings.containsKey("workerTimeout")){
				originalWorkerTimeoutValue = System.getProperty(workerTimeoutProperty);
				System.setProperty(workerTimeoutProperty, parallelProcessingSettings.get("workerTimeout").toString());
				logger.info(workerTimeoutProperty+" now set to: "+System.getProperty(workerTimeoutProperty));
			}
		}
		
		for (MimeTypeSetting mimeTypeSetting : mimeTypeSettings) {
			logger.info(mimeTypeSetting.toString());
			mimeTypeSetting.apply(processor);
		}
		
		EvidenceContainer evidence = evidenceSettings.createEvidenceContainer(processor);
		
		List<Map<Object, Object>> evidenceSettingMaps = buildEvidenceSettingsMaps();
		for(Map<Object,Object> evidenceSettingMap : evidenceSettingMaps){
			logger.info("Adding network location...");
			for(Map.Entry<Object,Object> entry : evidenceSettingMap.entrySet()){
				logger.info(entry.getKey()+": "+entry.getValue());
			}
			evidence.addNetworkLocation(evidenceSettingMap);	
		}
		
		if (evidenceSettings.getInitialCustodian() != null){
			evidence.setInitialCustodian(evidenceSettings.getInitialCustodian());
		}
		
		evidence.save();
	}
	
	/***
	 * Restores worker timeout Java system property previously clobbered by a call to {@link #configureProcessorAndEnqueueData(Processor)}.
	 */
	public void restoreWorkerTimeout(){
		String value = originalWorkerTimeoutValue == null ? "" : originalWorkerTimeoutValue; 
		logger.info("Restoring original worker timeout value of: "+value);
		System.setProperty(workerTimeoutProperty, value);
	}
	
	/***
	 * Gets a Map of processing settings as accepted by Processor.setProcessingSettings
	 * @return A Map of processing settings
	 */
	public Map<String, Object> getProcessingSettings() {
		return processingSettings;
	}
	
	/***
	 * Sets Map of processing settings as accepted by Processor.setProcessingSettings
	 * @param processingSettings The new processing settings
	 */
	public void setProcessingSettings(Map<String, Object> processingSettings) {
		this.processingSettings = processingSettings;
	}
	
	/***
	 * Gets a Map of parallel processing settings as accepted by Processor.setParallelProcessingSettings
	 * @return A Map of parallel processing settings
	 */
	public Map<String, Object> getParallelProcessingSettings() {
		return parallelProcessingSettings;
	}
	
	/***
	 * Sets Map of parallel processing settings as accepted by Processor.setParallelProcessingSettings
	 * @param parallelProcessingSettings The new parallel processing settings
	 */
	public void setParallelProcessingSettings(Map<String, Object> parallelProcessingSettings) {
		this.parallelProcessingSettings = parallelProcessingSettings;
	}
	
	/***
	 * Gets the list of mime type settings associated with this instance
	 * @return The list of mime type settings associated with this instance
	 */
	public List<MimeTypeSetting> getMimeTypeSettings() {
		return mimeTypeSettings;
	}
	
	/***
	 * Sets the list of mime type settings associated with this instance
	 * @param mimeTypeSettings The new list of mime type settings to associate with this instance
	 */
	public void setMimeTypeSettings(List<MimeTypeSetting> mimeTypeSettings) {
		this.mimeTypeSettings = mimeTypeSettings;
	}
	
	@Override
	public String toString() {
		StringJoiner result = new StringJoiner("\n");
		
		result.add(evidenceSettings.toString());
		
		List<Map<Object, Object>> evidenceSettingMaps;
		try {
			evidenceSettingMaps = buildEvidenceSettingsMaps();
			int evidenceMapIndex = 0;
			for(Map<Object,Object> evidenceSettingMap : evidenceSettingMaps){
				evidenceMapIndex++;
				result.add("== Network Location Settings "+evidenceMapIndex+" ==");
				for(Map.Entry<Object,Object> entry : evidenceSettingMap.entrySet()){
					result.add("\t"+entry.getKey()+": "+entry.getValue());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if(processingSettings != null){
			result.add("== Processing Settings ==");
			for(Map.Entry<String,Object> entry : processingSettings.entrySet()){
				result.add(entry.getKey()+": "+entry.getValue());
			}
		}
		
		if(parallelProcessingSettings != null){
			result.add("== Parallel Processing Settings ==");
			for(Map.Entry<String,Object> entry : parallelProcessingSettings.entrySet()){
				result.add(entry.getKey()+": "+entry.getValue());
			}
		}
		
		return result.toString();
	}
	
	/***
	 * Produces a series of strings in the form "STORE_NAME/ARCHIVE_NAME", joining each archive with the appropriate store
	 * @return A series of strings marrying vault store to vault archive
	 */
	public List<String> getEVDataNameListing(){
		List<String> result = new ArrayList<String>();
		for(VaultStore store : vaultStores){
			//Determine provided associated archives (if any)
			List<VaultArchive> associatedArchives =
					vaultArchives.stream()
					.filter(a -> a.getStoreEntryID().equals(store.getStoreEntryID())).collect(Collectors.toList());
			if(associatedArchives.size() > 0){
				for(VaultArchive archive : associatedArchives){
					result.add(store.getName()+"/"+archive.getArchiveName());
				}
			} else {
				result.add(store.getName());
			}
		}
		return result;
	}
}
