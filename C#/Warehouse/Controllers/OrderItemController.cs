using Microsoft.AspNetCore.Mvc;
using projekt.Data;
using projekt.Models;
using System;
using System.Collections.Generic;

namespace projekt.Controllers
{
    [ApiController]
    [Route("[controller]")]
    public class OrderItemController : ControllerBase
    {
        private readonly DataContextDapper _dapper;

        public OrderItemController(IConfiguration config)
        {
            _dapper = new DataContextDapper(config);
        }

        [HttpGet("TestConnection")]
        public IActionResult TestConnection()
        {
            return Ok(_dapper.LoadDataSingle<DateTime>("SELECT GETDATE()"));
        }

        [HttpGet("GetOrderItems")]
        public IActionResult GetOrderItems()
        {
            string sql = @"
                SELECT [order_item_id],
                       [order_id],
                       [product_id],
                       [quantity],
                       [price_per_unit]
                FROM [Order_Item]";
            return Ok(_dapper.LoadData<OrderItem>(sql));
        }

        [HttpGet("GetOrderItem/{order_item_id}")]
        public IActionResult GetOrderItem(int order_item_id)
        {
            string sql = $@"
                SELECT [order_item_id],
                       [order_id],
                       [product_id],
                       [quantity],
                       [price_per_unit]
                FROM [Order_Item]
                WHERE order_item_id = {order_item_id}";
            return Ok(_dapper.LoadDataSingle<OrderItem>(sql));
        }

        [HttpPost("AddOrderItem")]
        public IActionResult AddOrderItem(OrderItem orderItem)
        {
            string sql = $@"
                INSERT INTO [Order_Item] ([order_id], [product_id], [quantity], [price_per_unit])
                VALUES ({orderItem.order_id}, {orderItem.product_id}, {orderItem.quantity}, {orderItem.price_per_unit})";
            return ExecuteSql(sql);
        }

        [HttpPut("EditOrderItem")]
        public IActionResult EditOrderItem(OrderItem orderItem)
        {
            string sql = $@"
                UPDATE [Order_Item]
                SET [order_id] = {orderItem.order_id},
                    [product_id] = {orderItem.product_id},
                    [quantity] = {orderItem.quantity},
                    [price_per_unit] = {orderItem.price_per_unit}
                WHERE [order_item_id] = {orderItem.order_item_id}";
            return ExecuteSql(sql);
        }

        [HttpDelete("DeleteOrderItem/{order_item_id}")]
        public IActionResult DeleteOrderItem(int order_item_id)
        {
            string sql = $@"
                DELETE FROM [Order_Item]
                WHERE order_item_id = {order_item_id}";
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
