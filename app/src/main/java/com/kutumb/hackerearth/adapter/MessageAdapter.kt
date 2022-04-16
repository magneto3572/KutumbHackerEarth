package com.kutumb.hackerearth.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.kutumb.hackerearth.R
import com.kutumb.hackerearth.databinding.RecyclerMessageIitemBinding
import com.kutumb.hackerearth.model.SmsModel
import com.kutumb.hackerearth.util.MyDiffUtil
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class MessageAdapter(
    private var context : Context,
    private var list: List<SmsModel>
) : RecyclerView.Adapter<MessageAdapter.ViewHolder>() {

    override fun getItemCount(): Int {
        return list.size
    }

    inner class ViewHolder(val binding: RecyclerMessageIitemBinding): RecyclerView.ViewHolder(binding.root)

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: MessageAdapter.ViewHolder, position: Int) {
        val messagelist = list[position]
        holder.binding.apply {
            messaeg.text = messagelist.msg
            val OTP = messagelist.msg
            if (OTP != null) {
                if(OTP.contains("OTP")){
                    otp.visibility = View.VISIBLE
                }else{
                    otp.visibility = View.GONE
                }
            }
            sender.text = messagelist.address
            val netdate = getShortDate(messagelist.time?.toLong())
            time.text = netdate.toString()
        }

        holder.itemView.setOnClickListener{ view ->
            val bundle = Bundle()
            bundle.putString("sender", messagelist.address)
            bundle.putString("message", messagelist.msg)
            Navigation.findNavController(holder.itemView).navigate(R.id.action_homeFragment_to_messageDetail, bundle)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int):MessageAdapter.ViewHolder {
        val v =  RecyclerMessageIitemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(v)
    }

    fun setData(newItemlist : ArrayList<SmsModel>){
        val diffUtil = MyDiffUtil(list as ArrayList<SmsModel> , newItemlist)
        val diffResult = DiffUtil.calculateDiff(diffUtil)
        list = newItemlist
        diffResult.dispatchUpdatesTo(this)
    }

    fun getShortDate(ts:Long?):String{
        if(ts == null) return ""
        val calendar = Calendar.getInstance(Locale.getDefault())
        calendar.timeInMillis = ts
        return android.text.format.DateFormat.format("E, dd MMM yyyy", calendar).toString()
    }
}