package com.rachitbhutani.whysave.helper

import android.view.View
import androidx.core.text.isDigitsOnly

fun String.validatePhone(): Boolean {
    val isValidLength = length == 12 || length == 10 || length == 13
    if (!isValidLength)
        return false
    if (length == 13 && !startsWith("+"))
        return false
    if (!startsWith("+") && !isDigitsOnly())
        return false
    return true
}

fun String.stripDigits(): String {
    var str = ""
    map {
        str += if (it.isDigit()) "#" else it
    }
    return str
}


/**
 * View Extensions
 */
fun View.show() {
    visibility = View.VISIBLE
    invalidate()
}

fun View.hide() {
    visibility = View.GONE
    invalidate()
}

fun View.invisible() {
    visibility = View.INVISIBLE
    invalidate()
}

fun View.showIf(show: Boolean) {
    if (show) show() else hide()
    invalidate()
}