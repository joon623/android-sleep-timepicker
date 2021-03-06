# sleep time picker
![sleep-timer](https://user-images.githubusercontent.com/67637245/137584960-b4d2542a-72d5-4092-b0ab-d8d562db79e2.gif)



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
- ThreeTen Android Backport 라이브러리 추가 
```
dependencies {
    implementation 'com.jakewharton.threetenabp:threetenabp:1.3.0'
}
```

```
    override onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    AndroidThreeTen.init(this);
}

```

## 04.05
- 수면 시간 계산 확인 
- 기상 시간, 취침 시간 오류 발견 -> 해결할 수 있는 코드 만듦 
- 내일 나머지 모두 적용해서 정상적으로 돌아가게 만들 예정 

```
        fun snapTest(minutes: Int, step: Double): Double {
            val remainder = minutes % 10
            var rest = 0.0
            when (remainder) {
                0 ,1, 2 -> rest = 0.0
                3, 4-> rest = 2.5
                5, 6, 7 -> rest = 5.0
                8, 9 -> rest = 7.5
            }
            return minutes - (minutes % 10) + rest
        }

```

## 04.06
- 기상 시간, 취침 시간 오류 해결 완료 
- 수면 시간 계산 로직 변경 및 버그 해결 
- snapTest 함수 리팩토링 작업 

```
```
## 04.07
- 타이머 로테이션 기능 
```
