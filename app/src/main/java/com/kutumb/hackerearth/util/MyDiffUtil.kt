package com.kutumb.hackerearth.util

import androidx.recyclerview.widget.DiffUtil
import com.kutumb.hackerearth.model.SmsModel

class MyDiffUtil(
    private var oldlist : ArrayList<SmsModel>,
    private var newlist : ArrayList<SmsModel>
) : DiffUtil.Callback() {

    override fun getOldListSize(): Int {
       return oldlist.size
    }

    override fun getNewListSize(): Int {
        return newlist.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldlist[oldItemPosition].id == newlist[newItemPosition].id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return  when {
            oldlist[oldItemPosition].id != newlist[newItemPosition].id ->{
                return false
            }
            oldlist[oldItemPosition].time!= newlist[newItemPosition].time ->{
                return false
            }
            else -> true
        }
    }

}