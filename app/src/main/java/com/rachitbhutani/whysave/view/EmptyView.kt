package com.rachitbhutani.whysave.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import com.rachitbhutani.whysave.databinding.EmptyViewBinding

class EmptyView : ConstraintLayout {

    private lateinit var binding: EmptyViewBinding

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {
        init()
    }

    private fun init() {
        binding = EmptyViewBinding.inflate(LayoutInflater.from(context), this)
    }

    fun setData(data: Data, onClick: (() -> Unit)?) {
        binding.run {
            tvEmptyView.text = data.text
            btnEmptyView.isVisible = data.subText != null
            btnEmptyView.text = data.subText
            btnEmptyView.setOnClickListener {
                onClick?.invoke()
            }
        }
    }

    data class Data(
        val text: String,
        val subText: String?
    )
}