using Microsoft.AspNetCore.Mvc;
using projekt.Data;
using projekt.Dtos;
using projekt.Models;

namespace projekt.Controllers
{
    [ApiController]
    [Route("[controller]")]
    public class ProductController : ControllerBase
    {
        private readonly DataContextDapper _dapper;

        public ProductController(IConfiguration config)
        {
            _dapper = new DataContextDapper(config);
        }

        [HttpGet("TestConnection")]
        public IActionResult TestConnection()
        {
            return Ok(_dapper.LoadDataSingle<DateTime>("SELECT GETDATE()"));
        }

        [HttpGet("GetProducts")]
        public IActionResult GetProducts()
        {
            string sql = @"
                SELECT [product_id],
                       [name],
                       [description],
                       [price],
                       [stock_quantity],
                       [category_id],
                       [DIN],
                       [photo_url]
                FROM [Products]";

            return Ok(_dapper.LoadData<Product>(sql));
        }

        [HttpGet("GetProduct/{product_id}")]
        public IActionResult GetProduct(int product_id)
        {
            string sql = $@"
                SELECT [product_id],
                       [name],
                       [description],
                       [price],
                       [stock_quantity],
                       [category_id],
                       [DIN],
                       [photo_url]
                FROM [Products]
                WHERE [product_id] = {product_id}";

            return Ok(_dapper.LoadDataSingle<Product>(sql));
        }

        [HttpPut("EditProduct")]
        public IActionResult EditProduct(Product product)
        {
            string sql = $@"
                UPDATE [Products]
                SET [name] = '{product.name}',
                    [description] = '{product.description}',
                    [price] = {product.price},
                    [stock_quantity] = {product.stock_quantity},
                    [category_id] = {product.category_id},
                    [DIN] = '{product.DIN}',
                    [photo_url] = '{product.photo_url}'
                WHERE [product_id] = {product.product_id}";

            return ExecuteSql(sql);
        }

        [HttpPost("AddProduct")]
        public IActionResult AddProduct(ProductToAddDto product)
        {
            string sql = $@"
                INSERT INTO [Products] 
                    ([name], [description], [price], [stock_quantity], [category_id], [DIN], [photo_url])
                VALUES 
                    ('{product.name}', '{product.description}', {product.price}, 
                     {product.stock_quantity}, {product.category_id}, '{product.DIN}', '{product.photo_url}')";

            return ExecuteSql(sql);
        }

        [HttpDelete("DeleteProduct/{product_id}")]
        public IActionResult DeleteProduct(int product_id)
        {
            string sql = $@"
                DELETE FROM [Products]
                WHERE [product_id] = {product_id}";

            return ExecuteSql(sql);
        }

        [HttpGet("GetProductsByCategory/{category_id}")]
        public IActionResult GetProductsByCategory(int category_id)
        {
            string sql = $@"
                SELECT [product_id],
                       [name],
                       [description],
                       [price],
                       [stock_quantity],
                       [category_id],
                       [DIN],
                       [photo_url]
                FROM [Products]
                WHERE [category_id] = {category_id}";

            return Ok(_dapper.LoadData<Product>(sql));
        }

        private IActionResult ExecuteSql(string sql)
        {
            if (_dapper.ExecuteSql(sql))
                return Ok();

            throw new Exception("Failed to execute SQL query");
        }
    }
}
