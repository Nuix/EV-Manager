using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace ActiveDirectorySyncConsole
{
    public class SimpleCSVWriter
    {
        public delegate void CsvConsumerDel(SimpleCSVWriter csvWriter);
        private StreamWriter sw;

        private SimpleCSVWriter()
        {

        }

        public static string MakeCSVLine(IEnumerable<object> values)
        {
            List<string> stringValues = values.Select(v =>
            {
                string result = "";
                if (v != null)
                {
                    result = v.ToString();
                }
                result = result.Replace("\"", "\"\"");
                result = "\"" + result + "\"";
                return result;
            }).ToList();
            return string.Join(",", stringValues);
        }

        public static void BeginWriting(string path, CsvConsumerDel consumer)
        {
            SimpleCSVWriter writer = new SimpleCSVWriter();
            using (writer.sw = new StreamWriter(path))
            {
                consumer(writer);
            }
        }

        public void write(IEnumerable<string> values)
        {
            sw.WriteLine(MakeCSVLine(values));
        }
    }
}
