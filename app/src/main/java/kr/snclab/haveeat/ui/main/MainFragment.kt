package kr.snclab.haveeat.ui.main

import android.graphics.Point
import android.os.Bundle
import android.view.Display
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.behavior.HideBottomViewOnScrollBehavior
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kr.snclab.haveeat.Define
import kr.snclab.haveeat.R
import kr.snclab.haveeat.databinding.FragmentMainBinding
import kr.snclab.haveeat.extension.px
import kr.snclab.haveeat.ui.BaseFragment
import kr.snclab.haveeat.util.Log
import timber.log.Timber
import java.lang.Exception


@AndroidEntryPoint
class MainFragment : BaseFragment<FragmentMainBinding>() {

    private val viewModel by viewModels<MainViewModel>()

    override fun getLayoutResId() = R.layout.fragment_main

    override fun initFragment() {
        if(Define.isGuest) {
            bind.viewMainBottomNavigation.menu.clear()
            bind.viewMainBottomNavigation.inflateMenu(R.menu.menu_main_guest_bottom)
        }
        viewModel.lastSelectedTab.value?.let {
            moveBottomBar(it, false)
        }

        addKeyboardDetectListener()
        bind.viewMainBottomNavigation.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.menu_bottom_history -> {
                    bind.viewMainPager.currentItem = 0
                    moveBottomBar(0)
                    true
                }
                R.id.menu_bottom_habit -> {
                    bind.viewMainPager.currentItem = 1
                    moveBottomBar(1)
                    true
                }
                R.id.menu_bottom_foodinfo -> {
                    bind.viewMainPager.currentItem = 2
                    moveBottomBar(2)
                    true
                }
                R.id.menu_bottom_setting -> {
                    bind.viewMainPager.currentItem = 3
                    moveBottomBar(3)
                    true
                }
                R.id.menu_bottom_login -> {
                    findNavController().navigate(MainFragmentDirections.actionSnsSignInFragment())
                    true
                }
                else -> false
            }
        }
        bind.viewMainPager.adapter = MainPagerAdapter(this)
        bind.viewMainPager.registerOnPageChangeCallback(PageChangeCallback())
        bind.viewMainPager.isUserInputEnabled = false
    }

    private inner class PageChangeCallback : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            super.onPageSelected(position)
            bind.viewMainBottomNavigation.selectedItemId = when (position) {
                0 -> R.id.menu_bottom_history
                1 -> R.id.menu_bottom_habit
                2 -> R.id.menu_bottom_foodinfo
                3 -> R.id.menu_bottom_setting
                else -> error("no such position: $position")
            }
        }
    }

    private val bottomItemWidth: Int by lazy {
        val display: Display = requireActivity().windowManager.defaultDisplay
        val size = Point()
        display.getSize(size)
        (size.x - bind.viewMainBottomNavigation.paddingStart - bind.viewMainBottomNavigation.paddingEnd) / bind.viewMainBottomNavigation.menu.size()
    }
    private val firstGap: Int by lazy {
        ((bottomItemWidth - resources.getDimension(R.dimen.main_bottom_nav_bar_width)) / 2).toInt()
    }

    private fun moveBottomBar(position: Int, animation: Boolean = true) {
        val xPos = (firstGap + (bottomItemWidth * position)).toFloat()
        Log.d("test", "moveBottomBar ${bind.viewMainBottomNavigationBar.x}, $xPos")

        if(bind.viewMainBottomNavigationBar.x == 0f || !animation) {
            bind.viewMainBottomNavigationBar.x = (firstGap + (bottomItemWidth * position)).toFloat()
        } else  {
            bind.viewMainBottomNavigationBar.animate()
                .translationX((firstGap + (bottomItemWidth * position)).toFloat())
        }
        viewModel.lastSelectedTab.postValue(position)
    }

    private fun addKeyboardDetectListener() {
        val topView = activity?.window?.decorView?.findViewById<View>(android.R.id.content)
        topView?.let { it ->
            it.viewTreeObserver.addOnGlobalLayoutListener {
                val heightDifference = (it.rootView?.height ?: 0) - it.height

                if (heightDifference > 200.px) {
                    bind.viewMainBottomNavigation.let { bottomMenu ->
                        Log.d(Define.TAG, "keyboard shown")
                        ((bottomMenu.layoutParams as? CoordinatorLayout.LayoutParams)?.behavior as? HideBottomViewOnScrollBehavior)?.slideDown(bottomMenu)
                    }
                } else {
                    bind.viewMainBottomNavigation.let { bottomMenu ->
                        Log.d(Define.TAG, "keyboard hidden")
                        ((bottomMenu.layoutParams as? CoordinatorLayout.LayoutParams)?.behavior as? HideBottomViewOnScrollBehavior)?.slideUp(bottomMenu)
                    }
                }
            }
        }
    }
}