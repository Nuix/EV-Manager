using ActiveDirectorySyncConsole.ActiveDirectory;
using ActiveDirectorySyncConsole.Configuration;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace ActiveDirectorySyncConsole.Base
{
    public interface IUserProvider
    {
        IEnumerable<ActiveDirectoryUserRecord> GetUserRecords(ActiveDirectoryServer adServer);
    }
}
