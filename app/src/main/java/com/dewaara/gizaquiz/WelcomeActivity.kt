package com.dewaara.gizaquiz

/*
Giza Quiz developed by Er. Md. Halim from Dewaara.Pvt.Ltd. 01/09/2019
 */

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.view.Window
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.Toast


class WelcomeActivity : AppCompatActivity(), Animation.AnimationListener {
    lateinit var imageView:ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        setContentView(R.layout.activity_welcome)

        imageView = findViewById(R.id.logo)
        val animation = AnimationUtils.loadAnimation(this, R.anim.anim)
        imageView.animation = animation

        animation.setAnimationListener(this)
        Handler().postDelayed({
            val homeIntent = Intent(this@WelcomeActivity, CategoryActivity::class.java)
            startActivity(homeIntent)
        }, SFLASH_TIME_OUT.toLong())
    }


    override fun onAnimationStart(animation: Animation) {
        //Toast.makeText(this@WelcomeActivity, "", Toast.LENGTH_SHORT).show()
    }

    override fun onAnimationEnd(animation: Animation) {
        //Toast.makeText(this@WelcomeActivity, "", Toast.LENGTH_SHORT).show()
    }

    override fun onAnimationRepeat(animation: Animation) {
        //Toast.makeText(this@WelcomeActivity, "", Toast.LENGTH_SHORT).show()

    }

    companion object {
        private val SFLASH_TIME_OUT = 1500
    }
}


