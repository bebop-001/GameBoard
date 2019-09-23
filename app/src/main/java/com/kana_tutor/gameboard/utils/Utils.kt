package com.kana_tutor.gameboard.utils

import android.content.res.Resources
import android.util.Log
import android.view.MotionEvent
import android.view.View
import kotlin.math.abs

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
class TouchEvent (val x : Int = 0, val y : Int = 0
                  , val time : Long = System.currentTimeMillis()) {
    override fun toString(): String {
        return String.format("x:%4d y:%4d time:%d", x, y, time)
    }
}
class TouchState {
    private var start = TouchEvent(ACTION_NO_ACTION)
    private var end = TouchEvent(ACTION_NO_ACTION)
    fun startEvent(x : Int, y : Int) {
        start = TouchEvent(x, y)
        end = TouchEvent()
    }
    fun endEvent(x: Int, y:Int) {
        end = TouchEvent(x, y)
    }
    fun cancelEvent() {
        start = TouchEvent()
        end = TouchEvent()
    }
    fun deltaX()  = abs(start.x - end.x).toDP()
    fun deltaY() = abs (start.y - end.y).toDP()
    fun deltaT() = end.time - start.time
    fun swipeDirection() : Swipe {
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
    override fun toString(): String {
        return "start:$start, end:$end : direction: ${swipeDirection()}"
    }
}

fun swipeDetect (view: View, event : MotionEvent, state : TouchState) : Boolean {
    var rv = true
    val action = event.actionMasked
    val x = event.rawX.toInt()
    val y = event.rawY.toInt()
    when (action) {
        MotionEvent.ACTION_DOWN -> state.startEvent(x, y)
        MotionEvent.ACTION_UP -> {
            state.endEvent(x, y)
            Log.d(
                "TouchEvent", "x:${state.deltaX()} "
                        + "y:${state.deltaY()} "
                        + "t:${state.deltaT()} "
                        + "swipe: ${state.swipeDirection()}"
            )
        }
        MotionEvent.ACTION_MOVE -> {}
        else -> { 
            state.cancelEvent()
            rv = false
        }
    }
    return rv
}
