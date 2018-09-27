using ActiveDirectorySyncConsole.ActiveDirectory;
using ActiveDirectorySyncConsole.Base;
using ActiveDirectorySyncConsole.Configuration;
using Csv;
using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace ActiveDirectorySyncConsole.Test
{
    public class CsvUserRecordProvider : IUserProvider
    {
        public string CsvFilePath { get; private set; }

        public CsvUserRecordProvider(string csvFilePath)
        {
            CsvFilePath = csvFilePath;
        }

        public IEnumerable<ActiveDirectoryUserRecord> GetUserRecords(ActiveDirectoryServer adServer)
        {
            using (FileStream fs = new FileStream(CsvFilePath,FileMode.Open))
            {
                foreach (var record in CsvReader.ReadFromStream(fs))
                {
                    ActiveDirectoryUserRecord userRecord = new ActiveDirectoryUserRecord()
                    {
                        EmployeeID = record["EmployeeID"],
                        Name = record["Name"],
                        Title = record["Title"],
                        Department = record["Department"],
                        Location = record["Location"],
                        Addresses = record["EmailAddresses"].Split(new string[] { "; " }, StringSplitOptions.RemoveEmptyEntries).ToList(),
                        PhoneNumbers = record["PhoneNumbers"].Split(new string[] { "; " }, StringSplitOptions.RemoveEmptyEntries).ToList(),
                        SIDs = record["SIDs"].Split(new string[] { "; " }, StringSplitOptions.RemoveEmptyEntries).ToList(),
                    };
                    yield return userRecord;
                }
            }

        }
    }
}
