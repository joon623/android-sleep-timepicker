package com.jun.customview.customview

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.Toast

class circularview @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {


    // paint
    private var outterCirclePaint: Paint? = null
    private var innerCirclePaint: Paint? = null
    private var arcPaint: Paint? = null

    // Parameter
    private var innerCx = 100f
    private var innerCy = 100f

    companion object {
        private const val TAG = "CircleTimerView"


        // Default Color
        private const val DEFAULT_OUTTER_CIRCLE_COLOR = -0x161d27
        private const val DEFAULT_INNER_CIRCLE_COLOR = -0x161d27
    }

    init {
        initialize()
    }

    private fun initialize() {

        // init Paint
        outterCirclePaint = Paint()
        innerCirclePaint = Paint()
        arcPaint = Paint()


        // Setting Paint
        outterCirclePaint!!.color = -0xe7ece8
        outterCirclePaint!!.style = Paint.Style.STROKE

        innerCirclePaint!!.color = -0x58889
        innerCirclePaint!!.style = Paint.Style.FILL
    }

    // onDraw 메서드는 빈번하게 호출이 되기 때문에 오래 걸리는 작업은 해주지 않는게 좋다.'
    // view의 모양을 그리는 곳이다.
    override fun onDraw(canvas: Canvas) {
        canvas.drawCircle(100F, 100F, 100F, outterCirclePaint!!)
        canvas.drawCircle(innerCx, innerCy, 80F, innerCirclePaint!!)
        super.onDraw(canvas)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        Log.d(TAG, event.actionMasked.toString())
        when(event.action) {
            MotionEvent.ACTION_MOVE-> {
                Log.d(TAG, "action down")
                innerCx = event.x
                innerCy = event.y
//                if(Math.pow((100 - innerCx).toDouble(), 2.0) + Math.pow((100 - innerCy).toDouble(),
//                        2.0
//                    ) > 50){
//                    return true
//                }


            }  MotionEvent.ACTION_DOWN  -> {
                Log.d(TAG, "action down")
                innerCx = event.x
                innerCy = event.y
                if(Math.pow((100 - innerCx).toDouble(), 2.0) + Math.pow((100 - innerCy).toDouble(),
                        2.0
                    ) > 50){
                    return true
                }

            }
            MotionEvent.ACTION_UP -> {
                Log.d(TAG, "action up")
            }
        }
        invalidate()
        return true
    }


}

private fun Canvas.drawArc(fl: Float, fl1: Float, fl2: Float, arcPaint: Paint) {

}
