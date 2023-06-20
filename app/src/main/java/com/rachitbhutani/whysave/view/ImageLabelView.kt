package com.rachitbhutani.whysave.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.rachitbhutani.whysave.R
import com.rachitbhutani.whysave.databinding.ImageLabelViewBinding
import com.rachitbhutani.whysave.helper.StaticImage
import com.rachitbhutani.whysave.helper.TutorialObject
import com.rachitbhutani.whysave.helper.loadStaticImage

class ImageLabelView : ConstraintLayout {

    private lateinit var binding: ImageLabelViewBinding

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {
        init(attrs)
    }

    private fun init(attrs: AttributeSet? = null) {
        binding = ImageLabelViewBinding.inflate(LayoutInflater.from(context), this)
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.ImageLabelView,
            0, 0
        ).apply {

            try {
                val title = getString(R.styleable.ImageLabelView_title).orEmpty()
                val drawable =
                    StaticImage.Drawable(getResourceId(R.styleable.ImageLabelView_image, 0))
                setData(TutorialObject(title, drawable))
                invalidate()
            } finally {
                recycle()
            }
        }
    }

    fun setData(data: TutorialObject) {
        binding.run {
            tvTitle.text = data.title
            ivTutorial.loadStaticImage(data.image)
        }
    }

}