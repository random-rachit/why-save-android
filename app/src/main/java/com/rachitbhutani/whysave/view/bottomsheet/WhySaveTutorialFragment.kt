package com.rachitbhutani.whysave.view.bottomsheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.rachitbhutani.whysave.HomeViewModel
import com.rachitbhutani.whysave.databinding.FragmentWhySaveTutorialBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WhySaveTutorialFragment : Fragment() {

    private lateinit var binding: FragmentWhySaveTutorialBinding

    val viewModel: HomeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentWhySaveTutorialBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUi()
        markUserOnboarded()
    }

    private fun markUserOnboarded() {
        viewModel.markUserOnboarded()
    }

    private fun setupUi() {
        binding.btnAction.setOnClickListener {
            findNavController().popBackStack()
        }
    }
}