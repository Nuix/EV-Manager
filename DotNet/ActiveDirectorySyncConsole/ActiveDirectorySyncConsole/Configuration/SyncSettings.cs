using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace ActiveDirectorySyncConsole.Configuration
{
    /*
        "SyncSettings":{
		"CollectX500ProxyAddresses": true,
		"CollectX400ProxyAddresses": true,
		"CollectSmtpProxyAddresses": true,
		"CollectSipProxyAddresses": true,
		"UpdateUserInformation": true
	},
    */
    public class SyncSettings
    {
        public bool EmailAddressesIncludesMailNicknameProperty { get; set; }
        public bool EmailAddressesIncludesMailProperty { get; set; }
        public bool EmailAddressesIncludesProxyAddressesProperty { get; set; }
        public bool CollectX500ProxyAddresses { get; set; }
        public bool CollectX400ProxyAddresses { get; set; }
        public bool CollectSmtpProxyAddresses { get; set; }
        public bool CollectSipProxyAddresses { get; set; }
        public bool UpdateUserInformation { get; set; }
    }
}
