package com.kana_tutor.gameboard.utils

import android.content.res.Resources
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import androidx.core.view.doOnLayout
import java.lang.RuntimeException
import kotlin.math.abs
import kotlin.math.min

val displayDensity = Resources.getSystem().displayMetrics.density
fun Int.toDP() : Int = (toFloat() / displayDensity).toInt()

enum class Swipe {
    UP, DOWN, LEFT, RIGHT, CLICK, NONE
}

const val ACTION_NO_ACTION  = -1
// Anything of longer duration is not a swipe or click.
const val MAX_TOUCH_DURATION = 500 // milliseconds
// minimum distance to define a swipe.  Anything shorter is a click.
const val MIN_SWIPE_DISTANCE = 50 // dp
class SwipeDetector (val setSwipe : (Swipe) -> Unit){
    class Event (val x : Int = 0, val y : Int = 0
                      , val time : Long = System.currentTimeMillis()) {
        override fun toString(): String {
            return String.format("x:%4d y:%4d time:%d", x, y, time)
        }
    }
    private var start = Event(ACTION_NO_ACTION)
    private var end = Event(ACTION_NO_ACTION)
    private fun startEvent(x : Int, y : Int) {
        start = Event(x, y)
        end = Event()
    }
    private fun endEvent(x: Int, y:Int) {
        end = Event(x, y)
    }
    private fun cancelEvent() {
        start = Event()
        end = Event()
    }
    private fun deltaX()  = abs(start.x - end.x).toDP()
    private fun deltaY() = abs (start.y - end.y).toDP()
    private fun deltaT() = end.time - start.time
    private fun swipeDirection() : Swipe {
        val rv: Swipe
        // event < 500 ms is no event
        if (deltaT() < MAX_TOUCH_DURATION) {
            // event distance < 50 dp is CLICK.
            rv = if (deltaX() > MIN_SWIPE_DISTANCE || deltaY() > MIN_SWIPE_DISTANCE) {
                if (deltaX() > deltaY()) {
                    if (start.x > end.x) Swipe.LEFT else Swipe.RIGHT
                }
                else {
                    if (start.y > end.y) Swipe.UP else Swipe.DOWN
                }
            }
            else {
                Swipe.CLICK
            }
        }
        else {
            rv = Swipe.NONE
        }
        return rv
    }
    fun detect (view: View, event : MotionEvent) : Boolean {
        var rv = true
        val action = event.actionMasked
        val x = event.rawX.toInt()
        val y = event.rawY.toInt()
        when (action) {
            MotionEvent.ACTION_DOWN -> startEvent(x, y)
            MotionEvent.ACTION_UP -> {
                endEvent(x, y)
                setSwipe(swipeDirection())
                Log.d(
                    "swipeDetect", "x:${deltaX()} "
                            + "y:${deltaY()} "
                            + "t:${deltaT()} "
                            + "swipe: ${swipeDirection()}"
                )
            }
            MotionEvent.ACTION_MOVE -> {}
            else -> {
                cancelEvent()
                rv = false
            }
        }
        return rv
    }

    override fun toString(): String {
        return "start:$start, end:$end : direction: ${swipeDirection()}"
    }
}


/*
    Method for setting size of views in a grid layout.  Assumes square
    (i.e. rows == columns) layout.  Calculates max possible size and then
    sets size to that or maxSizeDP -- which ever is smaller,

    Works by queiong a layout change listener which is called when layout
    is complete then uses the measured size of the view group containing the
    grid layout to calculate tile size.
 */
fun ViewGroup.setGridAndButtonsSize(maxSizeDP : Int) {
    doOnLayout {
        addOnLayoutChangeListener(object : View.OnLayoutChangeListener {
            override fun onLayoutChange(
                view: View, left: Int, top: Int, right: Int, bottom: Int,
                oldLeft: Int, oldTop: Int, oldRight: Int, oldBottom: Int
            ) {
                val gl : GridLayout
                if(view is ViewGroup && view.childCount > 0 && view.getChildAt(0) is GridLayout) {
                    gl = view.getChildAt(0) as GridLayout
                }
                else {
                    throw RuntimeException("setGridAndButtonsSize: "
                            + "Failed to find GridLayout at child(0)")
                }
                val rows = gl.rowCount; val cols = gl.columnCount
                if (rows != cols)
                    throw RuntimeException("setGridAndButtonsSize:"
                            + "currently works only with rows == columns.\n"
                            + "Found rows = $rows, columns = $cols")
                val w = min(min(abs(right - left), abs(top - bottom)), maxSizeDP / rows)
                for (i in 0..(gl.childCount - 1)) {
                    val child = gl.getChildAt(i)
                    val layoutParams = child.layoutParams
                    layoutParams.height = w
                    layoutParams.width = w
                    child.layoutParams = layoutParams
                }
                Log.d("onLayout:", " w = $w dp")
                view.removeOnLayoutChangeListener(this)
            }
        })
    }
}
