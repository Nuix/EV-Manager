using ActiveDirectorySyncConsole.ActiveDirectory;
using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace ActiveDirectorySyncConsole.Configuration
{
    public class ActiveDirectorySyncSettings
    {
        public DatabaseConnectionSettings AddressBookDatabaseConnectionSettings { get; set; }
        public SyncSettings SyncSettings { get; set; }
        public string LogDirectory { get; set; }
        public List<ActiveDirectoryServer> ActiveDirectoryServers { get; set; }

        public static ActiveDirectorySyncSettings FromJsonFile(string jsonFile)
        {
            ActiveDirectorySyncSettings result = JsonConvert.DeserializeObject<ActiveDirectorySyncSettings>(File.ReadAllText(jsonFile));
            foreach (var server in result.ActiveDirectoryServers)
            {
                if (string.IsNullOrWhiteSpace(server.Domain)) { server.UseMachineDomain(); }
            }
            return result;
        }

        public void ConfigureExtendedUserPrincipal()
        {
            ExtendedUserPrincipal.CollectX400ProxyAddresses = SyncSettings.CollectX400ProxyAddresses;
            ExtendedUserPrincipal.CollectX500ProxyAddresses = SyncSettings.CollectX500ProxyAddresses;
            ExtendedUserPrincipal.CollectSmtpProxyAddresses = SyncSettings.CollectSmtpProxyAddresses;
            ExtendedUserPrincipal.CollectSipProxyAddresses = SyncSettings.CollectSipProxyAddresses;
            ExtendedUserPrincipal.EmailAddressesIncludesMailNicknameProperty = SyncSettings.EmailAddressesIncludesMailNicknameProperty;
            ExtendedUserPrincipal.EmailAddressesIncludesMailProperty = SyncSettings.EmailAddressesIncludesMailProperty;
            ExtendedUserPrincipal.EmailAddressesIncludesProxyAddressesProperty = SyncSettings.EmailAddressesIncludesProxyAddressesProperty;
            ExtendedUserPrincipal.UniqueIdentifierAttribute = SyncSettings.UniqueIdentifierAttribute;
        }

        public override string ToString()
        {
            StringBuilder result = new StringBuilder();

            result.AppendLine("Address Book Database Connection Settings");
            result.AppendLine("\tServer: " + AddressBookDatabaseConnectionSettings.Server);
            result.AppendLine("\tPort: " + AddressBookDatabaseConnectionSettings.Port);
            result.AppendLine("\tInstance: " + AddressBookDatabaseConnectionSettings.Instance);
            result.AppendLine("\tDatabase: " + AddressBookDatabaseConnectionSettings.Database);
            result.AppendLine("\tUser: " + AddressBookDatabaseConnectionSettings.User);
            result.AppendLine("\tPassword: " + AddressBookDatabaseConnectionSettings.Password);
            result.AppendLine("\tDomain: " + AddressBookDatabaseConnectionSettings.Domain);

            result.AppendLine("");
            result.AppendLine("Sync Settings:");
            result.AppendLine("\tCollectX400ProxyAddresses: " + SyncSettings.CollectX400ProxyAddresses);
            result.AppendLine("\tCollectX500ProxyAddresses: " + SyncSettings.CollectX500ProxyAddresses);
            result.AppendLine("\tCollectSmtpProxyAddresses: " + SyncSettings.CollectSmtpProxyAddresses);
            result.AppendLine("\tCollectSipProxyAddresses: " + SyncSettings.CollectSipProxyAddresses);
            result.AppendLine("\tUniqueIdentifierAttribute (Employee ID source attribute): " + ExtendedUserPrincipal.UniqueIdentifierAttribute);

            result.AppendLine("");
            result.AppendLine("Log Directory: " + LogDirectory);

            result.AppendLine("");
            result.AppendLine("Active Directory Servers:");
            foreach (var server in ActiveDirectoryServers)
            {
                result.AppendLine("Name: " + server.Name);
                result.AppendLine("\tDomain: " + server.Domain);
                result.AppendLine("\tContextContainer: " + server.ContextContainer);
            }

            return result.ToString();
        }
    }
}
