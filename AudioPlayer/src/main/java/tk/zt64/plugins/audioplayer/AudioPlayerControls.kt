package tk.zt64.plugins.audioplayer

import android.annotation.SuppressLint
import android.content.Context
import android.view.MotionEvent
import android.widget.LinearLayout
import com.lytefast.flexinput.R

@SuppressLint("AppCompatCustomView")
class ControlsLayout(context: Context) : LinearLayout(context, null, 0, R.i.UiKit_ViewGroup) {
    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        requestDisallowInterceptTouchEvent(true)
        return super.onTouchEvent(event)
    }
}