package at.sunilson.stylishmaps.export

import android.Manifest
import android.app.WallpaperManager
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import at.sunilson.stylishmaps.R
import at.sunilson.stylishmaps.base.BaseFragment
import at.sunilson.stylishmaps.databinding.FragmentExportBinding
import at.sunilson.stylishmaps.utils.Do
import at.sunilson.stylishmaps.utils.setMargins
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.android.synthetic.main.fragment_export.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File


class ExportFragment : BaseFragment() {

    private val viewModel: ExportViewModel by viewModel()
    private val args: ExportFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.setImage(Uri.parse(args.image))
    }

    override fun onResume() {
        super.onResume()
        setNavColors()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("currentImage", viewModel.currentState.image.toString())
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        savedInstanceState?.let {
            it.getString("currentImage")?.let {
                viewModel.setImage(Uri.parse(it))
            }
        }
    }

    override fun applyInsets(insets: WindowInsetsCompat) {
        map_result.setMargins(top = insets.systemWindowInsetTop)
        crop_view.setMargins(top = insets.systemWindowInsetTop)
        toolbar.setMargins(bottom = insets.systemWindowInsetBottom)
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

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == 123 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            viewModel.saveToDownload()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.commands.observe(viewLifecycleOwner, Observer {
            Do exhaustive when (it) {
                is ExportCommand.ShowToast -> Toast.makeText(
                    requireContext(),
                    it.text,
                    Toast.LENGTH_LONG
                ).show()
                is ExportCommand.DownloadUri -> requestPermissions(
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    123
                )
                is ExportCommand.ShareUri -> {
                    val shareUri = FileProvider.getUriForFile(
                        requireContext(),
                        "at.sunilson.fileprovider",
                        File(it.uri.path)
                    )
                    val shareIntent: Intent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_STREAM, shareUri)
                        flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                        type = "image/jpeg"
                    }
                    startActivity(Intent.createChooser(shareIntent, "Share map"))
                }
                is ExportCommand.SetWallpaper -> {
                    MaterialAlertDialogBuilder(requireContext())
                        .setTitle("Set wallpaper")
                        .setMessage("Do you really want to set this as your wallpaper?")
                        .setPositiveButton("Ok") { _, _ ->
                            WallpaperManager.getInstance(requireContext()).setBitmap(it.bitmap)
                        }
                        .setNegativeButton("Cancel", null)
                        .show()
                }
                ExportCommand.CropImage -> {
                    crop_view.setOnCropImageCompleteListener { _, result ->
                        viewModel.processBitmap(result.bitmap)
                    }
                    crop_view.getCroppedImageAsync()
                }
            }
        })
    }
}