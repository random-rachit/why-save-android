package com.rachitbhutani.whysave

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.window.OnBackInvokedCallback
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.rachitbhutani.whysave.analytics.EventLogger
import com.rachitbhutani.whysave.databinding.ActivityMainBinding
import com.rachitbhutani.whysave.dialpad.DialpadFragment
import com.rachitbhutani.whysave.helper.showIf
import com.rachitbhutani.whysave.helper.stripDigits
import com.rachitbhutani.whysave.helper.validatePhone
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), HistoryListItemListener {

    @Inject
    lateinit var eventLogger: EventLogger

    private var mAdapter: HistoryListAdapter? = null
    private lateinit var viewModel: HomeViewModel
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[HomeViewModel::class.java]
        setupUI()
        handleIntent(intent)
        setupObservers()
    }

    private fun setupObservers() {
        viewModel.contactLiveData.observe(this) {
            mAdapter?.setData(it)
            handleEmptyView()
        }
    }

    private fun handleEmptyView() {
        binding.emptyView.showIf(mAdapter?.itemCount == 0)
        binding.rvHistory.showIf(mAdapter?.itemCount != 0)
    }

    private fun handleIntent(currentIntent: Intent?) {
        if (currentIntent == null)
            return
        currentIntent.clipData?.getItemAt(0)?.let {
            eventLogger.sendFormatTrackerEvent(it.text.toString().stripDigits())

            val text = (if (it.text.startsWith("+"))
                it.text.substring(1) else it.text.toString()).filter { c -> !c.isWhitespace() }

            handledRefinedText(text)
        }
    }

    private fun setupUI() {
        setupActionBar()
        setupRecyclerView()

        binding.btnKeypad.setOnClickListener {
            (supportFragmentManager.findFragmentByTag(DialpadFragment.TAG) as? DialpadFragment)?.getText()
                ?.let { text ->
                    handledRefinedText(text)
                } ?: run { openDialpad() }
        }

        viewModel.fetchContacts()
    }

    private fun handledRefinedText(text: String) {
        if (text.validatePhone()) {
            viewModel.insertContact(text)
            openWhatsapp(text)
        } else {
            Snackbar.make(binding.root, "Invalid number", Snackbar.LENGTH_INDEFINITE).apply {
                setAction("Dismiss") {
                    dismiss()
                }
                show()
            }
        }
    }

    private fun openDialpad() {
        //TODO: Change to navigation component soon
        val fragment = DialpadFragment()
        supportFragmentManager.beginTransaction()
            .add(binding.fragContainer.id, fragment, DialpadFragment.TAG)
            .addToBackStack(null)
            .commitAllowingStateLoss()
        binding.btnKeypad.setImageDrawable(
            ContextCompat.getDrawable(
                this,
                R.drawable.digital_glyph_black
            )
        )
    }

    private fun setupRecyclerView() {
        mAdapter = HistoryListAdapter(this, this)
        val llm = LinearLayoutManager(this)
        llm.orientation = LinearLayoutManager.VERTICAL
        binding.rvHistory.layoutManager = llm
        binding.rvHistory.adapter = mAdapter
    }

    private fun setupActionBar() {
        supportActionBar?.apply {
            title = getString(R.string.recent_chats)
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

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    private fun openWhatsapp(phone: String) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data =
            Uri.parse("whatsapp://send/?phone=${if (phone.length == 10) "91" else ""}$phone")
        startActivity(intent)
    }

    override fun onWhatsappClick(phone: String) {
        openWhatsapp(phone)
        viewModel.insertContact(phone)
    }
}