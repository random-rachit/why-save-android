package com.rachitbhutani.whysave.tutorial

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RawRes
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.rachitbhutani.whysave.R
import com.rachitbhutani.whysave.databinding.TutorialViewBinding

class TutorialAdapter(val context: Context) : RecyclerView.Adapter<TutorialAdapter.Holder>() {

    private val tutorials = listOf(R.raw.tutorial_square, R.raw.tutorial_square)

    inner class Holder(private val binding: TutorialViewBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bindData(@RawRes tutorial: Int) {
            Glide.with(context).asGif().load(tutorial)
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE).into(binding.ivTutorialItem)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(TutorialViewBinding.inflate(LayoutInflater.from(context), parent, false))
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bindData(tutorials[position])
    }

    override fun getItemCount() = tutorials.size
}