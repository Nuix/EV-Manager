EV Manager
==========

![Last tested in Nuix 7.4](https://img.shields.io/badge/Nuix-7.4-green.svg)

View the GitHub project [here](https://github.com/Nuix/EV-Manager) or download the latest release [here](https://github.com/Nuix/EV-Manager/releases).

# Overview

**Written By:** Jason Wells

This script provides an interface to assist with locating and processing EV stores and archives in Nuix.  The script supports filtering by a custodian which is selected from a user record database (the address book) which is stored in SQL.

Additionally this repository contains:
- A SQL script to build the requisite tables in a database
- A tool to sync data from Active Directory or a CSV into the address book

# Getting Started

## Setup

Begin by downloading the latest release of this code.  Extract the contents of the archive into your Nuix scripts directory.  In Windows the script directory is likely going to be either of the following:

- `%appdata%\Nuix\Scripts` - User level script directory
- `%programdata%\Nuix\Scripts` - System level script directory

## Configuration

Before you can make use of this script you will need to perform some configuration.  Within the `settings` sub directory, there are several [JSON](https://en.wikipedia.org/wiki/JSON) files which provide configuration settings to the script.

### AddressBookSqlConnectionSettings.json

This [file](/Ruby/EVManager.nuixscript/settings/AddressBookSqlConnectionSettings.json) allows you to provide database connection settings which will allow the script to interact with the address book database.

**Example**

```json
{
	"server": "localhost",
	"port": null,
	"database": "UserData",
	"instance": null,
	"domain": null,
	"user": null,
	"password": null
}
```

| Key | Description |
|-----|-------------|
| `server` | An IP or host name which can be used to resolve the SQL server. |
| `port` | Allows you to specify an alternative port number if your SQL server is running on a non-standard port number. |
| `database` | The default database to connect to, by default this should be `UserData`. |
| `instance` | If the target machine has more than one running instance of SQL server, this allows you to specify which instance of SQL server to connect to. |
| `domain` | Specifies the Windows domain to authenticate in. If present and the user name and password are provided, [jTDS](http://jtds.sourceforge.net/) (library used to connect to SQL) uses Windows (NTLM) authentication instead of the usual SQL Server authentication (i.e. the user and password provided are the domain user and password). This allows non-Windows clients to log in to servers which are only configured to accept Windows authentication.  If the domain parameter is present but no user name and password are provided, jTDS uses its native Single-Sign-On library and logs in with the logged Windows user's credentials (for this to work one would need to be on Windows, logged into a domain, and also have the SSO library installed). |
| `user` | User name used to authenticate |
| `password` | Password used to authenticate |

**Note:** Providing a value of `null` for a setting instructs the script to ignore that setting.

**Note:** If this file cannot be found or the database cannot be reached using the settings provided, the script will still run with limited functionality.  The script will start with a warning and the address book functionality will be unavailable, but the script should otherwise be functional.

The combination of settings needed depends on your MSSQL environment and how it is setup.

### EnterpriseVaultConnectionSettings.json

This [file](/Ruby/EVManager.nuixscript/settings/EnterpriseVaultConnectionSettings.json) allows you to provide SQL connection settings to one or more Enterprise Vault SQL backend servers.  The script connects to these servers to query information about available vault stores and associated vault archives.

**Example - Single Server**

```json
[
	{
		"name": "Server 1",
		"sql_connection_settings": {
			"server": "192.168.1.10",
			"port": null,
			"database": "EnterpriseVaultDirectory",
			"instance": "",
			"domain": null,
			"user": null,
			"password": null
		}
	}
]
```

**Example - Multiple Servers**

```json
[
	{
		"name": "Server 1",
		"sql_connection_settings": {
			"server": "192.168.1.10",
			"port": null,
			"database": "EnterpriseVaultDirectory",
			"instance": "",
			"domain": null,
			"user": null,
			"password": null
		}
	},
	{
		"name": "Server 2",
		"sql_connection_settings": {
			"server": "192.168.1.20",
			"port": null,
			"database": "EnterpriseVaultDirectory",
			"instance": "SQLEXPRESS",
			"domain": null,
			"user": null,
			"password": null
		}
	},
	{
		"name": "Server 3",
		"sql_connection_settings": {
			"server": "192.168.1.30",
			"port": null,
			"database": "EnterpriseVaultDirectory",
			"instance": "",
			"domain": null,
			"user": "sa",
			"password": "sapassword"
		}
	}
]
```

| Key | Description |
|-----|-------------|
| /`name` | The display name used in the GUI for the given server entry. |
| /`sql_connection_settings`/`server` | An IP or host name which can be used to resolve the SQL server. |
| /`sql_connection_settings`/`port` | Allows you to specify an alternative port number if your SQL server is running on a non-standard port number. |
| /`sql_connection_settings`/`database` | The default database to connect to, by default this should be `UserData`. |
| /`sql_connection_settings`/`instance` | If the target machine has more than one running instance of SQL server, this allows you to specify which instance of SQL server to connect to. |
| /`sql_connection_settings`/`domain` | Specifies the Windows domain to authenticate in. If present and the user name and password are provided, jTDS (library used to connect to SQL) uses Windows (NTLM) authentication instead of the usual SQL Server authentication (i.e. the user and password provided are the domain user and password). This allows non-Windows clients to log in to servers which are only configured to accept Windows authentication.  If the domain parameter is present but no user name and password are provided, jTDS uses its native Single-Sign-On library and logs in with the logged Windows user's credentials (for this to work one would obviously need to be on Windows, logged into a domain, and also have the SSO library installed). |
| /`sql_connection_settings`/`user` | User name used to authenticate |
| /`sql_connection_settings`/`password` | Password used to authenticate |

**Note:** Providing a value of `null` for a setting instructs the script to ignore that setting.

The combination of settings needed depends on your MSSQL environment and how it is setup.

### DefaultProcessingSettings.json

This [file](/Ruby/EVManager.nuixscript/settings/DefaultProcessingSettings.json) defines the default processing settings used by the "Data Processing Settings" tab.  If this file does not exist the script will use its own internal default settings (seen below).  See [Processor.setProcessingSettings](https://download.nuix.com/releases/desktop/stable/docs/en/scripting/api/nuix/Processor.html#setProcessingSettings-java.util.Map-) for details on what settings are available and what they do.

**Example**

```json
{
  "addBccToEmailDigests": false,
  "addCommunicationDateToEmailDigests": false,
  "analysisLanguage": "en",
  "calculateAuditedSize": true,
  "calculateSSDeepFuzzyHash": false,
  "carveFileSystemUnallocatedSpace": false,
  "createThumbnails": false,
  "detectFaces": false,
  "digests": [
    "MD5"
  ],
  "enableExactQueries": false,
  "extractEndOfFileSlackSpace": false,
  "extractFromSlackSpace": false,
  "extractNamedEntitiesFromProperties": false,
  "extractNamedEntitiesFromText": false,
  "extractNamedEntitiesFromTextStripped": false,
  "extractShingles": false,
  "hideEmbeddedImmaterialData": false,
  "identifyPhysicalFiles": true,
  "maxDigestSize": 250000000,
  "maxStoredBinarySize": 1000000000,
  "processFamilyFields": false,
  "processText": true,
  "processTextSummaries": false,
  "recoverDeletedFiles": false,
  "reportProcessingStatus": "none",
  "reuseEvidenceStores": true,
  "skinToneAnalysis": false,
  "smartProcessRegistry": false,
  "stemming": false,
  "stopWords": false,
  "storeBinary": false,
  "traversalScope": "full_traversal"
}
```

### DefaultWorkerSettings.json

This [file](/Ruby/EVManager.nuixscript/settings/DefaultWorkerSettings.json) defines the default settings used by the "Worker Settings" tab.  If this file does not exist the script will use its own internal default settings (seen below).  See [Processor.setParallelProcessingSettings](https://download.nuix.com/releases/desktop/stable/docs/en/scripting/api/nuix/ParallelProcessingConfigurable.html#setParallelProcessingSettings-java.util.Map-) for details on what these settings do.  Note that currently only `workerCount`, `workerMemory` and `workerTemp` are supported by the script.

**Example**

```json
{
	"workerCount": 4,
	"workerMemory": 2048,
	"workerTemp": "C:\\WorkerTemp",
	"workerTimeout": 3600
}
```

### DefaultMimeTypeSettings.csv

This CSV [file](/Ruby/EVManager.nuixscript/settings/DefaultMimeTypeSettings.csv) configures how mime type settings will be configured [through the API](https://download.nuix.com/releases/desktop/stable/docs/en/scripting/api/nuix/Processor.html#setMimeTypeProcessingSettings-java.lang.String-java.util.Map-) before processing begins.

| Column Name | Description |
|-------------|-------------|
| `Kind` | The Nuix kind value for this type, provided to make finding a particular type entry easier |
| `Type Name` | The user fiendly name for this type, provided to make finding a particular type entry easier |
| `Extension` | The Nuix preferred extension for this type, provided to make finding a particular type entry easier |
| `Mime Type` | The mime type value for this type entry. |
| `Enabled` | If `FALSE` items matching this MIME type, and their embedded descendants, will not be processed. |
| `ProcessEmbedded` | If `FALSE` descendants of items matching this MIME type will not be processed. |
| `ProcessText` | If `FALSE` items matching this MIME type will not have their text processed. |
| `TextStrip` | If `TRUE` items matching this MIME type will have their binary data text stripped.  **Note:** when this is `TRUE`, `ProcessText` is implicitly set to `FALSE`. |
| `ProcessNamedEntities` | If `FALSE` items matching this MIME type will not have named entities extracted. |
| `ProcessImages` | If `FALSE` items matching this MIME type will not have their image data processed. |
| `StoreBinary` | If `FALSE` items matching this MIME type will not have their binary data stored. |

**Note:** If a mime type value is encountered that is not a known type in the version of Nuix you are using to run the script, the settings will be skipped for that particular type to prevent errors. A message will also be logged noting this.

# Address Book Database

The address book information is stored in a series of tables which hold information about users.  See the included file [`CreateDatabaseTables.sql`](/SQL/CreateDatabaseTables.sql) for details.

## Tables

### UserRecord

Used to store the main details of a user.

| SQL Column Name | SQL Data Type | Nullable? | Indexed? | AD Field | Notes |
|-----------------|---------------|--------------|-----------|----------|-------|
| ID | PRIMARY KEY AUTOINCREMENT INT | NO | YES, UNIQUE | N/A | Database relational ID only |
| EmployeedID | VARCHAR(100) | NO | YES, UNIQUE | `EmployeedID` | May have leading zeros, possibly needs to be a VARCHAR field in order to retain leading zeros |
| Name | VARCHAR(100) | NO | YES | `DisplayName` |  |
| Title | VARCHAR(100) | YES | YES | `Title` |  |
| Department | VARCHAR(100) | YES | YES | `Department` |  |
| Location | VARCHAR(100) | YES | YES | `PhysicalDeliveryOfficeName` |  |
| RecordCreated | DateTime | NO | NO | `WhenCreated` | Records the time when the record was initially created in the database. |
| RecordLastModified | DateTime | YES | NO | `WhenChanged` | If a value is present, represents the last time the record was modified via an AD sync |

### UserAddress

Used to store the email addresses associate to a user.

| SQL Column Name | SQL Data Type | Nullable? | Indexed? | AD Field | Notes |
|-----------------|---------------|--------------|-----------|----------|-------|
| ID | PRIMARY KEY AUTOINCREMENT INT | NO | YES, UNIQUE | N/A | Database relational ID only |
| UserRecordID | INT | NO | YES | N/A | Associates this address record to a `UserRecord` with the same `ID` value |
| RecordCreated | DateTime | NO | NO | N/A | Records the time when the record was initially created in the database. |
| RecordLastModified | DateTime | YES | NO | N/A | If a value is present, represents the last time the record was modified |
| Address | VARCHAR(1000) | NO | NO | `mail`, `mailNickname`, `proxyAddresses` | An email address |

### UserPhoneNumber

Used to store the phone numbers associate to a user.

| SQL Column Name | SQL Data Type | Nullable? | Indexed? | AD Field | Notes |
|-----------------|---------------|--------------|-----------|----------|-------|
| ID | PRIMARY KEY AUTOINCREMENT INT | NO | YES, UNIQUE | N/A | Database relational ID only |
| UserRecordID | INT | NO | YES | N/A | Associates this phone number record to a `UserRecord` with the same `ID` value |
| RecordCreated | DateTime | NO | NO | N/A | Records the time when the record was initially created in the database. |
| PhoneNumber | VARCHAR(30) | NO | NO | `TelephoneNumber` | A phone number |

### UserSID

Used to store the SIDs associate to a user.

| SQL Column Name | SQL Data Type | Nullable? | Indexed? | AD Field | Notes |
|-----------------|---------------|--------------|-----------|----------|-------|
| ID | PRIMARY KEY AUTOINCREMENT INT | NO | YES, UNIQUE | N/A | Database relational ID only |
| UserRecordID | INT | NO | YES | N/A | Associates this SID record to a `UserRecord` with the same `ID` value |
| RecordCreated | DateTime | NO | NO | N/A | Records the time when the record was initially created in the database. |
| SID | VARCHAR(190) | NO | NO | `SID` |  |

### IngestionHistory

When ingestion is performed with a custodian selected, this table records to which case and when that ingestion was performed.

| SQL Column Name | SQL Data Type | Nullable? | Indexed? | AD Field | Notes |
|-----------------|---------------|--------------|-----------|----------|-------|
| ID | PRIMARY KEY AUTOINCREMENT INT | NO | YES, UNIQUE | N/A | Database relational ID only |
| UserRecordID | INT | NO | YES | N/A | Associates this ingestion event record to a `UserRecord` with the same `ID` value |
| DateIngested | DATETIME | NO | NO | N/A | DateTime when an ingestion was performed |
| CaseName | VARCHAR(200) | NO | NO | N/A | Name of case to which ingestion occurred |
| CaseLocation | VARCHAR(255) | NO | NO | N/A | Location of case to which ingestion occurred |

# Address Book Active Directory Sync Console

This is a headless executable (no GUI) which performs the sync operation between a data source (usually Active Directory) and the address book database from which the script pulls the custodian information.

## Sync Logic

For each record in input source:
1. If input record has an empty or only whitespace character value for `EmployeeID` the record is skipped.  Since this is the only way to uniquely identify each entry this is a required field.
2. If the database does not already contain a record with this `EmployeeID`, a new record is added.  If the database does already contain a record with this `EmployeeID`:
	1. If settings specify to update user base data:
		1. If `Name` is different from value in database, update it
		2. If `Title` is different from value in database, update it
		3. If `Department` is different from value in database, update it
		4. If `Location` is different from value in database, update it
	2. Update database with any email addresses not already present for user (values are added, but never modified or removed)
	3. Update database with any phone numbers not already present for user (values are added, but never modified or removed)
	4. Update database with any SID values which are not already present for user record (values are added, but never modified or removed)

## Settings File

By default the tool will look for the file `Settings.json` located in the same directory as the executable.  This is a JSON file containing information regarding:

- SQL Server connection settings for the database server hosting the address book database.
- Sync settings controlling some aspects regarding how data is collected from Active Directory.
- The directory log files are written to.
- Settings for each Active Directory server to collect user information from.

**Note:** See the arguments section below for details on specifying where the settings file is located.

```json
{
	"AddressBookDatabaseConnectionSettings":{
		"Server": "localhost",
		"Port": null,
		"Instance": "SQLEXPRESS",
		"Database": "UserData",
		"User": null,
		"Password": null,
		"Domain": null,
	},
	"SyncSettings":{
		"EmailAddressesIncludesMailNicknameProperty": true,
		"EmailAddressesIncludesMailProperty": true,
		"EmailAddressesIncludesProxyAddressesProperty": true,
		"CollectX400ProxyAddresses": true,
		"CollectX500ProxyAddresses": true,
		"CollectSmtpProxyAddresses": true,
		"CollectSipProxyAddresses": true,
		"UpdateUserInformation": true
	},
	"LogDirectory": "C:\\Temp\\ADSyncLogs\\",
	"ActiveDirectoryServers":[
		{
			"Name": "Server 1",
			"Domain": null,
			"ContextContainer": null
		},
		{
			"Name": "Server 2",
			"Domain": null,
			"ContextContainer": null
		}
	]
}
```

This file is comprised of several sections:

- [AddressBookDatabaseConnectionSettings](#addressbookdatabaseconnectionsettings)
- [SyncSettings](#syncsettings)
- [ActiveDirectoryServers](#activedirectoryservers)

### AddressBookDatabaseConnectionSettings

Contains information about how to connect to the SQL server instance hosting the address book database.

| Setting | Description |
|---------|-------------|
| `Server` | IP or hostname of SQL server. |
| `Port` | The port number of the SQL server.  Only really needed if the port is not the standard SQL port of `1433`. |
| `Instance` | Name of the SQL server instance.  Usually only needed if there is more than one instance of SQL server running on the target machine. |
| `Database` | Name of the address book database. |
| `User` | Used to specify a user name.  See below for more details. |
| `Password` | Used to specify a password.  See below for more details. |
| `Domain` | Used to specify a authentication domain.  See below for more details. |

**SQL Server Authentication**

Authentication to the SQL server hosting the address book database will be performed in different ways, depending on the settings provided.

1. Trusted connection - If no values are provided for: `User`, `Password` or `Domain` then a trusted connection will be used to connect to the SQL server.  The credentials of user running the tool will be used to authenticate against the SQL server.
2. SQL Server Authentication - If value are provided for `User` and `Password`, then those credentials will be used to authenticate against a login setup within the SQL server instance.
3. Domain Authentication - If values are provided for `User`, `Password` and `Domain` then those credentials will be used to authenticate with the specified `Domain`.

### SyncSettings

| Setting | Description |
|---------|-------------|
| `EmailAddressesIncludesMailNicknameProperty` | Determines whether address values will be pulled from the Active Directory user property `mailNickname` |
| `EmailAddressesIncludesMailProperty` | Determines whether address values will be pulled from the Active Directory user property `mail` |
| `EmailAddressesIncludesProxyAddressesProperty` | Determines whether address values will be pulled from the Active Directory user property `proxyAddresses` |
| `CollectX400ProxyAddresses` | Determines whether x400 addresses will be collected from the `proxyAddresses` property (if it is enabled) |
| `CollectX500ProxyAddresses` | Determines whether x500 addresses will be collected from the `proxyAddresses` property (if it is enabled) |
| `CollectSmtpProxyAddresses` | Determines whether SMTP addresses will be collected from the `proxyAddresses` property (if it is enabled) |
| `CollectSipProxyAddresses` | Determines whether SIP addresses will be collected from the `proxyAddresses` property (if it is enabled) |
| `UpdateUserInformation` | Determines whether basic user attributes in the table `UserRecord` will be updated to reflect changes in the source data.  Regardless of how this set, new Addresses, Phone Numbers and SIDs will still be added to the address book. |

### ActiveDirectoryServers

This section allows you to specify 1 or more Active Directory servers to collect sync data from.  Each server entry takes the following form:

```json
{
	"Name": "Server 1",
	"Domain": null,
	"ContextContainer": null,
	"UserName": null,
	"Password": null
}
```

| Setting | Description |
|---------|-------------|
| `Name` | Friendly name for server.  Mostly for user reference, has no impact on how instance is connected to. |
| `Domain` | Domain of the Active Directory instance to connect to.  If not provided, will be inferred from machine the sync tool is running on. |
| `ContextContainer` | Optional, specifies a contextual root container within which user search will occur.  Example: `OU=Personal,OU=Accounts,DC=nam,DC=ent,DC=acompany,DC=com`. |
| `UserName` | User name used to authenticate against the given AD server, can be null to authenticate using credentials of user running the tool. |
| `Password` | Password used to authenticate against the given AD server, can be null to authenticate using credentials of user running the tool. |

## Arguments

The tool also supports several option arguments to modify how it behaves.

```
  -t, --templatefile    (Default: None) When specified the tool will save a
                        blank template CSV (suitable for import) to the
                        location specified.

  -i, --inputfile       (Default: None) Used to specify a CSV file to import.
                        When this option is provided, import will not occur
                        from active directory.

  -e, --exportfile      (Default: None) Used to specify an file to export to.
                        When this option is provided, entries will be read from
                        Active Directory but written to the output file rather
                        than the address book database.  Supports extensions:
                        CSV, TXT

  -l, --limit           (Default: 0) Used to specify a maximum number of
                        records to process.  A value of 0 means no limit.

  -s, --settingsfile    (Default: None) Used to specify an alternate settings
                        JSON file.  If not provided default is to look for
                        Settings.json in same directory as executable.

  -n, --servername      (Default: None) When provided, only the Active
                        Directory server in the settings file with the provided
                        name will be processed.

  --help                Display this help screen.
```

| Command Line Argument | Description |
|-----------------------|-------------|
| `settingsfile` | Used to specify an alternate settings JSON file.  If not provided default is to look for `Settings.json` in the same directory as the executable. |
| `inputfile` | Used to specify a CSV file to import.  When this option is provided, import will use the specified CSV as the source of user records instead of Active directory. |
| `templatefile` | When specified, then tool will save a blank template CSV suitable for import via the `--input-file` argument. |
| `exportfile` | When specified, data collected from Active Directory will be written to an output file rather than be synced to the address book database.  This is useful to sample the data being pulled from active directory for review.  Output file specified can end with extension `.txt` or `.csv`.  When `.csv` is specified, the data will be written to a CSV.  When `.txt` is specified the sample data will be written to a text file in a more verbose manner to help determine what addresses are coming from where. |
| `limit` | Used to specify a maximum number of records to process.  A value of `0` means no limit. |
| `servername` | When provided, only the Active Directory server in the settings file with the provided name will be processed. |

### Examples

**Specify the settings file**

```
ActiveDirectorySyncConsole.exe --settings "C:\SomeDirectory\AlternativeSettings.json"
```

**Export to a CSV**

```
ActiveDirectorySyncConsole.exe --exportfile "C:\SomeDirectory\ADUserSample.csv"
```

**Export 100 records to a CSV**

```
ActiveDirectorySyncConsole.exe --exportfile "C:\SomeDirectory\ADUserSample.csv" --limit 100
```

**Import from a CSV**

```
ActiveDirectorySyncConsole.exe --importfile "C:\SomeDirectory\ADUserSample.csv"
```

**Process only specific AD server**

```
ActiveDirectorySyncConsole.exe --servername "Server 1"
```

**Process only 100 records from specific AD server**

```
ActiveDirectorySyncConsole.exe --servername "Server 1" --limit 100
```

## Return Codes

The tool returns a few different codes depending on errors which may have occurred.

| Code | Description |
|------|-------------|
| `0` | No issues |
| `1` | Unexpected exception |
| `2` | Invalid settings file path |
| `3` | Error while loading settings file |
| `4` | Error connecting to address book database |
| `5` | 1 or more errors while syncing user records |

# License

```
Copyright 2018 Nuix

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```