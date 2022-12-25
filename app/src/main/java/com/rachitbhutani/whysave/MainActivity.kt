package com.rachitbhutani.whysave

import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.rachitbhutani.whysave.analytics.EventLogger
import com.rachitbhutani.whysave.analytics.Source
import com.rachitbhutani.whysave.databinding.ActivityMainBinding
import com.rachitbhutani.whysave.helper.*
import dagger.hilt.android.AndroidEntryPoint
import java.util.regex.Pattern
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var eventLogger: EventLogger

    private lateinit var viewModel: HomeViewModel

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(this)[HomeViewModel::class.java]
        setContentView(binding.root)
        handleIntent(this.intent)
        setupActionBar()
    }

    private fun setupActionBar() {
        supportActionBar?.apply {
            setBackgroundDrawable(
                ColorDrawable(
                    ContextCompat.getColor(
                        this@MainActivity,
                        R.color.teal_700
                    )
                )
            )
            elevation = 24f
        }
    }

    private fun handleIntent(currentIntent: Intent?) {
        if (currentIntent == null)
            return
        currentIntent.clipData?.getItemAt(0)?.let {
            eventLogger.sendFormatTrackerEvent(
                it.text.toString().stripDigits(),
                source = Source.INTENT
            )

            val pattern = Regex("\"(.*)\"")
            val text: String
            val matcher = pattern.containsMatchIn(it.text)
            text = if (matcher) {
                val rawMatch = pattern.find(it.text)?.value
                rawMatch?.substring(1, rawMatch.lastIndex).orUnknown()
            } else it.text.toString()
            handledRefinedText((if (text.startsWith("+")) text.substring(1) else text).filter { c -> !c.isWhitespace() })
        }
    }

    private fun handledRefinedText(text: String) {
        if (text.validatePhone()) {
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
