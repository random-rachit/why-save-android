package com.rachitbhutani.whysave.dialpad

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.rachitbhutani.whysave.R
import com.rachitbhutani.whysave.databinding.FragmentDialpadBinding

class DialpadFragment : Fragment() {

    private lateinit var binding: FragmentDialpadBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentDialpadBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    fun getText() = binding.etPhone.text.toString()

    companion object {
        const val TAG = "Dialpad Fragment"
    }


}