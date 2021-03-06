package com.kana_tutor.gameboard

import android.content.Context
import android.graphics.Typeface
import android.media.AudioManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.text.Spanned
import android.util.TypedValue
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import com.kana_tutor.gameboard.game15.Game15Fragment
import com.kana_tutor.gameboard.game2048.Game2048Fragment
import kotlinx.android.synthetic.main.frag_container.*
import java.io.File
import java.lang.RuntimeException
import java.text.SimpleDateFormat
import android.content.Intent
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T



class MainActivity : AppCompatActivity() {
    companion object {
        var displayTheme = 0 // for light or dark theme.
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
    private fun newGameSelected(gameId : Int) {
        selectedGame = gameId
        getSharedPreferences("user_prefs.txt", Context.MODE_PRIVATE)
            .edit()
            .putInt("selectedGame", selectedGame)
            .apply()
        recreate()
    }
    private fun changeDisplayTheme(currentName : String) {
        // if currentName is dark, select dark theme.
        if (currentName == resources.getString(R.string.dark_theme))
            displayTheme = R.string.dark_theme
        else
            displayTheme = R.string.light_theme
        getSharedPreferences("user_prefs.txt", Context.MODE_PRIVATE)
            .edit()
            .putInt("displayTheme", displayTheme)
            .apply()
        recreate()
    }
    // Menu item selected listener.
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        var rv = true

        when (item.itemId) {
            R.id.build_info_item -> displayAboutInfo()
            R.id.game_15_select -> newGameSelected(R.string.play_15)
            R.id.game_2048_select -> newGameSelected(R.string.play_2048)
            R.id.select_display_theme -> changeDisplayTheme(item.title.toString())
            // If item isn't for this menu, you must call the super or
            // other things that must happen (eg: up-button in onSupportNavigateUp)
            // won't happen.
            else -> rv = super.onOptionsItemSelected(item);        }
        return rv
    }
    private fun updateMenuSelections(menu: Menu) {
        if (selectedGame == R.string.play_2048)
            menu.findItem(R.id.game_2048_select).isChecked = true
        else
            menu.findItem(R.id.game_15_select).isChecked = true
        // selection is opposite of what is selected
        if (displayTheme == R.string.light_theme)
            menu.findItem(R.id.select_display_theme).setTitle(R.string.dark_theme)
        else
            menu.findItem(R.id.select_display_theme).setTitle(R.string.light_theme)
    }
    // called from fragment to set action bar title.
    fun setActionBarTitle(title : String) {
        toolbar?.title = title
    }
    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        if (menu != null)
            updateMenuSelections(menu)
        return super.onPrepareOptionsMenu(menu)
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        if (menu != null)
            updateMenuSelections(menu)
        return true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.frag_container)

        val prefs
                = getSharedPreferences("user_prefs.txt", Context.MODE_PRIVATE)
        selectedGame = prefs.getInt("selectedGame", R.string.play_2048)
        displayTheme = prefs.getInt("displayTheme", R.string.light_theme)
        if (displayTheme == R.string.light_theme)
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        else
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)

        toolbar.overflowIcon = ContextCompat.getDrawable(
            this, R.drawable.vert_ellipsis_light_img)
        setSupportActionBar(toolbar)

        val fragTransaction = supportFragmentManager.beginTransaction()
        when (selectedGame) {
            R.string.play_2048 ->
                fragTransaction
                    .replace(R.id.container, Game2048Fragment.newInstance(), "Play 2048")
                    .commit()
            R.string.play_15 ->
                fragTransaction
                    .replace(R.id.container, Game15Fragment.newInstance(), "Play Fifteen")
                    .commit()
            else -> throw RuntimeException(
                String.format("Unknown fragment name resource id: 0x%08x", selectedGame )
            )
        }
    }
}

