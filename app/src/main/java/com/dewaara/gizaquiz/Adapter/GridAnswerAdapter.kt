package com.dewaara.gizaquiz.Adapter

/*
Giza Quiz developed by Er. Md. Halim from Dewaara.Pvt.Ltd. 01/09/2019
 */

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dewaara.gizaquiz.Common.Common
import com.dewaara.gizaquiz.Model.CurrentQuestion
import com.dewaara.gizaquiz.R

class GridAnswerAdapter(internal var context: Context,
                        internal var  answerSheetList: List<CurrentQuestion>):
    RecyclerView.Adapter<GridAnswerAdapter.MyViewHolder>() {


    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): MyViewHolder {
        val itemView = LayoutInflater.from(context).inflate(R.layout.layout_grid_answer_item,p0,false)
        return MyViewHolder(itemView)

    }

    override fun getItemCount(): Int {
        return answerSheetList.size

    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
         if (answerSheetList[position].type == Common.ANSWER_TYPE.RIGHT_ANSWER)
             holder.question_item.setBackgroundResource(R.drawable.grid_item_right_answer)
        else if (answerSheetList[position].type == Common.ANSWER_TYPE.WRONG_ANSWER)
             holder.question_item.setBackgroundResource(R.drawable.grid_item_wrong_answer)
        else
             holder.question_item.setBackgroundResource(R.drawable.grid_item_no_answer)
    }

    inner class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

        internal var question_item:View
        init {
            question_item = itemView.findViewById(R.id.question_item) as View

        }

    }

}