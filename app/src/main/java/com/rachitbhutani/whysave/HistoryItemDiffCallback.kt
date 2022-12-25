package com.rachitbhutani.whysave

import androidx.recyclerview.widget.DiffUtil
import com.rachitbhutani.whysave.model.ContactItem

class HistoryItemDiffCallback(
    private val oldList: List<ContactItem>,
    private val newList: List<ContactItem>
) : DiffUtil.Callback() {

    override fun getOldListSize() = oldList.size

    override fun getNewListSize() = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) =
        oldList[oldItemPosition].id == newList[newItemPosition].id

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val (id, phone, time) = oldList[oldItemPosition]
        val (newId, newPhone, newTime) = newList[newItemPosition]
        return id == newId && phone == newPhone && time == newTime
    }
}