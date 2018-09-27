using ActiveDirectorySyncConsole.ActiveDirectory;
using ActiveDirectorySyncConsole.AddressBook;
using ActiveDirectorySyncConsole.Configuration;
using ActiveDirectorySyncConsole.Test;
using CommandLine.Text;
using NLog;
using NLog.Config;
using NLog.Targets;
using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Reflection;
using System.Text;
using System.Text.RegularExpressions;
using System.Threading.Tasks;

namespace ActiveDirectorySyncConsole
{
    class Program
    {
        private static string appDirectory = null;
        private static string settingsFile = null;
        private static Logger logger = LogManager.GetLogger("Main");

        static int Main(string[] args)
        {
            AppDomain.CurrentDomain.UnhandledException += CurrentDomain_UnhandledException;

            /*
             * Return Codes:
             * 0 - No issues
             * 1 - Unexpected exception
             * 2 - Invalid settings file path
             * 3 - Error loading settings file
             * 4 - Error connecting to address book
             * 5 - 1 or more errors while syncing user records
            */

            // Default return code if nothing had an issue
            int returnCode = 0;

            // Default settings file path
            appDirectory = Path.GetDirectoryName(Assembly.GetExecutingAssembly().Location);
            settingsFile = Path.Combine(appDirectory, "Settings.json");

            try
            {
                // Parse command line options
                CommandLineOptions options = new CommandLineOptions();
                bool commandLineOptionsValid = CommandLine.Parser.Default.ParseArguments(args, options);

                // Override settings file location if arguments specify to do so
                if (!string.IsNullOrWhiteSpace(options.SettingsFile) && options.SettingsFile != "None")
                {
                    settingsFile = options.SettingsFile;
                }

                // Make sure settings file we are using exists
                if (!File.Exists(settingsFile))
                {
                    logger.Error("Invalid settings file path: " + settingsFile);
                    return 2;
                }

                // Load settings
                ActiveDirectorySyncSettings adSyncSettings = null;
                try
                {
                    adSyncSettings = ActiveDirectorySyncSettings.FromJsonFile(settingsFile);
                }
                catch (Exception exc)
                {
                    logException("Error Loading Settings File", exc);
                    return 3;
                }
                adSyncSettings.ConfigureExtendedUserPrincipal();

                // Did we have any invalid arguments or did user just ask for argument help screen?  If
                // not we can proceed
                if (commandLineOptionsValid && !options.HelpShown)
                {
                    // Configure logger
                    string timeStamp = DateTime.Now.ToString("yyyy-MM-dd_HH.mm.ss");
                    string mainLogPath = Path.Combine(adSyncSettings.LogDirectory, timeStamp + "_ADSyncLog_All.txt");
                    string errorLogPath = Path.Combine(adSyncSettings.LogDirectory, timeStamp + "_ADSyncLog_ErrorsAndWarnings.txt");
                    configureLogger(mainLogPath, errorLogPath);

                    // Log the arguments provided
                    logger.Info("Arguments: " + string.Join(" ", args));

                    // Is user just asking for a template import CSV?  If so save it and return
                    if (options.TemplateFile != "None" && !string.IsNullOrWhiteSpace(options.TemplateFile))
                    {
                        try
                        {
                            saveTemplateCsv(options);
                        }
                        catch (Exception exc)
                        {
                            logException("Error while Generating Template CSV", exc);
                            return 1;
                        }
                        return 0;
                    }

                    DateTime startTime = DateTime.Now;
                    logger.Info("Starting at " + startTime.ToString());

                    logger.Info("");
                    logger.Info(adSyncSettings.ToString());

                    logger.Debug("");
                    logger.Debug("Machine Info:");
                    logger.Debug("Machine Name: " + Environment.MachineName);
                    logger.Debug("OS: " + Environment.OSVersion);
                    logger.Debug("User Domain: " + Environment.UserDomainName);
                    logger.Debug("User Name: " + Environment.UserName);
                    logger.Info("Processed Record Limit: " + options.Limit);
                    if (!string.IsNullOrWhiteSpace(options.ServerName) && options.ServerName != "None")
                    {
                        logger.Info("Server Name Option: " + options.ServerName);
                    }

                    // If arguments specify to filter AD servers processed we need to filter servers listed in settings file to just those
                    List<ActiveDirectoryServer> serversToExport = new List<ActiveDirectoryServer>();
                    if (!string.IsNullOrWhiteSpace(options.ServerName) && options.ServerName != "None")
                    {
                        serversToExport = adSyncSettings.ActiveDirectoryServers.Where(adServer => adServer.Name.ToLower() == options.ServerName.ToLower()).ToList();
                    }
                    else
                    {
                        serversToExport = adSyncSettings.ActiveDirectoryServers;
                    }

                    // Are we exporting a sample to a file?
                    if (!string.IsNullOrWhiteSpace(options.ExportFile) && options.ExportFile != "None")
                    {
                        exportToFile(serversToExport, options);
                    }
                    else
                    {
                        // Init address book connection and obtain users before count
                        logger.Info("Initializing address book store...");
                        AddressBookStore addressBook = null;
                        try
                        {
                            addressBook = new AddressBookStore(adSyncSettings.AddressBookDatabaseConnectionSettings);
                        }
                        catch (Exception addressBookException)
                        {
                            logException("Error Connecting to Address Book Database", addressBookException);
                            return 4;
                        }

                        // Record users before count so we can report before/after counts later
                        int usersBefore = addressBook.UserRecordCount;

                        // Are we processing from an input file or AD?
                        if (!string.IsNullOrWhiteSpace(options.InputFile) && options.InputFile != "None")
                        {
                            // Processing from input file
                            int recordsIterated = 0;
                            CsvUserRecordProvider provider = new CsvUserRecordProvider(options.InputFile);
                            try
                            {
                                logger.Info(new String('=', 50));
                                logger.Info("Processing: " + options.InputFile);
                                foreach (var adUser in provider.GetUserRecords(null))
                                {
                                    addressBook.AddOrSyncADUser(adUser, adSyncSettings.SyncSettings.UpdateUserInformation);
                                    recordsIterated++;
                                    if (options.Limit > 0 && recordsIterated >= options.Limit)
                                    {
                                        logger.Info("\nObtained maximum record count of " + options.Limit);
                                        break;
                                    }
                                }

                                addressBook.LogSummary();
                            }
                            catch (Exception exc)
                            {
                                logException("Unexpected Exception Occured", exc);
                                returnCode = 1;
                            }
                        }
                        else
                        {
                            // Process from active directory, to address book database, based on settings JSON file
                            returnCode = syncFromActiveDirectory(returnCode, adSyncSettings, addressBook, options);
                            addressBook.LogSummary();
                            if (addressBook.ErroredUsers > 0)
                            {
                                returnCode = 5;
                            }
                        }
                    }

                    logger.Info("");
                    DateTime stopTime = DateTime.Now;
                    logger.Info("Stopping at " + stopTime.ToString());
                    logger.Info("Elapsed: " + (stopTime - startTime).ToString("hh\\:mm\\:ss"));
                }

                logger.Info("Return Code: " + returnCode);

                if (System.Diagnostics.Debugger.IsAttached)
                {
                    Console.WriteLine("Press any key to continue . . .");
                    Console.ReadLine();
                }
            }
            catch (Exception exc)
            {
                logException("Unexpected Exception Occured", exc);
                returnCode = 1;
            }

            return returnCode;
        }

        private static void CurrentDomain_UnhandledException(object sender, UnhandledExceptionEventArgs e)
        {
            logger.Error((Exception)e.ExceptionObject,"Unhandled Exception");
        }

        private static void exportToFile(IEnumerable<ActiveDirectoryServer> serversToExport, CommandLineOptions options)
        {
            logger.Info("Export File: " + options.ExportFile);
            Regex csvExtension = new Regex("\\.csv$", RegexOptions.Compiled | RegexOptions.IgnoreCase);

            if (csvExtension.IsMatch(options.ExportFile))
            {
                logger.Info("Exporting to CSV file...");
                try
                {
                    ActiveDirectoryUserProvider reader = new ActiveDirectoryUserProvider();
                    reader.DumpUserRecordsToCSV(options.ExportFile, serversToExport, options.Limit);
                }
                catch (Exception csvException)
                {
                    logException("Error while exporting AD records to CSV file", csvException);
                }
            }
            else
            {
                logger.Info("Exporting to text file...");
                try
                {
                    ActiveDirectoryUserProvider reader = new ActiveDirectoryUserProvider();
                    reader.DumpUserRecordsToText(options.ExportFile, serversToExport, options.Limit);
                }
                catch (Exception txtException)
                {
                    logException("Error while exporting AD records to text file", txtException);
                }
            }
        }

        private static void logException(string brief, Exception exc)
        {
            string message = brief + ": " + exc.Message + " (see logs for detail)";
            logger.Error("\n*** " +  message + "\n");
            logger.Debug(exc);
        }

        private static int syncFromActiveDirectory(int returnCode, ActiveDirectorySyncSettings adSyncSettings, AddressBookStore addressBook, CommandLineOptions options)
        {
            logger.Info("Initializing active directory reader...");
            ActiveDirectoryUserProvider reader = new ActiveDirectoryUserProvider();
            int recordsIterated = 0;
            try
            {
                foreach (var adServer in adSyncSettings.ActiveDirectoryServers)
                {
                    if (!string.IsNullOrWhiteSpace(options.ServerName) && options.ServerName != "None" && adServer.Name != options.ServerName)
                    {
                        // If user provides a specific server name, then skip all other servers
                        logger.Warn("Skipping AD server: " + adServer.Name);
                        continue;
                    }

                    logger.Info(new String('=', 50));
                    string displayContextContainer = adServer.ContextContainer;
                    if (string.IsNullOrWhiteSpace(displayContextContainer))
                        displayContextContainer = "No Context Container Provided";
                    logger.Info("Processing: " + adServer.Name + " (" + adServer.Domain + " / " + displayContextContainer + ")");

                    foreach (var adUser in reader.GetUserRecords(adServer))
                    {
                        addressBook.AddOrSyncADUser(adUser, adSyncSettings.SyncSettings.UpdateUserInformation);
                        recordsIterated++;

                        if (options.Limit > 0 && recordsIterated >= options.Limit)
                        {
                            break;
                        }
                    }

                    if (options.Limit > 0 && recordsIterated >= options.Limit)
                    {
                        logger.Info("\nObtained maximum record count of " + options.Limit);
                        break;
                    }
                }
            }
            catch (Exception exc)
            {
                logException("Unexpected Exception Occured", exc);
                returnCode = 1;
            }
            return returnCode;
        }

        private static void saveTemplateCsv(CommandLineOptions options)
        {
            string[] headers = new string[]{
                "EmployeeID",
                "Name",
                "Title",
                "Department",
                "Location",
                "EmailAddresses",
                "PhoneNumbers",
                "SIDs",
            };
            logger.Info("Saving import template CSV to: " + options.TemplateFile);
            File.WriteAllText(options.TemplateFile, string.Join(",", headers.Select(h => "\"" + h + "\"")));
        }

        private static void configureLogger(string allEntriesFileName, string errorEntriesFileName)
        {
            string layoutFormat = @"${date:format=yyyyMMdd HH\:mm\:ss} ${logger}.${level} ${message}";
            var config = new LoggingConfiguration();
            var allEntriesFileTarget = new FileTarget();
            allEntriesFileTarget.FileName = allEntriesFileName;
            allEntriesFileTarget.Layout = layoutFormat;
            config.AddTarget("file", allEntriesFileTarget);

            var errorEntriesFileTarget = new FileTarget();
            errorEntriesFileTarget.FileName = errorEntriesFileName;
            errorEntriesFileTarget.Layout = layoutFormat;
            config.AddTarget("file", errorEntriesFileTarget);

            MethodCallTarget methodTarget = new MethodCallTarget();
            methodTarget.ClassName = typeof(LogEventBroadcaster).AssemblyQualifiedName;
            methodTarget.MethodName = "log";
            methodTarget.Parameters.Add(new MethodCallParameter("${message}"));
            config.AddTarget("broadcaster", methodTarget);

            var rule1 = new LoggingRule("*", LogLevel.Info, methodTarget);
            config.LoggingRules.Add(rule1);

            var rule2 = new LoggingRule("*", LogLevel.Trace, allEntriesFileTarget);
            config.LoggingRules.Add(rule2);

            var rule3 = new LoggingRule("*", LogLevel.Warn, errorEntriesFileTarget);
            config.LoggingRules.Add(rule3);

            LogManager.Configuration = config;
        }
    }
}
