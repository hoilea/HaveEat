package kr.snclab.haveeat.ui.splash

import android.os.Bundle
import android.view.View
import androidx.core.app.ActivityOptionsCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.ActivityNavigatorExtras
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kr.snclab.haveeat.Define
import kr.snclab.haveeat.R
import kr.snclab.haveeat.SharedData
import kr.snclab.haveeat.databinding.FragmentSplashBinding
import kr.snclab.haveeat.model.SigninProvider
import kr.snclab.haveeat.ui.BaseFragment
import retrofit2.HttpException
import androidx.core.util.Pair as UtilPair

@AndroidEntryPoint
class SplashFragment : BaseFragment<FragmentSplashBinding>() {

    private val viewModel by viewModels<SplashViewModel>()

    override fun getLayoutResId() = R.layout.fragment_splash

    override fun initFragment() {
        //accessToken 을 가지고 있으면 메인화면으로 이동 시킨다.
        lifecycleScope.launch {
            delay(1000)
            if (!Define.getAccessToken().isNullOrEmpty() && !SharedData.lastSignInProvider.isNullOrEmpty() && SharedData.lastSignInProvider != SigninProvider.guest.name) {
                try {
                    viewModel.tokenRefresh()
                    if (Define.userData?.gender == null) {
                        //정보 입력 하지 않았으면 정보 입력 화면으로
                        findNavController().navigate(SplashFragmentDirections.actionAddInfoFragment())
                    } else {
                        viewModel.getRecommendDaily()
                        //정보 입력 되었으면 메인 화면으로
                        findNavController().navigate(
                            SplashFragmentDirections.actionMainFragment()
                        )
                    }
                } catch (e: HttpException) {
                    e.printStackTrace()
//                    e.code() == 401
                    findNavController().navigate(SplashFragmentDirections.actionSnsSignInFragment())
                }
            } else {
                //정보가 없으면 snsSignIn 화면으로
                activity?.let { a ->
                    val options = ActivityOptionsCompat.makeSceneTransitionAnimation(a,
                        UtilPair.create(bind.title, "title"), UtilPair.create(bind.headMessage, "head_message"))
                    val extras = ActivityNavigatorExtras(options)
                    findNavController().navigate(
                        SplashFragmentDirections.actionSnsSignInFragment(), extras
                    )
                }
            }
        }
    }
}