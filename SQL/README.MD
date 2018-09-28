In this directory are resources regarding the SQL Server aspect of this script.

# Address Book Database
- `/CreateDatabaseTables.sql`: Builds the series of tables into an *already existing* database as needed by the address book functionality of this script.  You will need to run this against your database before making use of the address book functionality, either for testing or production.

# Testing
- `/DatabaseTestData/GenerateUserRecordStore.rb`: Populates an address book database with a series of fake user entries for testing.
- `/DatabaseTestData/NameData.txt`: Provides a list of names used by `GenerateUserRecordStore.rb` while generating fake user data.
- `/DatabaseTestData/Database.rb`: Dependency providing other scripts code for accessing SQL.
- `/DatabaseTestData/SQLite.rb`: Dependency providing other scripts code for accessing SQL.
- `/DatabaseTestData/SQLServer.rb`: Dependency providing other scripts code for accessing SQL.
- `/DatabaseTestData/SampleTableData.sql`: Convenience SQL query for sampling data from test address book database.
- `/DatabaseTestData/TruncateAllTables.sql`: Convenience SQL query for wiping tables of a test address book database.