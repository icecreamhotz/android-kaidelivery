package app.icecreamhot.kaidelivery.model

data class Rate(
    val rate_id:Int,
    val rate_kilometers:String,
    val rate_price:Int,
    val rate_status:String,
    val rate_details:String
)