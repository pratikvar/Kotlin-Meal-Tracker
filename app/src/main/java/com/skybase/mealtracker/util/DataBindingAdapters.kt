package com.skybase.mealtracker.util

import android.content.Context
import android.graphics.BitmapFactory
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.GlideDrawable
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.skybase.mealtracker.R
import java.io.UnsupportedEncodingException
import java.net.URLEncoder

//<editor-fold desc="Load Image">
@BindingAdapter("android:imageUrl")
fun loadImageToImageView(iv: ImageView, url: String?) {
    if (!url.isNullOrEmpty())
        setImageOnImageView(iv, url)
    else
        iv.setImageResource(R.drawable.ic_meal_placeholder)
}

private fun setImageOnImageView(iv: ImageView, url: String) {
    // Get the dimensions of the View
    try {
        val targetW: Int = iv.width
        val targetH: Int = iv.height

        val bmOptions = BitmapFactory.Options().apply {
            // Get the dimensions of the bitmap
            inJustDecodeBounds = true

            val photoW: Int = outWidth
            val photoH: Int = outHeight

            // Determine how much to scale down the image
            val scaleFactor: Int = Math.min(photoW / targetW, photoH / targetH)

            // Decode the image file into a Bitmap sized to fill the View
            inJustDecodeBounds = false
            inSampleSize = scaleFactor
        }
        BitmapFactory.decodeFile(url, bmOptions)?.also { bitmap ->
            iv.setImageBitmap(bitmap)
        }
    } catch (e: Exception) {
        iv.setImageResource(R.drawable.ic_meal_placeholder)
    }

}
//</editor-fold>


//</editor-fold>

//<editor-fold desc="Static Map">
@BindingAdapter("bind:locationImage")
fun loadLocationImage(
    imageView: ImageView,
    latLngString: String?
) {
    if (latLngString != null && latLngString.isNotEmpty()) {
        Glide.with(imageView.context)
            .load(getStaticMapImage(latLngString, imageView.context))
            .fitCenter()
            .placeholder(
                imageView.context.resources
                    .getDrawable(R.drawable.ic_map_placeholder)
            )
            .listener(object : RequestListener<String?, GlideDrawable?> {
                override fun onException(
                    e: Exception?,
                    model: String?,
                    target: Target<GlideDrawable?>?,
                    isFirstResource: Boolean
                ): Boolean {
                    return false
                }

                override fun onResourceReady(
                    resource: GlideDrawable?,
                    model: String?,
                    target: Target<GlideDrawable?>?,
                    isFromMemoryCache: Boolean,
                    isFirstResource: Boolean
                ): Boolean {
                    return true
                }
            })
            .into(imageView)
    } else {
        Glide.with(imageView.context)
            .load("")
            .fitCenter()
            .placeholder(
                imageView.context.resources
                    .getDrawable(R.drawable.ic_map_placeholder)
            )
            .into(imageView)
    }
}

private fun getStaticMapImage(latLngString: String, context: Context): String? {
    return try {
        val center = "center=$latLngString"
        val size = "&size=600x250"
        val markerData = "color:red|label:#|$latLngString"
        val marker = "&markers=" + URLEncoder.encode(markerData, "UTF-8")
        val zoom = "&zoom=13"
        val key = "&key=" + context.resources.getString(R.string.google_maps_key)
        getStaticMapUrl().toString() + center + size + marker + zoom + key
    } catch (e: UnsupportedEncodingException) {
        ""
    }
}

fun getStaticMapUrl(): String? {
    return "https://maps.googleapis.com/maps/api/staticmap?"
}
//</editor-fold>