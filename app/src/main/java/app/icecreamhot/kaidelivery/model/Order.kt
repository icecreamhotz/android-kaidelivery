package app.icecreamhot.kaidelivery.model

data class Order(
    val order_id: Int,
    val order_name: String,
    val order_details: String?,
    val min_minute: Int?,
    val user_id: Int,
    val res_id: Int,
    val emp_id: Int?,
    val resscore_id: Int,
    val empscore_id: Int,
    val rate_id: Int,
    val pro_id: Int?,
    val endpoint_name: String,
    val endpoint_details: String?,
    val endpoint_lat: Double,
    val endpoint_lng: Double,
    val order_score: Int?,
    val order_comment: String?,
    val order_status: String,
    val order_statusdetails: String?,
    val order_timeout: String?,
    val order_deliveryprice: Double,
    var order_price: Double,
    val order_discount: Double?,
    val order_date: String,
    val order_start: String
)