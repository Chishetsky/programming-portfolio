using System.Data;
using Dapper;
using Microsoft.Data.SqlClient;

namespace projekt.Data
{
    class DataContextDapper
    {
        private readonly IConfiguration _config;

        public DataContextDapper(IConfiguration config)
        {
            _config = config;
        }

        private IDbConnection CreateConnection()
        {
            return new SqlConnection(_config.GetConnectionString("DefaultConnection"));
        }

        public IEnumerable<T> LoadData<T>(string sql)
        {
            using (IDbConnection connection = CreateConnection())
            {
                return connection.Query<T>(sql);
            }
        }

        public T LoadDataSingle<T>(string sql)
        {
            using (IDbConnection connection = CreateConnection())
            {
                return connection.QuerySingle<T>(sql);
            }
        }

        public bool ExecuteSql(string sql)
        {
            using (IDbConnection connection = CreateConnection())
            {
                return connection.Execute(sql) > 0;
            }
        }

        public int ExecuteSqlWithRowCount(string sql)
        {
            using (IDbConnection connection = CreateConnection())
            {
                return connection.Execute(sql);
            }
        }

        public bool ExecuteSqlWithParameters(string sql, List<SqlParameter> parameters)
        {
            using (SqlConnection connection = new SqlConnection(_config.GetConnectionString("DefaultConnection")))
            {
                using (SqlCommand command = new SqlCommand(sql, connection))
                {
                    foreach (SqlParameter param in parameters)
                    {
                        command.Parameters.Add(param);
                    }

                    connection.Open();
                    int rowsAffected = command.ExecuteNonQuery();
                    return rowsAffected > 0;
                }
            }
        }
    }
}
