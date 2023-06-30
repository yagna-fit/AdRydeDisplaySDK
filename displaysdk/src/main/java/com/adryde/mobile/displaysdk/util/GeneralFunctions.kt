package com.adryde.mobile.displaysdk.util

import java.text.SimpleDateFormat
import java.util.*

class GeneralFunctions {

    companion object {
        fun getDisplayDate(date:String?):String {
            if (date == null) {
                return "-"
            }

            var spf = if (date.contains("-")) SimpleDateFormat("dd-MM-yyyy")
            else if (date.contains("/"))
                SimpleDateFormat("dd/MM/yyyy")
            else
                null

            if (spf==null){
                return  date
            }
            val newDate: Date = spf.parse(date) as Date
            spf = SimpleDateFormat("dd MMM yyyy")
            return spf.format(newDate)
        }

        fun nullProof(data: String?): String {
            return if (data == null)
                "-"
            else if(data.isEmpty() || data.lowercase() == "null")
                "-"
            else
                data

        }

        fun nullProof(data: Int?): String {
            return data?.toString() ?: "-"

        }
    }
}