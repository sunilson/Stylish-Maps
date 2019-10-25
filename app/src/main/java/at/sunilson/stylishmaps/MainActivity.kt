package at.sunilson.stylishmaps

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.fragment.NavHostFragment
import at.sunilson.stylishmaps.base.BaseFragment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    var insets: WindowInsetsCompat? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(nav_host_fragment.requireView()) { v, insets ->
            this.insets = insets
            supportFragmentManager.fragments.first().let {
                if (it is NavHostFragment) {
                    it.childFragmentManager.fragments.forEach {
                        if (it is BaseFragment) it.insetsChanged(insets)
                    }
                }
            }

            insets
        }
    }
}
