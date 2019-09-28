package com.dewaara.gizaquiz

/*
Giza Quiz developed by Er. Md. Halim from Dewaara.Pvt.Ltd. 01/09/2019
 */

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import com.dewaara.gizaquiz.Adapter.CategoryAdapter
import com.dewaara.gizaquiz.Common.SpacesItemDecoration
import com.dewaara.gizaquiz.DBHelper.DBHelper
import com.google.android.gms.ads.*
import kotlinx.android.synthetic.main.activity_category.*

class CategoryActivity : AppCompatActivity() {

    private lateinit var mInterstitialAd: InterstitialAd

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category)

        toolbar.title = "Giza Quiz"
        setSupportActionBar(toolbar)

        // Start Ads----  as a Banner Ad
        MobileAds.initialize(this,getString(R.string.admob_app_id))

        // Start Ads full screen Image show
        // Full Screen Image show Ads------
        mInterstitialAd = InterstitialAd(this)
        mInterstitialAd.adUnitId = "ca-app-pub-4698516881015916/8800162639"
        mInterstitialAd.loadAd(AdRequest.Builder().build())

        mInterstitialAd.adListener = object: AdListener(){

            override fun onAdLoaded() {
                mInterstitialAd.show()
                super.onAdLoaded()
            }
        } // End of the Ads show----!!

        recycler_category.setHasFixedSize(true)
        recycler_category.layoutManager = GridLayoutManager(this,2)

        val adapter = CategoryAdapter(this,DBHelper.getInstance(this).allCategories)
        recycler_category.addItemDecoration(SpacesItemDecoration(4))
        recycler_category.adapter = adapter





    }
}
