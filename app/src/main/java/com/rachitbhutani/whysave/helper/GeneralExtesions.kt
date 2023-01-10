package com.rachitbhutani.whysave.helper

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.view.View
import android.widget.Toast
import androidx.core.text.isDigitsOnly
import com.google.android.material.snackbar.Snackbar

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

fun String?.formatPhoneNumber(): String {
    if (this == null) return ""
    var str = this
    if (this.length == 12)
        str = "+${this.substring(0, 2)} ${this.substring(2, this.lastIndex)}"
    return str
}


fun String.stripDigits(): String {
    var str = ""
    map {
        str += if (it.isDigit()) "#" else it
    }
    return str
}

fun String?.orUnknown(): String {
    return this ?: "Unknown"
}

fun Activity.openWhatsapp(phone: String) {
    val intent = Intent(Intent.ACTION_VIEW)
    intent.data =
        Uri.parse("whatsapp://send/?phone=${if (phone.length == 10) "91" else ""}$phone")
    intent.resolveActivity(packageManager)?.let {
        startActivity(intent)
    } ?: run {
        Toast.makeText(this, "Whatsapp is not installed on the device", Toast.LENGTH_LONG).show()
    }
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

fun View.showSnackBar(message: String, function: (() -> Unit)? = null) {
    Snackbar.make(this, message, Snackbar.LENGTH_INDEFINITE).apply {
        setAction("Dismiss") {
            function?.invoke()
            dismiss()
        }
        show()
    }
}