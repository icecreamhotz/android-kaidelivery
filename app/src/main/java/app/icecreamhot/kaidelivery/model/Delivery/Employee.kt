package app.icecreamhot.kaidelivery.model.Delivery

import com.google.gson.annotations.SerializedName

data class Employee(
    val emp_id: Int,
    val emp_name: String,
    val emp_lastname: String,
    val emp_avatar: String?,
    @SerializedName("employeescores")
    val emp_score: ArrayList<EmployeeScore>
)