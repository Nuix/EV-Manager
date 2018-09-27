package com.nuix.evmanager.data;

import org.apache.log4j.Logger;

import nuix.EvidenceContainer;
import nuix.Processor;

/***
 * Encapsulates settings specific to the creation of a Nuix EvidenceContainer
 * @author Jason Wells
 *
 */
public class EvidenceSettings {
	private static Logger logger = Logger.getLogger(EvidenceSettings.class);
	
	private String evidenceName = "";
	private String sourceTimeZoneID = null;
	private String sourceEncoding = null;
	private String initialCustodian = null;

	/***
	 * Gets the name to assign to the evidence container
	 * @return The name to assign to the evidence container
	 */
	public String getEvidenceName() {
		return evidenceName;
	}

	/***
	 * Sets the name to assign to the evidence container
	 * @param evidenceName The name to assign to the evidence container
	 */
	public void setEvidenceName(String evidenceName) {
		this.evidenceName = evidenceName;
	}
	
	/***
	 * Gets the default source time zone ID to assign to the evidence container
	 * @return The default source time zone ID to assign to the evidence container
	 */
	public String getSourceTimeZoneID() {
		return sourceTimeZoneID;
	}

	/***
	 * Sets the default source time zone ID to assign to the evidence container
	 * @param sourceTimeZoneID The default source time zone ID to assign to the evidence container
	 */
	public void setSourceTimeZoneID(String sourceTimeZoneID) {
		this.sourceTimeZoneID = sourceTimeZoneID;
	}

	/***
	 * Gets the default source encoding to assign to the evidence container
	 * @return The default source encoding to assign to the evidence container
	 */
	public String getSourceEncoding() {
		return sourceEncoding;
	}

	/***
	 * Sets the default source encoding to assign to the evidence container
	 * @param sourceEncoding The default source encoding to assign to the evidence container
	 */
	public void setSourceEncoding(String sourceEncoding) {
		this.sourceEncoding = sourceEncoding;
	}

	/***
	 * Gets the initial custodian to assign to the evidence container
	 * @return The initial custodian to assign to the evidence container
	 */
	public String getInitialCustodian() {
		return initialCustodian;
	}

	/***
	 * Sets the initial custodian to assign to the evidence container
	 * @param initialCustodian The initial custodian to assign to the evidence container
	 */
	public void setInitialCustodian(String initialCustodian) {
		this.initialCustodian = initialCustodian;
	}

	/***
	 * Creates an evidence container using the settings of this instance
	 * @param processor The Nuix Processor object the evidence container will be created against
	 * @return The resulting evidence container
	 */
	public EvidenceContainer createEvidenceContainer(Processor processor){
		logger.info("Building evidence container...");
		
		EvidenceContainer result = processor.newEvidenceContainer(evidenceName);
		if(sourceTimeZoneID != null){
			logger.info("Setting evidence time zone: "+sourceTimeZoneID);
			result.setTimeZone(sourceTimeZoneID);
		}
		if(sourceEncoding != null){
			logger.info("Setting evidence source encoding: "+sourceEncoding);
			result.setEncoding(sourceEncoding);
		}
		if (initialCustodian != null){
			logger.info("Setting evidence initial custodian: "+initialCustodian);
			result.setInitialCustodian(initialCustodian);
		}
		return result;
	}

	@Override
	public String toString() {
		return "EvidenceSettings [evidenceName=" + evidenceName + ", sourceTimeZoneID=" + sourceTimeZoneID
				+ ", sourceEncoding=" + sourceEncoding + ", initialCustodian=" + initialCustodian + "]";
	}
}
