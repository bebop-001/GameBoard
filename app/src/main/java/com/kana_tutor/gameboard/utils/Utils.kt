package com.kana_tutor.gameboard.utils

import android.content.Context
import android.content.Context.VIBRATOR_SERVICE
import android.content.res.Resources
import android.os.Build
import android.os.VibrationEffect
import android.os.VibrationEffect.createOneShot
import android.os.Vibrator
import android.util.Log
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import android.view.ViewTreeObserver
import android.widget.FrameLayout
import android.widget.GridLayout
import android.widget.TextView
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import kotlin.math.abs
import kotlin.math.min

/*
 * From: https://stackoverflow.com/questions/33050999
 *      /programmatically-set-text-color-to-primary-android-textview
 *      /33839580
 *
 * This was needed to get the default text color from
 * android which is used when setting dark/light theme.
 *
 * You need to check if the attribute got resolved
 * to a resource or a color value.
 *
 * The default value of textColorPrimary is not a
 * Color but a ColorStateList, which is a resource.
 */

fun resolveThemeAttr(context: Context, @AttrRes attrRes: Int): TypedValue {
    val theme = context.theme
    val typedValue = TypedValue()
    theme.resolveAttribute(attrRes, typedValue, true)
    return typedValue
}
@ColorInt
fun resolveColorAttr(context: Context, @AttrRes colorAttr: Int): Int {
    val resolvedAttr = resolveThemeAttr(context, colorAttr)
    // resourceId is used if it's a ColorStateList, and data if it's a color reference or a hex color
    val colorRes = if (resolvedAttr.resourceId != 0)
            resolvedAttr.resourceId
        else
            resolvedAttr.data
    return ContextCompat.getColor(context, colorRes)
}

val displayDensity = Resources.getSystem().displayMetrics.density
fun Int.toDP() : Int = (toFloat() / displayDensity).toInt()
fun Int.toPix() : Int = (toFloat() * displayDensity).toInt()

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
                val swipe = swipeDirection()
                setSwipe(swipe)
                if (swipe != Swipe.NONE && swipe != Swipe.CLICK) {
                    val vibrator
                        = (view.context.getSystemService(VIBRATOR_SERVICE) as Vibrator)
                    if (Build.VERSION.SDK_INT >= 26) {
                        vibrator.vibrate(
                            createOneShot(
                                50,
                                VibrationEffect.DEFAULT_AMPLITUDE
                            )
                        )
                    }
                    else {
                        @Suppress("DEPRECATION")
                        vibrator.vibrate(50)
                    }
                }// view.playSoundEffect(AudioManager.FX_KEY_CLICK)
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

const val MAX_FONT_SIZE  = 30f
const val MAX_BUTTON_SIZE = 150
var textScalingMin = 75 to 20f // 100 dp button gets 16sp font
var textScalingMax = MAX_BUTTON_SIZE to MAX_FONT_SIZE
fun calcScaledFontSize(buttonSizeDP : Int) : Float {
    val (w1,f1) = textScalingMin
    val (w2,f2) = textScalingMax
    val m = (f1 - f2) / (w1.toFloat() - w2.toFloat())
    return (m * (buttonSizeDP - w1)) + f1
}
/*
    Method for setting size of views in a grid layout.  Assumes square
    (i.e. rows == columns) layout.  Calculates max possible size and then
    sets size to that or maxSizeDP -- which ever is smaller,

    Works by queing a layout change listener which is called when layout
    is complete then uses the measured size of the view group containing the
    grid layout to calculate tile size.
 */
fun GridLayout.setGridTileSize(scaleText : Boolean) {
    viewTreeObserver.addOnGlobalLayoutListener(
        object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                if (parent != null) {
                    viewTreeObserver
                        .removeOnGlobalLayoutListener(this)
                    // for some reason, API 26/android 8 sometimes comes in wrong
                    // after recreate in theme change.
                    if (parent != null) {
                        val p = parent as FrameLayout
                        // viewTreeObserver.removeOnGlobalLayoutListener()
                        var calcButtonSize = (min(
                            p.width,
                            p.height
                        ) / rowCount) - 4.toPix() //4.toPix for grid view 4dp padding
                        calcButtonSize = min(calcButtonSize, MAX_BUTTON_SIZE.toPix())
                        val fontSize: Float = if (scaleText)
                            calcScaledFontSize(calcButtonSize.toDP())
                        else
                            MAX_FONT_SIZE
                        val dims = listOf(
                            p.width,
                            p.height,
                            p.width.toDP(),
                            p.height.toDP(),
                            calcButtonSize.toDP(),
                            fontSize.toInt()
                        )
                        Log.d(
                            "setGridTileSize:",
                            "display size:$dims"
                        )
                        for (i in 0..(childCount - 1)) {
                            val child: TextView = getChildAt(i) as TextView
                            child.width = calcButtonSize
                            child.height = calcButtonSize
                            child.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize)
                        }
                    }
                    else
                        Log.d("setGridTileSize:", "null parent")
                }
            }
        }
    )
}
