package com.rachitbhutani.whysave.helper

import android.text.format.DateUtils
import java.text.SimpleDateFormat
import java.util.*

object DateHelper {

    fun mapTimestampToTime(timestamp: Long?): String {
        if (timestamp == null) return ""
        val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
        val timeFormatWithDate = SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.getDefault())
        val date = Date(timestamp)

        val calendar = Calendar.getInstance()
        calendar.time = date

        return when {
            DateUtils.isToday(timestamp) -> timeFormat.format(date)
            isYesterday(timestamp) -> "Yesterday ${timeFormat.format(date)}"
            else -> timeFormatWithDate.format(date)
        }
    }

    private fun isYesterday(timestamp: Long): Boolean {
        return DateUtils.isToday(timestamp + DateUtils.DAY_IN_MILLIS);
    }

}