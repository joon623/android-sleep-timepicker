package com.jun.customview.customview

import android.content.Context
import android.graphics.*
import android.os.Bundle
import android.os.Parcelable
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View


class CircleAlarmTimerView @JvmOverloads constructor(
    context: Context?,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) :
    View(context, attrs, defStyleAttr) {
    // Paint
    private var mCirclePaint: Paint? = null
    private var mHighlightLinePaint: Paint? = null
    private var mLinePaint: Paint? = null
    private var mCircleButtonPaint: Paint? = null
    private var mNumberPaint: Paint? = null
    private var mTimerNumberPaint: Paint? = null
    private var mTimerTextPaint: Paint? = null
    private var mTimerColonPaint: Paint? = null

    // Dimension
    private var mGapBetweenCircleAndLine = 0f
    private var mNumberSize = 0f
    private var mLineWidth = 0f
    private var mCircleButtonRadius = 0f
    private var mCircleStrokeWidth = 0f
    private var mTimerNumberSize = 0f
    private var mTimerTextSize = 0f

    // Color
    private var mCircleColor = 0
    private var mCircleButtonColor = 0
    private var mLineColor = 0
    private var mHighlightLineColor = 0
    private var mNumberColor = 0
    private var mTimerNumberColor = 0
    private var mTimerTextColor = 0

    // Parameters
    private var mCx = 0f
    private var mCy = 0f
    private var mRadius = 0f
    private var mCurrentRadian = 0f
    private var mCurrentRadian1 = 0f
    private var mPreRadian = 0f
    private var mInCircleButton = false
    private var mInCircleButton1 = false
    private var ismInCircleButton = false
    private var mCurrentTime // seconds
            = 0
    private var mListener: OnTimeChangedListener? = null
    private fun initialize() {
        Log.d(TAG, "initialize")
        // Set default dimension or read xml attributes
        mGapBetweenCircleAndLine = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, DEFAULT_GAP_BETWEEN_CIRCLE_AND_LINE,
            context.resources.displayMetrics
        )
        mNumberSize = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, DEFAULT_NUMBER_SIZE, context.resources
                .displayMetrics
        )
        mLineWidth = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, DEFAULT_LINE_WIDTH, context.resources
                .displayMetrics
        )
        mCircleButtonRadius = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, DEFAULT_CIRCLE_BUTTON_RADIUS, context
                .resources.displayMetrics
        )
        mCircleStrokeWidth = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, DEFAULT_CIRCLE_STROKE_WIDTH, context
                .resources.displayMetrics
        )
        mTimerNumberSize = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, DEFAULT_TIMER_NUMBER_SIZE, context
                .resources.displayMetrics
        )
        mTimerTextSize = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, DEFAULT_TIMER_TEXT_SIZE, context
                .resources.displayMetrics
        )

        // Set default color or read xml attributes
        mCircleColor = DEFAULT_CIRCLE_COLOR
        mCircleButtonColor = DEFAULT_CIRCLE_BUTTON_COLOR
        mLineColor = DEFAULT_LINE_COLOR
        mHighlightLineColor = DEFAULT_HIGHLIGHT_LINE_COLOR
        mNumberColor = DEFAULT_NUMBER_COLOR
        mTimerNumberColor = DEFAULT_TIMER_NUMBER_COLOR
        mTimerTextColor = DEFAULT_TIMER_TEXT_COLOR

        // Init all paints
        mCirclePaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mCircleButtonPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mHighlightLinePaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mLinePaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mNumberPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mTimerNumberPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mTimerTextPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mTimerColonPaint = Paint(Paint.ANTI_ALIAS_FLAG)

        // CirclePaint
        mCirclePaint!!.color = mCircleColor
        mCirclePaint!!.style = Paint.Style.STROKE
        mCirclePaint!!.strokeWidth = mCircleStrokeWidth

        // CircleButtonPaint
        mCircleButtonPaint!!.color = mCircleButtonColor
        mCircleButtonPaint!!.isAntiAlias = true
        mCircleButtonPaint!!.style = Paint.Style.FILL

        // LinePaint
        mLinePaint!!.color = Color.parseColor("#7381a4")
        //
        mLinePaint!!.strokeWidth = 50f
        mLinePaint!!.style = Paint.Style.STROKE

        // HighlightLinePaint
        mHighlightLinePaint!!.color = mHighlightLineColor
        mHighlightLinePaint!!.strokeWidth = mLineWidth

        // NumberPaint
        mNumberPaint!!.color = mNumberColor
        mNumberPaint!!.textSize = mNumberSize
        mNumberPaint!!.textAlign = Paint.Align.CENTER
        mNumberPaint!!.style = Paint.Style.STROKE
        mNumberPaint!!.strokeWidth = mCircleButtonRadius * 2 + 8

        // TimerNumberPaint
        mTimerNumberPaint!!.color = mTimerNumberColor
        mTimerNumberPaint!!.textSize = mTimerNumberSize
        mTimerNumberPaint!!.textAlign = Paint.Align.CENTER

        // TimerTextPaint
        mTimerTextPaint!!.color = mTimerTextColor
        mTimerTextPaint!!.textSize = mTimerTextSize
        mTimerTextPaint!!.textAlign = Paint.Align.CENTER

        // TimerColonPaint
        mTimerColonPaint!!.color = DEFAULT_TIMER_COLON_COLOR
        mTimerColonPaint!!.textAlign = Paint.Align.CENTER
        mTimerColonPaint!!.textSize = mTimerNumberSize

        // Solve the target version related to shadow
        // setLayerType(View.LAYER_TYPE_SOFTWARE, null); // use this, when targetSdkVersion is greater than or equal to api 14
    }

    override fun onDraw(canvas: Canvas) {
        canvas.save()
        canvas.drawCircle(
            mCx, mCy, mRadius - mCircleStrokeWidth / 2 - mGapBetweenCircleAndLine,
            mNumberPaint!!
        )
        canvas.save()
        canvas.rotate(-90f, mCx, mCy)
        val rect = RectF(
            mCx - (mRadius - mCircleStrokeWidth / 2 - mGapBetweenCircleAndLine),
            mCy - (mRadius - mCircleStrokeWidth / 2 - mGapBetweenCircleAndLine),
            mCx + (mRadius - mCircleStrokeWidth / 2 - mGapBetweenCircleAndLine),
            mCy + (mRadius - mCircleStrokeWidth / 2 - mGapBetweenCircleAndLine)
        )
        if (mCurrentRadian1 > mCurrentRadian) {
            canvas.drawArc(
                rect,
                Math.toDegrees(mCurrentRadian1.toDouble()).toFloat(),
                Math.toDegrees((2 * Math.PI.toFloat()).toDouble())
                    .toFloat() - Math.toDegrees(mCurrentRadian1.toDouble())
                    .toFloat() + Math.toDegrees(mCurrentRadian.toDouble())
                    .toFloat(),
                false,
                mLinePaint!!
            )
        } else {
            canvas.drawArc(
                rect,
                Math.toDegrees(mCurrentRadian1.toDouble()).toFloat(),
                Math.toDegrees(mCurrentRadian.toDouble())
                    .toFloat() - Math.toDegrees(mCurrentRadian1.toDouble()).toFloat(),
                false,
                mLinePaint!!
            )
        }
        canvas.restore()
        canvas.save()
        canvas.rotate(Math.toDegrees(mCurrentRadian.toDouble()).toFloat(), mCx, mCy)
        canvas.drawCircle(
            mCx,
            measuredHeight / 2 - mRadius + mCircleStrokeWidth / 2 + mGapBetweenCircleAndLine,
            0.01f,
            mLinePaint!!
        )
        canvas.restore()
        // TimerNumber
        canvas.save()
        canvas.rotate(Math.toDegrees(mCurrentRadian1.toDouble()).toFloat(), mCx, mCy)
        canvas.drawCircle(
            mCx,
            measuredHeight / 2 - mRadius + mCircleStrokeWidth / 2 + mGapBetweenCircleAndLine,
            0.01f,
            mLinePaint!!
        )
        canvas.restore()
        // TimerNumber
        canvas.save()
        if (ismInCircleButton) {
            canvas.rotate(Math.toDegrees(mCurrentRadian.toDouble()).toFloat(), mCx, mCy)
            canvas.drawCircle(
                mCx,
                measuredHeight / 2 - mRadius + mCircleStrokeWidth / 2 + mGapBetweenCircleAndLine,
                mCircleButtonRadius,
                mCircleButtonPaint!!
            )
            canvas.restore()
            // TimerNumber
            canvas.save()
            canvas.rotate(Math.toDegrees(mCurrentRadian1.toDouble()).toFloat(), mCx, mCy)
            canvas.drawCircle(
                mCx,
                measuredHeight / 2 - mRadius + mCircleStrokeWidth / 2 + mGapBetweenCircleAndLine,
                mCircleButtonRadius,
                mTimerColonPaint!!
            )
            canvas.restore()
            // TimerNumber
            canvas.save()
        } else {
            canvas.rotate(Math.toDegrees(mCurrentRadian1.toDouble()).toFloat(), mCx, mCy)
            canvas.drawCircle(
                mCx,
                measuredHeight / 2 - mRadius + mCircleStrokeWidth / 2 + mGapBetweenCircleAndLine,
                mCircleButtonRadius,
                mTimerColonPaint!!
            )
            canvas.restore()
            // TimerNumber
            canvas.save()
            canvas.rotate(Math.toDegrees(mCurrentRadian.toDouble()).toFloat(), mCx, mCy)
            canvas.drawCircle(
                mCx,
                measuredHeight / 2 - mRadius + mCircleStrokeWidth / 2 + mGapBetweenCircleAndLine,
                mCircleButtonRadius,
                mCircleButtonPaint!!
            )
            canvas.restore()
            // TimerNumber
            canvas.save()
        }
        val i = mCurrentTime / 150
        canvas.drawText(
            (if (i < 10) "0$i" else i).toString() + " " + if ((mCurrentTime - 150 * i) * 10 / 25 < 10) "0" + (mCurrentTime - 150 * i) * 10 / 25 else (mCurrentTime - 150 * i) * 10 / 25,
            mCx,
            mCy + getFontHeight(mTimerNumberPaint) / 2,
            mTimerNumberPaint!!
        )
        canvas.drawText(":", mCx, mCy + getFontHeight(mTimerNumberPaint) / 2, mTimerColonPaint!!)
        if (null != mListener) {
            if (ismInCircleButton) {
                mListener!!.start((if (i < 10) "0$i" else i).toString() + ":" + if ((mCurrentTime - 150 * i) * 10 / 25 < 10) "0" + (mCurrentTime - 150 * i) * 10 / 25 else (mCurrentTime - 150 * i) * 10 / 25)
            } else {
                mListener!!.end((if (i < 10) "0$i" else i).toString() + ":" + if ((mCurrentTime - 150 * i) * 10 / 25 < 10) "0" + (mCurrentTime - 150 * i) * 10 / 25 else (mCurrentTime - 150 * i) * 10 / 25)
            }
        }
        canvas.restore()
        // Timer Text
        canvas.save()
        canvas.restore()
        super.onDraw(canvas)
    }

    private fun getFontHeight(paint: Paint?): Float {
        // FontMetrics sF = paint.getFontMetrics();
        // return sF.descent - sF.ascent;
        val rect = Rect()
        paint!!.getTextBounds("1", 0, 1, rect)
        return rect.height().toFloat()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action and event.actionMasked) {
            MotionEvent.ACTION_DOWN ->                 // If the point in the circle button
                if (mInCircleButton(event.x, event.y) && isEnabled) {
                    mInCircleButton = true
                    ismInCircleButton = false
                    mPreRadian = getRadian(event.x, event.y)
                    Log.d(TAG, "In circle button")
                } else if (mInCircleButton1(event.x, event.y) && isEnabled) {
                    mInCircleButton1 = true
                    ismInCircleButton = true
                    mPreRadian = getRadian(event.x, event.y)
                }
            MotionEvent.ACTION_MOVE -> if (mInCircleButton && isEnabled) {
                val temp = getRadian(event.x, event.y)
                if (mPreRadian > Math.toRadians(270.0) && temp < Math.toRadians(90.0)) {
                    mPreRadian -= (2 * Math.PI).toFloat()
                } else if (mPreRadian < Math.toRadians(90.0) && temp > Math.toRadians(270.0)) {
                    mPreRadian = (temp + (temp - 2 * Math.PI) - mPreRadian).toFloat()
                }
                mCurrentRadian += temp - mPreRadian
                mPreRadian = temp
                if (mCurrentRadian > 2 * Math.PI) {
                    mCurrentRadian -= (2 * Math.PI).toFloat()
                }
                if (mCurrentRadian < 0) {
                    mCurrentRadian += (2 * Math.PI).toFloat()
                }
                mCurrentTime = (60 / (2 * Math.PI) * mCurrentRadian * 60).toInt()
                invalidate()
            } else if (mInCircleButton1 && isEnabled) {
                val temp = getRadian(event.x, event.y)
                if (mPreRadian > Math.toRadians(270.0) && temp < Math.toRadians(90.0)) {
                    mPreRadian -= (2 * Math.PI).toFloat()
                } else if (mPreRadian < Math.toRadians(90.0) && temp > Math.toRadians(270.0)) {
                    mPreRadian = (temp + (temp - 2 * Math.PI) - mPreRadian).toFloat()
                }
                mCurrentRadian1 += temp - mPreRadian
                mPreRadian = temp
                if (mCurrentRadian1 > 2 * Math.PI) {
                    mCurrentRadian1 -= (2 * Math.PI).toFloat()
                }
                if (mCurrentRadian1 < 0) {
                    mCurrentRadian1 += (2 * Math.PI).toFloat()
                }
                mCurrentTime = (60 / (2 * Math.PI) * mCurrentRadian1 * 60).toInt()
                invalidate()
            }
            MotionEvent.ACTION_UP -> if (mInCircleButton && isEnabled) {
                mInCircleButton = false
            } else if (mInCircleButton1 && isEnabled) {
                mInCircleButton1 = false
            }
        }
        return true
    }

    // Whether the down event inside circle button
    private fun mInCircleButton1(x: Float, y: Float): Boolean {
        val r = mRadius - mCircleStrokeWidth / 2 - mGapBetweenCircleAndLine
        val x2 = (mCx + r * Math.sin(mCurrentRadian1.toDouble())).toFloat()
        val y2 = (mCy - r * Math.cos(mCurrentRadian1.toDouble())).toFloat()
        return if (Math.sqrt(((x - x2) * (x - x2) + (y - y2) * (y - y2)).toDouble()) < mCircleButtonRadius) {
            true
        } else false
    }

    // Whether the down event inside circle button
    private fun mInCircleButton(x: Float, y: Float): Boolean {
        val r = mRadius - mCircleStrokeWidth / 2 - mGapBetweenCircleAndLine
        val x2 = (mCx + r * Math.sin(mCurrentRadian.toDouble())).toFloat()
        val y2 = (mCy - r * Math.cos(mCurrentRadian.toDouble())).toFloat()
        return if (Math.sqrt(((x - x2) * (x - x2) + (y - y2) * (y - y2)).toDouble()) < mCircleButtonRadius) {
            true
        } else false
    }

    // Use tri to cal radian
    private fun getRadian(x: Float, y: Float): Float {
        var alpha = Math.atan(((x - mCx) / (mCy - y)).toDouble()).toFloat()
        // Quadrant
        if (x > mCx && y > mCy) {
            // 2
            alpha += Math.PI.toFloat()
        } else if (x < mCx && y > mCy) {
            // 3
            alpha += Math.PI.toFloat()
        } else if (x < mCx && y < mCy) {
            // 4
            alpha = (2 * Math.PI + alpha).toFloat()
        }
        return alpha
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        Log.d(TAG, "onMeasure")
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        // Ensure width = height
        val height = MeasureSpec.getSize(heightMeasureSpec)
        val width = MeasureSpec.getSize(widthMeasureSpec)
        mCx = (width / 2).toFloat()
        mCy = (height / 2).toFloat()
        // Radius
        if (mGapBetweenCircleAndLine + mCircleStrokeWidth >= mCircleButtonRadius) {
            mRadius = width / 2 - mCircleStrokeWidth / 2
        } else {
            mRadius =
                width / 2 - (mCircleButtonRadius - mGapBetweenCircleAndLine - mCircleStrokeWidth / 2)
        }
        setMeasuredDimension(width, height)
    }

    override fun onSaveInstanceState(): Parcelable? {
        val bundle = Bundle()
        bundle.putParcelable(INSTANCE_STATUS, super.onSaveInstanceState())
        bundle.putFloat(STATUS_RADIAN, mCurrentRadian)
        return bundle
    }

    override fun onRestoreInstanceState(state: Parcelable) {
        if (state is Bundle) {
            val bundle = state
            super.onRestoreInstanceState(bundle.getParcelable(INSTANCE_STATUS))
            mCurrentRadian = bundle.getFloat(STATUS_RADIAN)
            mCurrentTime = (60 / (2 * Math.PI) * mCurrentRadian * 60).toInt()
            return
        }
        super.onRestoreInstanceState(state)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
    }

    fun setOnTimeChangedListener(listener: OnTimeChangedListener?) {
        if (null != listener) {
            mListener = listener
        }
    }

    interface OnTimeChangedListener {
        fun start(starting: String?)
        fun end(ending: String?)
    }

    companion object {
        private const val TAG = "CircleTimerView"

        // Status
        private const val INSTANCE_STATUS = "instance_status"
        private const val STATUS_RADIAN = "status_radian"

        // Default dimension in dp/pt
        private const val DEFAULT_GAP_BETWEEN_CIRCLE_AND_LINE = 30f
        private const val DEFAULT_NUMBER_SIZE = 10f
        private const val DEFAULT_LINE_WIDTH = 0.5f
        private const val DEFAULT_CIRCLE_BUTTON_RADIUS = 15f
        private const val DEFAULT_CIRCLE_STROKE_WIDTH = 1f
        private const val DEFAULT_TIMER_NUMBER_SIZE = 0f
        private const val DEFAULT_TIMER_TEXT_SIZE = 18f

        // Default color
        private const val DEFAULT_CIRCLE_COLOR = -0x161d27
        private const val DEFAULT_CIRCLE_BUTTON_COLOR = -0x1
        private const val DEFAULT_LINE_COLOR = -0x131fe
        private const val DEFAULT_HIGHLIGHT_LINE_COLOR = -0x973a29
        private const val DEFAULT_NUMBER_COLOR = -0xe7ece8
        private const val DEFAULT_TIMER_NUMBER_COLOR = -0x1
        private const val DEFAULT_TIMER_COLON_COLOR = -0x58889
        private const val DEFAULT_TIMER_TEXT_COLOR = -0x660f0601
    }

    init {
        initialize()
    }
}