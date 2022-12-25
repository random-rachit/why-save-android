package com.rachitbhutani.whysave

import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import com.rachitbhutani.whysave.analytics.EventLogger
import com.rachitbhutani.whysave.analytics.Source
import com.rachitbhutani.whysave.databinding.ActivityMainBinding
import com.rachitbhutani.whysave.helper.openWhatsapp
import com.rachitbhutani.whysave.helper.showSnackBar
import com.rachitbhutani.whysave.helper.stripDigits
import com.rachitbhutani.whysave.helper.validatePhone
import dagger.hilt.android.AndroidEntryPoint
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
    }

    fun setupActionBar(title: String) {
        supportActionBar?.apply {
            this.title = title
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

            val text = (if (it.text.startsWith("+"))
                it.text.substring(1) else it.text.toString()).filter { c -> !c.isWhitespace() }

            handledRefinedText(text)
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
