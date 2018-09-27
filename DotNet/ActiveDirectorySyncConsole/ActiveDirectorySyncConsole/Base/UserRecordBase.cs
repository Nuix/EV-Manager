using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace ActiveDirectorySyncConsole.Base
{
    public class UserRecordBase
    {
        public string Name { get; set; }
        public string EmployeeID { get; set; }
        public string Title { get; set; }
        public string Department { get; set; }
        public string Location { get; set; }

        public DateTime? WhenCreated { get; set; }
        public DateTime? WhenChanged { get; set; }

        public List<string> Addresses { get; set; }
        public List<string> PhoneNumbers { get; set; }
        public List<string> SIDs { get; set; }

        public UserRecordBase()
        {
            Addresses = new List<string>();
            PhoneNumbers = new List<string>();
            SIDs = new List<string>();
        }
    }
}
