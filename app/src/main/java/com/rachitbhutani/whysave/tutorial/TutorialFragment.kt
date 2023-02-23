package com.rachitbhutani.whysave.tutorial

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.tabs.TabLayoutMediator
import com.rachitbhutani.whysave.databinding.FragmentTutorialBinding

class TutorialFragment : Fragment() {

    lateinit var binding: FragmentTutorialBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentTutorialBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewpager.adapter = TutorialAdapter(requireContext())
        binding.viewpager.currentItem = 0
        TabLayoutMediator(binding.tabLayout, binding.viewpager) { tab, position ->
            //Some implementation
        }.attach()
        binding.tvDone.setOnClickListener {
            findNavController().navigateUp()
        }
    }
}