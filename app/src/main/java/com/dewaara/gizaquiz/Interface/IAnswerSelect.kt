package com.dewaara.gizaquiz.Interface

/*
Giza Quiz developed by Er. Md. Halim from Dewaara.Pvt.Ltd. 01/09/2019
 */

import com.dewaara.gizaquiz.Model.CurrentQuestion

interface IAnswerSelect {
    fun selectedAnswer():CurrentQuestion
    fun showCorrectAnswer()
    fun disableAnswer()
    fun resetQuestion()
}