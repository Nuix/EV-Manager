using ActiveDirectorySyncConsole.ActiveDirectory;
using ActiveDirectorySyncConsole.Base;
using ActiveDirectorySyncConsole.Configuration;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace ActiveDirectorySyncConsole.Test
{
    public class FauxActiveDirectoryStore : IUserProvider
    {
        public IEnumerable<ActiveDirectoryUserRecord> GetUserRecords(ActiveDirectoryServer adServer)
        {
            int sequence = 0;
            ActiveDirectoryUserRecord record = null;

            record = new ActiveDirectoryUserRecord()
            {
                EmployeeID = (++sequence).ToString("0000000#"),
                Name = "Doe, John",
                Department = "IT",
                Title = "IT Manager",
                Location = "San Francisco",
            };
            record.Addresses.AddRange(new string[]{
                "j.doe@company.com",
            });
            record.PhoneNumbers.AddRange(new string[]{
                "1 555-555-1234",
            });
            record.SIDs.AddRange(new string[]{
                "",
            });
            yield return record;
        }
    }
}
