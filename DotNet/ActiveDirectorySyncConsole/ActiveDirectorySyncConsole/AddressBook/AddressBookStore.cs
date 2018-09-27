using ActiveDirectorySyncConsole.ActiveDirectory;
using ActiveDirectorySyncConsole.Configuration;
using NLog;
using System;
using System.Collections.Generic;
using System.Data.SqlClient;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace ActiveDirectorySyncConsole.AddressBook
{
    public class AddressBookStore
    {
        private static Logger logger = LogManager.GetLogger("AddressBookStore");
        public delegate void DatabaseRecordCallbackDelegate(SqlDataReader reader);
        public DatabaseConnectionSettings DatabaseConnectionSettings { get; private set; }
        public string ConnectionString { get; private set; }

        public int UsersBefore { get; private set; }
        public int AddressesBefore { get; private set; }
        public int PhoneNumbersBefore { get; private set; }
        public int SIDsBefore { get; private set; }

        public int AddedUsers { get; private set; }
        public int UpdatedUsers { get; private set; }
        public int ProcessedUsers { get; private set; }
        public int ErroredUsers { get; private set; }
        public int WarnedUsers { get; private set; }

        public void ResetTrackingCounts()
        {
            AddedUsers = UpdatedUsers = ProcessedUsers = 0;
            UsersBefore = UserRecordCount;
            AddressesBefore = AddressCount;
            PhoneNumbersBefore = PhoneNumberCount;
            SIDsBefore = SIDCount;
        }

        public AddressBookStore(DatabaseConnectionSettings databaseConnectionSettings)
        {
            DatabaseConnectionSettings = databaseConnectionSettings;
            ConnectionString = DatabaseConnectionSettings.BuildConnectionString();
            ResetTrackingCounts();
        }

        public void LogSummary()
        {
            logger.Info("");
            logger.Info("Summary:");
            logger.Info("User Records: " + UserRecordCount + " (+" + (UserRecordCount - UsersBefore) + ")");
            logger.Info("Users Processed from Input: " + ProcessedUsers);
            logger.Info("  Users Updated: " + UpdatedUsers);
            logger.Info("    Users Added: " + AddedUsers);
            logger.Info("  User Warnings: " + WarnedUsers);
            logger.Info("  Users Errored: " + ErroredUsers);
            logger.Info("");
            logger.Info("    Total Addresses: " + AddressCount + " (+"+(AddressCount - AddressesBefore)+")");
            logger.Info("         Total SIDs: " + SIDCount + " (+" + (SIDCount - SIDsBefore) + ")");
            logger.Info("Total Phone Numbers: " + PhoneNumberCount + " (+" + (PhoneNumberCount - PhoneNumbersBefore) + ")");
        }

        private AddressBookUserRecord initializeUserRecordFromReader(SqlDataReader reader)
        {
            AddressBookUserRecord userRecord = new AddressBookUserRecord();
            userRecord.Store = this;
            userRecord.DatabaseID = (int)reader["ID"];
            userRecord.EmployeeID = (String)reader["EmployeeID"];
            userRecord.Name = (String)reader["Name"];
            userRecord.Title = (String)reader["Title"];
            userRecord.Department = (String)reader["Department"];
            userRecord.Location = (String)reader["Location"];
            logger.Info("Loaded user from address book: DBID: " + userRecord.DatabaseID + ", EmployeeID: " + userRecord.EmployeeID + ", Name: " + userRecord.Name);
            return userRecord;
        }

        private void populateSecondaryDataForUserRecord(AddressBookUserRecord userRecord)
        {
            Dictionary<string, object> bindData = new Dictionary<string, object>();
            bindData.Add("@id", userRecord.DatabaseID);
            string addressQuery = "SELECT Address FROM UserAddress WHERE UserRecordID = @id";
            string phoneQuery = "SELECT PhoneNumber FROM UserPhoneNumber WHERE UserRecordID = @id";
            string sidQuery = "SELECT SID FROM UserSID WHERE UserRecordID = @id";

            ExecuteQuery(addressQuery, (reader) => { userRecord.AddAddress((string)reader["Address"]); }, bindData);
            ExecuteQuery(phoneQuery, (reader) => { userRecord.AddPhoneNumber((string)reader["PhoneNumber"]); }, bindData);
            ExecuteQuery(sidQuery, (reader) => { userRecord.AddSID((string)reader["SID"]); }, bindData);
        }

        public int UserRecordCount { get { return ExecuteScalar<int>("SELECT COUNT(1) FROM UserRecord"); } }
        public int AddressCount { get { return ExecuteScalar<int>("SELECT COUNT(1) FROM UserAddress"); } }
        public int SIDCount { get { return ExecuteScalar<int>("SELECT COUNT(1) FROM UserSID"); } }
        public int PhoneNumberCount { get { return ExecuteScalar<int>("SELECT COUNT(1) FROM UserSID"); } }

        public bool EmployeeIdExists(string employeeId, bool caseInsensitive = false)
        {
            logger.Debug("Checking for existence of UserRecord with EmployeeID: " + employeeId);
            Dictionary<string, object> bindData = new Dictionary<string, object>();
            bindData.Add("@id", employeeId);
            int count = ExecuteScalar<int>("SELECT COUNT(1) FROM UserRecord WHERE EmployeeID = @id", bindData);
            logger.Debug("EmployeeID " + employeeId + " Exists: " + (count > 0));
            return count > 0;
        }

        public AddressBookUserRecord GetUserByEmployeeId(string employeeId)
        {
            logger.Info("Loading record for EmployeeId: " + employeeId);
            AddressBookUserRecord userRecord = null;
            Dictionary<string, object> bindData = new Dictionary<string, object>();
            bindData.Add("@id", employeeId);
            ExecuteQuery("SELECT TOP 1 * FROM UserRecord WHERE EmployeeID = @id", (reader) =>
            {
                userRecord = initializeUserRecordFromReader(reader);
            }, bindData);
            populateSecondaryDataForUserRecord(userRecord);
            return userRecord;
        }

        public void AddOrSyncADUser(ActiveDirectoryUserRecord adUser, bool updateUserBaseData = true)
        {
            logger.Info(new String('-', 50));
            logger.Info("Processing user: " + adUser.ToString());
            ProcessedUsers++;

            if (string.IsNullOrWhiteSpace(adUser.EmployeeID))
            {
                logger.Warn("User record for `"+adUser.Name+"` does not have an Employee ID (a required value), AD user will be skipped");
                WarnedUsers++;
                return;
            }

            if (EmployeeIdExists(adUser.EmployeeID))
            {
                logger.Info("Syncing data to existing user...");
                AddressBookUserRecord existingUserRecord = GetUserByEmployeeId(adUser.EmployeeID);
                syncToExistingRecord(existingUserRecord, adUser, updateUserBaseData);
            }
            else
            {
                logger.Info("Adding new user...");
                addNewRecord(adUser);
            }
        }

        private void syncToExistingRecord(AddressBookUserRecord addressBookUser, ActiveDirectoryUserRecord adUser, bool updateUserBasedData = true)
        {
            if (!string.Equals(addressBookUser.EmployeeID, adUser.EmployeeID, StringComparison.OrdinalIgnoreCase))
            {
                throw new ArgumentException("ActiveDirectoryUserRecord EmployeeID does not match this AddressBookUserRecord EmployeeID!: " + addressBookUser.EmployeeID + " != " + adUser.EmployeeID);
            }

            Dictionary<string, object> baseRecordChanges = new Dictionary<string, object>();
            List<string> newAddresses = new List<string>();
            List<string> newPhoneNumbers = new List<string>();
            List<string> newSIDs = new List<string>();
            StringBuilder baseUpdateQuery = new StringBuilder();

            if (updateUserBasedData)
            {
                logger.Debug("\tComparing basic user information...");
                if (addressBookUser.Name != adUser.Name)
                {
                    baseRecordChanges.Add("Name", adUser.Name ?? "");
                    logger.Debug("\tName will be updated from '" + addressBookUser.Name + "' to '" + adUser.Name + "'");
                }

                if (addressBookUser.Title != adUser.Title)
                {
                    baseRecordChanges.Add("Title", adUser.Title ?? "");
                    logger.Debug("\tTitle will be update from '" + addressBookUser.Title + "' to '" + adUser.Title + "'");
                }

                if (addressBookUser.Department != adUser.Department)
                {
                    baseRecordChanges.Add("Department", adUser.Department ?? "");
                    logger.Debug("\tDepartment will be updated from '" + addressBookUser.Department + "' to '" + adUser.Department + "'");
                }

                if (addressBookUser.Location != adUser.Location)
                {
                    baseRecordChanges.Add("Location", adUser.Location ?? "");
                    logger.Debug("\tLocation will be updated from '" + addressBookUser.Location + "' to '" + adUser.Location + "'");
                }

                if (baseRecordChanges.Count > 0)
                {
                    baseRecordChanges.Add("RecordLastModified", DateTime.Now);

                    baseUpdateQuery.Append("UPDATE UserRecord SET ");
                    baseUpdateQuery.Append(string.Join(", ", baseRecordChanges.Select(kvp => kvp.Key + " = @" + kvp.Key.ToLower())));
                    baseUpdateQuery.Append(" WHERE ID = @id");

                    baseRecordChanges.Add("ID", addressBookUser.DatabaseID);
                }
            }
            else
            {
                logger.Debug("\tSkipping comparison/update of basic user information");
            }

            foreach (var address in adUser.Addresses) { if (!addressBookUser.HasAddress(address)) { newAddresses.Add(address); } }
            foreach (var phoneNumber in adUser.PhoneNumbers) { if (!addressBookUser.HasPhoneNumber(phoneNumber)) { newPhoneNumbers.Add(phoneNumber); } }
            foreach (var sid in adUser.SIDs) { if (!addressBookUser.HasSID(sid)) { newSIDs.Add(sid); } }

            string insertAddressSql = "INSERT INTO UserAddress (Address,UserRecordID) VALUES (@address,@recordid)";
            string insertPhoneNumberSql = "INSERT INTO UserPhoneNumber (PhoneNumber,UserRecordID) VALUES (@phonenumber,@recordid)";
            string insertSidSql = "INSERT INTO UserSID (SID,UserRecordID) VALUES (@sid,@recordid)";

            bool updatesMade = false;

            using (SqlConnection connection = new SqlConnection(ConnectionString))
            {
                connection.Open();
                // Use transaction to run update as a whole
                using (SqlTransaction transaction = connection.BeginTransaction())
                {
                    try
                    {
                        // Update base values
                        if (updateUserBasedData)
                        {
                            if (baseRecordChanges.Count > 0)
                            {
                                logger.Info("\tUpdating user base data");
                                using (SqlCommand command = new SqlCommand(baseUpdateQuery.ToString(), connection, transaction))
                                {
                                    foreach (var item in baseRecordChanges)
                                    {
                                        command.Parameters.AddWithValue("@" + item.Key.ToLower(), item.Value);
                                    }
                                    command.ExecuteNonQuery();
                                    updatesMade = true;
                                }
                            }
                            else
                            {
                                logger.Info("\tBase data matches, no changes will be made");
                            }
                        }

                        logger.Info("\tDiffs: Addresses: +" + newAddresses.Count + ", Phone Numbers: +" + newPhoneNumbers.Count + ", SIDs: +" + newSIDs.Count);

                        //Add any new addresses
                        if (newAddresses.Count > 0)
                        {
                            foreach (var address in newAddresses)
                            {
                                logger.Debug("\tAdding new address: " + address);
                                using (SqlCommand command = new SqlCommand(insertAddressSql, connection, transaction))
                                {
                                    command.Parameters.AddWithValue("@address", address);
                                    command.Parameters.AddWithValue("@recordid", addressBookUser.DatabaseID);
                                    command.ExecuteNonQuery();
                                    updatesMade = true;
                                }
                            }
                        }

                        //Add any new phone numbers
                        if (newPhoneNumbers.Count > 0)
                        {
                            foreach (var phoneNumber in newPhoneNumbers)
                            {
                                logger.Debug("\tAdding new phone number: " + phoneNumber);
                                using (SqlCommand command = new SqlCommand(insertPhoneNumberSql, connection, transaction))
                                {
                                    command.Parameters.AddWithValue("@phonenumber", phoneNumber);
                                    command.Parameters.AddWithValue("@recordid", addressBookUser.DatabaseID);
                                    command.ExecuteNonQuery();
                                    updatesMade = true;
                                }
                            }
                        }

                        //Add any new SIDs
                        if (newSIDs.Count > 0)
                        {
                            foreach (var sid in newSIDs)
                            {
                                logger.Debug("\tAdding new SID: " + sid);
                                using (SqlCommand command = new SqlCommand(insertSidSql, connection, transaction))
                                {
                                    command.Parameters.AddWithValue("@sid", sid);
                                    command.Parameters.AddWithValue("@recordid", addressBookUser.DatabaseID);
                                    command.ExecuteNonQuery();
                                    updatesMade = true;
                                }
                            }
                        }

                        transaction.Commit();

                        if (updatesMade) { UpdatedUsers++; }
                    }
                    catch (Exception exc)
                    {
                        logger.Error("Error while syncing AD user data to address book database:");
                        logger.Error(exc);
                        logger.Error("\tRolling back transaction");
                        transaction.Rollback();
                        ErroredUsers++;
                        throw;
                    }
                }
            }
        }

        private void addNewRecord(ActiveDirectoryUserRecord adUser)
        {
            string insertUserBaseData = "INSERT INTO UserRecord (EmployeeID,Name,Title,Department,Location,RecordCreated,RecordLastModified) output INSERTED.ID VALUES (@id,@name,@title,@department,@location,@created,@modified)";
            string insertAddressSql = "INSERT INTO UserAddress (Address,UserRecordID) VALUES (@address,@recordid)";
            string insertPhoneNumberSql = "INSERT INTO UserPhoneNumber (PhoneNumber,UserRecordID) VALUES (@phonenumber,@recordid)";
            string insertSidSql = "INSERT INTO UserSID (SID,UserRecordID) VALUES (@sid,@recordid)";

            using (SqlConnection connection = new SqlConnection(ConnectionString))
            {
                connection.Open();
                using (SqlTransaction transaction = connection.BeginTransaction())
                {
                    try
                    {
                        int insertedID = 0;
                        using (SqlCommand command = new SqlCommand(insertUserBaseData, connection, transaction))
                        {
                            command.Parameters.AddWithValue("@id", adUser.EmployeeID);
                            command.Parameters.AddWithValue("@name", adUser.Name ?? "");
                            command.Parameters.AddWithValue("@title", adUser.Title ?? "");
                            command.Parameters.AddWithValue("@department", adUser.Department ?? "");
                            command.Parameters.AddWithValue("@location", adUser.Location ?? "");
                            command.Parameters.AddWithValue("@created", adUser.WhenCreated ?? DateTime.Now);
                            command.Parameters.AddWithValue("@modified", adUser.WhenChanged ?? DateTime.Now);

                            insertedID = (int)command.ExecuteScalar();
                        }

                        logger.Info("\tAddresses: +" + adUser.Addresses.Count + ", Phone Numbers: +" + adUser.PhoneNumbers.Count + ", SIDs: +" + adUser.SIDs.Count);

                        //Add any new addresses
                        foreach (var address in adUser.Addresses)
                        {
                            logger.Debug("\tAdding new address: " + address);
                            using (SqlCommand command = new SqlCommand(insertAddressSql, connection, transaction))
                            {
                                command.Parameters.AddWithValue("@address", address);
                                command.Parameters.AddWithValue("@recordid", insertedID);
                                command.ExecuteNonQuery();
                            }
                        }

                        //Add any new phone numbers
                        foreach (var phoneNumber in adUser.PhoneNumbers)
                        {
                            logger.Debug("\tAdding new phone number: " + phoneNumber);
                            using (SqlCommand command = new SqlCommand(insertPhoneNumberSql, connection, transaction))
                            {
                                command.Parameters.AddWithValue("@phonenumber", phoneNumber);
                                command.Parameters.AddWithValue("@recordid", insertedID);
                                command.ExecuteNonQuery();
                            }
                        }

                        //Add any new SIDs
                        foreach (var sid in adUser.SIDs)
                        {
                            logger.Debug("\tAdding new SID: " + sid);
                            using (SqlCommand command = new SqlCommand(insertSidSql, connection, transaction))
                            {
                                command.Parameters.AddWithValue("@sid", sid);
                                command.Parameters.AddWithValue("@recordid", insertedID);
                                command.ExecuteNonQuery();
                            }
                        }

                        transaction.Commit();

                        AddedUsers++;
                    }
                    catch (Exception exc)
                    {
                        logger.Error("Error while adding new user to address book database:");
                        logger.Error(exc);
                        logger.Error("\tRolling back transaction");
                        transaction.Rollback();
                        ErroredUsers++;
                        throw;
                    }
                }
            }
        }

        #region SQL Boilerplate Methods
        public T ExecuteScalar<T>(string sql, Dictionary<string, object> bindData = null)
        {
            using (SqlConnection connection = new SqlConnection(ConnectionString))
            {
                connection.Open();
                using (SqlCommand command = new SqlCommand(sql, connection))
                {
                    if (bindData != null)
                    {
                        foreach (var entry in bindData)
                        {
                            command.Parameters.AddWithValue(entry.Key, entry.Value);
                        }
                    }

                    object value = command.ExecuteScalar();
                    return (T)value;
                }
            }
        }

        public void ExecuteQuery(string sql, DatabaseRecordCallbackDelegate recordCallback, Dictionary<string, object> bindData = null)
        {
            using (SqlConnection connection = new SqlConnection(ConnectionString))
            {
                connection.Open();
                using (SqlCommand command = new SqlCommand(sql, connection))
                {
                    if (bindData != null)
                    {
                        foreach (var entry in bindData)
                        {
                            command.Parameters.AddWithValue(entry.Key, entry.Value);
                        }
                    }

                    using (SqlDataReader reader = command.ExecuteReader())
                    {
                        while (reader.Read())
                        {
                            recordCallback(reader);
                        }
                    }
                }
            }
        }

        public int ExecuteUpdate(string sql, Dictionary<string, object> bindData)
        {
            using (SqlConnection connection = new SqlConnection(ConnectionString))
            {
                connection.Open();
                using (SqlCommand command = new SqlCommand(sql, connection))
                {
                    if (bindData != null)
                    {
                        foreach (var entry in bindData)
                        {
                            command.Parameters.AddWithValue(entry.Key, entry.Value);
                        }
                    }

                    return command.ExecuteNonQuery();
                }
            }
        }
        #endregion
    }
}
