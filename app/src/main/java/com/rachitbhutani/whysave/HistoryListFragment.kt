package com.rachitbhutani.whysave

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.view.menu.MenuBuilder
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.rachitbhutani.whysave.analytics.EventLogger
import com.rachitbhutani.whysave.analytics.Source
import com.rachitbhutani.whysave.databinding.FragmentHistoryListBinding
import com.rachitbhutani.whysave.helper.*
import com.rachitbhutani.whysave.tutorial.TutorialFragment
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
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
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
        setupMenu()
    }

    private fun setupMenu() {
        binding.toolbar.apply {
            inflateMenu(R.menu.tutorial_menu)
            setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.tutorial -> openTutorialFragment()
                }
                true
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
        binding.emptyView.showIf(mAdapter.itemCount == 0)
        binding.emptyView.setOnClickListener {
            binding.emptyView.text = String.format(
                getString(R.string.empty_view_desc),
                if (binding.ivTutorial.isVisible) "hide" else "view"
            )
            binding.ivTutorial.showIf(binding.ivTutorial.isVisible.not())
            openTutorialFragment()
        }
        binding.rvHistory.showIf(mAdapter.itemCount != 0)
    }

    private fun openTutorialFragment() {
        val action = HistoryListFragmentDirections.historyListToTutorialFragment()
        findNavController().navigate(action)
    }

    private fun setupUI() {
        setupRecyclerView()
        binding.btnKeypad.setOnClickListener {
            val action = HistoryListFragmentDirections.historyListToDialpadFragment()
            it.findNavController().navigate(action)
        }
        binding.emptyView.text = String.format(getString(R.string.empty_view_desc), "view")
        Glide.with(this).asGif().load(R.raw.tutorial_square)
            .diskCacheStrategy(DiskCacheStrategy.RESOURCE).into(binding.ivTutorial)
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

    override fun onResume() {
        super.onResume()
        binding.toolbar.title = "Recent Chats"
    }

    override fun onStart() {
        super.onStart()
        viewModel.fetchContacts()
    }
}