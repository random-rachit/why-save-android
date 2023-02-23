package com.rachitbhutani.whysave

import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.rachitbhutani.whysave.analytics.EventLogger
import com.rachitbhutani.whysave.analytics.Source
import com.rachitbhutani.whysave.databinding.ActivityMainBinding
import com.rachitbhutani.whysave.helper.*
import com.rachitbhutani.whysave.tutorial.TutorialFragment
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

        (checkForExtraProcessText(currentIntent)
            ?: currentIntent.clipData?.getItemAt(0)?.text)?.let {
            eventLogger.sendFormatTrackerEvent(
                it.toString().stripDigits(),
                source = Source.INTENT
            )
            val pattern = Regex("\"(.*)\"")
            val text: String
            val matcher = pattern.containsMatchIn(it)
            text = if (matcher) {
                val rawMatch = pattern.find(it)?.value
                rawMatch?.substring(1, rawMatch.lastIndex).orUnknown()
            } else it.toString()
            handledRefinedText((if (text.startsWith("+")) text.substring(1) else text).filter { c -> !c.isWhitespace() })
        }
    }

    private fun checkForExtraProcessText(currentIntent: Intent) =
        if (currentIntent.action == Intent.ACTION_PROCESS_TEXT) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) currentIntent.getStringExtra(Intent.EXTRA_PROCESS_TEXT)
            else null
        } else null


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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.tutorial_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.tutorial -> {
                openTutorialFragment()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun openTutorialFragment() {
        val fragment = TutorialFragment()
        supportFragmentManager.beginTransaction().add(binding.frameLayout.id, fragment).commitNow()
    }
}
