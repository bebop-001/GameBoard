package com.kana_tutor.gameboard.game2048

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import board.Direction
import com.kana_tutor.gameboard.utils.Swipe
import games.game2048.Game2048
import games.game2048.newGame2048

class Game2048ViewModel : ViewModel() {
    companion object {
        var game : Game2048 = newGame2048().initialize()
    }
    private var _boardPositions = MutableLiveData<String>(game.toString())
    val boardPositions : LiveData<String>
        get() = _boardPositions
    private var _gameWon = MutableLiveData<Boolean>(false)
    val gameWon : LiveData<Boolean>
        get() = _gameWon
    private var _gameOver = MutableLiveData<Boolean>(false)
    val gameOver : LiveData<Boolean>
        get() = _gameOver

    fun newGame() : Game2048 {
        _gameOver.value = false
        _gameWon.value = false
        game = newGame2048()
        game.initialize()
        _boardPositions.value = game.toString()
        return game
    }
    fun getScore() : String = game.score
    fun onSwipe(swipe : Swipe) {
        _gameOver.value = ! game.canMove()
        if (_gameOver.value == false) {
            when(swipe) {
                Swipe.LEFT -> game.processMove(Direction.LEFT)
                Swipe.RIGHT -> game.processMove(Direction.RIGHT)
                Swipe.UP -> game.processMove(Direction.UP)
                Swipe.DOWN -> game.processMove(Direction.DOWN)
                else -> {/* for now, ignore... */ }
            }
            _boardPositions.value = "$swipe:$game"
            if (game.hasWon()) _gameWon.value = true
        }
        // Log.d("Swipe:", "$swipe -> ${_boardPositions.value}")
    }

}
