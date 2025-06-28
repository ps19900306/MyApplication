package com.nwq.view;

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.appcompat.widget.AppCompatTextView
import kotlin.math.abs

class DraggableTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = android.R.attr.selectableItemBackground
) : AppCompatTextView(context, attrs, defStyleAttr) {

    private var lastX = 0f
    private var lastY = 0f
    private var isLongPressed = false
    private val longPressRunnable = Runnable { isLongPressed = true }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                lastX = event.rawX
                lastY = event.rawY
                isLongPressed = false
                // 设置长按延迟，同时检测微小移动
                postDelayed(longPressRunnable, 500)
                return true
            }

            MotionEvent.ACTION_MOVE -> {
                val dx = event.rawX - lastX
                val dy = event.rawY - lastY

                // 移动阈值判断（避免误触发长按）
                if (!isLongPressed && (abs(dx) > 10 || abs(dy) > 10)) {
                    removeCallbacks(longPressRunnable)
                }

                if (isLongPressed) {
                    // 使用 translation 更可靠
                    translationX += dx
                    translationY += dy
                    lastX = event.rawX
                    lastY = event.rawY
                }
                return true
            }

            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                removeCallbacks(longPressRunnable)
                isLongPressed = false
                return true
            }

            else -> return super.onTouchEvent(event)
        }
    }
}
