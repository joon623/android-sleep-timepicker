package com.jun.customview

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*
import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)



        timePicker.listener = { bedTime: LocalTime, wakeTime: LocalTime ->
            handleUpdate(bedTime, wakeTime)
        }
        handleUpdate(timePicker.getBedTime(), timePicker.getWakeTime())
    }

    private fun handleUpdate(bedTime: LocalTime, wakeTime: LocalTime) {
        val formatter = DateTimeFormatter.ofPattern("h:mm a", Locale.KOREA)
        tvBedTime.text = bedTime.format(formatter)
        tvWakeTime.text = wakeTime.format(formatter)

//        val bedDate = bedTime.atDate(LocalDate.now())
//        var wakeDate = wakeTime.atDate(LocalDate.now())
//        if (bedDate >= wakeDate) wakeDate = wakeDate.plusDays(1)
//        val duration = Duration.between(bedDate, wakeDate)
//        val hours = duration.toHours()
//        val minutes = duration.toMinutes() % 60
//        tvHours.text = hours.toString()
//        tvMins.text = minutes.toString()
//        if (minutes > 0) llMins.visibility = View.VISIBLE else llMins.visibility = View.GONE
    }
}