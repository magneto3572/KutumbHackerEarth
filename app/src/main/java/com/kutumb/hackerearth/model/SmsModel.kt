package com.kutumb.hackerearth.model

class SmsModel {
    var id: String? = null
    var address: String? = null
    var msg: String? = null
    var readState //"0" for have not read sms and "1" for have read sms
            : String? = null
    var time: String? = null
    var folderName: String? = null
}