package at.sunilson.stylishmaps.maps

import android.Manifest
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.ALPHA
import android.view.View.TRANSLATION_Y
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.animation.doOnEnd
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.marginBottom
import androidx.core.view.marginTop
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import at.sunilson.stylishmaps.R
import at.sunilson.stylishmaps.base.BaseFragment
import at.sunilson.stylishmaps.data.entities.Location
import at.sunilson.stylishmaps.databinding.FragmentMapsBinding
import at.sunilson.stylishmaps.maps.searchList.SearchListRecyclerAdapter
import at.sunilson.stylishmaps.maps.styleList.StyleListRecyclerAdapter
import at.sunilson.stylishmaps.utils.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.*
import kotlinx.android.synthetic.main.fragment_maps.*
import org.koin.androidx.viewmodel.ext.android.viewModel


class MapsFragment : BaseFragment(), OnMapReadyCallback {

    private val stylePicker: BottomSheetBehavior<ConstraintLayout>
        get() = BottomSheetBehavior.from(style_picker_sheet)

    private var googleMap: GoogleMap? = null
    private val viewModel: MapsViewModel by viewModel()
    private var hidden: Boolean = false
    private var currentAnimator: AnimatorSet? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkPermissions()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding =
            generateBinding<FragmentMapsBinding>(inflater, R.layout.fragment_maps, container)
        binding.viewModel = viewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        stylePicker.state = STATE_HIDDEN
        observeCommands()
        observeBottomSheet()
        setupMap()
        setupStylesList()
        setupSearchList()
    }

    private fun checkPermissions() {
        if (!hasPermission(Manifest.permission.ACCESS_FINE_LOCATION)) {
            requestPermissions(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                PERMISSION_REQUEST
            )
        } else {
            viewModel.locateUser()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == PERMISSION_REQUEST
            && (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)
        ) {
            viewModel.locateUser()
        }
    }

    private fun setupMap() {
        (childFragmentManager.findFragmentById(R.id.google_map) as SupportMapFragment)
            .getMapAsync(this)
    }

    private fun observeBottomSheet() {
        backdrop.setOnClickListener { stylePicker.state = STATE_HIDDEN }

        stylePicker.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) {}

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == STATE_HIDDEN) {
                    styles_list.scrollToPosition(0)
                    ObjectAnimator.ofFloat(backdrop, "alpha", backdrop.alpha, 0f).apply {
                        duration = 200
                        doOnEnd { backdrop.visibility = View.GONE }
                        start()
                    }
                } else {
                    ObjectAnimator.ofFloat(backdrop, "alpha", backdrop.alpha, 1f).apply {
                        duration = 200
                        backdrop.visibility = View.VISIBLE
                        start()
                    }
                }
            }
        })
    }

    private fun setupSearchList() {
        search_list.adapter = SearchListRecyclerAdapter { viewModel.searchResultSelected(it) }
    }

    private fun setupStylesList() {
        styles_list.adapter = StyleListRecyclerAdapter {
            stylePicker.state = STATE_HIDDEN
            viewModel.setStyle(it)
        }
    }

    override fun applyInsets(insets: WindowInsetsCompat) {
        map_search.setMargins(top = insets.systemWindowInsetTop + map_search.marginTop)
        fab_design.setMargins(bottom = insets.systemWindowInsetBottom + fab_design.marginBottom)
        fab_export.setMargins(bottom = insets.systemWindowInsetBottom + fab_export.marginBottom)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        this.googleMap = googleMap
        googleMap.setOnMapClickListener { hideMapUi() }
        viewModel.currentState.style?.let { viewModel.setStyle(it) }
    }

    private fun hideMapUi() {
        map_search_input.hideKeyboard()
        map_search_input.clearFocus()
        currentAnimator?.cancel()
        currentAnimator = AnimatorSet().apply {
            playTogether(
                map_search.generateAnimator(hidden, true),
                fab_design.generateAnimator(hidden),
                fab_export.generateAnimator(hidden)
            )
            duration = 200
            start()
            hidden = !hidden
        }
    }

    private fun View.generateAnimator(
        hidden: Boolean = false,
        negative: Boolean = false
    ): ObjectAnimator {

        val valueHolders = if (!hidden) {
            val distance = 60f.convertToPx(requireContext())
            arrayOf(
                if (!negative) PropertyValuesHolder.ofFloat(
                    TRANSLATION_Y,
                    distance
                ) else PropertyValuesHolder.ofFloat(TRANSLATION_Y, -distance),
                PropertyValuesHolder.ofFloat(ALPHA, 0f)
            )
        } else {
            arrayOf(
                PropertyValuesHolder.ofFloat(TRANSLATION_Y, 0f),
                PropertyValuesHolder.ofFloat(ALPHA, 1f)
            )
        }

        return ObjectAnimator.ofPropertyValuesHolder(this, *valueHolders)
    }

    private fun observeCommands() {
        viewModel.commands.observe(viewLifecycleOwner, Observer {
            Do exhaustive when (it) {
                is MapsCommands.ChooseStyle -> {
                    if (it.currentStyle != null) {
                        stylePicker.state = STATE_EXPANDED
                    } else {
                        stylePicker.state = STATE_COLLAPSED
                    }
                    (styles_list.layoutManager as GridLayoutManager).scrollToPositionWithOffset(
                        (styles_list.adapter as StyleListRecyclerAdapter).getPositionForItem(
                            it.currentStyle ?: return@Observer
                        ), 0
                    )
                }
                MapsCommands.TakePicture -> googleMap?.snapshot { viewModel.pictureTaken(it) }
                is MapsCommands.ExportPicture -> findNavController().navigate(
                    MapsFragmentDirections.moveToExport(
                        it.uri.toString()
                    )
                )
                is MapsCommands.MoveMap -> {
                    hideMapUi()
                    googleMap?.animateCamera(
                        CameraUpdateFactory.newCameraPosition(
                            CameraPosition(LatLng(it.location.lat, it.location.lng), 14f, 0f, 0f)
                        )
                    )
                }
                is MapsCommands.SetMapStyle -> {
                    googleMap?.setMapStyle(
                        MapStyleOptions.loadRawResourceStyle(
                            requireContext(),
                            it.style
                        )
                    )
                }
            }
        })
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        if (savedInstanceState == null) return

        savedInstanceState.getInt("style", -1).let {
            viewModel.restoreStyleFromResource(it)
        }

        savedInstanceState.getDouble("lat", 100.0).let { lat ->
            if (lat != 100.0) {
                savedInstanceState.getDouble("lng", 200.0).let { lng ->
                    if (lng != 200.0) {
                        viewModel.setLocation(Location(lat, lng))
                    }
                }
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        viewModel.currentState.style?.let { outState.putInt("style", it) }
        viewModel.currentState.currentLocation?.let {
            outState.putDouble("lat", it.lat)
            outState.putDouble("lng", it.lng)
        }
    }

    override fun onResume() {
        super.onResume()
        setNavColors(transparent = true)
    }

    companion object {
        const val PERMISSION_REQUEST = 123
    }
}