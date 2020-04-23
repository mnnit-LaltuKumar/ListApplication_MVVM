package com.listapplication.utils

import android.graphics.Color
import android.graphics.PorterDuff
import android.view.View
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.sqlite.db.SimpleSQLiteQuery
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.facebook.shimmer.ShimmerFrameLayout

object DBUtil {

    /**
     * DBUtil function provides filter criteria to fetch specific repositories.
     */
    fun sqlWhere(table: String, params: Map<String, String>): SimpleSQLiteQuery {
        var query = "SELECT * FROM $table"
        params.keys.forEachIndexed { i, s ->
            query += if (i == 0) " WHERE" else " AND"
            query += " $s = ?"
        }

        val args = params.values.toTypedArray()
        return SimpleSQLiteQuery(query, args)
    }
}

/**
 * Helper object to provide functions to provide binding between view and business logic.
 */
object BindingAdapter {
    @JvmStatic
    @BindingAdapter("android:src")
    fun setImageUrl(view: ImageView, url: String?) {
        url?.let {
            Glide
                .with(view.context)
                .load(url)
                .diskCacheStrategy(DiskCacheStrategy.DATA)
                .into(view)
        }
    }

    @JvmStatic
    @BindingAdapter("imageBgSource")
    fun setImageResource(imageView: ImageView, hexString: String?) {
        hexString?.let {
            imageView.setColorFilter(Color.parseColor(it), PorterDuff.Mode.SRC_ATOP)
        }
    }

    @JvmStatic
    @BindingAdapter("changeState")
    fun changeShimmerState(shimmerFrameLayout: ShimmerFrameLayout, state: Boolean) {
        if(state) {
            shimmerFrameLayout.visibility = View.VISIBLE
            shimmerFrameLayout.startShimmerAnimation()
        } else {
            shimmerFrameLayout.visibility = View.GONE
            shimmerFrameLayout.stopShimmerAnimation()
        }
    }
}