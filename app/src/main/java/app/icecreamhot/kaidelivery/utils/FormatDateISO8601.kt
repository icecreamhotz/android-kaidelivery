package app.icecreamhot.kaidelivery.utils

import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*

class FormatDateISO8601 {
    fun getDateTime(s: String): String? {
        var parsed: Date? = null
        var outputDate = ""
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