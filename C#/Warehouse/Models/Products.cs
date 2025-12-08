namespace projekt.Models
{
    public partial class Product
    {
        public int product_id { get; set; }
        public string name { get; set; }
        public string description { get; set; }
        public decimal price { get; set; }
        public int stock_quantity { get; set; }
        public int category_id { get; set; }
        public string DIN { get; set; }
        public string photo_url { get; set; }

         public Product()
        {
            if (name == null)
            {
                name = "";
            }
             if (description == null)
            {
                description = "";
            }
             if (DIN == null)
            {
                DIN = "";
            }
             if (photo_url == null)
            {
                photo_url = "";
            }
        }
    }
}
