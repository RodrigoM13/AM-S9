package com.example.app_s9

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.google.android.material.bottomnavigation.BottomNavigationView

class PreferencesActivity : AppCompatActivity() {

    private lateinit var buttonDarkMode: Button
    private lateinit var buttonLightMode: Button
    private lateinit var bottomNavigation: BottomNavigationView
    private lateinit var sharedPreferencesHelper: SharedPreferencesHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_preferences)

        sharedPreferencesHelper = SharedPreferencesHelper(this)

        buttonDarkMode = findViewById(R.id.buttonDarkMode)
        buttonLightMode = findViewById(R.id.buttonLightMode)
        bottomNavigation = findViewById(R.id.bottomNavigationView)

        buttonDarkMode.setOnClickListener {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            sharedPreferencesHelper.saveInt(SharedPreferencesHelper.KEY_THEME_MODE, AppCompatDelegate.MODE_NIGHT_YES)
            recreate()
        }

        buttonLightMode.setOnClickListener {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            sharedPreferencesHelper.saveInt(SharedPreferencesHelper.KEY_THEME_MODE, AppCompatDelegate.MODE_NIGHT_NO)
            recreate()
        }

        bottomNavigation.selectedItemId = R.id.nav_preferences
        setupBottomNav()
    }

    private fun setupBottomNav() {
        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                    true
                }
                R.id.nav_users -> {
                    startActivity(Intent(this, UserActivity::class.java))
                    finish()
                    true
                }
                R.id.nav_preferences -> true
                else -> false
            }
        }
    }
}
