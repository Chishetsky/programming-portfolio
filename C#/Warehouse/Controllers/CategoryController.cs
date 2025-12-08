using Microsoft.AspNetCore.Mvc;
using projekt.Data;
using projekt.Models;
using System;
using System.Collections.Generic;

namespace projekt.Controllers
{
    [ApiController]
    [Route("[controller]")]
public class CategoryController  : ControllerBase
{
    private readonly DataContextDapper _dapper;
   public CategoryController (IConfiguration config)
   {
        _dapper = new DataContextDapper(config);
   }

        [HttpGet("TestConnection")]
        public IActionResult TestConnection()
        {
            return Ok(_dapper.LoadDataSingle<DateTime>("SELECT GETDATE()"));
        }

        [HttpGet("GetCategories")]
        public IActionResult GetCategories()
        {
            string sql = @"
                SELECT [category_id],
                       [name],
                       [photo_url]
                FROM [Category]";
            return Ok(_dapper.LoadData<Category>(sql));
        }

        [HttpGet("GetCategory/{category_id}")]
        public IActionResult GetCategory(int category_id)
        {
            string sql = $@"
                SELECT [category_id],
                       [name],
                       [photo_url]
                FROM [Category]
                WHERE category_id = {category_id}";
            return Ok(_dapper.LoadDataSingle<Category>(sql));
        }

        [HttpPost("AddCategory")]
        public IActionResult AddCategory(Category category)
        {
            string sql = $@"
                INSERT INTO [Category] ([name], [photo_url])
                VALUES ('{category.name}', '{category.photo_url}')";
            return ExecuteSql(sql);
        }

        [HttpPut("EditCategory")]
        public IActionResult EditCategory(Category category)
        {
            string sql = $@"
                UPDATE [Category]
                SET [name] = '{category.name}',
                    [photo_url] = '{category.photo_url}'
                WHERE [category_id] = {category.category_id}";
            return ExecuteSql(sql);
        }

        [HttpDelete("DeleteCategory/{category_id}")]
        public IActionResult DeleteCategory(int category_id)
        {
            string sql = $@"
                DELETE FROM [Category]
                WHERE category_id = {category_id}";
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
