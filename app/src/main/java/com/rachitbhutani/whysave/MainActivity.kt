package com.rachitbhutani.whysave

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.rachitbhutani.whysave.analytics.EventLogger
import com.rachitbhutani.whysave.databinding.ActivityMainBinding
import com.rachitbhutani.whysave.helper.PhoneNumberUtil
import com.rachitbhutani.whysave.helper.openWhatsapp
import com.rachitbhutani.whysave.helper.showSnackBar
import com.rachitbhutani.whysave.helper.validatePhoneNumber
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var eventLogger: EventLogger

    @Inject
    lateinit var phoneNumberUtil: PhoneNumberUtil

    private lateinit var viewModel: HomeViewModel

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(this)[HomeViewModel::class.java]
        setContentView(binding.root)
        handleIntent(this.intent)
    }

    private fun handleIntent(currentIntent: Intent?) {
        if (currentIntent == null)
            return
        (checkForExtraProcessText(currentIntent)
            ?: currentIntent.clipData?.getItemAt(0)?.text)?.let { rawText ->
            val refinedText = viewModel.refineRawText(rawText)
            handledRefinedText(refinedText)
        }
    }

    private fun checkForExtraProcessText(currentIntent: Intent) =
        if (currentIntent.action == Intent.ACTION_PROCESS_TEXT) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) currentIntent.getStringExtra(Intent.EXTRA_PROCESS_TEXT)
            else null
        } else null

    private fun handledRefinedText(text: String) {
        if (text.validatePhoneNumber()) {
            viewModel.insertContact(text)
            openWhatsapp(text)
        } else {
            binding.root.showSnackBar("Invalid number")
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }
}
