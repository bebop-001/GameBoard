package com.kana_tutor.gameboard.game2048

import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.*
import android.widget.GridLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.text.isDigitsOnly
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.kana_tutor.gameboard.MainActivity

// import com.kana_tutor.gameboard.databinding.GameFragmentBinding
import com.kana_tutor.gameboard.R
import com.kana_tutor.gameboard.utils.SwipeDetector
import com.kana_tutor.gameboard.utils.setGridTileSize
import java.lang.RuntimeException


class Game2048Fragment : Fragment() {

    companion object {
        fun newInstance() = Game2048Fragment()
    }
    private lateinit var viewModel : Game2048ViewModel
    // private lateinit var binding: GameFragmentBinding

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        var rv = true
        when (item.itemId) {
            R.id.new_game_item -> {viewModel.newGame()}
            else -> rv = super.onOptionsItemSelected(item)
        }
        return rv
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.fragment_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    private fun displayScore() {
        val score = viewModel.getScore()
        // use a text-view in the alert so we can display an html
        // formatted string.
        val scoreTv = TextView(this.context)
        scoreTv.apply {
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 20.0f)
            setTypeface(null, Typeface.BOLD)
            // text = htmlString(htmlString)
            text = "Game Over.  Your score is:\n$score"
            // gravity = Gravity.CENTER
        }

        AlertDialog.Builder(this.context!!)
            .setView(scoreTv)
            .show()

    }

    val colorsMap = hashMapOf(
        2 to R.color.color_2,
        4 to R.color.color_4,
        8 to R.color.color_8,
        16 to R.color.color_16,
        32 to R.color.color_32,
        64 to R.color.color_64,
        128 to R.color.color_128,
        256 to R.color.color_256,
        512 to R.color.color_512,
        1024 to R.color.color_1024,
        2048 to R.color.color_2048,
        4096 to R.color.color_4096
    )
    private fun updateView(positions:String, view : GridLayout) {
        val cellValues = """(\d+|-)""".toRegex().findAll(positions).map{it.value}.toList()
        if (cellValues.size != 16)
            throw RuntimeException("updateGrid: Bad positions string: $positions\n"
                + "Does not contain 16 values")
        if (view.childCount != 16)
            throw RuntimeException("updateGrid: Expected 16 views. found ${view.childCount}")
        for (i in 0 until view.childCount) {
            val tv = view.getChildAt(i) as TextView
            val tvText = if (cellValues[i] == "-") "" else cellValues[i]
            tv.text = tvText
            if (tvText != "" && tvText.isDigitsOnly()) {
                val colorVal = tvText.toInt()
                tv.setTextColor(resources.getColor(colorsMap[colorVal]!!))
            }

            tv.text = if (cellValues[i] == "-") "" else cellValues[i]
            Log.d("tv update:", "$i: \"${tv.text}\"")
        }
        view.invalidate()
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProviders.of(this).get(Game2048ViewModel::class.java)
        viewModel.boardPositions.observe(
            this
            , Observer { updateView(it, view as GridLayout) }
        )
        viewModel.gameOver.observe(
            this,
            Observer {
                if (it) {
                    // Toast.makeText(this.context, "Game Over", Toast.LENGTH_LONG).show()
                    displayScore()
                }
            }
        )
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        (activity as MainActivity).setActionBarTitle(tag!!)

        val gl = inflater.inflate(
            R.layout.game_fragment, container, false
        ) as GridLayout

        val sd = SwipeDetector {viewModel.onSwipe(it)}
        container!!.setOnTouchListener{view, event -> sd.detect(view, event)}
        gl.setGridTileSize(600)

        setHasOptionsMenu(true)

        return gl
    }

}
