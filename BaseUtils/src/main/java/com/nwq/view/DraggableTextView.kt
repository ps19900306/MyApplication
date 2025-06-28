import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.appcompat.widget.AppCompatTextView

class DraggableTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = android.R.attr.selectableItemBackground
) : AppCompatTextView(context, attrs, defStyleAttr) {

    private var lastX = 0f
    private var lastY = 0f
    private var isLongPressed = false

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                lastX = event.rawX
                lastY = event.rawY
                isLongPressed = false
                this@DraggableTextView.postDelayed({
                    isLongPressed = true
                }, 500) // 长按时间设定为500毫秒
                return true
            }

            MotionEvent.ACTION_MOVE -> {
                if (isLongPressed) {
                    val dx = event.rawX - lastX
                    val dy = event.rawY - lastY
                    x += dx
                    y += dy
                    lastX = event.rawX
                    lastY = event.rawY
                }
                return true
            }

            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                this@DraggableTextView.removeCallbacks(null)
                isLongPressed = false
                return true
            }

            else -> return super.onTouchEvent(event)
        }
    }
}
