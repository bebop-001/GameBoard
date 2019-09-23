package com.kana_tutor.gameboard

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.kana_tutor.gameboard.game2048.Game2048Fragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.frag_container)

        supportFragmentManager
            .beginTransaction()
            .add(R.id.container, Game2048Fragment.newInstance(), "Play 2048")
            .commit()
    }
}

