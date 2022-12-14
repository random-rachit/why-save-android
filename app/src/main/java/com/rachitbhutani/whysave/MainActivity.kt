package com.rachitbhutani.whysave

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.lifecycle.ViewModelProvider
import com.rachitbhutani.whysave.helper.validatePhone
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: HomeViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewModel = ViewModelProvider(this)[HomeViewModel::class.java]
        handleIntent()
        setupUI()
    }

    private fun handleIntent() {
        if (intent == null)
            return
        intent.clipData?.getItemAt(0)?.let {
            val text = if (it.text.startsWith("+"))
                it.text.substring(1) else it.text.toString()
            if (text.validatePhone())
                openWhatsapp(text)
        }
    }

    private fun setupUI() {

    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        handleIntent()
    }

    private fun openWhatsapp(phone: String) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data =
            Uri.parse("whatsapp://send/?phone=${if (phone.length == 10) "91" else ""}$phone")
        startActivity(intent)
    }
}