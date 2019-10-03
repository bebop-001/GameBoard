package com.kana_tutor.gameboard.game15

import android.graphics.Typeface
import android.os.Bundle
import android.util.TypedValue
import android.view.*
import android.widget.GridLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.text.isDigitsOnly
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.kana_tutor.gameboard.MainActivity

import com.kana_tutor.gameboard.R
import com.kana_tutor.gameboard.utils.SwipeDetector
import com.kana_tutor.gameboard.utils.setGridTileSize
import com.kana_tutor.gameboard.utils.toPix
import java.lang.RuntimeException


class Game15Fragment : Fragment() {

    companion object {
        fun newInstance() = Game15Fragment()
    }
    private lateinit var viewModel : Game15ViewModel
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
        // use a text-view in the alert so we can display an html
        // formatted string.
        val scoreTv = TextView(this.context)
        scoreTv.apply {
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 20.0f)
            setTypeface(null, Typeface.BOLD)
            text = getString(R.string.your_score, viewModel.getScore())
        }

        AlertDialog.Builder(this.context!!)
            .setView(scoreTv)
            .show()

    }
    private fun updateView(positions:String, view : GridLayout) {
        val cellValues = """(\d+|-)""".toRegex().findAll(positions).map{it.value}.toList()
        if (cellValues.size != 16)
            throw RuntimeException("updateGrid: Bad positions string: $positions\n"
                + "Does not contain 16 values")
        if (view.childCount != 16)
            throw RuntimeException("updateGrid: Expected 16 views. found ${view.childCount}")
        if (viewModel.gameOver.value == false) {
            for (i in 0 until view.childCount) {
                val tv = view.getChildAt(i) as TextView
                val textVal = if (cellValues[i] == "-") "" else cellValues[i]
                tv.text = textVal
                if(textVal != "" && textVal.isDigitsOnly() && textVal.toInt() % 2 == 0)
                    tv.setTextColor(
                        ContextCompat.getColor(context!!, R.color.red))
                else
                    tv.setTextColor(
                        ContextCompat.getColor(context!!, R.color.game_15_dark))
            }
            view.invalidate()
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProviders.of(this).get(Game15ViewModel::class.java)
        viewModel.boardPositions.observe(
            this
            , Observer { updateView(it, view as GridLayout) }
        )
        viewModel.gameOver.observe(
            this,
            Observer {
                if (it) {
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
        gl.setGridTileSize(600.toPix(), false)

        setHasOptionsMenu(true)

        return gl
    }

}
