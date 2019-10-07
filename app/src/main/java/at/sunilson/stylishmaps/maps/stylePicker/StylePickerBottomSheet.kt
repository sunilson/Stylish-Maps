package at.sunilson.stylishmaps.maps.stylePicker

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import at.sunilson.stylishmaps.maps.MapsViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

//TODO Rounded corners
class StylePickerBottomSheet : BottomSheetDialogFragment() {

    private val mapsViewModel: MapsViewModel by sharedViewModel(from = { requireParentFragment() })

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return super.onCreateView(inflater, container, savedInstanceState)
    }
}