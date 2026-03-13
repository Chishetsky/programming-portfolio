using Microsoft.AspNetCore.Mvc;
using projekt.Data;
using projekt.Dtos;
using projekt.Models;

namespace projekt.Controllers;

[ApiController]
[Route("[controller]")]
public class UserController : ControllerBase
{
    private readonly DataContextDapper _dapper;
    //DI KONFIGURACIE PRE DATABAZU
    public UserController(IConfiguration config)
    {
        _dapper = new DataContextDapper(config);
    }
    //Endpoint: GET /User/TestConnection
    [HttpGet("TestConnection")]
    public DateTime TestConnection()
    {
        return _dapper.LoadDataSingle<DateTime>("SELECT GETDATE()");
    }
    //Endpoint: GET /User/GetUsers
    [HttpGet("GetUsers")]
    public IEnumerable<User> GetUsers()
    {
        string sql = @"
            SELECT [user_id],
                   [username],
                   [password],
                   [email],
                   [role]
            FROM [Users]";

        return _dapper.LoadData<User>(sql);
    }
    //Endpoint: GET /User/GetUser/user_id
    [HttpGet("GetUser/{user_id}")]
    public User GetUser(int user_id)
    {
        string sql = @"
            SELECT [user_id],
                   [username],
                   [password],
                   [email],
                   [role]
            FROM [Users]
            WHERE user_id = " + user_id;

        return _dapper.LoadDataSingle<User>(sql);
    }
    //Endpoint: PUT /User/EditUser
    [HttpPut("EditUser")]
    public IActionResult EditUser(User user)
    {
        string sql = @"
            UPDATE [Users]
            SET [username] = '" + user.username + @"',
                [password] = '" + user.password + @"',
                [email] = '" + user.email + @"',
                [role] = '" + user.role + @"'
            WHERE [user_id] = " + user.user_id;

        if (_dapper.ExecuteSql(sql))
            return Ok();

        throw new Exception("Failed to update user");
    }
    //Endpoint: POST /User/AddUser
    [HttpPost("AddUser")]
    public IActionResult AddUser(UserToAddDto user)
    {
        string sql = @"
            INSERT INTO [Users] (
                [username],
                [password],
                [email],
                [role]
            ) VALUES (
                '" + user.username + @"',
                '" + user.password + @"',
                '" + user.email + @"',
                '" + user.role + @"'
            )";


        if (_dapper.ExecuteSql(sql))
            return Ok();

        throw new Exception("Failed to create user");
    }
    //Endpoint: DELETE /User/DeleteUser/user_id
    [HttpDelete("DeleteUser/{user_id}")]
    public IActionResult DeleteUser(int user_id)
    {
        string sql = @"
            DELETE FROM [Users]
            WHERE user_id = " + user_id;

        if (_dapper.ExecuteSql(sql))
            return Ok();

        throw new Exception("Failed to delete user");
    }
}
