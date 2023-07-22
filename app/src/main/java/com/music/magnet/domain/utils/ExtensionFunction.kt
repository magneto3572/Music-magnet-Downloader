package com.music.magnet.domain.utils

import android.content.Context
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import coil.load
import coil.request.CachePolicy
import coil.transform.Transformation
import com.music.magnet.R

fun Fragment.superNavigate(action: NavDirections){
    try {
        this.findNavController().navigate(action)
    }catch (e : Exception){
        e.printStackTrace()
    }
}

fun ImageView.Load(context: Context, url: Any, type: Transformation, image: Int? = null ){
    this.load(url) {
        crossfade(true)
        transformations(type)
        diskCachePolicy(CachePolicy.ENABLED)
        error(image?: R.drawable.youtube_icon)
    }
}