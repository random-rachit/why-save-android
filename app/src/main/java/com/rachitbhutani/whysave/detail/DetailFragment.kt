package com.rachitbhutani.whysave.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.rachitbhutani.whysave.HomeViewModel
import com.rachitbhutani.whysave.databinding.FragmentDetailBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DetailFragment : Fragment() {

    private lateinit var binding: FragmentDetailBinding

    private val viewModel: HomeViewModel by viewModels()

    private val args: DetailFragmentArgs by navArgs()

    private val logAdapter = LogListAdapter()

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
        setupUi()
        setupObservers()
    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.detailContactFlow.collectLatest {
                logAdapter.setData(it?.logList.orEmpty())
            }
        }
    }

    private fun setupUi() {
        binding.run {
            tvNumber.text = args.number
            rvLogs.adapter = logAdapter
            rvLogs.layoutManager = LinearLayoutManager(requireContext())
        }

        viewModel.fetchContactByNumber(args.number)
    }
}