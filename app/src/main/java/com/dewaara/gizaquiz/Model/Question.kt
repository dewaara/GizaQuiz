package com.dewaara.gizaquiz.Model

/*
Giza Quiz developed by Er. Md. Halim from Dewaara.Pvt.Ltd. 01/09/2019
 */

class Question (
    var id:Int,
    var questionText:String?,
    var questionImage: String?,
    var answerA:String,
    var answerB:String,
    var answerC:String,
    var answerD:String,
    var correctAnswer:String?,
    var isImageQuestion:Boolean,
    var categoryId:Int

)