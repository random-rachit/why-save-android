package com.rachitbhutani.whysave

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.rachitbhutani.whysave.databinding.HistoryListItemBinding
import com.rachitbhutani.whysave.helper.DateHelper
import com.rachitbhutani.whysave.model.ContactItem

class HistoryListAdapter(
    private val context: Context,
    private val listener: HistoryListItemListener
) : RecyclerView.Adapter<HistoryListAdapter.Holder>() {

    private val mList: MutableList<ContactItem> = mutableListOf()

    fun setData(list: List<ContactItem>) {
        val diffCallback = HistoryItemDiffCallback(mList, list)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        mList.clear()
        mList.addAll(list)
        diffResult.dispatchUpdatesTo(this)
    }

    inner class Holder(private val binding: HistoryListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bindData(data: ContactItem) {
            binding.tvPhone.text = data.phone
            binding.tvTime.text = DateHelper.mapTimestampToTime(data.timestamp)
            binding.root.setOnClickListener {
                listener.onWhatsappClick(data.phone.orEmpty())
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(HistoryListItemBinding.inflate(LayoutInflater.from(context), parent, false))
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bindData(mList[position])
    }

    override fun getItemCount() = mList.size

}