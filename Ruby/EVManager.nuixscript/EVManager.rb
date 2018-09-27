script_directory = File.dirname(__FILE__)
require File.join(script_directory,"ev-manager.jar")

java_import com.nuix.evmanager.data.VaultServer
java_import com.nuix.evmanager.data.SQLUserRecordStore
java_import com.nuix.evmanager.data.UserRecordCriteria
java_import com.nuix.evmanager.controls.EnterpriseVaultIngestionDialog
java_import com.nuix.evmanager.controls.CommonDialogs
java_import com.nuix.evmanager.controls.ProcessingStatusDialog
java_import com.nuix.evmanager.NuixConnection
java_import com.nuix.evmanager.controls.ProcessingStatusDialog
com.nuix.evmanager.LookAndFeelHelper.setWindowsIfMetal
NuixConnection.setUtilities($utilities)
NuixConnection.setCurrentNuixVersion(NUIX_VERSION)

# This needs to have a case open, if someone is running this via nuix_console.exe we need
# to prompt them to open a case
$script_opened_case = false
if $current_case.nil?
	case_directory = CommonDialogs.getDirectory('c:\\',"Choose a case to open")
	if case_directory.nil?
		CommonDialogs.showError("This script requires a case to work with, exiting...")
		exit 1
	else
		begin
			$current_case = $utilities.getCaseFactory.open(case_directory)
			$script_opened_case = true
		rescue Exception => exc
			CommonDialogs.showError("Error opening case: #{exc.message}")
			exc.printStackTrace
			exit 1
		end
	end
end

if !$script_opened_case
	message = "This script will need to close all workbench tabs once processing begins.  Is this okay?"
	unless CommonDialogs.getConfirmation(message,"Okay to close all workbench tabs?")
		exit 1
	end
end

require 'json'

settings_directory = File.join(script_directory,"settings")

#======================================#
# Configure user record database setup #
#======================================#
address_book_connection_settings_file = File.join(settings_directory,"AddressBookSqlConnectionSettings.json")
# Set to true only if you know that the database containing the user record data has
# had a full text catalog created against it
SQLUserRecordStore::setUseFullTextFiltering(false)
user_store = nil
if !java.io.File.new(address_book_connection_settings_file).exists
	# If we reached here, means we cannot find the address book settings file
	message = "Could not locate address book SQL server connection settings file at:\n\n#{address_book_connection_settings_file}"+
		"The script will start, but the address book will not be available to select a custodian."
	CommonDialogs.showError(message,"Missing address book database settings file")
	puts message
	user_store = SQLUserRecordStore.new({})
else
	# Load address book settings file
	address_book_connection_settings = JSON.parse(File.read(address_book_connection_settings_file))
	# Configure user record store object
	user_store = SQLUserRecordStore.new(address_book_connection_settings)
	# Test if we can interact with the user record database
	if !user_store.canConnect
		message = "Unable to connect to the user record SQL server, "+
			"please ensure your settings are correct in 'AddressBookSqlConnectionSettings.json'.\n"+
			"The script will start, but as long as the this database cannot be reached, the address "+
			"book will not be available to select a custodian."
		CommonDialogs.showError(message,"Unable to connect to user record database")
	end
end

#====================#
# Configure EV Setup #
#====================#
ev_connection_settings_file = File.join(settings_directory,"EnterpriseVaultConnectionSettings.json")
if !java.io.File.new(ev_connection_settings_file).exists
	message = "Could not locate EV connection settings file at:\n\n#{ev_connection_settings_file}"
	CommonDialogs.showError(message)
	puts message
	exit 1
end
ev_server_settings = JSON.parse(File.read(ev_connection_settings_file))

# Load settings specifying where EV servers are located
ev_servers = []
ev_server_settings.each do |server_settings|
	name = server_settings["name"]
	sql_settings = server_settings["sql_connection_settings"]
	ev_servers << VaultServer.new(name,sql_settings)
end

# When true, a selected archive will be passed to Nuix API paired with the store to which
# it belongs.  When false, only the archive will be passed to Nuix.
com.nuix.evmanager.data.EnterpriseVaultIngestionSettings.setIncludeStoreWithArchive(true)

# Path to default processing settings file
default_processing_settings_file = File.join(settings_directory,"DefaultProcessingSettings.json")
# Path to default worker settings file
default_worker_settings_file = File.join(settings_directory,"DefaultWorkerSettings.json")

# Display dialog
dialog = EnterpriseVaultIngestionDialog.new
# Configure the vault servers loaded
dialog.setVaultServers(ev_servers)
# Configure where the address book database is
dialog.setUserRecordSource(user_store)

# Configure default processing settings if we have a file for this
if java.io.File.new(default_processing_settings_file).exists
	dialog.setDefaultProcessingSettingsFromJSONFile(default_processing_settings_file)
end
# Configure default worker settings if we have a file for this
if java.io.File.new(default_worker_settings_file).exists
	dialog.setDefaultWorkerSettingsFromJSONFile(default_worker_settings_file)
end

#=========================#
# Load Mime Type Settings #
#=========================#
current_version_mime_types = {}
$utilities.getItemTypeUtility.getAllTypes.each{|t| current_version_mime_types[t.getName] = true}
mime_type_settings_file = File.join(settings_directory,"DefaultMimeTypeSettings.csv")
if java.io.File.new(mime_type_settings_file).exists
	require 'csv'
	headers = nil
	CSV.foreach(mime_type_settings_file) do |row|
		if headers.nil?
			headers = row.map{|v|v.strip.downcase}
		else
			record = {}
			row.each_with_index do |v,i|
				if v.nil?
					v = ""
				end
				record[headers[i]] = v.strip.downcase
			end
			mime_type = record["mime type"]
			type_settings = {
				"enabled" => record["enabled"] == "true",
				"processEmbedded" => record["processembedded"] == "true",
				"processText" => record["processtext"] == "true",
				"textStrip" => record["textstrip"] == "true",
				"processNamedEntities" => record["processnamedentities"] == "true",
				"storeBinary" => record["storebinary"] == "true",
			}
			if current_version_mime_types[mime_type] == true
				puts "Configuring mime type settings for '#{mime_type}':"
				type_settings.each do |k,v|
					puts "\t#{k} => #{v}"
				end
				dialog.setMimeTypeSettings(mime_type,type_settings)
			else
				puts "Skipping mime type setting entry for '#{mime_type}', not a known mime type in Nuix #{NUIX_VERSION}"
			end
		end
	end
end

# Show the dialog
dialog.setVisible(true)

# If the user hit ok and results passed validation, lets take the settings they picked
# and use them to load some data
if dialog.getDialogResult == true
	# If we are running in the GUI we should close all workbench tabs!
	if !$script_opened_case
		$window.closeAllTabs
	end
	# Get the EnterpriseVaultIngestionSettings object representing all the settings
	# and enterprise vault selections made by the user in the settings dialog
	settings = dialog.getEnterpriseVaultIngestionSettings
	# Dump listing of the settings to console
	puts settings

	# Create the processor
	processor = $current_case.createProcessor
	# Have the settings object get everything setup
	settings.configureProcessorAndEnqueueData(processor)
	# Create dialog to monitor and initiate processing
	processing_status_dialog = ProcessingStatusDialog.new
	processing_status_dialog.addProcessingFinishedListener do
		address_book_custodians = settings.getCustodianRecords
		case_name = $current_case.getName
		case_location = $current_case.getLocation.getPath
		if !address_book_custodians.nil?
			# When processing completes, we should record this ingestion event
			# against the selected custodians in the address book, that is
			# assuming one or more custodians was provided in the settings
			address_book_custodians.each do |custodian|
				# Additionally we now record each vault store/archive pair this custodian was ingested against
				settings.getEVDataNameListing.each do |data_name|
					custodian.recordCustodianIngestionEvent(case_name,case_location,data_name)
				end
			end
		end
	end
	# This will display the ingestion status dialog and begin processing
	processing_status_dialog.displayAndBeginProcessing(processor)

	# Restore worker timeout Java property if we modified it
	puts "Restoring worker timeout..."
	settings.restoreWorkerTimeout
	
	# If we closed all the tabs, then we should be polite and open
	# a new tab when we are done processing
	if !$script_opened_case
		$window.openTab("workbench",{:search=>""})
	end
end

# If the script opened the case, then the script should close it as well
# Basically this is to support running the script via nuix_console.exe
if $script_opened_case && !$current_case.nil?
	$current_case.close
end