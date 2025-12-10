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

    public UserController(IConfiguration config)
    {
        _dapper = new DataContextDapper(config);
    }

    [HttpGet("TestConnection")]
    public DateTime TestConnection()
    {
        return _dapper.LoadDataSingle<DateTime>("SELECT GETDATE()");
    }

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

        Console.WriteLine(sql);

        if (_dapper.ExecuteSql(sql))
            return Ok();

        throw new Exception("Failed to update user");
    }

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

        Console.WriteLine(sql);

        if (_dapper.ExecuteSql(sql))
            return Ok();

        throw new Exception("Failed to create user");
    }

    [HttpDelete("DeleteUser/{user_id}")]
    public IActionResult DeleteUser(int user_id)
    {
        string sql = @"
            DELETE FROM [Users]
            WHERE user_id = " + user_id;

        Console.WriteLine(sql);

        if (_dapper.ExecuteSql(sql))
            return Ok();

        throw new Exception("Failed to delete user");
    }
}
