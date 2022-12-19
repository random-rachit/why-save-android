package com.rachitbhutani.whysave.helper

import java.text.SimpleDateFormat
import java.util.*

object DateHelper {

    fun mapTimestampToTime(timestamp: Long?): String {
        val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
        val date = Date(timestamp ?: -1)
        return timeFormat.format(date)
    }

}