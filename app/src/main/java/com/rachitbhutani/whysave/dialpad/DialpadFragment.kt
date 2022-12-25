package com.rachitbhutani.whysave.dialpad

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.rachitbhutani.whysave.HomeViewModel
import com.rachitbhutani.whysave.MainActivity
import com.rachitbhutani.whysave.R
import com.rachitbhutani.whysave.analytics.EventLogger
import com.rachitbhutani.whysave.analytics.Source
import com.rachitbhutani.whysave.databinding.FragmentDialpadBinding
import com.rachitbhutani.whysave.helper.openWhatsapp
import com.rachitbhutani.whysave.helper.showSnackBar
import com.rachitbhutani.whysave.helper.stripDigits
import com.rachitbhutani.whysave.helper.validatePhone
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
    }

    private fun showKeyboard() {
        val imm: InputMethodManager? = getSystemService(requireContext(), InputMethodManager::class.java)
        imm?.showSoftInput(binding.etPhone, InputMethodManager.SHOW_IMPLICIT)
    }

    private fun handleOpenWhatsapp() {
        val phone = binding.etPhone.text.toString()
        if (phone.validatePhone()) {
            viewModel.insertContact(phone)
            eventLogger.sendFormatTrackerEvent(phone.stripDigits(), source = Source.DIALPAD)
            requireActivity().openWhatsapp(phone)
        } else {
            binding.root.showSnackBar("Invalid number")
        }
    }

}