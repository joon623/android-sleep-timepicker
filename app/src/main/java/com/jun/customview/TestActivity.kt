package com.jun.customview

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.jakewharton.threetenabp.AndroidThreeTen
import com.jun.customview.R
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime
import org.threeten.bp.LocalTime
import org.threeten.bp.format.DateTimeFormatter

class TestActivity : AppCompatActivity() {

    private val TAG = "TestActivity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)

        AndroidThreeTen.init(this)
        test()
    }

    private fun test() {

//        val timeNow = LocalDate.now()
//        val dateNow = LocalTime.now()
//        val dateTimeNow = LocalDateTime.now()
//
//        val dateOf = LocalDate.of(2019, 5, 3)
//        val timeOf = LocalTime.of(15, 30)
//        Log.d(TAG,localDate.toString())
//        Log.d(TAG, dateNow.toString())
//        Log.d(TAG, dateTimeNow.toString())
        // 타입 변환
        //
        val dateTime = LocalDateTime.now()
//        Log.d(TAG, dateTime.format(DateTimeFormatter.ofPattern(dateTime.toString())))
    }
}