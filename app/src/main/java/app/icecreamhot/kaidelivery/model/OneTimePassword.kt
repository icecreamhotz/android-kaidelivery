package app.icecreamhot.kaidelivery.model

data class OneTimePassword(
    val otp_id: Int,
    val otp_code: String,
    val otp_status: String,
    val otp_expiredToken: String,
    val user_id: String
)