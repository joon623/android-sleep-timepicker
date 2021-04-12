package com.jun.customview

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.jakewharton.threetenabp.AndroidThreeTen
import kotlinx.android.synthetic.main.activity_main.*
import org.threeten.bp.format.DateTimeFormatter
import java.util.*

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
        var bedHour: Int = (bedTime/30 % 12).toInt()
        var bedMinutes: Int = ((bedTime % 30) * 2).toInt()
        var wakeHour: Int = (wakeTime/30 % 12).toInt()
        var wakeMinutes: Int = ((wakeTime % 30) * 2).toInt()
        if(bedHour == 0) {
            bedHour = 12
        }
        if(wakeHour == 0) {
            wakeHour = 12
        }
        tvBedTime.text = "${bedHour}:${bedMinutes}"
        tvWakeTime.text = "${wakeHour}:${wakeMinutes}"
        tvWakeMeridiem.text = timePicker.getWakeMeridiem()
        tvBedMeridiem.text = timePicker.getBedMeridiem()

        Log.d(TAG, "----------------before----------------")
        Log.d(TAG, "bedTime is ${bedTime.toInt().toString()}")
        Log.d(TAG, "wakeTime is ${wakeTime.toInt().toString()}")
        Log.d(TAG, "-----------------------------------")

        var bedTime : Int = ((bedHour * 60) + bedMinutes)
        var wakeTime : Int = ((wakeHour * 60)  + wakeMinutes)
        if(timePicker.getBedMeridiem() == "오후" && bedHour != 12 ) bedTime += 720
        if(timePicker.getBedMeridiem() == "오전" && bedHour == 12 ) bedTime -= 720
        if(timePicker.getWakeMeridiem() == "오후" && wakeHour != 12 ) wakeTime += 720
        if(timePicker.getWakeMeridiem() == "오전" && wakeHour == 12 ) wakeTime -= 720
        if(bedTime >= wakeTime) wakeTime += 1440

        Log.d(TAG, "----------------after----------------")
        Log.d(TAG, "bedTime is ${bedTime.toInt().toString()}")
        Log.d(TAG, "wakeTime is ${wakeTime.toInt().toString()}")
        Log.d(TAG, "-----------------------------------")

        val duration = wakeTime - bedTime
        val hours = (duration / 60) % 60
        val minutes = duration % 60

        Log.d(TAG, "-----------------------------------")
        Log.d(TAG, "duration is ${duration.toInt().toString()}")
        Log.d(TAG, "hours is ${hours.toInt().toString()}")
        Log.d(TAG, "minutes is ${minutes.toInt().toString()}")
        tvHours.text = "${hours.toString()}시간 "
        tvMins.text = "${minutes.toString()}분"
    }
}