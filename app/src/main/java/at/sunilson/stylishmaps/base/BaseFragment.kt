package at.sunilson.stylishmaps.base

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.animation.Animation
import androidx.annotation.ColorRes
import androidx.annotation.LayoutRes
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import at.sunilson.stylishmaps.MainActivity
import at.sunilson.stylishmaps.R
import at.sunilson.stylishmaps.utils.convertToPx

abstract class BaseFragment : Fragment() {

    private var insets: WindowInsetsCompat? = null

    /**
     * Sets up the given layout resource with a [BaseViewModel] and data binding
     */
    protected fun <Binding : ViewDataBinding> generateBinding(
        inflater: LayoutInflater,
        @LayoutRes layout: Int,
        container: ViewGroup?
    ): Binding {
        val binding = DataBindingUtil.inflate<Binding>(inflater, layout, container, false)
        binding.lifecycleOwner = this
        return binding
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.setBackgroundColor(Color.WHITE)
        applyInsets((requireActivity() as? MainActivity)?.insets ?: return)
    }

    override fun onCreateAnimation(transit: Int, enter: Boolean, nextAnim: Int): Animation? {
        //Show above previous fragment for animation purposes
        if (nextAnim == R.anim.move_in_from_right || nextAnim == R.anim.move_out_to_right) {
            ViewCompat.setElevation(requireView(), 15f.convertToPx(requireContext()))
        } else if (nextAnim == R.anim.move_in_from_left_slightly || nextAnim == R.anim.move_out_to_left_slightly) {
            ViewCompat.setElevation(requireView(), 0f)
            ViewCompat.setTranslationZ(requireView(), 0f)
        }

        return super.onCreateAnimation(transit, enter, nextAnim)
    }

    fun insetsChanged(insets: WindowInsetsCompat) {
        this.insets = insets
        applyInsets(insets)
    }

    open fun applyInsets(insets: WindowInsetsCompat) {}

    protected fun setNavColors(
        @ColorRes statusColor: Int = android.R.color.black,
        @ColorRes navColor: Int = statusColor,
        darkStatus: Boolean = true,
        darkNav: Boolean = darkStatus,
        transparent: Boolean = false,
        onlyStatusTransparent: Boolean = false
    ) {
        activity?.let { activity ->
            //Make bars opaque again
            activity.window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)
            activity.window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            activity.window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)

            //Set status and nav bar transparent and draw content below
            if (transparent) {
                activity.window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
                if (!onlyStatusTransparent) {
                    activity.window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)
                } else {
                    activity.window.navigationBarColor =
                        ContextCompat.getColor(activity, statusColor)
                }
                activity.window.decorView.systemUiVisibility =
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    activity.window.decorView.systemUiVisibility =
                        activity.window.decorView.systemUiVisibility and View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR.inv()
                }
                return
            }

            activity.window.decorView.systemUiVisibility =
                activity.window.decorView.systemUiVisibility or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION

            //Set colors
            activity.window.navigationBarColor = ContextCompat.getColor(activity, navColor)
            activity.window.statusBarColor = ContextCompat.getColor(activity, statusColor)

            //Set light or dark icons
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                activity.window.decorView.systemUiVisibility = if (!darkNav) {
                    activity.window.decorView.systemUiVisibility or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
                } else {
                    activity.window.decorView.systemUiVisibility and View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR.inv()
                }

                activity.window.decorView.systemUiVisibility = if (!darkStatus) {
                    activity.window.decorView.systemUiVisibility or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                } else {
                    activity.window.decorView.systemUiVisibility and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
                }
            }
        }
    }
}