package com.kutumb.hackerearth.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentResolver
import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kutumb.hackerearth.model.SmsModel
import com.kutumb.hackerearth.util.CShowProgress
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class HomeViewModel @Inject constructor(): ViewModel() {

    private val _myMessage: MutableLiveData<List<SmsModel>> = MutableLiveData()
    val myMessage: LiveData<List<SmsModel>>
        get() = _myMessage

    @SuppressLint("Range")
    fun getAllSms(context : Activity) {
        viewModelScope.launch {
            try {
                coroutineScope {
                    val myMessage = async { getsms(context) }
                    val myMessagefromstorage = myMessage.await()
                    _myMessage.value = myMessagefromstorage
                }
            } catch (e: Exception) {
                Log.d("LogExecption", e.toString())
            }
        }
    }

    @SuppressLint("Range")
    private fun getsms(context: Activity): List<SmsModel> {
        val lstSms: MutableList<SmsModel> = ArrayList()
        var objSms = SmsModel()
        val message = Uri.parse("content://sms/inbox")
        try {
            val cr: ContentResolver = context.contentResolver
            val c = cr.query(message, null, null, null, null)
            context.startManagingCursor(c)
            val totalSMS = c!!.count
            if (c.moveToFirst()) {
                for (i in 0 until totalSMS) {
                    objSms = SmsModel()
                    objSms.id = c.getString(c.getColumnIndexOrThrow("_id"))
                    objSms.address = c.getString(c.getColumnIndexOrThrow("address"))
                    objSms.msg = c.getString(c.getColumnIndexOrThrow("body"))
                    objSms.readState = c.getString(c.getColumnIndex("read"))
                    objSms.time = c.getString(c.getColumnIndexOrThrow("date"))
                    if (c.getString(c.getColumnIndexOrThrow("type")).contains("1")) {
                        objSms.folderName = "inbox"
                    } else {
                        objSms.folderName = "sent"
                    }
                    lstSms.add(objSms)
                    c.moveToNext()
                }
            }

            c.close()
        }catch (e : Exception){
            e.printStackTrace()
        }
        return lstSms
    }
}