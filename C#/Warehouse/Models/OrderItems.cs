namespace projekt.Models
{
    public partial class OrderItem
    {
        public int order_item_id { get; set; }
        public int order_id { get; set; }
        public int product_id { get; set; }
        public int quantity { get; set; }
        public decimal price_per_unit { get; set; }
    }
}
