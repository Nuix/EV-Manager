using CommandLine;
using CommandLine.Text;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace ActiveDirectorySyncConsole.Configuration
{
    public class CommandLineOptions
    {
        public CommandLineOptions()
        {
            HelpShown = false;
        }

        [Option('t', "templatefile", DefaultValue = "None", HelpText = "When specified the tool will save a blank template CSV (suitable for import) to the location specified.")]
        public string TemplateFile { get; set; }

        [Option('i',"inputfile", DefaultValue = "None", HelpText="Used to specify a CSV file to import.  When this option is provided, import will not occur from active directory.")]
        public string InputFile { get; set; }

        [Option('e', "exportfile", DefaultValue = "None", HelpText = "Used to specify an file to export to.  When this option is provided, entries will be read from Active Directory but written to the output file rather than the address book database.  Supports extensions: CSV, TXT")]
        public string ExportFile { get; set; }

        [Option('l', "limit", DefaultValue = 0, HelpText = "Used to specify a maximum number of records to process.  A value of 0 means no limit.")]
        public int Limit { get; set; }

        [Option('s', "settingsfile", DefaultValue = "None", HelpText = "Used to specify an alternate settings JSON file.  If not provided default is to look for Settings.json in same directory as executable.")]
        public string SettingsFile { get; set; }

        [Option('n', "servername", DefaultValue = "None", HelpText = "When provided, only the Active Directory server in the settings file with the provided name will be processed.")]
        public string ServerName { get; set; }

        [HelpOption]
        public string GetUsage()
        {
            var help = new HelpText
            {
                Heading = new HeadingInfo("Active Directory Sync Console", "1.3"),
                Copyright = new CopyrightInfo("Nuix", DateTime.Now.Year),
                AdditionalNewLineAfterOption = true,
                AddDashesToOption = true,
                MaximumDisplayWidth = Console.BufferWidth,
            };
            help.AddPreOptionsLine("Usage: ActiveDirectorySyncConsole.exe [options]");
            help.AddOptions(this);
            help.AddPostOptionsLine("Return Codes:\n");
            help.AddPostOptionsLine("0 - No issues");
            help.AddPostOptionsLine("1 - Unexpected exception");
            help.AddPostOptionsLine("2 - Invalid settings file path");
            help.AddPostOptionsLine("3 - Error loading settings file");
            help.AddPostOptionsLine("4 - Error connecting to address book database");
            help.AddPostOptionsLine("\n");
            
            HelpShown = true;
            return help;
        }

        public bool HelpShown { get; private set; }
    }
}
