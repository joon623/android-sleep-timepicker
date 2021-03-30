package com.jun.customview.customview

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import com.jun.customview.R
import kotlin.math.*

class drawArc @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) :
    View(context, attrs, defStyleAttr) {

    companion object {
        private const val ANGLE_START_PROGRESS_BACKGROUND = 0
        private const val ANGLE_END_PROGRESS_BACKGROUND = 360
        private const val DEFAULT_STROKE_WIDTH_DP = 8F
        private const val DEFAULT_DIVISION_LENGTH_DP = 8F
        private const val DEFAULT_DIVISION_OFFSET_DP = 12F
        private const val DEFAULT_LABEL_OFFSET_DP = 36F
        private const val DEFAULT_DIVISION_WIDTH_DP = 2F
        private const val SCALE_LABEL_TEXT_SIZE = 13F
        private const val DEFAULT_PROGRESS_BACKGROUND_COLOR = "#e0e0e0"
        private const val BLUR_STROKE_RATIO = 3 / 8F
        private const val BLUR_RADIUS_RATIO = 1 / 4F
    }

    init {
        init(context, attrs)
    }

    private fun init(@NonNull context: Context, @Nullable attrs: AttributeSet?) {
        // 처리가 필요
        circleBounds = RectF(100.0f, 200.0f, 300.0f, 400.0f)

        var progressBgStrokeWidth = dp2px(DEFAULT_STROKE_WIDTH_DP)

        var progressBackgroundColor = Color.parseColor(DEFAULT_PROGRESS_BACKGROUND_COLOR)
        var sleepLayoutId = 0
        var wakeLayoutId = 0


        if (attrs != null) {
            val a = context.obtainStyledAttributes(attrs, R.styleable.customviewpractice)
        }


        progressBackgroundPaint = Paint()
        progressBackgroundPaint?.style = Paint.Style.STROKE
        progressBackgroundPaint?.strokeWidth = progressBgStrokeWidth.toFloat()
        progressBackgroundPaint?.color = progressBackgroundColor
        progressBackgroundPaint?.isAntiAlias = true
    }


    // The progress circle ring background
    private lateinit var progressBackgroundPaint: Paint

    private lateinit var circleBounds: RectF
    private lateinit var sleepLayout: View
    private lateinit var wakeLayout: View
    private var radius: Float = 0F
    private var center = Point(0, 0)


    override fun onDraw(canvas: Canvas?) {

        canvas?.drawArc(
            circleBounds, ANGLE_START_PROGRESS_BACKGROUND.toFloat(),
            ANGLE_END_PROGRESS_BACKGROUND.toFloat(),
            false, progressBackgroundPaint!!
        )

        super.onDraw(canvas)
    }

    private fun dp2px(dp: Float): Int {
        val metrics = resources.displayMetrics
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, metrics).toInt()
    }

//    private fun calculateBounds(w: Int, h: Int) {
//        val maxChildWidth = max(sleepLayout.measuredWidth, wakeLayout.measuredWidth)
//        val maxChildHeight = max(sleepLayout.measuredHeight, wakeLayout.measuredHeight)
//        val maxChildSize = max(maxChildWidth, maxChildHeight)
//        val offset = abs(progressBackgroundPaint.strokeWidth / 2 - maxChildSize / 2)
//        val width = w - paddingStart - paddingEnd - maxChildSize - offset
//        val height = h - paddingTop - paddingBottom - maxChildSize - offset
//
//        radius = min(width, height) / 2F
//        center = Point(w / 2, h / 2)
//
//        circleBounds.left = center.x - radius
//        circleBounds.top = center.y - radius
//        circleBounds.right = center.x + radius
//        circleBounds.bottom = center.y + radius
//    }
//
//    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
//        calculateBounds(w, h)
//    }
}