namespace projekt.Models
{
    public partial class Category
    {
        public int category_id { get; set; }
        public string name { get; set; }
        public string photo_url { get; set; }
        public Category()
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