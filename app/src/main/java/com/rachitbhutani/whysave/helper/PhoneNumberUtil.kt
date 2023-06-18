package com.rachitbhutani.whysave.helper

import com.rachitbhutani.whysave.analytics.EventLogger
import com.rachitbhutani.whysave.analytics.Source
import javax.inject.Inject

class PhoneNumberUtil @Inject constructor(private val eventLogger: EventLogger) {

    fun refinePhoneNumber(rawText: String): String {
        eventLogger.sendFormatTrackerEvent(
            rawText.stripDigits(),
            source = Source.INTENT
        )
        val pattern = Regex("\"(.*)\"")
        val text: String
        val matcher = pattern.containsMatchIn(rawText)
        text = if (matcher) {
            val rawMatch = pattern.find(rawText)?.value
            rawMatch?.substring(1, rawMatch.lastIndex).orUnknown()
        } else rawText
        return (if (text.startsWith("+")) text.substring(1) else text).filter { c -> !c.isWhitespace() }
    }

}