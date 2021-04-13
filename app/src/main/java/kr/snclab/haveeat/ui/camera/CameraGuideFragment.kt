package kr.snclab.haveeat.ui.camera

import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kr.snclab.haveeat.R
import kr.snclab.haveeat.databinding.FragmentCameraGuideBinding
import kr.snclab.haveeat.ui.BaseFragment
import kr.snclab.haveeat.ui.main.MainViewModel

@AndroidEntryPoint
class CameraGuideFragment : BaseFragment<FragmentCameraGuideBinding>() {

    private val viewModel by viewModels<MainViewModel>()

    override fun getLayoutResId() = R.layout.fragment_camera_guide

    override fun initFragment() {
        bind.close.setOnClickListener {
            findNavController().popBackStack()
        }
    }
}