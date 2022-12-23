package com.rachitbhutani.whysave.analytics

import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.logEvent
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EventLogger @Inject constructor(private val firebaseAnalytics: FirebaseAnalytics) {

    fun sendFormatTrackerEvent(text: String) {
        firebaseAnalytics.logEvent(EventName.NUMBER_FORMAT_TRACKER) {
            param("format", text)
        }
    }
}