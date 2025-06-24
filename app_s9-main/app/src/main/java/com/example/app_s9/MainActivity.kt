package com.example.app_s9

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatDelegate

class MainActivity : AppCompatActivity() {

    private lateinit var sharedPreferencesHelper: SharedPreferencesHelper
    private lateinit var editTextUsername: EditText
    private lateinit var buttonSave: Button
    private lateinit var buttonLoad: Button
    private lateinit var buttonClear: Button
    private lateinit var buttonResetCounter: Button
    private lateinit var textViewResult: TextView
    private lateinit var bottomNavigation: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        sharedPreferencesHelper = SharedPreferencesHelper(this)
        applySavedTheme()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initViews()
        setupListeners()
        setupBottomNav()

        increaseOpenCounter()
        showOpenCounter()
    }

    private fun applySavedTheme() {
        val savedMode = sharedPreferencesHelper.getInt(SharedPreferencesHelper.KEY_THEME_MODE, AppCompatDelegate.MODE_NIGHT_NO)
        AppCompatDelegate.setDefaultNightMode(savedMode)
    }

    private fun initViews() {
        editTextUsername = findViewById(R.id.editTextUsername)
        buttonSave = findViewById(R.id.buttonSave)
        buttonLoad = findViewById(R.id.buttonLoad)
        buttonClear = findViewById(R.id.buttonClear)
        buttonResetCounter = findViewById(R.id.buttonResetCounter)
        textViewResult = findViewById(R.id.textViewResult)
        bottomNavigation = findViewById(R.id.bottomNavigationView)
    }

    private fun setupListeners() {
        buttonSave.setOnClickListener {
            saveData()
        }

        buttonLoad.setOnClickListener {
            loadData()
        }

        buttonClear.setOnClickListener {
            clearAllData()
        }

        buttonResetCounter.setOnClickListener {
            sharedPreferencesHelper.saveInt(SharedPreferencesHelper.KEY_APP_OPEN_COUNT, 0)
            Toast.makeText(this, "Contador reiniciado", Toast.LENGTH_SHORT).show()
            showOpenCounter()
        }
    }

    private fun setupBottomNav() {
        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> true
                R.id.nav_users -> {
                    startActivity(Intent(this, UserActivity::class.java))
                    true
                }
                R.id.nav_preferences -> {
                    startActivity(Intent(this, PreferencesActivity::class.java))
                    true
                }
                else -> false
            }
        }
        bottomNavigation.selectedItemId = R.id.nav_home
    }

    private fun saveData() {
        val username = editTextUsername.text.toString().trim()
        if (username.isEmpty()) {
            Toast.makeText(this, "Por favor ingresa un nombre", Toast.LENGTH_SHORT).show()
            return
        }
        sharedPreferencesHelper.saveString(SharedPreferencesHelper.KEY_USERNAME, username)
        sharedPreferencesHelper.saveInt(SharedPreferencesHelper.KEY_USER_ID, (1000..9999).random())
        Toast.makeText(this, "Datos guardados exitosamente", Toast.LENGTH_SHORT).show()
        editTextUsername.setText("")
    }

    private fun loadData() {
        val username = sharedPreferencesHelper.getString(SharedPreferencesHelper.KEY_USERNAME, "Sin nombre")
        val userId = sharedPreferencesHelper.getInt(SharedPreferencesHelper.KEY_USER_ID, 0)
        val openCount = sharedPreferencesHelper.getInt(SharedPreferencesHelper.KEY_APP_OPEN_COUNT, 0)

        val result = "Usuario: $username\nID: $userId\nVeces abierta la app: $openCount"
        textViewResult.text = result
    }

    private fun clearAllData() {
        sharedPreferencesHelper.clearAll()
        textViewResult.text = ""
        editTextUsername.setText("")
        Toast.makeText(this, "Todas las preferencias han sido eliminadas", Toast.LENGTH_SHORT).show()
    }

    private fun increaseOpenCounter() {
        val alreadyCounted = sharedPreferencesHelper.getBoolean(SharedPreferencesHelper.KEY_COUNTED_THIS_SESSION, false)
        if (alreadyCounted) return

        val currentCount = sharedPreferencesHelper.getInt(SharedPreferencesHelper.KEY_APP_OPEN_COUNT, 0)
        val newCount = currentCount + 1
        sharedPreferencesHelper.saveInt(SharedPreferencesHelper.KEY_APP_OPEN_COUNT, newCount)
        sharedPreferencesHelper.saveBoolean(SharedPreferencesHelper.KEY_COUNTED_THIS_SESSION, true)

        if (newCount == 1) {
            Toast.makeText(this, "¡Bienvenido por primera vez!", Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(this, "App abierta $newCount veces", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showOpenCounter() {
        val openCount = sharedPreferencesHelper.getInt(SharedPreferencesHelper.KEY_APP_OPEN_COUNT, 0)
        val currentText = textViewResult.text.toString()
        val counterText = "Veces abierta la app: $openCount"
        val updatedText = if (currentText.contains("Veces abierta la app:")) {
            currentText.replace(Regex("Veces abierta la app: \\d+"), counterText)
        } else {
            if (currentText.isNotEmpty()) "$currentText\n$counterText" else counterText
        }
        textViewResult.text = updatedText
    }

    override fun onDestroy() {
        super.onDestroy()
        if (isFinishing) {
            sharedPreferencesHelper.remove(SharedPreferencesHelper.KEY_COUNTED_THIS_SESSION)
        }
    }
}
