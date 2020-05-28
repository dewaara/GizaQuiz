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

 lateinit var mAdView : AdView   
 private lateinit var mInterstitialAd: InterstitialAd

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category)

        toolbar.title = "Giza Quiz"
        setSupportActionBar(toolbar)

       // Banner Ads show start
        MobileAds.initialize(this)

        mAdView = findViewById(R.id.adView1)
        val adRequest = AdRequest.Builder().build()
        mAdView.loadAd(adRequest)

        
        // Full Screen Image show Ads------
        mInterstitialAd = InterstitialAd(this)
        mInterstitialAd.adUnitId = "ca-app-pub-2026058591107844/8705941982"
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
