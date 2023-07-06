package com.rachitbhutani.whysave.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.rachitbhutani.whysave.HomeViewModel
import com.rachitbhutani.whysave.analytics.EventLogger
import com.rachitbhutani.whysave.analytics.Source
import com.rachitbhutani.whysave.databinding.FragmentDetailBinding
import com.rachitbhutani.whysave.helper.hideKeyboard
import com.rachitbhutani.whysave.helper.observeTextChanges
import com.rachitbhutani.whysave.helper.openWhatsapp
import com.rachitbhutani.whysave.helper.setImeActionListener
import com.rachitbhutani.whysave.helper.stripDigits
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@AndroidEntryPoint
class DetailFragment : Fragment() {

    private lateinit var binding: FragmentDetailBinding

    private val viewModel: HomeViewModel by viewModels()

    private val args: DetailFragmentArgs by navArgs()

    @Inject
    lateinit var eventLogger: EventLogger

    private val logAdapter = LogListAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentDetailBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupObservers()
        setupUi()
    }

    private fun setupObservers() {
        viewModel.detailContactLiveData.observe(viewLifecycleOwner) {
            logAdapter.setData(it?.logList.orEmpty())
            binding.rvLogs.adapter = logAdapter
            binding.etNote.setText(it?.note)
        }
    }

    private fun setupUi() {
        binding.run {
            tvNumber.text = args.number
            rvLogs.layoutManager = LinearLayoutManager(requireContext())

            ivBack.setOnClickListener {
                findNavController().popBackStack()
            }

            setupTextField()

            fabChat.setOnClickListener {
                val phone = viewModel.detailContactLiveData.value?.phone.orEmpty()
                eventLogger.sendFormatTrackerEvent(phone.stripDigits(), source = Source.LIST)
                activity?.openWhatsapp(phone)
                viewModel.insertContact(phone)
            }
        }

        viewModel.fetchContactByNumber(args.number)
    }

    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
    private fun FragmentDetailBinding.setupTextField() {
        etNote.observeTextChanges().debounce(500).onEach {
            viewModel.updateNote(it.toString())
            etNote.clearFocus()
            requireContext().hideKeyboard(etNote)
        }.launchIn(lifecycleScope)

        etNote.setImeActionListener(EditorInfo.IME_ACTION_DONE) {
            val text = etNote.text.toString()
            viewModel.updateNote(text)
            etNote.clearFocus()
            requireContext().hideKeyboard(it)
        }
    }
}