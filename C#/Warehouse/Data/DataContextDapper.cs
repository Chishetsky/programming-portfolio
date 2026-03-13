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
        //VYTVORENIE SPOJENIA S DB
        private IDbConnection CreateConnection()
        {
            return new SqlConnection(_config.GetConnectionString("DefaultConnection"));
        }
        //NACITANIE VIACERYCH DAT
        public IEnumerable<T> LoadData<T>(string sql)
        {
            using (IDbConnection connection = CreateConnection())
            {
                return connection.Query<T>(sql);
            }
        }
        //NACIATANIE JEDNHEO ZAZNAMU
        public T LoadDataSingle<T>(string sql)
        {
            using (IDbConnection connection = CreateConnection())
            {
                return connection.QuerySingle<T>(sql);
            }
        }
        //VYKONANIE SQL DOTAZU
        public bool ExecuteSql(string sql)
        {
            using (IDbConnection connection = CreateConnection())
            {
                return connection.Execute(sql) > 0;
            }
        }
        //POCET EDITOVANYCH RIADKOV
        public int ExecuteSqlWithRowCount(string sql)
        {
            using (IDbConnection connection = CreateConnection())
            {
                return connection.Execute(sql);
            }
        }
        //ZABEZPECENIE PROTI SQL INJECTION
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
