using Microsoft.AspNetCore.Mvc;
using projekt.Data;
using projekt.Models;
using System;
using System.Collections.Generic;

namespace projekt.Controllers
{
    [ApiController]
    [Route("[controller]")]
    public class CategoryController : ControllerBase
    {
        private readonly DataContextDapper _dapper;

        //DI KONFIGURACIE PRE DATABAZU
        public CategoryController(IConfiguration config)
        {
            _dapper = new DataContextDapper(config);
        }
        //Endpoint: GET /Category/TestConnection
        [HttpGet("TestConnection")]
        public IActionResult TestConnection()
        {
            return Ok(_dapper.LoadDataSingle<DateTime>("SELECT GETDATE()"));
        }
        //Endpoint: GET /Category/GetCategories
        [HttpGet("GetCategories")]
        public IActionResult GetCategories()
        {
            string sql = @"
                SELECT [category_id],
                       [name],
                       [photo_url]
                FROM [Categories]";
            return Ok(_dapper.LoadData<Category>(sql));
        }
        //Endpoint: GET /Category/category_id
        [HttpGet("GetCategory/{category_id}")]
        public IActionResult GetCategory(int category_id)
        {
            string sql = $@"
                SELECT [category_id],
                       [name],
                       [photo_url]
                FROM [Categories]
                WHERE category_id = {category_id}";
            return Ok(_dapper.LoadDataSingle<Category>(sql));
        }
        //Endpoint: POST /Category/AddCategory
        [HttpPost("AddCategory")]
        public IActionResult AddCategory(Category category)
        {
            string sql = $@"
                INSERT INTO [Categories] ([name], [photo_url])
                VALUES ('{category.name}', '{category.photo_url}')";
            return ExecuteSql(sql);
        }
        //Endpoint: PUT /Category/EditCategory
        [HttpPut("EditCategory")]
        public IActionResult EditCategory(Category category)
        {
            string sql = $@"
                UPDATE [Categories]
                SET [name] = '{category.name}',
                    [photo_url] = '{category.photo_url}'
                WHERE [category_id] = {category.category_id}";
            return ExecuteSql(sql);
        }
        //Endpoint: DELETE /Category/category_id
        [HttpDelete("DeleteCategory/{category_id}")]
        public IActionResult DeleteCategory(int category_id)
        {
            string sql = $@"
                DELETE FROM [Categories]
                WHERE category_id = {category_id}";
            return ExecuteSql(sql);
        }
        //HELPER METODA NA VYKONANIE DOTAZU
        private IActionResult ExecuteSql(string sql)
        {
            if (_dapper.ExecuteSql(sql))
                return Ok();
            throw new Exception("Failed to execute SQL query");
        }
    }
}
