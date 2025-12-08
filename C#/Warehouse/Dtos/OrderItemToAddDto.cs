namespace projekt.Dtos
{
    public partial class OrderItemToAddDto
    {
        public int order_id { get; set; }
        public int product_id { get; set; }
        public int quantity { get; set; }
        public decimal price_per_unit { get; set; }
    }
}
