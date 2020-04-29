package com.kTs.movieobserver

import android.content.Context
import android.content.res.Configuration
import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.kTs.movieobserver.utils.LocaleHelper
import kotlinx.android.synthetic.main.movieapp_activity.*


class MovieAppActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.movieapp_activity)

        toolbarSetup()

        val navController = this.findNavController(R.id.nav_host_fragment)
        NavigationUI.setupActionBarWithNavController(this, navController)

        setupBottomNavigation()
    }

    private fun setupBottomNavigation() {
        NavigationUI.setupWithNavController(
            bottom_navigation,
            (nav_host_fragment as NavHostFragment).navController
        )
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = this.findNavController(R.id.nav_host_fragment)
        return navController.navigateUp()
    }

    override fun attachBaseContext(base: Context?) {
        base?.let {
            super.attachBaseContext(LocaleHelper(it).updateBaseContextLocale())
        }
    }

    override fun applyOverrideConfiguration(overrideConfiguration: Configuration) {
        val uiMode = overrideConfiguration.uiMode
        overrideConfiguration.setTo(baseContext.resources.configuration)
        overrideConfiguration.uiMode = uiMode
        super.applyOverrideConfiguration(overrideConfiguration)
    }

    private fun toolbarSetup() {
        setSupportActionBar(movie_toolbar)
        val upArrow: Drawable? =
            ContextCompat.getDrawable(applicationContext, R.drawable.ic_arrow_back)
        upArrow?.colorFilter = BlendModeColorFilterCompat.createBlendModeColorFilterCompat(
            ContextCompat.getColor(
                applicationContext,
                R.color.colorSecondaryDark
            ), BlendModeCompat.SRC_ATOP
        )
        actionBar?.setHomeAsUpIndicator(upArrow)

        supportActionBar
    }


}
