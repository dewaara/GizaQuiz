package com.dewaara.gizaquiz.Common

/*
Giza Quiz developed by Er. Md. Halim from Dewaara.Pvt.Ltd. 01/09/2019
 */

import com.dewaara.gizaquiz.Model.Category
import com.dewaara.gizaquiz.Model.CurrentQuestion
import com.dewaara.gizaquiz.Model.Question
import com.dewaara.gizaquiz.QuestionFragment

object Common {

    val TOTAL_TIME = 20*60*1000 //20 min
    var answerSheetListFiltered:MutableList<CurrentQuestion> = ArrayList()
    var answerSheetList:MutableList<CurrentQuestion> = ArrayList()
    var questionList:MutableList<Question> = ArrayList()
    var selectedCategory:Category?=null

    var fragmentList:MutableList<QuestionFragment> = ArrayList()

    var selected_values:MutableList<String> = ArrayList()

    var timer = 0
    var right_answer_count = 0
    var wrong_answer_count = 0
    var no_answer_count = 0
    var data_question=StringBuilder()


    val KEY_GO_TO_QUESTION: String? ="position_go_to"
    val KEY_BACK_FROM_RESULT:String? = "back_from_result"

    enum class ANSWER_TYPE{
        NO_ANSWER,
        RIGHT_ANSWER,
        WRONG_ANSWER
    }
}