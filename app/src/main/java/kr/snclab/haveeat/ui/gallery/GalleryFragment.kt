package kr.snclab.haveeat.ui.gallery

import androidx.fragment.app.viewModels
//import com.github.dhaval2404.imagepicker.ImagePicker
import kr.snclab.haveeat.ui.BaseFragment
import kr.snclab.haveeat.R
import dagger.hilt.android.AndroidEntryPoint
import kr.snclab.haveeat.databinding.FragmentGalleryBinding
import kr.snclab.haveeat.ui.main.MainViewModel

@AndroidEntryPoint
class GalleryFragment : BaseFragment<FragmentGalleryBinding>() {

    private val viewModel by viewModels<MainViewModel>()

    override fun getLayoutResId() = R.layout.fragment_gallery

    override fun initFragment() {
//        ImagePicker.with(this)
//            .galleryMimeTypes(  //Exclude gif images
//                mimeTypes = arrayOf(
//                    "image/png",
//                    "image/jpg",
//                    "image/jpeg"
//                )
//            )
//            .start{ resultCode, data ->
//
//            }
    }

}