package com.rachitbhutani.whysave.helper

import android.text.format.DateUtils
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object DateHelper {

    private const val TIME_FORMAT = "hh:mm a"
    private const val DATE_FORMAT = "dd/MM/yyyy"

    private const val LABEL_YESTERDAY = "Yesterday"
    private const val LABEL_TODAY = "Today"

    private const val INVALID_DATA = "INVALID DATA"

    fun mapTimestampToSingleLineString(timestamp: Long?): String {
        if (timestamp == null) return ""

        val timeFormat = SimpleDateFormat(TIME_FORMAT, Locale.getDefault())
        val timeFormatWithDate = SimpleDateFormat("$DATE_FORMAT $TIME_FORMAT", Locale.getDefault())

        val date = Date(timestamp)

        return when {
            DateUtils.isToday(timestamp) -> timeFormat.format(date)
            isYesterday(timestamp) -> "$LABEL_YESTERDAY ${timeFormat.format(date)}"
            else -> timeFormatWithDate.format(date)
        }
    }

    fun mapTimestampToDateAndTimeString(timestamp: Long?): Pair<String, String> {
        if (timestamp == null) return Pair(INVALID_DATA, INVALID_DATA)

        val timeFormat = SimpleDateFormat(TIME_FORMAT, Locale.getDefault())
        val dateFormat = SimpleDateFormat(DATE_FORMAT, Locale.getDefault())

        val date = Date(timestamp)

        return when {
            DateUtils.isToday(timestamp) -> Pair(LABEL_TODAY, timeFormat.format(date))
            isYesterday(timestamp) -> Pair(LABEL_YESTERDAY, timeFormat.format(date))
            else -> Pair(dateFormat.format(date), timeFormat.format(date))
        }
    }

    private fun isYesterday(timestamp: Long): Boolean {
        return DateUtils.isToday(timestamp + DateUtils.DAY_IN_MILLIS);
    }

}