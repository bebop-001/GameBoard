package com.kana_tutor.gameboard

import android.content.res.Resources
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.MotionEvent.*
import android.view.View
import android.widget.FrameLayout
import androidx.lifecycle.LiveData
import com.kana_tutor.gameboard.game2048.Game2048Fragment
import com.kana_tutor.gameboard.utils.TouchState
import com.kana_tutor.gameboard.utils.swipeDetect
import games.game2048.Game2048
import kotlin.math.abs


enum class Swipe {
    UP, DOWN, LEFT, RIGHT, CLICK, NONE
}
val displayDensity = Resources.getSystem().displayMetrics.density
fun Int.toDP() : Int {return (this.toFloat() / displayDensity).toInt()}

class MainActivity : AppCompatActivity() {

    companion object {
        const val ACTION_NO_ACTION  = -1
        val touchState = TouchState()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.frag_container)

        val rootElement = findViewById<FrameLayout>(R.id.container)

        rootElement.setOnTouchListener (
            {view, event -> swipeDetect(view, event, touchState)}
        )

        supportFragmentManager
            .beginTransaction()
            .add(R.id.container, Game2048Fragment.newInstance(), "Play 2048")
            .commit()
    }
}

