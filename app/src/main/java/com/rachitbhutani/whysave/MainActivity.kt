package com.rachitbhutani.whysave

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.rachitbhutani.whysave.databinding.ActivityMainBinding
import com.rachitbhutani.whysave.helper.showIf
import com.rachitbhutani.whysave.helper.validatePhone
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), HistoryListItemListener {

    private var mAdapter: HistoryListAdapter? = null
    private lateinit var viewModel: HomeViewModel
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[HomeViewModel::class.java]
        setupUI()
        handleIntent()
        setupObservers()
    }

    private fun setupObservers() {
        viewModel.contactLiveData.observe(this) {
            mAdapter?.addItems(it)
            handleEmptyView()
        }
    }

    private fun handleEmptyView() {
        binding.emptyView.showIf(mAdapter?.itemCount == 0)
        binding.rvHistory.showIf(mAdapter?.itemCount != 0)
    }

    private fun handleIntent() {
        if (intent == null)
            return
        intent.clipData?.getItemAt(0)?.let {
            val text = (if (it.text.startsWith("+"))
                it.text.substring(1) else it.text.toString()).filter { c -> !c.isWhitespace() }
            if (text.validatePhone()) {
                viewModel.insertContact(text)
                openWhatsapp(text)
            }
        }
    }

    private fun setupUI() {
        actionBar?.title = "Recent Chats"
        mAdapter = HistoryListAdapter(this, this)
        val llm =   LinearLayoutManager(this)
        llm.orientation = LinearLayoutManager.VERTICAL
        binding.rvHistory.layoutManager = llm

        binding.rvHistory.adapter = mAdapter

        viewModel.fetchContacts()
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

    override fun onWhatsappClick(phone: String) {
        openWhatsapp(phone)
        viewModel.insertContact(phone)
    }
}