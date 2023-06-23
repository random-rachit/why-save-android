package com.rachitbhutani.whysave

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.rachitbhutani.whysave.analytics.EventLogger
import com.rachitbhutani.whysave.analytics.Source
import com.rachitbhutani.whysave.databinding.FragmentHistoryListBinding
import com.rachitbhutani.whysave.helper.hideKeyboard
import com.rachitbhutani.whysave.helper.openWhatsapp
import com.rachitbhutani.whysave.helper.setImeActionListener
import com.rachitbhutani.whysave.helper.showIf
import com.rachitbhutani.whysave.helper.stripDigits
import com.rachitbhutani.whysave.helper.validatePhoneNumber
import com.rachitbhutani.whysave.view.EmptyView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class HistoryListFragment : Fragment(), HistoryListItemListener {

    private lateinit var binding: FragmentHistoryListBinding
    private lateinit var mAdapter: HistoryListAdapter

    @Inject
    lateinit var eventLogger: EventLogger

    private val viewModel: HomeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING)
    }

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
        showTutorial()
    }

    private fun showTutorial() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.isUserOnboarded.collectLatest {
                if (it.not()) {
                    openTutorialFragment()
                }
            }
        }
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
                openTutorialFragment()
            }
            rvHistory.showIf(show.not())
            tvHistoryLabel.showIf(show.not())
        }
    }

    private fun openTutorialFragment() {
        val action = HistoryListFragmentDirections.historyListToTutorial()
        findNavController().navigate(action)
    }

    private fun setupUI() {

        setupRecyclerView()

        binding.etDialpad.setImeActionListener(EditorInfo.IME_ACTION_DONE) { v ->
            val refinedText = viewModel.refineRawText(binding.etDialpad.text.toString())
            if (refinedText.validatePhoneNumber()) {
                viewModel.insertContact(refinedText)
                eventLogger.sendFormatTrackerEvent(
                    refinedText.stripDigits(),
                    source = Source.DIALPAD
                )
                requireActivity().openWhatsapp(refinedText)
                binding.etDialpad.clearFocus()
                requireContext().hideKeyboard(v)
            } else {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.invalid_number),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        binding.tvHowTo.setOnClickListener {
            openTutorialFragment()
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

    override fun onItemClick(phone: String) {
        val action = HistoryListFragmentDirections.historyListToDetailFragment(phone)
        findNavController().navigate(action)
    }

    override fun onStart() {
        super.onStart()
        viewModel.fetchContacts()
    }
}