using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace ActiveDirectorySyncConsole
{
    public static class LogEventBroadcaster
    {
        public delegate void MessageLoggedDel(string message);
        public static event MessageLoggedDel MessageLogged;
        public static void log(string message)
        {
            if (MessageLogged != null)
                MessageLogged(message);

            Console.WriteLine(message);
        }
    }
}
