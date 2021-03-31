package com.jun.customview.customview

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import com.jun.customview.R
import com.jun.customview.Utils
import kotlin.math.*


class drawArc @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ViewGroup(context, attrs, defStyleAttr) {

    init {
        init(context, attrs)
    }

    companion object {
        private const val TAG = "CircleTimerView"

        //angle
        private const val ANGLE_START_PROGRESS_BACKGROUND = 0
        private const val ANGLE_END_PROGRESS_BACKGROUND = 360

        // stoke
        private const val DEFAULT_STROKE_WIDTH_DP = 5F
        private const val DEFAULT_STROKE_PG_WIDTH_DP = 10F
        private const val DEFAULT_DIVISION_LENGTH_DP = 8F
        private const val DEFAULT_DIVISION_OFFSET_DP = 12F
        private const val DEFAULT_LABEL_OFFSET_DP = 36F
        private const val DEFAULT_DIVISION_WIDTH_DP = 2F
        private const val SCALE_LABEL_TEXT_SIZE = 13F
        private const val DEFAULT_DIVISION_TEXT_SIZE = 12F

        //color
        private const val DEFAULT_PROGRESS_BACKGROUND_COLOR = "#202633"
        private const val DEFAULT_PROGRESS_COLOR = "#7381a4"
        private const val DEFAULT_DIVISION_COLOR = "#7381a4"
        private const val DEFAULT_DIVISION_TEXT_COLOR = "#ffffff"

        // ratio
        private const val BLUR_STROKE_RATIO = 3 / 8F
        private const val BLUR_RADIUS_RATIO = 1 / 4F
    }

    // The progress circle ring background
    private lateinit var progressBackgroundPaint: Paint
    private lateinit var progressPaint: Paint
    private lateinit var divisionPaint: Paint
    private lateinit var divisionTextPaint: Paint


    private var divisionOffset = 0
    private var divisionLength = 0
    private var divisionTextSize = 0

    private var divisionShortLength = 0


    private lateinit var circleBounds: RectF
    private var radius: Float = 0F
    private var center = Point(0, 0)
    private var divisionWidth = 0

    //    private val hourLabels = listOf(12, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11)
    private val hourLabels = listOf(12, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11)
    private val hourText = listOf("오전 12시", "오전 6시", "오후 12시", "오후 6시")

    private lateinit var sleepLayout: View
    private lateinit var wakeLayout: View
    private var sleepAngle = 30.0
    private var wakeAngle = 225.0

    private fun init(@NonNull context: Context, @Nullable attrs: AttributeSet?) {
        // 처리가 필요
        circleBounds = RectF(100.0f, 200.0f, 600.0f, 700.0f)

        divisionOffset = dp2px(DEFAULT_DIVISION_OFFSET_DP)
        divisionLength = dp2px(DEFAULT_DIVISION_LENGTH_DP) * 2
        divisionWidth = dp2px(DEFAULT_DIVISION_WIDTH_DP)
        divisionTextSize = sp2Px(DEFAULT_DIVISION_TEXT_SIZE)

        divisionShortLength = dp2px(DEFAULT_DIVISION_LENGTH_DP)

        var progressBgStrokeWidth = dp2px(DEFAULT_STROKE_WIDTH_DP)
        var progressBackgroundColor = Color.parseColor(DEFAULT_PROGRESS_BACKGROUND_COLOR)
        var divisionColor = Color.parseColor(DEFAULT_DIVISION_COLOR)
        var divisionTextColor = Color.parseColor(DEFAULT_DIVISION_TEXT_COLOR)
        var sleepLayoutId = 0
        var wakeLayoutId = 0


        var progressStrokeWidth = dp2px(DEFAULT_STROKE_PG_WIDTH_DP)
        var progressColor = Color.parseColor(DEFAULT_PROGRESS_COLOR)

        if (attrs != null) {
            val a = context.obtainStyledAttributes(attrs, R.styleable.customviewpractice)
            sleepLayoutId = a.getResourceId(R.styleable.customviewpractice_sleepLayoutId, 0)
            wakeLayoutId = a.getResourceId(R.styleable.customviewpractice_wakeLayoutId, 0)
        }

        progressBackgroundPaint = Paint()
        progressBackgroundPaint?.style = Paint.Style.STROKE
        progressBackgroundPaint?.strokeWidth = progressBgStrokeWidth.toFloat()
        progressBackgroundPaint?.color = progressBackgroundColor
        progressBackgroundPaint?.isAntiAlias = true

        progressPaint = Paint()
        progressPaint?.style = Paint.Style.STROKE
        progressPaint?.strokeWidth = progressStrokeWidth.toFloat()
        progressPaint?.color = progressColor
        progressPaint?.isAntiAlias = true

        divisionPaint = Paint(0)
        divisionPaint.strokeCap = Paint.Cap.BUTT
        divisionPaint.strokeWidth = divisionWidth.toFloat()
        divisionPaint.color = divisionColor
        divisionPaint.style = Paint.Style.STROKE
        divisionPaint.isAntiAlias = true

        divisionTextPaint = Paint(0)
        divisionTextPaint.textSize = divisionTextSize.toFloat()
        divisionTextPaint.color = divisionTextColor
        divisionTextPaint.isAntiAlias = true


        val inflater = LayoutInflater.from(context)
        sleepLayout = inflater.inflate(sleepLayoutId, this, false)
        wakeLayout = inflater.inflate(wakeLayoutId, this, false)
        addView(sleepLayout)
        addView(wakeLayout)

        // 하드웨어 가속 금지
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        setWillNotDraw(false)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        calculateBounds(w, h)
        requestLayout()
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        layoutView(sleepLayout, sleepAngle)
        layoutView(wakeLayout, wakeAngle)
    }

    private fun layoutView(view: View, angle: Double) {
        val measuredWidth = view.measuredWidth
        val measuredHeight = view.measuredHeight
        val halfWidth = measuredWidth / 2
        val halfHeight = measuredHeight / 2
        val parentCenterX = width / 2
        val parentCenterY = height / 2
        val centerX = (parentCenterX + radius * cos(Math.toRadians(angle))).toInt()
        val centerY = (parentCenterY - radius * sin(Math.toRadians(angle))).toInt()
        view.layout(
            (centerX - halfWidth),
            centerY - halfHeight,
            centerX + halfWidth,
            centerY + halfHeight
        )
    }

    private fun dp2px(dp: Float): Int {
        val metrics = resources.displayMetrics
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, metrics).toInt()
    }

    private fun sp2Px(sp: Float): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_SP,
            sp,
            resources.displayMetrics
        )
            .toInt()
    }

    private fun isTouchOnView(view: View, ev: MotionEvent): Boolean {
        return (ev.x > view.left && ev.x < view.right
                && ev.y > view.top && ev.y < view.bottom)
    }

    override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            if (isTouchOnView(sleepLayout, event)) {
//                draggingSleep = true
                return true
            }
            if (isTouchOnView(wakeLayout, event)) {
//                draggingWake = true
                return true
            }
        }
        return super.onInterceptTouchEvent(event)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x
        val y = event.y
        Log.d(TAG, event.actionMasked.toString())
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                Log.d(TAG, "action down ${event}")

                return true
            }
            MotionEvent.ACTION_MOVE -> {
                Log.d(TAG, "action move ${event}")

                Log.d(TAG, "action move")
            }

            MotionEvent.ACTION_UP -> {

                Log.d(TAG, "action up ${event}")
            }
        }
        invalidate()
        return super.onTouchEvent(event)
    }

    private fun calculateBounds(w: Int, h: Int) {
        val maxChildWidth = max(sleepLayout.measuredWidth, wakeLayout.measuredWidth)
        val maxChildHeight = max(sleepLayout.measuredHeight, wakeLayout.measuredHeight)
        val maxChildSize = max(maxChildWidth, maxChildHeight)
        val offset = abs(progressBackgroundPaint.strokeWidth / 2 - maxChildSize / 2)
        val width = w - paddingStart - paddingEnd - maxChildSize - offset
        val height = h - paddingTop - paddingBottom - maxChildSize - offset

        radius = min(width, height) / 2F
        center = Point(w / 2, h / 2)

        circleBounds.left = center.x - radius
        circleBounds.top = center.y - radius
        circleBounds.right = center.x + radius
        circleBounds.bottom = center.y + radius
    }


    private fun drawProgressBackground(canvas: Canvas) {
        canvas.drawArc(
            circleBounds, ANGLE_START_PROGRESS_BACKGROUND.toFloat(),
            ANGLE_END_PROGRESS_BACKGROUND.toFloat(),
            false, progressBackgroundPaint!!
        )
    }

    private fun drawProgress(canvas: Canvas) {
        val startAngle = -sleepAngle.toFloat()
        val sweep = Utils.to_0_360(sleepAngle - wakeAngle).toFloat()
        canvas.drawArc(
            circleBounds, startAngle.toFloat(),
            sweep.toFloat(),
            false, progressPaint!!
        )
    }

    private fun drawDivisions(canvas: Canvas) {
        val divisionAngle = 360 / 60
        for (index in 0..59) {
            val angle = (divisionAngle * index) - 90
            val radians = Math.toRadians(angle.toDouble())
            val bgStrokeWidth = progressBackgroundPaint.strokeWidth
            val startX = center.x + (radius - bgStrokeWidth / 2 - divisionOffset) * cos(radians)
            val endX =
                center.x + (radius - bgStrokeWidth / 2 - divisionOffset - divisionLength) * cos(
                    radians
                )
            val startY = center.y + (radius - bgStrokeWidth / 2 - divisionOffset) * sin(radians)
            val endY =
                center.y + (radius - bgStrokeWidth / 2 - divisionOffset - divisionLength) * sin(
                    radians
                )

            if ((index + 1) % 5 == 0) {
                    divisionLength = dp2px(DEFAULT_DIVISION_LENGTH_DP) * 2
                    canvas.drawLine(
                        startX.toFloat(),
                        startY.toFloat(),
                        endX.toFloat(),
                        endY.toFloat(),
                        divisionPaint
                    )
            } else {
                divisionLength = dp2px(DEFAULT_DIVISION_LENGTH_DP)
                canvas.drawLine(
                    startX.toFloat(),
                    startY.toFloat(),
                    endX.toFloat(),
                    endY.toFloat(),
                    divisionPaint
                )
            }
        }
    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val measuredWidth = View.MeasureSpec.getSize(widthMeasureSpec)
        val measuredHeight = View.MeasureSpec.getSize(heightMeasureSpec)
        measureChildren(widthMeasureSpec, heightMeasureSpec)

        val smallestSide = Math.min(measuredWidth, measuredHeight)
        setMeasuredDimension(smallestSide, smallestSide)
    }

    override fun onDraw(canvas: Canvas) {
        drawDivisions(canvas)
        drawProgressBackground(canvas)
        drawProgress(canvas)
        super.onDraw(canvas)
    }

}
