package com.dewaara.gizaquiz

/*
Giza Quiz developed by Er. Md. Halim from Dewaara.Pvt.Ltd. 01/09/2019
 */



import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.CountDownTimer
import android.support.constraint.ConstraintLayout
import android.support.design.widget.NavigationView
import android.support.v4.content.LocalBroadcastManager
import android.support.v4.view.GravityCompat
import android.support.v4.view.ViewPager
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.dewaara.gizaquiz.Adapter.GridAnswerAdapter
import com.dewaara.gizaquiz.Adapter.MyFragmentAdapter
import com.dewaara.gizaquiz.Adapter.QuestionListHelperAdapter
import com.dewaara.gizaquiz.Common.Common
import com.dewaara.gizaquiz.Common.SpacesItemDecoration
import com.dewaara.gizaquiz.DBHelper.DBHelper
import com.dewaara.gizaquiz.Model.CurrentQuestion
import com.github.javiersantos.materialstyleddialogs.MaterialStyledDialog
import com.google.android.gms.ads.*
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_question.*
import kotlinx.android.synthetic.main.app_bar_question.*
import kotlinx.android.synthetic.main.content_question.*
import java.lang.StringBuilder
import java.util.concurrent.TimeUnit


class QuestionActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    val CODE_GET_RESULT = 9999

    lateinit var countDownTimer:CountDownTimer

    var time_play = Common.TOTAL_TIME

    var isAnswerModeView = false

    lateinit var adapter: GridAnswerAdapter
    lateinit var questionHelperAdapter:QuestionListHelperAdapter

    lateinit var txt_wrong_answer:TextView

    private lateinit var mInterstitialAd: InterstitialAd


    internal var goToQuestionNum:BroadcastReceiver = object:BroadcastReceiver(){
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent!!.action!!.toString() == Common.KEY_GO_TO_QUESTION){
                val question = intent!!.getIntExtra(Common.KEY_GO_TO_QUESTION,-1)
                if (question != -1)
                    view_pager.currentItem = question

                drawer_layout.closeDrawer(Gravity.LEFT)
            }
        }

    }

    override fun onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(goToQuestionNum)
        if (countDownTimer != null)
            countDownTimer!!.cancel()
        if (Common.fragmentList != null)
            Common.fragmentList.clear()
        if (Common.answerSheetList != null)
            Common.answerSheetList.clear()
        super.onDestroy()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_question)
        setSupportActionBar(toolbar)

        // start

        toolbar.title = Common.selectedCategory!!.name
        setSupportActionBar(toolbar)

        //toolbar end

        // Full Screen Image show Ads------
        MobileAds.initialize(this)
        mInterstitialAd = InterstitialAd(this)
        mInterstitialAd.adUnitId = "ca-app-pub-2026058591107844/3453615301"
        mInterstitialAd.loadAd(AdRequest.Builder().build())

        mInterstitialAd.adListener = object: AdListener(){

            override fun onAdLoaded() {
                mInterstitialAd.show()
                super.onAdLoaded()
            }
        } // End of the Ads show----!!



        LocalBroadcastManager.getInstance(this).registerReceiver(goToQuestionNum, IntentFilter(Common.KEY_GO_TO_QUESTION))



        val toggle = ActionBarDrawerToggle(
            this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)

        val recycler_helper_answer_sheet = nav_view.getHeaderView(0).findViewById<View>(R.id.answer_sheet) as RecyclerView
        recycler_helper_answer_sheet.setHasFixedSize(true)
        recycler_helper_answer_sheet.layoutManager = GridLayoutManager(this,3)
        recycler_helper_answer_sheet.addItemDecoration(SpacesItemDecoration(2))

        val btn_done = nav_view.getHeaderView(0).findViewById<View>(R.id.btn_done) as Button
        btn_done.setOnClickListener{
            if (!isAnswerModeView)
            {
                MaterialStyledDialog.Builder(this@QuestionActivity)
                    .setTitle("Finish ?")
                    .setDescription("Do you really want to finish ?")
                    .setIcon(R.drawable.ic_mood_white_24dp)
                    .setNegativeText("No")
                    .onPositive { dialog, which -> dialog.dismiss() }
                    .setPositiveText("Yes")
                    .onPositive { dialog, which -> finishGame()
                    drawer_layout.closeDrawer(Gravity.LEFT)}
                    .show()
            }
            else{
                finishGame()
            }
        }

        //Gen Question base on category
        genQuestion()

        if (Common.questionList.size > 0)
        {
            //Show Timer , right Answer text view
            txt_timer.visibility = View.VISIBLE
            txt_right_answer.visibility = View.VISIBLE

            countTimer()

            //Gen item for grid_answer
            genItems()

            grid_answer.setHasFixedSize(true)
            if (Common.questionList.size > 0)
                grid_answer.layoutManager = GridLayoutManager(this,if(Common.questionList.size > 5) Common.questionList.size/2 else Common.questionList.size)
            adapter = GridAnswerAdapter(this,Common.answerSheetList)
            grid_answer.adapter = adapter

            // Gen fregment List
            genFragmentList()

            val fragmentAdapter = MyFragmentAdapter(supportFragmentManager,this,Common.fragmentList)
            view_pager.offscreenPageLimit = Common.questionList.size
            view_pager.adapter = fragmentAdapter // Bind question to View pager

            sliding_tabs.setupWithViewPager(view_pager)

            //Event
            view_pager.addOnPageChangeListener(object: ViewPager.OnPageChangeListener{

                val SCROLLING_RIGHT = 0
                val SCROLLING_LEFT = 1
                val SCROLLING_UNDETERMINED = 2

                var currentScrollDirection = SCROLLING_UNDETERMINED

                private val isScrollDirectionUndetermined:Boolean
                    get() = currentScrollDirection == SCROLLING_UNDETERMINED
                private val isScrollDirectionRight:Boolean
                    get() = currentScrollDirection == SCROLLING_RIGHT
                private val isScrollDirectionLeft:Boolean
                    get() = currentScrollDirection == SCROLLING_LEFT

                private fun setScrollingDirection(positionOffset:Float)
                {
                    if (1-positionOffset >= 0.5)
                        this.currentScrollDirection = SCROLLING_RIGHT
                    else if (1-positionOffset <= 0.5)
                        this.currentScrollDirection = SCROLLING_LEFT

                }

                override fun onPageScrollStateChanged(p0: Int) {
                    if (p0 == ViewPager.SCROLL_STATE_IDLE)
                        this.currentScrollDirection = SCROLLING_UNDETERMINED
                }

                override fun onPageScrolled(p0: Int, p1: Float, p2: Int) {
                    if (isScrollDirectionUndetermined)
                        setScrollingDirection(p1)
                }

                override fun onPageSelected(p0: Int) {

                    val questionFragment: QuestionFragment
                    var position = 0
                    if (p0 > 0){
                        if (isScrollDirectionRight)
                        {
                            questionFragment = Common.fragmentList[p0-1]
                            position = p0-1
                        }
                        else if (isScrollDirectionRight)
                        {
                            questionFragment = Common.fragmentList[p0+1]
                            position = p0+1
                        }
                        else{
                            questionFragment = Common.fragmentList[p0]
                        }
                    }
                    else
                    {
                        questionFragment = Common.fragmentList[0]
                        position = 0
                    }

                    if (Common.answerSheetList[position].type == Common.ANSWER_TYPE.NO_ANSWER)
                    {
                        //If you to show corect answer , just enable it
                        val question_state = questionFragment.selectedAnswer()
                        Common.answerSheetList[position] = question_state
                        adapter.notifyDataSetChanged()
                        questionHelperAdapter.notifyDataSetChanged()


                        countCorrectAnswer()

                        txt_right_answer.text = ("${Common.right_answer_count} / ${Common.questionList.size}")
                        txt_wrong_answer.text = "${Common.wrong_answer_count}"

                        if(question_state.type != Common.ANSWER_TYPE.NO_ANSWER)
                        {
                            questionFragment.showCorrectAnswer()
                            questionFragment.disableAnswer()
                        }

                    }

                }

            })


            txt_right_answer.text = "${Common.right_answer_count}/${Common.questionList.size}"
            questionHelperAdapter = QuestionListHelperAdapter(this,Common.answerSheetList)
            recycler_helper_answer_sheet.adapter = questionHelperAdapter

        }
    }

    private fun countCorrectAnswer() {
        Common.right_answer_count = 0 //reset
        Common.wrong_answer_count = 0

        for (item in Common.answerSheetList)
            if (item.type == Common.ANSWER_TYPE.RIGHT_ANSWER)
                Common.right_answer_count++
        else if (item.type == Common.ANSWER_TYPE.WRONG_ANSWER)
                Common.wrong_answer_count++
    }

    private fun genFragmentList() {
        for (i in Common.questionList.indices)
        {
            val bundle = Bundle()
            bundle.putInt("index",i)
            val fragment = QuestionFragment()
            fragment.arguments = bundle

            Common.fragmentList.add(fragment)
        }
    }

    private fun genItems() {
        for (i in Common.questionList.indices)
            Common.answerSheetList.add(CurrentQuestion(i,Common.ANSWER_TYPE.NO_ANSWER))  //No naswer for all questions

    }

    private fun countTimer() {
        countDownTimer = object:CountDownTimer(Common.TOTAL_TIME.toLong(),1000)
        {
            override fun onFinish() {
                finishGame()
            }

            override fun onTick(interval: Long) {
                txt_timer.text = (java.lang.String.format("%02d:%02d",
                    TimeUnit.MILLISECONDS.toMinutes(interval),
                    TimeUnit.MILLISECONDS.toSeconds(interval) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(interval))))
                time_play-=1000
            }

        }.start()
    }

    private fun finishGame() {

        val position = view_pager.currentItem
        val questionFragment = Common.fragmentList[position]


        val question_state = questionFragment.selectedAnswer()
        Common.answerSheetList[position] = question_state
        adapter.notifyDataSetChanged()
        questionHelperAdapter.notifyDataSetChanged()

        countCorrectAnswer()

        txt_right_answer.text = ("${Common.right_answer_count} / ${Common.questionList.size}")
        txt_wrong_answer.text = "${Common.wrong_answer_count}"

        if(question_state.type != Common.ANSWER_TYPE.NO_ANSWER)
        {
            questionFragment.showCorrectAnswer()
            questionFragment.disableAnswer()
        }

        val intent = Intent(this@QuestionActivity,ResultActivity::class.java)
        Common.timer = Common.TOTAL_TIME - time_play
        Common.no_answer_count = Common.questionList.size - (Common.right_answer_count+Common.wrong_answer_count)
        Common.data_question = StringBuilder(Gson().toJson(Common.answerSheetList))

        startActivityForResult(intent,CODE_GET_RESULT)

    }


    private fun genQuestion() {
        Common.questionList = DBHelper.getInstance(this)
            .getQuestionByCategory(Common.selectedCategory!!.id)

        if (Common.questionList.size == 0)
        {
            MaterialStyledDialog.Builder(this)
                .setTitle("Oppps!!")
                .setIcon(R.drawable.ic_sentiment_very_dissatisfied_black_24dp)
                .setDescription("We don't have any question in this ${Common.selectedCategory!!.name} category")
                .setPositiveText("OK")
                .onPositive{ dialog,which ->
                    dialog.dismiss()
                    finish()
                }.show()
        }
        // error solve start
       // else
        //{
          //  if (Common.answerSheetList.size > 0)
       //         Common.answerSheetList.clear()
        //}
        // error solve end
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            this.finish() // close this activity when click to back button
            super.onBackPressed()
        }
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        val item = menu!!.findItem(R.id.menu_wrong_answer)
        val layout = item.actionView as ConstraintLayout
        txt_wrong_answer = layout.findViewById(R.id.txt_wrong_answer) as TextView
        txt_wrong_answer.text = 0.toString()

        return true
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.question, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId)
        {
         R.id.menu_done -> {
             if (!isAnswerModeView)
             {
                 MaterialStyledDialog.Builder(this@QuestionActivity)
                     .setTitle("Finish ?")
                     .setDescription("Do you really want to finish ?")
                     .setIcon(R.drawable.ic_mood_white_24dp)
                     .setNegativeText("No")
                     .onPositive { dialog, which -> dialog.dismiss() }
                     .setPositiveText("Yes")
                     .onPositive { dialog, which -> finishGame()
                         drawer_layout.closeDrawer(Gravity.LEFT)}
                     .show()
             }
             else{
                 finishGame()
             }
         }
        }

             return true

    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_camera -> {
                // Handle the camera action
            }
            R.id.nav_gallery -> {

            }
            R.id.nav_slideshow -> {

            }
            R.id.nav_manage -> {

            }
            R.id.nav_share -> {

            }
            R.id.nav_send -> {

            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CODE_GET_RESULT)
        {
            if (resultCode == Activity.RESULT_OK)
            {
                val action = data!!.getStringExtra("action") // Code late
                if (action == null || TextUtils.isEmpty(action))
                {
                    val questionIndex = data.getIntExtra(Common.KEY_BACK_FROM_RESULT,-1)
                    view_pager.currentItem = questionIndex

                    isAnswerModeView = true
                    countDownTimer!!.cancel()

                    txt_wrong_answer.visibility = View.GONE
                    txt_right_answer.visibility = View.GONE
                    txt_timer.visibility = View.GONE

                }
                else {
                    if (action.equals("doquizagain")) {

                        view_pager.currentItem = 0
                        isAnswerModeView = false


                        txt_wrong_answer.visibility = View.VISIBLE
                        txt_right_answer.visibility = View.VISIBLE
                        txt_timer.visibility = View.VISIBLE

                        for (i in Common.fragmentList.indices) {
                            Common.fragmentList[i].resetQuestion()

                        }

                        for (i in Common.answerSheetList.indices)
                            Common.answerSheetList[i].type = Common.ANSWER_TYPE.NO_ANSWER

                        adapter.notifyDataSetChanged()
                        questionHelperAdapter.notifyDataSetChanged()


                        countTimer()

                    } else if (action.equals("viewanswer")) {
                        view_pager.currentItem = 0
                        isAnswerModeView = true
                        countDownTimer!!.cancel()

                        txt_wrong_answer.visibility = View.GONE
                        txt_right_answer.visibility = View.GONE
                        txt_timer.visibility = View.GONE

                        for (i in Common.fragmentList.indices) {
                            Common.fragmentList[i].showCorrectAnswer()
                            Common.fragmentList[i].disableAnswer()
                        }
                    }
                }
            }
        }
    }
}
