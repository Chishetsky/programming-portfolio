namespace projekt.Dtos
{
    //DTO PRE VYTVORENIE NOVEJ OBJEDNAVKY
    public partial class OrderToAddDto
    {
        public int user_id { get; set; }
        public DateTime order_date { get; set; }
        public string status { get; set; }

         public OrderToAddDto()
        {
             if (status == null)
            {
                status = "";
            }
        }
    }
}