# android_customview

### 03.29
- Custom View 생성 방법 파악 View 상속하기 
- OnDraw() 메서드, Canvas 메소드 파악 

### 03.30
- 하드웨어 가속화 방지 (ram 사용 중지를 위해서 )
```
 setLayerType(View.LAYER_TYPE_SOFTWARE, null);
```
- ViewGroup에서 Ondraw 호출하는 방법 
```
 setLayerType(View.LAYER_TYPE_SOFTWARE, null);
```

### 03.31
- division 안 시계 바늘 / 시간 추가 
- 아이콘 이동 기능 추가 

```
    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x
        val y = event.y
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                val touchAngleRad = atan2(center.y - y, x - center.x).toDouble()
                if (draggingSleep) {
                    val sleepAngleRad = Math.toRadians(sleepAngle)
                    val diff = Math.toDegrees(angleBetweenVectors(sleepAngleRad, touchAngleRad))
                    sleepAngle = to_0_720(sleepAngle + diff)
                    requestLayout()
//                    notifyChanges()
                    return true
                } else if (draggingWake) {
                    val wakeAngleRad = Math.toRadians(wakeAngle)
                    val diff = Math.toDegrees(angleBetweenVectors(wakeAngleRad, touchAngleRad))
                    wakeAngle = to_0_720(wakeAngle + diff)
                    requestLayout()
//                    notifyChanges()
                    return true
                }
            }

            MotionEvent.ACTION_UP -> {
                draggingSleep = false
                draggingWake = false
            }
        }
        invalidate()
        return super.onTouchEvent(event)
    }
```

## 04.01 
- 잔디밭 에러 해결 