package app.icecreamhot.kaidelivery.model.OrderAndFoodDetail

import app.icecreamhot.kaidelivery.model.Delivery.EmployeeScore
import com.google.gson.annotations.SerializedName

data class Employee(
    val emp_name: String,
    val emp_lastname: String,
    val emp_avatar: String? = null
)