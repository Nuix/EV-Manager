using NLog;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace ActiveDirectorySyncConsole.AddressBook
{
    public class AddressBookUserRecord
    {
        private static Logger logger = LogManager.GetLogger("AddressBookUserRecord");

        public AddressBookStore Store { get; set; }
        public int DatabaseID { get; set; }

        public string Name { get; set; }
        public string EmployeeID { get; set; }
        public string Title { get; set; }
        public string Department { get; set; }
        public string Location { get; set; }

        public List<string> Addresses { get; set; }
        public List<string> PhoneNumbers { get; set; }
        public List<string> SIDs { get; set; }

        private HashSet<string> addressLookup = new HashSet<string>(StringComparer.OrdinalIgnoreCase);
        private HashSet<string> phoneNumberLookup = new HashSet<string>(StringComparer.OrdinalIgnoreCase);
        private HashSet<string> sidLookup = new HashSet<string>(StringComparer.OrdinalIgnoreCase);

        public AddressBookUserRecord()
        {
            Addresses = new List<string>();
            PhoneNumbers = new List<string>();
            SIDs = new List<string>();
        }

        public void AddAddress(string address)
        {
            address = address.Trim();
            if (!HasAddress(address))
            {
                Addresses.Add(address);
                addressLookup.Add(address);
            }
        }

        public void AddPhoneNumber(string phoneNumber)
        {
            phoneNumber = phoneNumber.Trim();
            if (!HasPhoneNumber(phoneNumber))
            {
                PhoneNumbers.Add(phoneNumber);
                phoneNumberLookup.Add(phoneNumber);
            }
        }

        public void AddSID(string sid)
        {
            sid = sid.Trim();
            if (!HasSID(sid))
            {
                SIDs.Add(sid);
                sidLookup.Add(sid);
            }
        }

        public bool HasAddress(string address)
        {
            string normalized = address.Trim().ToLower();
            return addressLookup.Contains(normalized);
        }

        public bool HasPhoneNumber(string phoneNumber)
        {
            string normalized = phoneNumber.Trim().ToLower();
            return phoneNumberLookup.Contains(normalized);
        }

        public bool HasSID(string sid)
        {
            string normalized = sid.Trim().ToLower();
            return sidLookup.Contains(normalized);
        }
    }
}
