package com.nuix.evmanager.data;

import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;

import nuix.Processor;

/***
 * Encapsulates mime type settings as provided to Nuix via Processor.setMimeTypeProcessingSettings
 * @author Jason Wells
 *
 */
public class MimeTypeSetting {
	private String mimeType = null;
	private boolean enabled = true;
	private boolean processEmbedded = true;
	private boolean processText = true;
	private boolean textStrip = false;
	private boolean processNamedEntities = true;
	private boolean processImages = true;
	private boolean storeBinary = true;
	
	/***
	 * Creates a new instance associated with the given mime type
	 * @param mimeType The mime type name as obtained via ItemType.getName
	 */
	public MimeTypeSetting(String mimeType){
		this.mimeType = mimeType;
	}
	
	/***
	 * Creates a new instance associated with the given mime type and Map of settings
	 * @param mimeType The mime type name as obtained via ItemType.getName
	 * @param settings Map of settings to associate as accepted by Processor.setMimeTypeProcessingSettings
	 */
	public MimeTypeSetting(String mimeType, Map<String,Boolean> settings){
		this.mimeType = mimeType;
		
		if(settings.containsKey("enabled")){ enabled = settings.get("enabled"); }
		if(settings.containsKey("processEmbedded")){ processEmbedded = settings.get("processEmbedded"); }
		if(settings.containsKey("processText")){ processText = settings.get("processText"); }
		if(settings.containsKey("textStrip")){ textStrip = settings.get("textStrip"); }
		if(settings.containsKey("processNamedEntities")){ processNamedEntities = settings.get("processNamedEntities"); }
		if(settings.containsKey("processImages")){ processImages = settings.get("processImages"); }
		if(settings.containsKey("storeBinary")){ storeBinary = settings.get("storeBinary"); }
	}
	
	/***
	 * Gets a Map of the settings as accepted by Processor.setMimeTypeProcessingSettings.  Note that is accounts for issue in some versions of Nuix where
	 * you will receieve an error if the Map contains keys "textStrip" and "processText" at the same time.  If "textStrip" setting in this instance is
	 * true, then it is added to the Map with a value of true, otherwise "processText" is added to the Map with whatever value it may have.
	 * @return Map of settings suitable to provide to Nuix API
	 */
	public Map<String,Boolean> toMap(){
		Map<String,Boolean> result = new HashMap<String,Boolean>();
		result.put("enabled", enabled);
		result.put("processEmbedded", processEmbedded);
		
		if(textStrip == true)
			result.put("textStrip", textStrip);
		else
			result.put("processText", processText);
		
		result.put("processNamedEntities", processNamedEntities);
		result.put("processImages", processImages);
		result.put("storeBinary", storeBinary);
		return result;
	}
	
	@Override
	public String toString() {
		StringJoiner result = new StringJoiner("\n");
		
		result.add("Mime Type: "+mimeType);
		for (Map.Entry<String, Boolean> setting : toMap().entrySet()) {
			result.add("\t"+setting.getKey()+": "+setting.getValue());
		}
		
		return result.toString();
	}

	/***
	 * Applies the settings associated with this instance to the given Processor instance.
	 * @param processor The processor to apply the mime type settings to.
	 */
	public void apply(Processor processor){
		processor.setMimeTypeProcessingSettings(mimeType, toMap());
	}
}
