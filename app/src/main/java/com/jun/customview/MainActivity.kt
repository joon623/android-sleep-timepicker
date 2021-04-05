package com.jun.customview

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import org.threeten.bp.format.DateTimeFormatter
import java.util.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        handleUpdate(timePicker.getBedTime(), timePicker.getWakeTime())
        timePicker.listener = { bedTime: org.threeten.bp.LocalTime, wakeTime: org.threeten.bp.LocalTime ->
            handleUpdate(bedTime, wakeTime)
        }

    }

    private fun handleUpdate(bedTime: org.threeten.bp.LocalTime, wakeTime: org.threeten.bp.LocalTime) {
        val formatter = DateTimeFormatter.ofPattern("h:mm", Locale.KOREA)
        tvBedTime.text = bedTime.format(formatter)
        tvWakeTime.text = wakeTime.format(formatter)
        tvWakeMeridiem.text = timePicker.getWakeMeridiem()
        tvBedMeridiem.text = timePicker.getBedMeridiem()
    }
}