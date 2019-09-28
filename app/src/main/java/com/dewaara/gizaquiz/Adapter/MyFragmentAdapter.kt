package com.dewaara.gizaquiz.Adapter

/*
Giza Quiz developed by Er. Md. Halim from Dewaara.Pvt.Ltd. 01/09/2019
 */

import android.content.Context
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import com.dewaara.gizaquiz.QuestionFragment

class MyFragmentAdapter(fm:FragmentManager,var context: Context,
                        var fragmentList: List<QuestionFragment>):FragmentPagerAdapter(fm) {
    override fun getItem(p0: Int): Fragment {
        return fragmentList[p0]
    }

    override fun getCount(): Int {
        return fragmentList.size
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return StringBuilder("Question ").append(position+1).toString()

    }

    internal var instance:MyFragmentAdapter?=null
}