package com.androidstudioprojects.parseinstagram2

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.parse.ParseObject
import com.parse.ParseUser
import kotlin.math.sign


class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        /*val firstObject = ParseObject("FirstClass")
        firstObject.put("message","Hey ! First message from android. Parse is now connected")
        firstObject.saveInBackground {
            if (it != null){
                it.localizedMessage?.let { message -> Log.e("MainActivity", message) }
            }else{
                Log.d("MainActivity","Object saved.")
            }
        }*/

        if (ParseUser.getCurrentUser() != null){
            goToMainActivity()
        }

        findViewById<Button>(R.id.btnLogin).setOnClickListener{
            val username = findViewById<EditText>(R.id.etUsername).text.toString()
            val password = findViewById<EditText>(R.id.etPassword).text.toString()
            loginUser(username,password)
        }

        findViewById<Button>(R.id.btnSignUp).setOnClickListener{
            val username = findViewById<EditText>(R.id.etUsername).text.toString()
            val password = findViewById<EditText>(R.id.etPassword).text.toString()
            signUpuser(username,password)
        }
    }

    private fun signUpuser(username: String, password: String){
        val user = ParseUser()
        user.setUsername(username)
        user.setPassword(password)

        user.signUpInBackground { e ->
            if (e == null) {
                Toast.makeText(this, "Sign up successful!", Toast.LENGTH_SHORT)
                goToMainActivity()
            } else {
                Toast.makeText(this, "Sign up unsuccessful. Try again later.", Toast.LENGTH_SHORT)
                e.printStackTrace()
            }
        }
    }

    private fun loginUser(username: String, password: String) {
        ParseUser.logInInBackground(username, password, ({ user, e ->
            if (user != null) {
                Log.i(TAG, "login successful")
                goToMainActivity()
            } else {
                e.printStackTrace()
                Toast.makeText(this, "error logging in", Toast.LENGTH_SHORT).show()
            }})
        )
    }

    private fun goToMainActivity(){
        val intent = Intent(this@LoginActivity, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    companion object{
        const val TAG = "LoginActivity"
    }
}
