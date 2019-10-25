package at.sunilson.stylishmaps.export

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.fragment.findNavController
import at.sunilson.stylishmaps.R
import at.sunilson.stylishmaps.base.BaseFragment
import at.sunilson.stylishmaps.databinding.FragmentExportBinding
import at.sunilson.stylishmaps.maps.MapsViewModel
import at.sunilson.stylishmaps.utils.setMargins
import kotlinx.android.synthetic.main.fragment_export.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class ExportFragment : BaseFragment() {

    private val viewModel: ExportViewModel by viewModel()
    private val mapsViewModel: MapsViewModel by sharedViewModel()
    private var insets: WindowInsetsCompat? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val image = mapsViewModel.currentCapture.value
        if (image == null) {
            findNavController().popBackStack()
        } else {
            viewModel.image.value = image
        }
    }

    override fun onResume() {
        super.onResume()
        setNavColors(transparent = true)
    }


    override fun applyInsets(insets: WindowInsetsCompat) {
        crop_view.setMargins(
            insets.stableInsetLeft,
            insets.systemWindowInsetRight,
            insets.systemWindowInsetTop,
            insets.systemWindowInsetBottom
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding =
            generateBinding<FragmentExportBinding>(inflater, R.layout.fragment_export, container)
        binding.viewModel = viewModel
        return binding.root
    }
}