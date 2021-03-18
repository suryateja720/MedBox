package com.project.medbox.users.distributor.fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.project.medbox.R
import com.project.medbox.users.distributor.stocktabsui.SectionsPagerAdapter

class StockBillingFragment : Fragment() {
var mContext : Context? = null
    var viewPager: ViewPager? = null
    var tabs: TabLayout? =null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_stock_billing, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        activity?.title = "Stock & Billing"

        mContext = this.context
        viewPager = view.findViewById(R.id.tab_view_holder)
        tabs = view.findViewById(R.id.stock_manager_tabs)

        val sectionsPagerAdapter =
            SectionsPagerAdapter(mContext!!, childFragmentManager)

        viewPager?.adapter = sectionsPagerAdapter
        tabs?.setupWithViewPager(viewPager)
        tabs?.setOnTabSelectedListener(object : TabLayout.OnTabSelectedListener{
            override fun onTabReselected(tab: TabLayout.Tab?) {

            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                when (tabs?.selectedTabPosition) {
                    0 -> {
                        tabs?.setTabTextColors(resources.getColor(R.color.colorGrey200),resources.getColor(R.color.colorTeal200))
                        tabs?.setSelectedTabIndicatorColor(resources.getColor(R.color.colorTeal200))

                    }
                    1 -> {

                        tabs?.setTabTextColors(resources.getColor(R.color.colorGrey200),resources.getColor(R.color.colorTeal200))
                        tabs?.setSelectedTabIndicatorColor(resources.getColor(R.color.colorTeal200))

                    }
                    2 ->{
                        tabs?.setTabTextColors(resources.getColor(R.color.colorGrey200),resources.getColor(R.color.colorOrange300))
                        tabs?.setSelectedTabIndicatorColor(resources.getColor(R.color.colorOrange200))

                    }
                    3 ->{
                        tabs?.setTabTextColors(resources.getColor(R.color.colorGrey200),resources.getColor(R.color.colorRed))
                        tabs?.setSelectedTabIndicatorColor(resources.getColor(R.color.colorRed))

                    }
                }
            }

            override fun onTabSelected(tab: TabLayout.Tab?) {

            }

        })
    }


}
