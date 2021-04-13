package com.jun.customview

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.jakewharton.threetenabp.AndroidThreeTen
import kotlinx.android.synthetic.main.activity_main.*
import org.threeten.bp.format.DateTimeFormatter
import java.util.*
import kotlin.math.abs
import kotlin.math.floor

class MainActivity : AppCompatActivity() {
    private val TAG = "main"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        AndroidThreeTen.init(this)
        handleUpdate(timePicker.getBedTime(), timePicker.getWakeTime())
        timePicker.listener =
            { bedTime: Double, wakeTime: Double ->
                handleUpdate(bedTime, wakeTime)
            }
    }

    private fun handleUpdate(
        bedTime: Double,
        wakeTime: Double
    ) {
        var bedHour: Int = (bedTime / 15 + 6).toInt()
        var bedMinutes = (60 - (bedTime % 15) * 4).toInt()
        var wakeHour: Int = (wakeTime / 15 + 6).toInt()
        var wakeMinutes = (60 - (wakeTime % 15) * 4).toInt()



        Log.d(TAG, "---------------------------")
        Log.d(TAG, "bedTime is ${bedTime}")
        Log.d(TAG, "wakeTime is ${wakeTime}")
        Log.d(TAG, "---------------------------")


        // 잠든 시간
        if (bedMinutes == 60) {
            bedMinutes = 0
        }

        //시간
        if (bedTime < 75.0) {
            bedHour = (6 - (bedTime / 15)).toInt()
        } else if (bedTime == 75.0) {
            bedHour = 1
        } else if (bedTime > 75.0 && bedTime < 255) {
            bedHour = floor(18 - (bedTime / 15)).toInt()
        } else if (bedTime == 255.0) {
            bedHour = 1
        } else if (bedTime > 255.0) {
            bedHour = floor(30 - (bedTime / 15)).toInt()
        }
        //////

        // 일어난 시간
        if (wakeMinutes == 60) {
            wakeMinutes = 0
        }
        // 시간
        if (wakeTime < 75.0) {
            wakeHour = (6 - (wakeTime / 15)).toInt()
        } else if (wakeTime == 75.0) {
            wakeHour = 1
        } else if (wakeTime > 75.0 && wakeTime < 255) {
            wakeHour = floor(18 - (wakeTime / 15)).toInt()
        } else if (wakeTime == 255.0) {
            wakeHour = 1
        } else if (wakeTime > 255.0) {
            wakeHour = floor(30 - (wakeTime / 15)).toInt()
        }


        if (wakeHour == 0) {
            wakeHour = 12
        }
        ////

        tvBedTime.text = "${bedHour}:${bedMinutes}"
        tvWakeTime.text = "${wakeHour}:${wakeMinutes}"
        tvWakeMeridiem.text = timePicker.getWakeMeridiem()
        tvBedMeridiem.text = timePicker.getBedMeridiem()

//        Log.d(TAG, "----------------before----------------")
//        Log.d(TAG, "bedTime is ${bedTime.toInt().toString()}")
//        Log.d(TAG, "wakeTime is ${wakeTime.toInt().toString()}")
//        Log.d(TAG, "-----------------------------------")

//        var bedTime : Int = ((bedHour * 60) + bedMinutes)
//        var wakeTime : Int = ((wakeHour * 60)  + wakeMinutes)
//        if(timePicker.getBedMeridiem() == "오후" && bedHour != 12 ) bedTime += 720
//        if(timePicker.getBedMeridiem() == "오전" && bedHour == 12 ) bedTime -= 720
//        if(timePicker.getWakeMeridiem() == "오후" && wakeHour != 12 ) wakeTime += 720
//        if(timePicker.getWakeMeridiem() == "오전" && wakeHour == 12 ) wakeTime -= 720
//        if(bedTime >= wakeTime) wakeTime += 1440
//
//        Log.d(TAG, "----------------after----------------")
//        Log.d(TAG, "bedTime is ${bedTime.toInt().toString()}")
//        Log.d(TAG, "wakeTime is ${wakeTime.toInt().toString()}")
//        Log.d(TAG, "-----------------------------------")
        var duration = wakeTime - bedTime
        if (duration > 0) {
            duration = 1440 - abs(duration)
        }
        val hours = ((duration / 60) % 60).toInt()
        val minutes = (duration % 60).toInt()
//        Log.d(TAG, "-----------------------------------")
//        Log.d(TAG, "duration is ${duration.toInt().toString()}")
//        Log.d(TAG, "hours is ${hours.toInt().toString()}")
//        Log.d(TAG, "minutes is ${minutes.toInt().toString()}")
        tvHours.text = "${hours.toString()}시간 "
        tvMins.text = "${minutes.toString()}분"
    }
}