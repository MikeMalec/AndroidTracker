package com.example.tracker.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.tracker.R
import com.example.tracker.service.TrackingService
import com.example.tracker.utils.ACTION_SHOW_MAP_FRAGMENT
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_Tracker)
        setContentView(R.layout.activity_main)
        setNavigation()
        checkIfTracking()
    }

    private fun setNavigation() {
        val appBarConfiguration = AppBarConfiguration(setOf(R.id.startFragment))
        setupActionBarWithNavController(getNavController(), appBarConfiguration)
    }

    override fun onSupportNavigateUp(): Boolean {
        return getNavController().navigateUp() || super.onSupportNavigateUp()
    }

    override fun onStart() {
        super.onStart()
        dispatchIntent()
        setTheme(R.style.Theme_Tracker)
    }

    private fun checkIfTracking() {
        TrackingService.isTracking.value?.also {
            if (it) {
                navigateToMapFragment()
            }
        }
    }

    private fun dispatchIntent() {
        intent?.also {
            if (it.action == ACTION_SHOW_MAP_FRAGMENT) {
                navigateToMapFragment()
            }
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        intent?.also {
            if (intent.action == ACTION_SHOW_MAP_FRAGMENT) {
                navigateToMapFragment()
            }
        }
    }

    private fun navigateToMapFragment() {
        getNavController()
            .navigate(
                R.id.mapFragment
            )
    }

    private fun getNavController(): NavController {
        return (supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment).navController
    }
}