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
import com.jun.customview.SleepTimerUtils
import com.jun.customview.SleepTimerUtils.Companion.angleBetweenVectors
import com.jun.customview.SleepTimerUtils.Companion.angleToMins
import com.jun.customview.SleepTimerUtils.Companion.snapMinutes
import com.jun.customview.SleepTimerUtils.Companion.to_0_360
import kotlin.math.*


class SleepTimePicker @JvmOverloads constructor(
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
        private const val DEFAULT_STROKE_WIDTH_DP = 10F
        private const val DEFAULT_STROKE_PG_WIDTH_DP = 18F
        private const val DEFAULT_DIVISION_LENGTH_DP = 8F
        private const val DEFAULT_DIVISION_OFFSET_DP = 12F
        private const val DEFAULT_DIVISION_WIDTH_DP = 2F
        private const val DEFAULT_DIVISION_TEXT_SIZE = 12F

        //color
        private const val DEFAULT_PROGRESS_BACKGROUND_COLOR = "#202633"
        private const val DEFAULT_PROGRESS_COLOR = "#7381a4"
        private const val DEFAULT_DIVISION_COLOR = "#7381a4"
        private const val DEFAULT_DIVISION_TEXT_COLOR = "#ffffff"
    }

    // The progress circle ring background
    private lateinit var progressBackgroundPaint: Paint
    private lateinit var progressPaint: Paint
    private lateinit var progressMiddlePaint: Paint
    private lateinit var divisionPaint: Paint
    private lateinit var divisionTextPaint: Paint
    private lateinit var divisionSmallTextPaint: Paint

    private var divisionOffset = 0
    private var divisionLength = 0
    private var divisionTextSize = 0

    private var divisionShortLength = 0

    private lateinit var circleBounds: RectF
    private var radius: Float = 0F
    private var center = Point(0, 0)
    private var divisionWidth = 0

    private var labelColor = Color.WHITE

    private lateinit var sleepLayout: View
    private lateinit var wakeLayout: View
    private var sleepAngle = 60.0
    private var wakeAngle = 30.0
    private var touchAngle = 0.0
    private var draggingSleep = false
    private var draggingWake = false
    private var draggingProgress = false

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
            labelColor = a.getColor(R.styleable.customviewpractice_labelColor, progressColor)
        }

        progressBackgroundPaint = Paint()
        progressBackgroundPaint.style = Paint.Style.STROKE
        progressBackgroundPaint.strokeWidth = progressBgStrokeWidth.toFloat()
        progressBackgroundPaint.color = progressBackgroundColor
        progressBackgroundPaint.isAntiAlias = true

        progressPaint = Paint()
        progressPaint.style = Paint.Style.STROKE
        progressPaint.strokeWidth = progressStrokeWidth.toFloat()
        progressPaint.color = progressColor
        progressPaint.isAntiAlias = true

        progressMiddlePaint = Paint()
        progressMiddlePaint.style = Paint.Style.STROKE
        progressMiddlePaint.strokeWidth = progressStrokeWidth.toFloat()
        progressMiddlePaint.color = progressColor
        progressMiddlePaint.isAntiAlias = true

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

        divisionSmallTextPaint = Paint()
        divisionSmallTextPaint.isAntiAlias = true
        divisionSmallTextPaint.textSize = sp2Px(20F).toFloat()
        divisionSmallTextPaint.color = divisionColor

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
            centerX - halfWidth,
            centerY - halfHeight,
            centerX + halfWidth,
            centerY + halfHeight
        )
    }

    private fun findProgress(ev: MotionEvent): Boolean {
        val parentCenterX = width / 2
        val parentCenterY = height / 2
        val x = ev.x
        val y = ev.y

        // touchAngle 계산
        val touchAngleRad = atan2(center.y - y, x - center.x).toDouble()
        val touchAngleDegree = Math.toDegrees(touchAngleRad)
        touchAngle = to_0_360(touchAngleDegree)

        // touchAngle, sleepAngle, wakeAngle 비교
        if (sleepAngle > wakeAngle) {
            if (wakeAngle < touchAngle && sleepAngle < touchAngle) return false
        }

        if (wakeAngle > sleepAngle) {
            if (touchAngle > sleepAngle && touchAngle < wakeAngle) return false
        }

        // event좌표 영역 확인
        val divisionPosition = Math.pow(
            (radius - DEFAULT_STROKE_PG_WIDTH_DP).toDouble(),
            2.0
        ) > (Math.pow((parentCenterX - x).toDouble(), 2.0) + Math.pow(
            (parentCenterY - y).toDouble(), 2.0
        ))
        val outOfCirclePosition = Math.pow(
            (radius + DEFAULT_STROKE_PG_WIDTH_DP).toDouble(),
            2.0
        ) < (Math.pow((parentCenterX - x).toDouble(), 2.0) + Math.pow(
            (parentCenterY - y).toDouble(), 2.0
        ))
        if (divisionPosition || outOfCirclePosition) return false
        return true
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
        ).toInt()
    }

    private fun isTouchOnView(view: View, ev: MotionEvent): Boolean {
        return (ev.x > view.left && ev.x < view.right
                && ev.y > view.top && ev.y < view.bottom)
    }

    override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            if (isTouchOnView(sleepLayout, event)) {
                draggingSleep = true
                return true
            }
            if (isTouchOnView(wakeLayout, event)) {
                draggingWake = true
                return true
            }
            if (findProgress(event)) {
                draggingProgress = true
                return true
            }
        }
        return super.onInterceptTouchEvent(event)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x
        val y = event.y
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                return true
            }

            MotionEvent.ACTION_MOVE -> {
                val touchAngleRad = atan2(center.y - y, x - center.x).toDouble()
                val sleepDiff = sleepAngle - touchAngle
                val wakeDiff = wakeAngle - touchAngle
                if (draggingSleep) {
                    val sleepAngleRad = Math.toRadians(sleepAngle)
                    val diff = Math.toDegrees(angleBetweenVectors(sleepAngleRad, touchAngleRad))
                    sleepAngle = floor(to_0_360(sleepAngle + diff))
                    Log.d(TAG, "-----------------------------")
                    Log.d(TAG, "sleepAngle is ${sleepAngle}")
//                    Log.d(TAG, "diff is ${to_0_360(floor(sleepAngle))}")
                    Log.d(TAG, "-----------------------------")
                    requestLayout()
                    notifyChanges()
                    return true
                } else if (draggingWake) {
                    val wakeAngleRad = Math.toRadians(wakeAngle)
                    val diff = Math.toDegrees(angleBetweenVectors(wakeAngleRad, touchAngleRad))
                    wakeAngle = to_0_360(wakeAngle + diff)
                    requestLayout()
                    notifyChanges()
                    return true
                } else if (draggingProgress) {
                    touchAngle = to_0_360(Math.toDegrees(touchAngleRad))
                    wakeAngle = to_0_360(touchAngle + wakeDiff)
                    sleepAngle = to_0_360(touchAngle + sleepDiff)
                    requestLayout()
                    notifyChanges()
                    return true
                }
            }

            MotionEvent.ACTION_UP -> {
                draggingSleep = false
                draggingWake = false
                draggingProgress = false
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

    // 나중에 위로 올리기
    private var startAngle: Float = 0.0f
    private var sweep: Float = 0.0f

    private fun drawProgress(canvas: Canvas) {
        startAngle = -sleepAngle.toFloat()
        sweep = SleepTimerUtils.to_0_360(sleepAngle - wakeAngle).toFloat()
        canvas.drawArc(
            circleBounds, startAngle,
            sweep,
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

            when (index) {
                0 -> {
                    canvas.drawText(
                        "오전 12시",
                        (startX - 50).toFloat(),
                        (startY + 60).toFloat(),
                        divisionTextPaint
                    )
                }
                15 -> {
                    canvas.drawText(
                        "오전 6시",
                        (endX - 90).toFloat(),
                        (endY + 15).toFloat(),
                        divisionTextPaint
                    )
                }
                30 -> {
                    canvas.drawText(
                        "오후 12시",
                        (startX - 50).toFloat(),
                        (startY - 50).toFloat(),
                        divisionTextPaint
                    )
                }
                45 -> {
                    canvas.drawText(
                        "오후 6시",
                        (startX + 40).toFloat(),
                        (endY + 15).toFloat(),
                        divisionTextPaint
                    )
                }


                5 -> {
                    canvas.drawText(
                        "2",
                        (endX - 20).toFloat(),
                        (endY + 40).toFloat(),
                        divisionSmallTextPaint
                    )
                    canvas.drawText(
                        "10",
                        (endX - 250).toFloat(),
                        (endY + 40).toFloat(),
                        divisionSmallTextPaint
                    )
                }

                10 -> {
                    canvas.drawText(
                        "4",
                        (endX - 30).toFloat(),
                        (endY + 30).toFloat(),
                        divisionSmallTextPaint
                    )
                    canvas.drawText(
                        "8",
                        (endX - 400).toFloat(),
                        (endY + 30).toFloat(),
                        divisionSmallTextPaint
                    )
                }

                20 -> {
                    canvas.drawText(
                        "8",
                        (endX - 30).toFloat(),
                        (endY).toFloat(),
                        divisionSmallTextPaint
                    )
                    canvas.drawText(
                        "4",
                        (endX - 400).toFloat(),
                        (endY).toFloat(),
                        divisionSmallTextPaint
                    )
                }

                25 -> {
                    canvas.drawText(
                        "10",
                        (endX - 20).toFloat(),
                        (endY - 15).toFloat(),
                        divisionSmallTextPaint
                    )
                    canvas.drawText(
                        "2",
                        (endX - 250).toFloat(),
                        (endY - 15).toFloat(),
                        divisionSmallTextPaint
                    )
                }
            }
        }
    }

    fun getBedTime() = computeBedTime()

    fun getWakeTime() = computeWakeTime()

    fun getWakeMeridiem() = checkWakeMeridiem()

    fun getBedMeridiem() = checkBedMeridiem()

    private fun computeBedTime(): Double {
        return snapMinutes(angleToMins(sleepAngle))
    }

    private fun computeWakeTime(): Double {
        return snapMinutes(angleToMins(wakeAngle))
    }

    private fun checkWakeMeridiem(): String {
        val wakeMinsMeridiem = snapMinutes(angleToMins(wakeAngle)).toInt()
        return if (wakeMinsMeridiem in 0..359) "오전" else "오후"
    }

    private fun checkBedMeridiem(): String {
        val bedMinsMeridiem = snapMinutes(angleToMins(sleepAngle)).toInt()
        return if (bedMinsMeridiem in 0..359) "오전" else "오후"
    }

    // 나중에 위로 올리기
    var listener: ((bedTime: Double, wakeTime: Double) -> Unit)? = null

    private fun notifyChanges() {
        val computeBedTime = computeBedTime()
        val computeWakeTime = computeWakeTime()
        listener?.invoke(computeBedTime, computeWakeTime)
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
