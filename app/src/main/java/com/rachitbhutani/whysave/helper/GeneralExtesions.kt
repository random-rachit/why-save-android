package com.rachitbhutani.whysave.helper

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