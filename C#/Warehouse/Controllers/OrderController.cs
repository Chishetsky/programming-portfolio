using Microsoft.AspNetCore.Mvc;
using projekt.Data;
using projekt.Models;

namespace projekt.Controllers
{
    [ApiController]
    [Route("[controller]")]
    public class OrderController : ControllerBase
    {
        private readonly DataContextDapper _dapper;

        //DI KONFIGURACIE PRE DATABAZU
        public OrderController(IConfiguration config)
        {
            _dapper = new DataContextDapper(config);
        }
        //HELPER METODA NA VYKONANIE DOTAZU
        private IActionResult ExecuteSql(string sql)
        {
            if (_dapper.ExecuteSql(sql))
                return Ok();

            throw new Exception("SQL execution failed");
        }
        //Endpoint: GET /Order/GetOrders
        [HttpGet("GetOrders")]
        public IActionResult GetOrders()
        {
            string sql = @"
                SELECT [order_id],
                       [user_id],
                       [order_date],
                       [status]
                FROM [Orders]";

            return Ok(_dapper.LoadData<Order>(sql));
        }
        //Endpoint: GET /Order/GetOrder/order_id
        [HttpGet("GetOrder/{order_id}")]
        public IActionResult GetOrder(int order_id)
        {
            string sql = $@"
                SELECT [order_id],
                       [user_id],
                       [order_date],
                       [status]
                FROM [Orders]
                WHERE order_id = {order_id}";

            return Ok(_dapper.LoadDataSingle<Order>(sql));
        }
        //Endpoint: POST /Order/AddOrder
        [HttpPost("AddOrder")]
        public IActionResult AddOrder(Order order)
        {
            string sql = $@"
                INSERT INTO [Orders] ([user_id], [order_date], [status])
                VALUES ({order.user_id}, '{order.order_date}', '{order.status}')";

            return ExecuteSql(sql);
        }
        //Endpoint: PUT /Order/EditOrder
        [HttpPut("EditOrder")]
        public IActionResult EditOrder(Order order)
        {
            string sql = $@"
                UPDATE [Orders]
                SET [user_id] = {order.user_id},
                    [order_date] = '{order.order_date}',
                    [status] = '{order.status}'
                WHERE [order_id] = {order.order_id}";

            return ExecuteSql(sql);
        }
        //Endpoint: DELETE /Order/DeleteOrder/order_id
        [HttpDelete("DeleteOrder/{order_id}")]
        public IActionResult DeleteOrder(int order_id)
        {
            string sql = $@"
                DELETE FROM [Orders]
                WHERE order_id = {order_id}";

            return ExecuteSql(sql);
        }
    }
}
