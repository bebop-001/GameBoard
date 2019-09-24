package com.kana_tutor.gameboard.game2048

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProviders

import com.kana_tutor.gameboard.databinding.GameFragmentBinding
import com.kana_tutor.gameboard.R
import com.kana_tutor.gameboard.utils.Swipe
import com.kana_tutor.gameboard.utils.SwipeDetector
import com.kana_tutor.gameboard.utils.setGridAndButtonsSize


class Game2048Fragment : Fragment() {

    private var _swipe = MutableLiveData<Swipe>(Swipe.NONE)
    val swipe : LiveData<Swipe>
        get () = _swipe
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

        viewModel = ViewModelProviders.of(this).get(Game2048ViewModel::class.java)

        val sd = SwipeDetector {viewModel.onSwipe(it)}
        container!!.setOnTouchListener{view, event -> sd.detect(view, event)}
        container!!.setGridAndButtonsSize(600)

        return binding.root
    }

}
