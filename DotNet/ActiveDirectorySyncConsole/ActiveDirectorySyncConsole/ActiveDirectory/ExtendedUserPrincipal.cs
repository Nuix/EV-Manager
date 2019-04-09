using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.DirectoryServices.AccountManagement;
using System.Text.RegularExpressions;

namespace ActiveDirectorySyncConsole.ActiveDirectory
{
    [DirectoryRdnPrefix("CN")]
    [DirectoryObjectClass("User")]
    public class ExtendedUserPrincipal : UserPrincipal
    {
        public static bool CollectX500ProxyAddresses { get; set; }
        public static bool CollectX400ProxyAddresses { get; set; }
        public static bool CollectSmtpProxyAddresses { get; set; }
        public static bool CollectSipProxyAddresses { get; set; }

        private static Regex x500header = new Regex("^x500:(.*)", RegexOptions.Compiled | RegexOptions.IgnoreCase);
        private static Regex x400header = new Regex("^x400:(.*)", RegexOptions.Compiled | RegexOptions.IgnoreCase);
        private static Regex smtpheader = new Regex("^smtp:(.*)", RegexOptions.Compiled | RegexOptions.IgnoreCase);
        private static Regex sipheader = new Regex("^sip:(.*)", RegexOptions.Compiled | RegexOptions.IgnoreCase);

        public static bool EmailAddressesIncludesMailNicknameProperty { get; set; }
        public static bool EmailAddressesIncludesMailProperty { get; set; }
        public static bool EmailAddressesIncludesProxyAddressesProperty { get; set; }

        public static string UniqueIdentifierAttribute { get; set; }

        static ExtendedUserPrincipal()
        {
            CollectX400ProxyAddresses = CollectX500ProxyAddresses = CollectSmtpProxyAddresses = CollectSipProxyAddresses = true;
            EmailAddressesIncludesMailNicknameProperty = EmailAddressesIncludesMailProperty = EmailAddressesIncludesProxyAddressesProperty = true;
        }

        public ExtendedUserPrincipal(PrincipalContext context) : base(context) { }

        public string UniqueIdentifier
        {
            get
            {
                if (string.IsNullOrWhiteSpace(UniqueIdentifierAttribute) || UniqueIdentifierAttribute.Trim().ToLower() == "employeeid")
                {
                    return EmployeeId;
                }
                else
                {
                    string normalizedAttributeName = UniqueIdentifierAttribute.Trim().ToLower();
                    switch (normalizedAttributeName)
                    {
                        case "employeenumber":
                            return EmployeeNumber;
                        default:
                            return "";
                    }
                }
            }
        }

        #region Supported Alternate Unique Identifiers

        [DirectoryProperty("employeeNumber")]
        public string EmployeeNumber
        {
            get
            {
                object[] values = ExtensionGet("employeeNumber");
                if (values.Length < 1)
                    return null;
                else
                    return (string)values[0];
            }
        }

        #endregion

        #region Common Attributes

        [DirectoryProperty("whenChanged")]
        public DateTime? WhenChanged
        {
            get
            {
                object[] values = ExtensionGet("whenChanged");
                if (values.Length < 1)
                    return null;
                else
                    return (DateTime)values[0];
            }
        }

        [DirectoryProperty("whenCreated")]
        public DateTime? WhenCreated
        {
            get
            {
                object[] values = ExtensionGet("whenCreated");
                if (values.Length < 1)
                    return null;
                else
                    return (DateTime)values[0];
            }
        }

        [DirectoryProperty("title")]
        public string Title
        {
            get
            {
                object[] values = ExtensionGet("title");
                if (values.Length < 1)
                    return null;
                else
                    return (string)values[0];
            }
        }

        [DirectoryProperty("department")]
        public string Department
        {
            get
            {
                object[] values = ExtensionGet("department");
                if (values.Length < 1)
                    return null;
                else
                    return (string)values[0];
            }
        }

        [DirectoryProperty("physicalDeliveryOfficeName")]
        public string PhysicalDeliveryOfficeName
        {
            get
            {
                object[] values = ExtensionGet("physicalDeliveryOfficeName");
                if (values.Length < 1)
                    return null;
                else
                    return (string)values[0];
            }
        }

        [DirectoryProperty("mail")]
        public List<string> Mail
        {
            get
            {
                object[] values = ExtensionGet("mail");
                return values.Select(v => v as string).Where(v => !string.IsNullOrWhiteSpace(v)).ToList();
            }
        }

        [DirectoryProperty("mailNickname")]
        public List<string> MailNickname
        {
            get
            {
                object[] values = ExtensionGet("mailNickname");
                return values.Select(v => v as string).Where(v => !string.IsNullOrWhiteSpace(v)).ToList();
            }
        }

        [DirectoryProperty("telephoneNumber")]
        public string TelephoneNumber
        {
            get
            {
                object[] values = ExtensionGet("telephoneNumber");
                if (values.Length < 1)
                    return null;
                else
                    return (string)values[0];
            }
        }

        [DirectoryProperty("proxyAddresses")]
        public List<string> ProxyAddressesRaw
        {
            get
            {
                object[] values = ExtensionGet("proxyAddresses");
                List<string> entries = values.Select(v => v as string).Where(v => !string.IsNullOrWhiteSpace(v)).ToList();
                return entries;
            }
        }

        #endregion

        public List<string> ProxyAddresses
        {
            get
            {
                List<string> entries = ProxyAddressesRaw;
                List<string> result = new List<string>();
                foreach (string entry in entries)
                {
                    if (smtpheader.IsMatch(entry)) { result.Add(smtpheader.Match(entry).Groups[1].Value); }
                    else if (x400header.IsMatch(entry)) { result.Add(x400header.Match(entry).Groups[1].Value); }
                    else if (x500header.IsMatch(entry)) { result.Add(x500header.Match(entry).Groups[1].Value); }
                    else if (sipheader.IsMatch(entry)) { result.Add(sipheader.Match(entry).Groups[1].Value); }
                }
                return result;
            }
        }

        public List<string> EmailAddresses
        {
            get
            {
                HashSet<string> distinctAddresses = new HashSet<string>(StringComparer.OrdinalIgnoreCase);
                if (EmailAddressesIncludesProxyAddressesProperty)
                {
                    foreach (var address in ProxyAddresses)
                    {
                        distinctAddresses.Add(address);
                    }
                }
                if (EmailAddressesIncludesMailProperty)
                {
                    foreach (var address in Mail)
                    {
                        distinctAddresses.Add(address);
                    }
                }
                if (EmailAddressesIncludesMailNicknameProperty)
                {
                    foreach (var address in MailNickname)
                    {
                        distinctAddresses.Add(address);
                    }
                }
                return distinctAddresses.Where(v => !string.IsNullOrWhiteSpace(v)).ToList();
            }
        }
    }
}
