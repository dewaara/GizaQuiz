package com.dewaara.gizaquiz.Adapter

/*
Giza Quiz developed by Er. Md. Halim from Dewaara.Pvt.Ltd. 01/09/2019
 */

import android.content.Context
import android.content.Intent
import android.support.v4.content.LocalBroadcastManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.dewaara.gizaquiz.Common.Common
import com.dewaara.gizaquiz.Interface.IOnRecyclerViewItemClickListener
import com.dewaara.gizaquiz.Model.CurrentQuestion
import com.dewaara.gizaquiz.R

class QuestionListHelperAdapter(internal var context: Context,
                                internal var answerSheetList: List<CurrentQuestion>):RecyclerView.Adapter<QuestionListHelperAdapter.MyViewHelper>() {
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): MyViewHelper {
        val itemView = LayoutInflater.from(context).inflate(R.layout.layout_question_list_helper_item,p0,false)
        return MyViewHelper(itemView)
    }

    override fun getItemCount(): Int {
         return answerSheetList.size
    }

    override fun onBindViewHolder(myViewHolder: MyViewHelper, p1: Int) {
        myViewHolder.txt_question_num.text = (p1+1).toString()
        if (answerSheetList[p1].type  == Common.ANSWER_TYPE.RIGHT_ANSWER)
            myViewHolder.layout_wrapper.setBackgroundResource(R.drawable.grid_item_right_answer)
        else if (answerSheetList[p1].type  == Common.ANSWER_TYPE.WRONG_ANSWER)
            myViewHolder.layout_wrapper.setBackgroundResource(R.drawable.grid_item_wrong_answer)
        else
                myViewHolder.layout_wrapper.setBackgroundResource(R.drawable.grid_item_no_answer)

        //When user click to yhis item , we will navigation this question
        myViewHolder.setiOnRecyclerViewItemClickListener(object:IOnRecyclerViewItemClickListener{
            override fun onClick(view: View, position: Int) {
                LocalBroadcastManager.getInstance(context)
                    .sendBroadcast(Intent(Common.KEY_GO_TO_QUESTION).putExtra(Common.KEY_GO_TO_QUESTION,position))
            }

        })

    }

    inner class MyViewHelper(itemView: View):RecyclerView.ViewHolder(itemView),View.OnClickListener {
        override fun onClick(p0: View?) {
            iOnRecyclerViewItemClickListener.onClick(p0!!,adapterPosition)

        }

        internal var txt_question_num:TextView
        internal var layout_wrapper:LinearLayout

        lateinit var iOnRecyclerViewItemClickListener: IOnRecyclerViewItemClickListener

        fun setiOnRecyclerViewItemClickListener(iOnRecyclerViewItemClickListener:IOnRecyclerViewItemClickListener)
        {
            this.iOnRecyclerViewItemClickListener = iOnRecyclerViewItemClickListener
        }

        init {
            txt_question_num = itemView.findViewById(R.id.txt_question_num) as TextView
            layout_wrapper = itemView.findViewById(R.id.layout_wrapper) as LinearLayout

            itemView.setOnClickListener(this)
        }

    }
}