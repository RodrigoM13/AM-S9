package com.example.app_s9

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class UserActivity : AppCompatActivity() {

    private lateinit var nameInput: EditText
    private lateinit var ageInput: EditText
    private lateinit var emailInput: EditText
    private lateinit var saveButton: Button
    private lateinit var loadButton: Button
    private lateinit var textViewUsers: TextView
    private lateinit var bottomNavigation: BottomNavigationView

    private lateinit var sharedPreferencesHelper: SharedPreferencesHelper

    data class User(val name: String, val age: Int, val email: String)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user)

        sharedPreferencesHelper = SharedPreferencesHelper(this)

        nameInput = findViewById(R.id.editTextName)
        ageInput = findViewById(R.id.editTextAge)
        emailInput = findViewById(R.id.editTextEmail)
        saveButton = findViewById(R.id.buttonSaveUser)
        loadButton = findViewById(R.id.buttonLoadUsers)
        textViewUsers = findViewById(R.id.textViewUserList)
        bottomNavigation = findViewById(R.id.bottomNavigationView)

        bottomNavigation.selectedItemId = R.id.nav_users
        setupBottomNav()

        saveButton.setOnClickListener {
            saveUser()
        }

        loadButton.setOnClickListener {
            loadUsers()
        }
    }

    private fun setupBottomNav() {
        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                    true
                }
                R.id.nav_users -> true
                R.id.nav_preferences -> {
                    startActivity(Intent(this, PreferencesActivity::class.java))
                    finish()
                    true
                }
                else -> false
            }
        }
    }

    private fun saveUser() {
        val name = nameInput.text.toString().trim()
        val age = ageInput.text.toString().toIntOrNull()
        val email = emailInput.text.toString().trim()

        if (name.isEmpty() || age == null || email.isEmpty()) {
            Toast.makeText(this, "Completa todos los campos correctamente", Toast.LENGTH_SHORT).show()
            return
        }

        val user = User(name, age, email)
        val userList = getUserList().toMutableList()
        userList.add(user)

        val json = Gson().toJson(userList)
        sharedPreferencesHelper.saveString("user_list", json)

        nameInput.text.clear()
        ageInput.text.clear()
        emailInput.text.clear()

        Toast.makeText(this, "Usuario guardado", Toast.LENGTH_SHORT).show()
    }

    private fun loadUsers() {
        val userList = getUserList()
        if (userList.isEmpty()) {
            textViewUsers.text = "No hay usuarios registrados"
        } else {
            textViewUsers.text = userList.joinToString("\n\n") { user ->
                "Nombre: ${user.name}\nEdad: ${user.age}\nEmail: ${user.email}"
            }
        }
    }

    private fun getUserList(): List<User> {
        val json = sharedPreferencesHelper.getString("user_list", "")
        if (json.isEmpty()) return emptyList()
        val type = object : TypeToken<List<User>>() {}.type
        return Gson().fromJson(json, type)
    }
}
