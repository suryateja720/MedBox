package com.project.medbox.users.distributor.stocktabsui

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.project.medbox.R
import com.project.medbox.common.BlankFragment
import com.project.medbox.users.distributor.fragments.ExpiredStockFragment
import com.project.medbox.users.distributor.fragments.InStockFragment
import com.project.medbox.users.distributor.fragments.InwardFragment
import com.project.medbox.users.distributor.fragments.OutwardFragment

private val TAB_TITLES = arrayOf(
    R.string.tab_text_1,
    R.string.tab_text_2,
    R.string.tab_text_3,
    R.string.tab_text_4
)

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
class SectionsPagerAdapter(
    private val context: Context,
    fm: FragmentManager
) :
    FragmentPagerAdapter(fm) {
    override fun getItem(position: Int): Fragment {
        // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment (defined as a static inner class below).

//        return PlaceholderFragment.newInstance(position + 1)

        return when (position) {
            0 -> {
                OutwardFragment()
            }
            1 -> {
                InwardFragment()
            }
            2 ->{
                InStockFragment()
            }
            3 ->{
                ExpiredStockFragment()
            }
            else -> {
                BlankFragment()
            }
        }


    }

    override fun getPageTitle(position: Int): CharSequence? {
        return context.resources.getString(TAB_TITLES[position])
    }

    override fun getCount(): Int {
        // Show 2 total pages.
        return 4
    }
}