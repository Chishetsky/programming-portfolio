using Microsoft.AspNetCore.Mvc;
using projekt.Data;
using projekt.Models;
using System;
using System.Collections.Generic;

namespace projekt.Controllers
{
    [ApiController]
    [Route("[controller]")]
    public class OrderController : ControllerBase
    {
        private readonly DataContextDapper _dapper;

        public OrderController(IConfiguration config)
        {
            _dapper = new DataContextDapper(config);
        }

        [HttpGet("TestConnection")]
        public IActionResult TestConnection()
        {
            return Ok(_dapper.LoadDataSingle<DateTime>("SELECT GETDATE()"));
        }

        [HttpGet("GetOrders")]
        public IActionResult GetOrders()
        {
            string sql = @"
                SELECT [order_id],
                       [user_id],
                       [order_date],
                       [status]
                FROM [Order]";
            return Ok(_dapper.LoadData<Order>(sql));
        }

        [HttpGet("GetOrder/{order_id}")]
        public IActionResult GetOrder(int order_id)
        {
            string sql = $@"
                SELECT [order_id],
                       [user_id],
                       [order_date],
                       [status]
                FROM [Order]
                WHERE order_id = {order_id}";
            return Ok(_dapper.LoadDataSingle<Order>(sql));
        }

        [HttpPost("AddOrder")]
        public IActionResult AddOrder(Order order)
        {
            string sql = $@"
                INSERT INTO [Order] ([user_id], [order_date], [status])
                VALUES ({order.user_id}, '{order.order_date}', '{order.status}')";
            return ExecuteSql(sql);
        }

        [HttpPut("EditOrder")]
        public IActionResult EditOrder(Order order)
        {
            string sql = $@"
                UPDATE [Order]
                SET [user_id] = {order.user_id},
                    [order_date] = '{order.order_date}',
                    [status] = '{order.status}'
                WHERE [order_id] = {order.order_id}";
            return ExecuteSql(sql);
        }

        [HttpDelete("DeleteOrder/{order_id}")]
        public IActionResult DeleteOrder(int order_id)
        {
            string sql = $@"
                DELETE FROM [Order]
                WHERE order_id = {order_id}";
            return ExecuteSql(sql);
        }

        private IActionResult ExecuteSql(string sql)
        {
            if (_dapper.ExecuteSql(sql))
                return Ok();
            throw new Exception("Failed to execute SQL query");
        }
    }
}
