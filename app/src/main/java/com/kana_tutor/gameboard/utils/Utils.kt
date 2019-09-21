package com.kana_tutor.gameboard.utils

import android.content.res.Resources
import android.util.Log
import android.view.MotionEvent
import android.view.View
import com.kana_tutor.gameboard.MainActivity
import com.kana_tutor.gameboard.Swipe
import com.kana_tutor.gameboard.toDP
import kotlin.math.abs

val displayDensity = Resources.getSystem().displayMetrics.density
fun Int.toDP() : Int {return (this.toFloat() / displayDensity).toInt()}

enum class Swipe {
    UP, DOWN, LEFT, RIGHT, CLICK, NONE
}

class TouchEvent (xPix : Int = 0, yPix : Int = 0
                  , val time : Long = System.currentTimeMillis()) {
    val x = xPix.toDP()
    val y = yPix.toDP()
    override fun toString(): String {
        return String.format("x:%4d y:%4d time:%d", x, y, time)
    }
}
class TouchState {
    private var start = TouchEvent(MainActivity.ACTION_NO_ACTION)
    private var end = TouchEvent(MainActivity.ACTION_NO_ACTION)
    fun startEvent(x : Int, y : Int) {
        start = TouchEvent(x, y)
        end = TouchEvent()
    }
    fun endEvent(x: Int, y:Int) {
        end = TouchEvent(x, y)
    }
    fun cancleEvent() {
        start = TouchEvent()
        end = TouchEvent()
    }
    fun deltaX()  = abs(start.x - end.x)
    fun deltaY() = abs (start.y - end.y)
    fun deltaT() = end.time - start.time
    fun swipeDirection() : Swipe {
        var rv = Swipe.NONE
        // event < 500 ms is no event
        if (deltaT() < 500) {
            // event distance < 50 dp is CLICK.
            if (deltaX() > 50 || deltaY() > 50) {
                if (deltaX() > deltaY()) {
                    rv = if (start.x > end.x) Swipe.LEFT else Swipe.RIGHT
                }
                else {
                    rv = if (start.y > end.y) Swipe.UP else Swipe.DOWN
                }
            }
            else {
                rv = Swipe.CLICK
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
    if (action == MotionEvent.ACTION_DOWN) {
        state.startEvent(x, y)
    }
    else if (action == MotionEvent.ACTION_UP) {
        state.endEvent(x, y)
        Log.d("TouchEvent",  "x:${state.deltaX()} "
                + "y:${state.deltaY()} "
                + "t:${state.deltaT()} "
                + "swipe: ${state.swipeDirection()}")
    }
    else if (action != MotionEvent.ACTION_MOVE) {
        state.cancleEvent()
        rv = false
    }
    return rv
}
