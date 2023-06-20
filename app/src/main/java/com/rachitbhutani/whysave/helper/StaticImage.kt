package com.rachitbhutani.whysave.helper

import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide

sealed class StaticImage {

    data class Drawable(@DrawableRes val drawableRes: Int) : StaticImage()
    data class Url(val url: String) : StaticImage()

}

fun ImageView.loadStaticImage(staticImage: StaticImage) {
    when (staticImage) {
        is StaticImage.Drawable -> this.setImageDrawable(
            ContextCompat.getDrawable(
                context,
                staticImage.drawableRes
            )
        )
        is StaticImage.Url -> Glide.with(context).load(staticImage.url).into(this)
    }
}
