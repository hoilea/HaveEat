package kr.snclab.haveeat.ui.main

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import kr.snclab.haveeat.ui.food.FoodInfoFragment
import kr.snclab.haveeat.ui.main.history.HistoryFragment
import kr.snclab.haveeat.ui.setting.SettingFragment
import kr.snclab.haveeat.ui.state.StateFragment

class MainPagerAdapter(fragment: MainFragment): FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = 4

    override fun createFragment(position: Int): Fragment = when(position) {
        0 -> HistoryFragment()
        1 -> StateFragment()
        2 -> FoodInfoFragment()
        3 -> SettingFragment()
        else -> HistoryFragment()
    }
}