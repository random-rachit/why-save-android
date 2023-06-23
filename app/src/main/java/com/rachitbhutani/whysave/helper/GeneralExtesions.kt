package com.rachitbhutani.whysave.helper

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.ResultReceiver
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.text.isDigitsOnly
import com.google.android.material.snackbar.Snackbar

fun String.validatePhoneNumber(): Boolean {
    val isValidLength = length == 12 || length == 10 || length == 13
    if (startsWith("+") && length < 13)
        return false
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
        str = "+${this.substring(0, 2)} ${this.substring(2, this.length)}"
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
    try {
        startActivity(intent)
    } catch (e: Exception) {
        e.printStackTrace()
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

fun EditText.setImeActionListener(imeAction: Int, function: (view: View) -> Unit) {
    setOnEditorActionListener { v, actionId, _ ->
        if (actionId == imeAction) {
            function.invoke(v)
        }
        return@setOnEditorActionListener true
    }
}

/**
 * Keyboard Extensions
 */
fun Context.showKeyboard(view: View) {
    val imm: InputMethodManager? =
        ContextCompat.getSystemService(this, InputMethodManager::class.java)
    imm?.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
}

fun Context.hideKeyboard(view: View) {
    val imm: InputMethodManager? =
        ContextCompat.getSystemService(this, InputMethodManager::class.java)
    imm?.hideSoftInputFromWindow(view.windowToken, 0)
}