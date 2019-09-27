package com.kana_tutor.gameboard.game2048

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.GridLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders

import com.kana_tutor.gameboard.databinding.GameFragmentBinding
import com.kana_tutor.gameboard.R
import com.kana_tutor.gameboard.utils.SwipeDetector
import com.kana_tutor.gameboard.utils.setGridAndButtonsSize
import java.lang.RuntimeException


class Game2048Fragment : Fragment() {

    companion object {
        fun newInstance() = Game2048Fragment()
    }
    private lateinit var viewModel : Game2048ViewModel
    private lateinit var binding: GameFragmentBinding

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
    }

    fun updateView(positions:String, view : GridLayout) {
        val cellValues = """(\d+|-)""".toRegex().findAll(positions).map{it.value}.toList()
        if (cellValues.size != 16)
            throw RuntimeException("updateGrid: Bad positions string: $positions\n"
                + "Does not contain 16 values")
        if (view.childCount != 16)
            throw RuntimeException("updateGrid: Expected 16 views. found ${view.childCount}")
        for (i in 0 .. (view.childCount - 1)) {
            val tv = view.getChildAt(i) as TextView
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
                if (it)
                    Toast.makeText(this.context,"Game Over", Toast.LENGTH_LONG).show()
            }
        )


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.game_fragment, container, false)
        binding.setLifecycleOwner(this)


        val sd = SwipeDetector {viewModel.onSwipe(it)}
        container!!.setOnTouchListener{view, event -> sd.detect(view, event)}
        container!!.setGridAndButtonsSize(600)

        setHasOptionsMenu(true)

        return binding.root
    }

}
