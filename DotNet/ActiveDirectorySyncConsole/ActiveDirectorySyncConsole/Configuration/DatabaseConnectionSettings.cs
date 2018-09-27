using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace ActiveDirectorySyncConsole.Configuration
{
    /*
       "AddressBookDatabaseConnectionSettings":{
		"Server": null,
		"Port": null,
		"Instance": null,
		"User": null,
		"Password": null,
		"Domain": null,
	},
    */
    public class DatabaseConnectionSettings
    {
        public string Server { get; set; }
        public int? Port { get; set; }
        public string Instance { get; set; }
        public string Database { get; set; }
        public string User { get; set; }
        public string Password { get; set; }
        public string Domain { get; set; }

        public string BuildConnectionString()
        {
            if (string.IsNullOrWhiteSpace(Server))
            {
                throw new InvalidOperationException("Server cannot be null or empty.");
            }

            StringBuilder result = new StringBuilder();

            // Server & Instance
            result.Append("Server=" + Server);
            if (Port.HasValue) { result.Append("," + Port.Value.ToString()); }
            if (!string.IsNullOrWhiteSpace(Instance)) { result.Append("\\" + Instance); }
            result.Append(";");

            // Database
            if (!string.IsNullOrWhiteSpace(Database)) { result.Append("Database=" + Database + ";"); }

            //User credentials
            if (!string.IsNullOrWhiteSpace(User) && !string.IsNullOrWhiteSpace(Password))
            {
                if (!string.IsNullOrWhiteSpace(Domain))
                {
                    result.Append("User Id=" + User + "\\" + Domain + ";");
                }
                else
                {
                    result.Append("User Id=" + User + ";");
                }

                result.Append("Password=" + Password + ";");
            }
            else
            {
                result.Append("Trusted_Connection=True;");
            }

            return result.ToString();
        }
    }
}
