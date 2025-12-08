namespace projekt.Dtos
{
    public partial class UserForLoginDto
    {
        public string email { get; set; }

        public string password { get; set; }


        public UserForLoginDto()
        {
            if (email == null)
            {
                email = "";
            }
             if (password == null)
            {
                password = "";
            }
        }
    }
}