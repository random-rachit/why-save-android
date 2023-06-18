package com.rachitbhutani.whysave.dialpad

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.rachitbhutani.whysave.HomeViewModel
import com.rachitbhutani.whysave.R
import com.rachitbhutani.whysave.analytics.EventLogger
import com.rachitbhutani.whysave.analytics.Source
import com.rachitbhutani.whysave.databinding.FragmentDialpadBinding
import com.rachitbhutani.whysave.helper.openWhatsapp
import com.rachitbhutani.whysave.helper.orUnknown
import com.rachitbhutani.whysave.helper.stripDigits
import com.rachitbhutani.whysave.helper.validatePhoneNumber
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class DialpadFragment : Fragment() {

    @Inject
    lateinit var eventLogger: EventLogger

    private lateinit var viewModel: HomeViewModel

    private lateinit var binding: FragmentDialpadBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        viewModel = ViewModelProvider(this)[HomeViewModel::class.java]
        binding = FragmentDialpadBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().title = (getString(R.string.start_a_chat))
        setupUi()
    }

    private fun setupUi() {
        binding.etPhone.showSoftInputOnFocus = true
        binding.etPhone.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_GO) {
                handleOpenWhatsapp()
            }
            return@setOnEditorActionListener true
        }
        binding.etPhone.requestFocus()
        showKeyboard()
        binding.btnWhatsapp.setOnClickListener {
            handleOpenWhatsapp()
        }
        binding.ivBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun showKeyboard() {
        val imm: InputMethodManager? =
            getSystemService(requireContext(), InputMethodManager::class.java)
        imm?.showSoftInput(binding.etPhone, InputMethodManager.SHOW_IMPLICIT)
    }

    private fun handleOpenWhatsapp() {
        val text = binding.etPhone.text.toString().replace(" ","")
        val pattern = Regex("\"(.*)\"")
        val matcher = pattern.containsMatchIn(text)
        val phone = if (matcher) {
            val rawMatch = pattern.find(text)?.value
            rawMatch?.substring(1, rawMatch.lastIndex).orUnknown()
        } else text
        if (phone.validatePhoneNumber()) {
            viewModel.insertContact(phone)
            eventLogger.sendFormatTrackerEvent(phone.stripDigits(), source = Source.DIALPAD)
            requireActivity().openWhatsapp(phone)
        } else {
            Toast.makeText(requireContext(), "Invalid Number", Toast.LENGTH_SHORT).show()
        }
    }

}