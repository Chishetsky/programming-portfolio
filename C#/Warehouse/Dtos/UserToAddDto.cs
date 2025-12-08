namespace projekt.Dtos
{
    public partial class UserToAddDto
    {
        public string username { get; set; }
        public string password { get; set; }
        public string email { get; set; }
        public string role { get; set; }

        public UserToAddDto()
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