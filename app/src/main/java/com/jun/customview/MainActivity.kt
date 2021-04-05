package com.jun.customview

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.jakewharton.threetenabp.AndroidThreeTen
import kotlinx.android.synthetic.main.activity_main.*
import org.threeten.bp.Duration
import org.threeten.bp.format.DateTimeFormatter
import java.util.*
import kotlin.time.minutes

class MainActivity : AppCompatActivity() {
    private val TAG = "main"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        AndroidThreeTen.init(this)
        handleUpdate(timePicker.getBedTime(), timePicker.getWakeTime())
        timePicker.listener =
            { bedTime: org.threeten.bp.LocalTime, wakeTime: org.threeten.bp.LocalTime ->
                handleUpdate(bedTime, wakeTime)
            }
    }

    private fun handleUpdate(
        bedTime: org.threeten.bp.LocalTime,
        wakeTime: org.threeten.bp.LocalTime
    ) {
        val formatter = DateTimeFormatter.ofPattern("h:mm", Locale.KOREA)
        tvBedTime.text = bedTime.format(formatter)
        tvWakeTime.text = wakeTime.format(formatter)
        tvWakeMeridiem.text = timePicker.getWakeMeridiem()
        tvBedMeridiem.text = timePicker.getBedMeridiem()

        Log.d(TAG, bedTime.toString())

        var bedTime = bedTime.minute + bedTime.hour * 60
        var wakeTime = wakeTime.minute + wakeTime.hour * 60
        if (timePicker.getBedMeridiem() == "오후" || bedTime == 0 ) bedTime += 720
        if (timePicker.getBedMeridiem() == "오후" && bedTime == 720) bedTime = 0
        if (timePicker.getWakeMeridiem() == "오후" || wakeTime == 0) wakeTime += 720
        if (timePicker.getWakeMeridiem() == "오후" && wakeTime == 720) wakeTime = 0
        if( bedTime >= wakeTime) wakeTime += 1440
        val duration = wakeTime - bedTime
        val hours = (duration / 60) % 60
        val minutes = duration % 60

        Log.d(TAG,"bedTime is  ${bedTime.toString()}")

        tvHours.text = "${hours.toString()}시간 "
        tvMins.text = "${minutes.toString()}분"
    }
}