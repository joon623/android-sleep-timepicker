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

        timePicker.listener = { bedTime: org.threeten.bp.LocalTime, wakeTime: org.threeten.bp.LocalTime ->
            handleUpdate(bedTime, wakeTime)
        }
        handleUpdate(timePicker.getBedTime(), timePicker.getWakeTime())
    }

    private fun handleUpdate(bedTime: org.threeten.bp.LocalTime, wakeTime: org.threeten.bp.LocalTime) {
        val formatter = DateTimeFormatter.ofPattern("h:mm a", Locale.KOREA)
        tvBedTime.text = bedTime.format(formatter)
        tvWakeTime.text = wakeTime.format(formatter)
    }
}