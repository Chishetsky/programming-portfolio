namespace projekt.Dtos
{
    public partial class UserForRegistrationDto
    {
        public string email { get; set; }

        public string password { get; set; }

        public string passwordConfirm { get; set; }

         public string username { get; set; }
        public UserForRegistrationDto()
        {
            if (email == null)
            {
                email = "";
            }
             if (password == null)
            {
                password = "";
            }
             if (passwordConfirm == null)
            {
                passwordConfirm = "";
            }
              if (username == null)
            {
                username = "";
            }
        }
    }
}