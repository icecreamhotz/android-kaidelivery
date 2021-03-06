package app.icecreamhot.kaidelivery.ui.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import app.icecreamhot.kaidelivery.R
import app.icecreamhot.kaidelivery.model.RateAndComment.EmployeeScore
import app.icecreamhot.kaidelivery.utils.BASE_URL_USER_IMG
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.item_employee_comment.view.*
import java.lang.Exception
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter
import java.util.*

class EmployeeCommentAdapter(val scoreCommentList: ArrayList<EmployeeScore>): RecyclerView.Adapter<EmployeeCommentAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_employee_comment, parent, false))
    }

    override fun getItemCount() = scoreCommentList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(scoreCommentList[position])
    }

    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {

        fun bind(scoreComment: EmployeeScore) {
            val fullName = if(scoreComment.user == null) "Guest Guest" else "${scoreComment.user.name} ${scoreComment.user.lastname}"
            val imgUser = BASE_URL_USER_IMG + if(scoreComment.user == null) "noimg.png" else scoreComment.user?.avatar
            val dateComment = getDateTime(scoreComment.empscore_date)
            val detailComment = scoreComment.empscore_comment
            val detailRate = scoreComment.empscore_rating.toString()

            itemView.apply {
                txtUserNameComment.text = fullName
                if(detailComment.isNullOrEmpty()) {
                    txtUserDetailComment.visibility = View.GONE
                } else {
                    txtUserDetailComment.visibility = View.VISIBLE
                    txtUserDetailComment.text = detailComment
                }
                txtUserRateComment.text = detailRate
                txtUserDateComment.text = dateComment
                Glide.with(itemView.context).load(imgUser).into(imgUserComment)
            }
        }

        fun getDateTime(s: String): String? {
            var parsed: Date? = null
            var outputDate: String
            var df_input = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
            var df_output = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

            try {
                parsed = df_input.parse(s)
                outputDate = df_output.format(parsed)

                return outputDate
            } catch (e: Exception) {
                return e.toString()
            }
        }

    }
}