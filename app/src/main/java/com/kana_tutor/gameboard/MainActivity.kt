package com.kana_tutor.gameboard

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
    // Menu item selected listener.
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        var rv = true
        when (item.itemId) {
            R.id.build_info_item -> displayAboutInfo()
            // If item isn't for this menu, you must call the super or
            // other things that must happen (eg: up-button in onSupportNavigateUp)
            // won't happen.
            else -> rv = super.onOptionsItemSelected(item);        }
        return rv
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.frag_container)



        supportFragmentManager
            .beginTransaction()
            .add(R.id.container, Game2048Fragment.newInstance(), "Play 2048")
            .commit()
    }
}

