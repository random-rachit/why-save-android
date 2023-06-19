package com.rachitbhutani.whysave

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.rachitbhutani.whysave.analytics.EventLogger
import com.rachitbhutani.whysave.analytics.Source
import com.rachitbhutani.whysave.databinding.FragmentHistoryListBinding
import com.rachitbhutani.whysave.helper.hideKeyboard
import com.rachitbhutani.whysave.helper.openWhatsapp
import com.rachitbhutani.whysave.helper.showIf
import com.rachitbhutani.whysave.helper.stripDigits
import com.rachitbhutani.whysave.helper.validatePhoneNumber
import com.rachitbhutani.whysave.view.EmptyView
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class HistoryListFragment : Fragment(), HistoryListItemListener {

    private lateinit var binding: FragmentHistoryListBinding
    private lateinit var mAdapter: HistoryListAdapter

    @Inject
    lateinit var eventLogger: EventLogger

    private val viewModel: HomeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentHistoryListBinding.inflate(inflater)
        mAdapter = HistoryListAdapter(requireContext(), this)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        setupObservers()
    }

    private fun setupObservers() {
        viewModel.contactLiveData.observe(requireActivity()) {
            mAdapter.setData(it)
            handleEmptyView()
        }
    }

    private fun handleEmptyView() {
        val show = mAdapter.itemCount == 0
        binding.run {
            evHistory.showIf(show)
            evHistory.setData(
                EmptyView.Data(
                    getString(R.string.empty_view_text),
                    getString(R.string.watch_tutorial)
                )
            ) {
                openTutorialBottomSheet()
            }
            rvHistory.showIf(show.not())
            tvHistoryLabel.showIf(show.not())
        }
    }

    private fun openTutorialBottomSheet() {
        TODO("Open Tutorial Bottom Sheet")
    }

    private fun setupUI() {
        setupRecyclerView()

        binding.etDialpad.setOnEditorActionListener { v, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_GO) {
                val refinedText = viewModel.refineRawText(binding.etDialpad.text.toString())
                if (refinedText.validatePhoneNumber()) {
                    viewModel.insertContact(refinedText)
                    eventLogger.sendFormatTrackerEvent(
                        refinedText.stripDigits(),
                        source = Source.DIALPAD
                    )
                    requireActivity().openWhatsapp(refinedText)
                    binding.etDialpad.isFocusable = false
                    requireContext().hideKeyboard(v)
                } else {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.invalid_number),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            return@setOnEditorActionListener true
        }
    }

    private fun setupRecyclerView() {
        val llm = LinearLayoutManager(requireContext())
        llm.orientation = LinearLayoutManager.VERTICAL
        binding.rvHistory.layoutManager = llm
        binding.rvHistory.adapter = mAdapter
    }

    override fun onWhatsappClick(phone: String) {
        eventLogger.sendFormatTrackerEvent(phone.stripDigits(), source = Source.LIST)
        activity?.openWhatsapp(phone)
        viewModel.insertContact(phone)
    }

    override fun onStart() {
        super.onStart()
        viewModel.fetchContacts()
    }
}