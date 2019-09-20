package com.kana_tutor.gameboard.game2048

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.kana_tutor.gameboard.databinding.GameFragmentBinding
import com.kana_tutor.gameboard.R

/**
 * A simple [Fragment] subclass.
 */
class Game2048Fragment : Fragment() {
    companion object {
        fun newInstance() = Game2048Fragment()
    }
    private lateinit var viewModel : Game2048ViewModel
    lateinit var binding: GameFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(
            inflater, R.layout.game_fragment, container, false)
        binding.setLifecycleOwner(this)
        return binding.root
    }

}
