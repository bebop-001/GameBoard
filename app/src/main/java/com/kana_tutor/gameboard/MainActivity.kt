package com.kana_tutor.gameboard

import android.content.Context
import android.graphics.Typeface
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.text.Spanned
import android.util.TypedValue
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.text.HtmlCompat
import com.kana_tutor.gameboard.game2048.Game2048Fragment
import java.io.File
import java.text.SimpleDateFormat

class MainActivity : AppCompatActivity() {
    companion object {
        const val PLAY_2048     = 0
        const val PLAY_15   = PLAY_2048 + 1
        var selectedGame = 0
    }
    // Return a spanned html string using the appropriate call for
    // the user's device.
    private fun htmlString(htmlString:String) : Spanned {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Html.fromHtml(htmlString, HtmlCompat.FROM_HTML_MODE_LEGACY)
        }
        else {
            @Suppress("DEPRECATION")
            Html.fromHtml(htmlString)
        }
    }
    // Display info about the build using an AlertDialog.
    private fun displayAboutInfo() : Boolean {
        val appInfo = packageManager
            .getApplicationInfo(BuildConfig.APPLICATION_ID, 0)
        val installTimestamp = File(appInfo.sourceDir).lastModified()

        // use html to format our output 'about' message.
        val htmlString = String.format(getString(R.string.about_query)
            , getString(R.string.app_name)
            , BuildConfig.VERSION_CODE, BuildConfig.VERSION_NAME
            , SimpleDateFormat.getInstance().format(
                java.util.Date(BuildConfig.BUILD_TIMESTAMP))
            , SimpleDateFormat.getInstance().format(
                java.util.Date(installTimestamp))
            , if(BuildConfig.DEBUG) "debug" else "release"
            , BuildConfig.BRANCH_NAME
        )

        // use a text-view in the alert so we can display an html
        // formatted string.
        val aboutTv = TextView(this)
        aboutTv.apply {
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 20.0f)
            setTypeface(null, Typeface.BOLD)
            text = htmlString(htmlString)
            gravity = Gravity.CENTER
        }

        AlertDialog.Builder(this)
            .setView(aboutTv)
            .show()
        return true
    }
    fun newGameSelected(gameId : Int) {
        selectedGame = gameId
        getSharedPreferences("user_prefs.txt", Context.MODE_PRIVATE)
            .edit()
            .putInt("selectedGame", selectedGame)
            .apply()
    }
    // Menu item selected listener.
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        var rv = true

        when (item.itemId) {
            R.id.build_info_item -> displayAboutInfo()
            R.id.game_15_select -> newGameSelected(PLAY_15)
            R.id.game_2048_select -> newGameSelected(PLAY_2048)
            // If item isn't for this menu, you must call the super or
            // other things that must happen (eg: up-button in onSupportNavigateUp)
            // won't happen.
            else -> rv = super.onOptionsItemSelected(item);        }
        return rv
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        if (selectedGame == PLAY_2048)
            menu!!.findItem(R.id.game_2048_select).setChecked(true)
        else
            menu!!.findItem(R.id.game_15_select).setChecked(true)

        return super.onPrepareOptionsMenu(menu)
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        if (selectedGame == PLAY_2048)
            menu!!.findItem(R.id.game_2048_select).setChecked(true)
        else
            menu!!.findItem(R.id.game_15_select).setChecked(true)
        return true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.frag_container)

        val prefs
                = getSharedPreferences("user_prefs.txt", Context.MODE_PRIVATE)
        selectedGame = prefs.getInt("selectedGame", PLAY_2048)

        supportFragmentManager
            .beginTransaction()
            .add(R.id.container, Game2048Fragment.newInstance(), "Play 2048")
            .commit()
    }
}

