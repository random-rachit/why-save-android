package com.rachitbhutani.whysave.detail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rachitbhutani.whysave.databinding.HistoryListItemBinding
import com.rachitbhutani.whysave.helper.DateHelper
import com.rachitbhutani.whysave.helper.hide

class LogListAdapter : RecyclerView.Adapter<LogListAdapter.Holder>() {

    private val logList = mutableListOf<Long>()

    fun setData(list: List<Long>) {
        logList.clear()
        logList.addAll(list)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val binding = HistoryListItemBinding.inflate(LayoutInflater.from(parent.context))
        return Holder(binding)
    }

    override fun getItemCount() = logList.size

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bindData(logList[position])
    }

    class Holder(private val binding: HistoryListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bindData(timestamp: Long) {
            binding.run {
                val (date, time) = DateHelper.mapTimestampToDateAndTimeString(timestamp)
                tvPhone.text = date
                tvTime.text = time
                ivOpenChat.hide()
            }
        }
    }
}