namespace projekt.Models
{
    //MODEL PRE OBJEDNAVKU Z TABULKY ORDERS
    public partial class Order
    {
        public int order_id { get; set; }
        public int user_id { get; set; }
        public DateTime order_date { get; set; }
        public string status { get; set; }

         public Order()
        {
             if (status == null)
            {
                status = "";
            }
        }
    }
}
