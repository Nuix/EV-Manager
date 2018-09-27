using ActiveDirectorySyncConsole.Base;
using ActiveDirectorySyncConsole.Configuration;
using NLog;
using System;
using System.Collections.Generic;
using System.DirectoryServices.AccountManagement;
using System.IO;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace ActiveDirectorySyncConsole.ActiveDirectory
{
    public class ActiveDirectoryUserProvider : IUserProvider
    {
        private static Logger logger = LogManager.GetLogger("ActiveDirectoryStore");
        public delegate void ProgressCallbackDelegate(int recordNumber);

        public ActiveDirectoryUserProvider()
        {
        }

        public IEnumerable<ExtendedUserPrincipal> GetUsersEntries(ActiveDirectoryServer adServer)
        {
            logger.Info(new String('=', 50));
            if (string.IsNullOrWhiteSpace(adServer.Domain))
            {
                logger.Info("Domain value is null/empty, using process domain...");
                adServer.UseMachineDomain();
            }

            logger.Info("Domain: " + adServer.Domain);

            if (!string.IsNullOrWhiteSpace(adServer.ContextContainer)) { logger.Info("Context Container: " + adServer.ContextContainer); }
            else { logger.Info("Context Container: None Provided"); }

            logger.Info("Initializing PrincipalContext...");
            using (PrincipalContext context = obtainContext(adServer))
            {
                logger.Info("Initializing PrincipalSearcher...");
                using (var searcher = new PrincipalSearcher(new ExtendedUserPrincipal(context)))
                {
                    logger.Info("Beginning UserPrincipal iteration...");
                    foreach (ExtendedUserPrincipal userEntry in searcher.FindAll())
                    {
                        yield return userEntry;
                    }
                }
            }
        }

        protected PrincipalContext obtainContext(ActiveDirectoryServer adServer)
        {
            // Did they provide username and password?
            if (adServer.UserName != null && adServer.Password != null)
            {
                // Did they provide a context container?
                if (!string.IsNullOrWhiteSpace(adServer.ContextContainer))
                {
                    return new PrincipalContext(ContextType.Domain, adServer.Domain, adServer.ContextContainer, adServer.UserName, adServer.Password);
                }
                else
                {
                    return new PrincipalContext(ContextType.Domain, adServer.Domain, null, adServer.UserName, adServer.Password);
                }
            }
            else // No user credentials
            {
                // Did they provide a context container?
                if (!string.IsNullOrWhiteSpace(adServer.ContextContainer))
                {
                    return new PrincipalContext(ContextType.Domain, adServer.Domain, adServer.ContextContainer);
                }
                else
                {
                    return new PrincipalContext(ContextType.Domain, adServer.Domain, null);
                }
            }
        }

        public IEnumerable<ActiveDirectoryUserRecord> GetUserRecords(ActiveDirectoryServer adServer)
        {
            foreach (var userEntry in GetUsersEntries(adServer))
            {
                ActiveDirectoryUserRecord result = new ActiveDirectoryUserRecord();
                
                result.SourceUserPrincipal = userEntry;
                string dn = userEntry.DistinguishedName;
                result.Name = userEntry.DisplayName ?? "";
                result.EmployeeID = userEntry.EmployeeId ?? "";
                result.Title = userEntry.Title ?? "";
                result.Department = userEntry.Department ?? "";
                result.Location = userEntry.PhysicalDeliveryOfficeName ?? "";
                result.Addresses = userEntry.EmailAddresses;

                if (!string.IsNullOrWhiteSpace(userEntry.TelephoneNumber))
                    result.PhoneNumbers.Add(userEntry.TelephoneNumber);

                if (userEntry.Sid != null)
                    result.SIDs.Add(userEntry.Sid.ToString());

                if (!string.IsNullOrWhiteSpace(userEntry.TelephoneNumber))
                    result.PhoneNumbers.Add(userEntry.TelephoneNumber);

                //logger.Debug("Employeed ID: " + result.EmployeeID);

                yield return result;
            }
        }

        public void DumpUserRecordsToCSV(string csvFilePath, IEnumerable<ActiveDirectoryServer> adServers, int maxRecords = 0)
        {
            int recordsIterated = 0;
            SimpleCSVWriter.BeginWriting(csvFilePath, (writer) =>
            {
                writer.write(new string[]{
                    "RecordNumber",
                    "Domain",
                    "Name",
                    "EmployeeID",
                    "Title",
                    "Department",
                    "Location",
                    "EmailAddresses",
                    "PhoneNumbers",
                    "SIDs"
                });

                foreach (var adServer in adServers)
                {
                    foreach (var userRecord in GetUserRecords(adServer))
                    {
                        recordsIterated++;
                        logger.Info("Recording record " + recordsIterated.ToString());
                        writer.write(new string[]{
                            recordsIterated.ToString(),
                            adServer.Domain,
                            userRecord.Name,
                            userRecord.EmployeeID,
                            userRecord.Title,
                            userRecord.Department,
                            userRecord.Location,
                            string.Join("; ",userRecord.Addresses),
                            string.Join("; ",userRecord.PhoneNumbers),
                            string.Join("; ",userRecord.SIDs)
                        });

                        if (maxRecords > 0 && recordsIterated >= maxRecords) { break; }
                    }

                    if (maxRecords > 0 && recordsIterated >= maxRecords)
                    {
                        logger.Info("\nObtained maximum record count of " + maxRecords.ToString());
                        break;
                    }
                }
            });
        }

        public void DumpUserRecordsToText(string textFilePath, IEnumerable<ActiveDirectoryServer> adServers, int maxRecords = 0)
        {
            using (StreamWriter sw = new StreamWriter(textFilePath))
            {
                sw.WriteLine("EmailAddressesIncludesMailNicknameProperty: " + ExtendedUserPrincipal.EmailAddressesIncludesMailNicknameProperty);
                sw.WriteLine("EmailAddressesIncludesMailProperty: " + ExtendedUserPrincipal.EmailAddressesIncludesMailProperty);
                sw.WriteLine("EmailAddressesIncludesProxyAddressesProperty: " + ExtendedUserPrincipal.EmailAddressesIncludesProxyAddressesProperty);
                sw.WriteLine("CollectX400ProxyAddresses: " + ExtendedUserPrincipal.CollectX400ProxyAddresses);
                sw.WriteLine("CollectX500ProxyAddresses: " + ExtendedUserPrincipal.CollectX500ProxyAddresses);
                sw.WriteLine("CollectSipProxyAddresses: " + ExtendedUserPrincipal.CollectSipProxyAddresses);
                sw.WriteLine("CollectSmtpProxyAddresses: " + ExtendedUserPrincipal.CollectSmtpProxyAddresses);

                int recordsIterated = 0;
                foreach (var adServer in adServers)
                {
                    sw.WriteLine("");
                    sw.WriteLine(new String('*', 40));
                    sw.WriteLine("Domain: " + adServer.Domain);
                    sw.WriteLine("Context Container: " + adServer.ContextContainer);

                    foreach (var userRecord in GetUserRecords(adServer))
                    {
                        recordsIterated++;
                        logger.Info("Recording record " + recordsIterated.ToString());

                        sw.WriteLine("");
                        sw.WriteLine(new String('=', 40));
                        sw.WriteLine("Record Number: " + recordsIterated);
                        sw.WriteLine("Name: " + userRecord.Name);
                        sw.WriteLine("EmployeeID: " + userRecord.EmployeeID);
                        sw.WriteLine("Title: " + userRecord.Title);
                        sw.WriteLine("Department: " + userRecord.Department);
                        sw.WriteLine("Location: " + userRecord.Location);

                        sw.WriteLine("\nPhone Numbers:");
                        foreach (var entry in userRecord.PhoneNumbers)
                        {
                            sw.WriteLine("\t" + entry);
                        }

                        sw.WriteLine("\nSIDs:");
                        foreach (var entry in userRecord.SIDs)
                        {
                            sw.WriteLine("\t" + entry);
                        }

                        sw.WriteLine("\nAddresses:");
                        foreach (var entry in userRecord.Addresses)
                        {
                            sw.WriteLine("\t" + entry);
                        }

                        sw.WriteLine("");
                        sw.WriteLine(new String('-', 32));
                        sw.WriteLine("Email Address Field Break Down:");
                        sw.WriteLine(new String('-', 32));

                        ExtendedUserPrincipal eup = userRecord.SourceUserPrincipal;

                        sw.WriteLine("\nproxyAddresses (raw):");
                        foreach (string value in eup.ProxyAddressesRaw)
                        {
                            sw.WriteLine("\t" + value);
                        }

                        sw.WriteLine("\nproxyAddresses (parsed):");
                        foreach (string value in eup.ProxyAddresses)
                        {
                            sw.WriteLine("\t" + value);
                        }

                        sw.WriteLine("\nmail:");
                        foreach (string value in eup.Mail)
                        {
                            sw.WriteLine("\t" + value);
                        }

                        sw.WriteLine("\nmailNickname:");
                        foreach (string value in eup.MailNickname)
                        {
                            sw.WriteLine("\t" + value);
                        }

                        if (maxRecords > 0 && recordsIterated >= maxRecords) { break; }
                    }

                    if (maxRecords > 0 && recordsIterated >= maxRecords)
                    {
                        logger.Info("\nObtained maximum record count of " + maxRecords.ToString());
                        break;
                    }
                }
            }
        }
    }
}
