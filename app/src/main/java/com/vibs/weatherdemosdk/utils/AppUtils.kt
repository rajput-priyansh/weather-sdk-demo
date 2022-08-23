package com.vibs.weatherdemosdk.utils


import android.util.Log
import java.lang.Exception
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

class AppUtils {
    companion object {

        @JvmStatic
        fun formatDate(dateTime: Long): String? {
            return try {
                val date = Date(dateTime * 1000)
                Log.d("TESTP", "formatDate() called with: dateTime = ${date.toString()}")
                SimpleDateFormat("EEEE dd/MM").format(date)
            } catch (e: Exception) {
                null
            }
        }

        @JvmStatic
        fun formatDay(dateTime: Long): String? {
            return try {
                val date = Date(dateTime * 1000)
                SimpleDateFormat("EEEE").format(date)
            } catch (e: Exception) {
                null
            }
        }

        @JvmStatic
        fun formatTime(dateTime: Long): String? {
            return try {
                val date = Date(dateTime * 1000)
                SimpleDateFormat("hh:mm:ss a").format(date)
            } catch (e: Exception) {
                null
            }
        }
    }
}