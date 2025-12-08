namespace projekt.Models
{
    public partial class User
    {
        public int user_id { get; set; }
        public string username { get; set; }
        public string password { get; set; }
        public string email { get; set; }
        public string role { get; set; }

        public User()
        {
            if (username == null)
            {
                username = "";
            }
             if (password == null)
            {
                password = "";
            }
             if (email == null)
            {
                email = "";
            }
             if (role == null)
            {
                role = "";
            }
        }
    }
}