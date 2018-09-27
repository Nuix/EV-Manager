using ActiveDirectorySyncConsole.Base;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace ActiveDirectorySyncConsole.ActiveDirectory
{
    public class ActiveDirectoryUserRecord : UserRecordBase
    {
        public ExtendedUserPrincipal SourceUserPrincipal { get; set; }

        public override string ToString()
        {
            return "Employeed ID: " + EmployeeID + ", Name: " + Name + ", Addresses: " + Addresses.Count + ", Phone Numbers: " + PhoneNumbers.Count + ", SIDs: " + SIDs.Count;
        }
    }
}
