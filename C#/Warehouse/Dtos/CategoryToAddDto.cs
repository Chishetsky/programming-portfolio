namespace projekt.Dtos
{
    public partial class CategoryToAddDto
    {
        public string name { get; set; }
        public string photo_url { get; set; }
        public CategoryToAddDto()
        {
       
             if (name == null)
            {
                name = "";
            }
             if (photo_url == null)
            {
                photo_url = "";
            }
        }
    }
}