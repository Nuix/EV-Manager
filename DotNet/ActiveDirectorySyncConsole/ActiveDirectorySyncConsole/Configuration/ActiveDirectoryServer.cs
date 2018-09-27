using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace ActiveDirectorySyncConsole.Configuration
{
    /*
        "ActiveDirectoryServers":[
		    {
			    "Name": "Server 1",
			    "Domain": null,
			    "ContextContainer": null,
                "UserName": null,
                "Password": null
		    },
		    {
			    "Name": "Server 2",
			    "Domain": null,
			    "ContextContainer": null
                "UserName": null,
                "Password": null
		    }
    	]
    */
    public class ActiveDirectoryServer
    {
        public string Name { get; set; }
        public string Domain { get; set; }
        public string ContextContainer { get; set; }

        public string UserName { get; set; }
        public string Password { get; set; }

        public void UseMachineDomain()
        {
            Domain = System.Net.NetworkInformation.IPGlobalProperties.GetIPGlobalProperties().DomainName;
        }
    }
}
