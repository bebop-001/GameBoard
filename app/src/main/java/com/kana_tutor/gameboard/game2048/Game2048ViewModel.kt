package com.kana_tutor.gameboard.game2048

import androidx.lifecycle.ViewModel
import com.kana_tutor.gameboard.utils.Swipe

class Game2048ViewModel : ViewModel() {

    var _swipeDetected = Swipe.NONE
    fun onSwipe(swipe : Swipe) {
        println("Swipe:$swipe")
    }

}
