package com.anvesh.nogozocustomerapplication.util

import android.text.TextUtils
import android.util.Patterns


class Helper {
    companion object{
        fun isValidEmail(target: String): Boolean {
            return if (TextUtils.isEmpty(target)) {
                false
            } else {
                Patterns.EMAIL_ADDRESS.matcher(target).matches()
            }
        }

        fun isPhoneNumber(string: String): Boolean{
            if(string.length != 10)
                return false
            return !Patterns.PHONE.matcher(string).matches();
        }
        fun getMonthString(month: String): String{
            when(month){
                "01" -> return "January"
                "02" -> return "February"
                "03" -> return "March"
                "04" -> return "April"
                "05" -> return "May"
                "06" -> return "June"
                "07" -> return "July"
                "08" -> return "August"
                "09" -> return "September"
                "10" -> return "October"
                "11" -> return "November"
                "12" -> return "December"
                else -> return ""
            }
        }
    }
}