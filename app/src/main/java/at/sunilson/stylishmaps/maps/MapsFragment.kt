package at.sunilson.stylishmaps.maps

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import at.sunilson.stylishmaps.R
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

//TODO Safeargs
//TODO Databinding
class MapsFragment : Fragment(), OnMapReadyCallback {

    private val viewModel: MapsViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_maps, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (childFragmentManager.findFragmentById(R.id.googleMap) as SupportMapFragment).getMapAsync(
            this
        )
    }

    override fun onMapReady(p0: GoogleMap?) {
        //TODO
    }
}