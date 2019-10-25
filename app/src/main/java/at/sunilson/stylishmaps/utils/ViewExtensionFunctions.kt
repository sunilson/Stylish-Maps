package at.sunilson.stylishmaps.utils

import android.content.Context
import android.util.DisplayMetrics
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.marginBottom
import androidx.core.view.marginLeft
import androidx.core.view.marginRight
import androidx.core.view.marginTop

//Converts dp to px
fun Int.convertToPx(context: Context): Int {
    return (this * (context.resources.displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)).toInt()
}

//Converts px to dp
fun Int.convertToDp(context: Context): Int {
    return (this / (context.resources.displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)).toInt()
}

//Converts dp to px
fun Float.convertToPx(context: Context): Float {
    return (this * (context.resources.displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT))
}

//Converts px to dp
fun Float.convertToDp(context: Context): Float {
    return (this / (context.resources.displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT))
}


fun View.setMargins(
    left: Int = this.marginLeft,
    right: Int = this.marginRight,
    top: Int = this.marginTop,
    bottom: Int = this.marginBottom
) {
    val layoutParams = this.layoutParams
    if (layoutParams is CoordinatorLayout.LayoutParams) {
        this.layoutParams = layoutParams.apply {
            setMargins(
                left,
                top,
                right,
                bottom
            )
        }
    } else if (layoutParams is ConstraintLayout.LayoutParams) {
        this.layoutParams = layoutParams.apply {
            setMargins(
                left,
                top,
                right,
                bottom
            )
        }
    }
}