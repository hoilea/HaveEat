package kr.snclab.haveeat.ui.dialog

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import kr.snclab.haveeat.Define
import kr.snclab.haveeat.R
import kr.snclab.haveeat.databinding.DialogWriteDietsBinding
import kr.snclab.haveeat.extension.getMimeType
import kr.snclab.haveeat.extension.startGetContent
import kr.snclab.haveeat.ui.BaseDialogFragment
import kr.snclab.haveeat.util.Log
import java.io.File
import java.io.FileOutputStream

private val PERMISSIONS_REQUIRED = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)

@AndroidEntryPoint
class WriteDietsDialog : BaseDialogFragment<DialogWriteDietsBinding>() {
    private val args: WriteDietsDialogArgs by navArgs()
    private val REQUEST_IMAGE_SELECT = 2001
    private val FACE_PHOTO_FILENAME = "food"
    private val PHOTO_EXTENSION = ".jpg"
    private var outputPhotoFile : String? = null

    private val outputDirectory: File by lazy {
        requireContext().applicationContext.filesDir
    }
    override fun initFragment() {

        bind.viewWriteDietsDialogCamera.setOnClickListener {
            findNavController().navigate(WriteDietsDialogDirections.actionCameraFragment(args.diets))
        }
        bind.viewWriteDietsDialogAlbum.setOnClickListener {
            gotoGallery()
        }
        bind.viewWriteDietsDialogSearch.setOnClickListener {
//                dismiss()
            findNavController().navigate(
                WriteDietsDialogDirections.actionHistoryDetailFragment(
                    args.diets
                )
            )
        }

        bind.viewWriteDietsDialogButton.setOnClickListener {
            dismiss()
        }
    }

    override fun getLayoutResId(): Int = R.layout.dialog_write_diets

    private fun gotoGallery() {
        startGetContent(REQUEST_IMAGE_SELECT, "Select Picture", "image/jpeg")
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_IMAGE_SELECT -> {
                    var unsupportedType = false
                    var added = false
                    data?.clipData?.also {clipData ->
                        clipData.getItemAt(0)?.also {
                            if(isSupportType(it.uri)) {
                                args.diets.tempImage = it.uri
                                findNavController().navigate(
                                    WriteDietsDialogDirections.actionHistoryDetailFragment(
                                        args.diets
                                    )
                                )
                            } else {
                                unsupportedType = true
                            }
                        }
                    }?: also {
                        data?.data?.also {
                            if(isSupportType(it)) {
                                args.diets.tempImage = it
                                findNavController().navigate(
                                    WriteDietsDialogDirections.actionHistoryDetailFragment(
                                        args.diets
                                    )
                                )
                            } else {
                                unsupportedType = true
                            }
                        }
                    }
                }
            }
        }
    }

    private fun isSupportType(uri: Uri): Boolean {
        val mimeType = uri.getMimeType(requireContext())
        Log.d(Define.TAG, "mimeType:$mimeType")
        return (mimeType.contains("jpg") || mimeType.contains("jpeg"))
    }

    private fun checkGallery(bitmap: Bitmap) {

        val photoFile =
            createFoodPhotoFile(outputDirectory, PHOTO_EXTENSION)
        val fo = FileOutputStream(photoFile)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fo)

        outputPhotoFile = photoFile.absolutePath

//            Navigation.findNavController(requireActivity(), R.id.fragment_container).navigate(
//                CameraFragmentDirections.actionCameraToGallery(outputPhotoFile)
//            )

    }

    private fun createFoodPhotoFile(baseFolder: File, extension: String) =
        File(baseFolder, FACE_PHOTO_FILENAME + extension)

    /** Convenience method used to check if all permissions required by this app are granted */
    fun hasPermissions(context: Context) = PERMISSIONS_REQUIRED.all {
        ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
    }
}