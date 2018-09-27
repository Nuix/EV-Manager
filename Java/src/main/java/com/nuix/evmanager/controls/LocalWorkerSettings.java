/**
 * NUIX MAKES NO EXPRESS OR IMPLIED REPRESENTATIONS OR WARRANTIES WITH RESPECT TO THIS CODE (INCLUDING BUT NOT LIMITED
 * TO ANY WARRANTIES OF MERCHANTABILITY, SATISFACTORY QUALITY, FITNESS FOR A PARTICULAR PURPOSE, OR SUITABILITY FOR
 * CUSTOMERâ€™S REQUIREMENTS). WITHOUT LIMITING THE FOREGOING, NUIX DOES NOT WARRANT THAT THIS CODE WILL MEET CUSTOMERâ€™S
 * REQUIREMENTS OR THAT ANY USE OF THIS CODE WILL BE ERROR-FREE OR THAT ANY ERRORS OR DEFECTS IN THIS CODE WILL BE CORRECTED.
 * THIS CODE IS PROVIDED TO CUSTOMER ON AN â€œAS ISâ€� AND â€œAS AVAILABLEâ€� BASIS AND FOR COMMERCIAL USE ONLY. CUSTOMER IS RESPONSIBLE
 * FOR DETERMINING WHETHER ANY INFORMATION GENERATED FROM USE OF THIS CODE IS ACCURATE AND SUFFICIENT FOR CUSTOMERâ€™S PURPOSES.
 */
package com.nuix.evmanager.controls;

import java.awt.Component;

import javax.swing.JPanel;
import java.awt.GridBagLayout;
import javax.swing.border.TitledBorder;

import org.apache.log4j.Logger;

import javax.swing.JLabel;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.base.Joiner;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.nuix.evmanager.NuixConnection;
import com.nuix.evmanager.controls.PathSelectionControl.ChooserType;

import nuix.Utilities;

import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.JButton;
import javax.swing.ImageIcon;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

/***
 * A control for providing settings relevant to local Nuix workers as configured through Processor.setParallelProcessingSettings
 * @author Jason Wells
 *
 */
@SuppressWarnings("serial")
public class LocalWorkerSettings extends JPanel {
	private static Logger logger = Logger.getLogger(LocalWorkerSettings.class);
	
	private JSpinner workerCount;
	private JSpinner memoryPerWorker;
	private PathSelectionControl workerTempDirectory;
	
	private Map<String,Object> defaultSettings = null;

	private JSpinner workerTimeout;
	
	private static String workerTimeoutProperty = "nuix.processing.worker.timeout";

	public LocalWorkerSettings() {
		setBorder(new TitledBorder(null, "Worker Settings", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{0, 100, 0, 0};
		gridBagLayout.rowHeights = new int[]{0, 0, 0, 0, 0, 0};
		gridBagLayout.columnWeights = new double[]{0.0, 0.0, 1.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		setLayout(gridBagLayout);
		
		JPanel panel = new JPanel();
		GridBagConstraints gbc_panel = new GridBagConstraints();
		gbc_panel.anchor = GridBagConstraints.WEST;
		gbc_panel.gridwidth = 3;
		gbc_panel.insets = new Insets(0, 0, 5, 0);
		gbc_panel.fill = GridBagConstraints.VERTICAL;
		gbc_panel.gridx = 0;
		gbc_panel.gridy = 0;
		add(panel, gbc_panel);
		
		JButton btnSaveWorkerSettings = new JButton("Save Worker Settings");
		btnSaveWorkerSettings.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				File outputFile = CommonDialogs.saveFileDialog("C:\\", "Worker Settings JSON", "json", "Save Worker Settings");
				if(outputFile != null){
					try {
						saveJSONFile(outputFile);
					} catch (Exception e1) {
						String message = "There was an error while saving the file:\n\n"+e1.getMessage();
						CommonDialogs.showError(message);
						logger.error(message,e1);
					}
				}
			}
		});
		btnSaveWorkerSettings.setIcon(new ImageIcon(LocalWorkerSettings.class.getResource("/com/nuix/evmanager/controls/page_save.png")));
		panel.add(btnSaveWorkerSettings);
		
		JButton btnLoadWorkerSettings = new JButton("Load Worker Settings");
		btnLoadWorkerSettings.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				File inputFile = CommonDialogs.openFileDialog("C:\\", "Worker Settings JSON", "json", "Load Worker Settings");
				if(inputFile != null){
					try {
						loadSettingsJSONFile(inputFile);
					} catch (Exception e1) {
						String message = "There was an error while loading the file:\n\n"+e1.getMessage();
						CommonDialogs.showError(message);
						logger.error(message,e1);
					}
				}
			}
		});
		btnLoadWorkerSettings.setIcon(new ImageIcon(LocalWorkerSettings.class.getResource("/com/nuix/evmanager/controls/folder_page.png")));
		panel.add(btnLoadWorkerSettings);
		
		JButton btnResetWorkerSettings = new JButton("Reset Worker Settings");
		btnResetWorkerSettings.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(CommonDialogs.getConfirmation("Are you sure you want to reset all settings?", "Reset Settings")){
					loadDefaultSettings();	
				}
			}
		});
		btnResetWorkerSettings.setIcon(new ImageIcon(LocalWorkerSettings.class.getResource("/com/nuix/evmanager/controls/cancel.png")));
		panel.add(btnResetWorkerSettings);
		
		JLabel lblNumberOfWorkers = new JLabel("Number of Workers");
		GridBagConstraints gbc_lblNumberOfWorkers = new GridBagConstraints();
		gbc_lblNumberOfWorkers.anchor = GridBagConstraints.EAST;
		gbc_lblNumberOfWorkers.insets = new Insets(0, 0, 5, 5);
		gbc_lblNumberOfWorkers.gridx = 0;
		gbc_lblNumberOfWorkers.gridy = 1;
		add(lblNumberOfWorkers, gbc_lblNumberOfWorkers);
		
		workerCount = new JSpinner();
		int initialWorkers = 2;
		int minWorkers = 1;
		int maxWorkers = 9999;
		Utilities util = NuixConnection.getUtilities();
		if(util != null && NuixConnection.getCurrentNuixVersion().isAtLeast("6.2.0")){
			initialWorkers = maxWorkers = util.getLicence().getWorkers();
		}
		workerCount.setModel(new SpinnerNumberModel(initialWorkers, minWorkers, maxWorkers, 1));
		GridBagConstraints gbc_workerCount = new GridBagConstraints();
		gbc_workerCount.fill = GridBagConstraints.HORIZONTAL;
		gbc_workerCount.insets = new Insets(0, 0, 5, 5);
		gbc_workerCount.gridx = 1;
		gbc_workerCount.gridy = 1;
		add(workerCount, gbc_workerCount);
		
		JLabel lblMemoryPerworkermb = new JLabel("Memory per-worker (MB)");
		GridBagConstraints gbc_lblMemoryPerworkermb = new GridBagConstraints();
		gbc_lblMemoryPerworkermb.anchor = GridBagConstraints.EAST;
		gbc_lblMemoryPerworkermb.insets = new Insets(0, 0, 5, 5);
		gbc_lblMemoryPerworkermb.gridx = 0;
		gbc_lblMemoryPerworkermb.gridy = 2;
		add(lblMemoryPerworkermb, gbc_lblMemoryPerworkermb);
		
		memoryPerWorker = new JSpinner();
		memoryPerWorker.setModel(new SpinnerNumberModel(new Integer(2048), new Integer(768), null, new Integer(1)));
		GridBagConstraints gbc_memoryPerWorker = new GridBagConstraints();
		gbc_memoryPerWorker.fill = GridBagConstraints.HORIZONTAL;
		gbc_memoryPerWorker.insets = new Insets(0, 0, 5, 5);
		gbc_memoryPerWorker.gridx = 1;
		gbc_memoryPerWorker.gridy = 2;
		add(memoryPerWorker, gbc_memoryPerWorker);
		
		JLabel lblWorkerTempDirectory = new JLabel("Worker temp directory");
		GridBagConstraints gbc_lblWorkerTempDirectory = new GridBagConstraints();
		gbc_lblWorkerTempDirectory.anchor = GridBagConstraints.EAST;
		gbc_lblWorkerTempDirectory.insets = new Insets(0, 0, 5, 5);
		gbc_lblWorkerTempDirectory.gridx = 0;
		gbc_lblWorkerTempDirectory.gridy = 3;
		add(lblWorkerTempDirectory, gbc_lblWorkerTempDirectory);
		
		workerTempDirectory = new PathSelectionControl(ChooserType.DIRECTORY, (String) null, (String) null, "Choose worker temp directory");
		GridBagConstraints gbc_workerTempDirectory = new GridBagConstraints();
		gbc_workerTempDirectory.insets = new Insets(0, 0, 5, 0);
		gbc_workerTempDirectory.gridwidth = 2;
		gbc_workerTempDirectory.fill = GridBagConstraints.BOTH;
		gbc_workerTempDirectory.gridx = 1;
		gbc_workerTempDirectory.gridy = 3;
		add(workerTempDirectory, gbc_workerTempDirectory);
		workerTempDirectory.setPath("C:\\WorkerTemp");
		
		JLabel lblWorkerTimeoutseconds = new JLabel("Worker Timeout (seconds):");
		GridBagConstraints gbc_lblWorkerTimeoutseconds = new GridBagConstraints();
		gbc_lblWorkerTimeoutseconds.insets = new Insets(0, 0, 0, 5);
		gbc_lblWorkerTimeoutseconds.gridx = 0;
		gbc_lblWorkerTimeoutseconds.gridy = 4;
		add(lblWorkerTimeoutseconds, gbc_lblWorkerTimeoutseconds);
		
		workerTimeout = new JSpinner();
		int currentTimeout = 3600;
		try {
			if(System.getProperties().containsKey(workerTimeoutProperty)){
				currentTimeout = Integer.parseInt(System.getProperty(workerTimeoutProperty));
			}
		} catch (NumberFormatException e1) {
			logger.warn("Error parsing user provided value for workerTimeout: "+e1.getMessage());
		}
		workerTimeout.setModel(new SpinnerNumberModel(currentTimeout, 10, 36000, 60));
		GridBagConstraints gbc_workerTimeout = new GridBagConstraints();
		gbc_workerTimeout.fill = GridBagConstraints.HORIZONTAL;
		gbc_workerTimeout.insets = new Insets(0, 0, 0, 5);
		gbc_workerTimeout.gridx = 1;
		gbc_workerTimeout.gridy = 4;
		add(workerTimeout, gbc_workerTimeout);
	}

	@Override
	public void setEnabled(boolean value){
		for(Component c : getComponents()){
			c.setEnabled(value);
		}
	}
	
	public int getWorkerCount() {
		return (Integer) workerCount.getValue();
	}
	
	public void setWorkerCount(int value) {
		Utilities util = NuixConnection.getUtilities();
		if(util != null && NuixConnection.getCurrentNuixVersion().isAtLeast("6.2.0")){
			int maxWorkers = util.getLicence().getWorkers();
			if(value > maxWorkers){
				logger.info("Requested set worker count to "+value+", using actual worker count of "+maxWorkers);
				value = maxWorkers;
			}
		}
		workerCount.setValue(value);
	}
	
	public int getMemoryPerWorker() {
		return (Integer) memoryPerWorker.getValue();
	}
	
	public void setMemoryPerWorker(int value) {
		memoryPerWorker.setValue(value);
	}
	
	public void setWorkerTempDirectory(String value){
		workerTempDirectory.setPath(value);
	}
	
	public String getWorkerTempDirectory() {
		return workerTempDirectory.getPath();
	}
	
	public File getWorkerTempDirectoryFile() {
		return workerTempDirectory.getPathFile();
	}
	
	public int getWorkerTimeout(){
		return (Integer) workerTimeout.getValue();
	}
	
	public void setWorkerTimeout(int value){
		workerTimeout.setValue(value);
	}
	
	public Map<String,Object> getSettingsMap(){
		Map<String,Object> result = new HashMap<String,Object>();
		result.put("workerCount", getWorkerCount());
		result.put("workerMemory", getMemoryPerWorker());
		result.put("workerTemp",getWorkerTempDirectory());
		result.put("workerTimeout",getWorkerTimeout());
		return result;
	}
	
	public void loadSettingsMap(Map<String,Object> settings){
		if(settings.containsKey("workerCount")){
			int workerCount = ((Double)settings.get("workerCount")).intValue();
			setWorkerCount(workerCount);	
		}
		
		if(settings.containsKey("workerMemory")){
			setMemoryPerWorker(((Double)settings.get("workerMemory")).intValue());
		}
		
		if(settings.containsKey("workerTemp")){
			setWorkerTempDirectory((String)settings.get("workerTemp"));
		}
		
		if(settings.containsKey("workerTimeout")){
			Double workerTimeoutValue = (Double)settings.get("workerTimeout");
			setWorkerTimeout(workerTimeoutValue.intValue());
		}
	}
	
	public String getSettingsJSON(){
		GsonBuilder builder = new GsonBuilder();
		builder.setPrettyPrinting();
		Gson gson = builder.create();
		return gson.toJson(getSettingsMap());
	}
	
	/***
	 * Saves the settings represented by this control as a JSON file
	 * @param filePath The location to save the file to
	 * @throws Exception Thrown if something goes wrong
	 */
	public void saveJSONFile(File filePath) throws Exception {
		FileWriter fw = null;
		PrintWriter pw = null;
		try{
			fw = new FileWriter(filePath);
			pw = new PrintWriter(fw);
			pw.print(getSettingsJSON());
		}catch(Exception exc){
			throw exc;
		}
		finally{
			try {
				fw.close();
			} catch (IOException e) {}
			pw.close();
		}
	}
	
	/***
	 * Saves the settings represented by this control as a JSON file
	 * @param filePath The location to save the file to
	 * @throws Exception Thrown if something goes wrong
	 */
	public void saveJSONFile(String filePath) throws Exception {
		saveJSONFile(new File(filePath));
	}
	
	/***
	 * Loads settings into the control from a JSON string
	 * @param json The JSON string of settings to load
	 */
	public void loadSettingsJSON(String json){
		Gson gson = new Gson();
		Map<String,Object> settings = gson.fromJson(json,new TypeToken<Map<String,Object>>(){}.getType());
		loadSettingsMap(settings);
	}
	
	/***
	 * Loads settings into the control from a JSON file
	 * @param filePath The location of the file to load
	 * @throws Exception Thrown if something goes wrong
	 */
	public void loadSettingsJSONFile(String filePath) throws Exception{
		List<String> lines = Files.readAllLines(Paths.get(filePath));
		String json = Joiner.on("\n").join(lines);
		loadSettingsJSON(json);
	}
	
	/***
	 * Loads settings into the control from a JSON file
	 * @param filePath The location of the file to load
	 * @throws Exception Thrown if something goes wrong
	 */
	public void loadSettingsJSONFile(File filePath) throws Exception{
		loadSettingsJSONFile(filePath.getPath());
	}
	
	public Map<String, Object> getDefaultSettings() {
		return defaultSettings;
	}

	public void setDefaultSettings(Map<String, Object> defaultSettings) {
		this.defaultSettings = defaultSettings;
		loadDefaultSettings();
	}
	
	public void setDefaultSettingsFromJSON(String json) {
		Gson gson = new Gson();
		Map<String,Object> settings = gson.fromJson(json,new TypeToken<Map<String,Object>>(){}.getType());
		setDefaultSettings(settings);
	}
	
	public void setDefaultSettingsFromJSONFile(String filePath) throws Exception {
		List<String> lines = Files.readAllLines(Paths.get(filePath));
		String json = Joiner.on("\n").join(lines);
		setDefaultSettingsFromJSON(json);
	}
	
	public void setDefaultSettingsFromJSONFile(File filePath) throws Exception {
		setDefaultSettingsFromJSONFile(filePath.getPath());
	}
	
	public void loadDefaultSettings(){
		clearSettings();
		if(defaultSettings != null){
			loadSettingsMap(defaultSettings);
		}
	}
	
	public void clearSettings(){
		Utilities util = NuixConnection.getUtilities();
		int initialWorkers = 2;
		if(util != null && NuixConnection.getCurrentNuixVersion().isAtLeast("6.2.0")){
			initialWorkers = util.getLicence().getWorkers();
		}
		setWorkerCount(initialWorkers);
		setMemoryPerWorker(2048);
		setWorkerTempDirectory("C:\\WorkerTemp");
	}
}
