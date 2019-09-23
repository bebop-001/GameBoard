package com.kana_tutor.gameboard.game2048

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import androidx.core.view.doOnLayout
import androidx.databinding.DataBindingUtil
import com.kana_tutor.gameboard.MainActivity
import com.kana_tutor.gameboard.databinding.GameFragmentBinding
import com.kana_tutor.gameboard.R
import com.kana_tutor.gameboard.toDP
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

/**
 * A simple [Fragment] subclass.
 */
class Game2048Fragment : Fragment() {
    companion object {
        fun newInstance() = Game2048Fragment()
    }
    private lateinit var viewModel : Game2048ViewModel
    private lateinit var binding: GameFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.game_fragment, container, false)
        binding.setLifecycleOwner(this)
        val gl = binding.gridLayout

        gl.doOnLayout {
            container!!.addOnLayoutChangeListener(object : View.OnLayoutChangeListener {
                override fun onLayoutChange(view: View, left: Int, top: Int, right: Int, bottom: Int,
                                            oldLeft: Int, oldTop: Int, oldRight: Int, oldBottom: Int) {
                    val w = (min(abs(right - left), abs(top - bottom)) - 6.toDP()) / 4
                    for (i in 0..(gl.childCount - 1)) {
                        val child = gl.getChildAt(i)
                        val layoutParams = child.layoutParams
                        layoutParams.height = w
                        layoutParams.width = w
                        child.layoutParams = layoutParams
                    }



                    view.removeOnLayoutChangeListener(this)
                }
            })
            /*
            if (it == container) {
                val height = it.height
                val width = it.width
                Log.d("GlobalLayoutListener:1", "x:${width}, y:${height}")
                val w = max(abs(height), abs(width)) - 6.toDP()

                var layoutParams = binding.gridLayout.layoutParams
                layoutParams.width - w; layoutParams.height = w;
                binding.gridLayout.layoutParams = layoutParams
                it.removeOnLayoutChangeListener()
            }*/

        }
        return binding.root
    }

}
