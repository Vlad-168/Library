package com.vladgroshkov.automatedlibrary

import AlertCustomDialog
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_login.*
import java.util.regex.Pattern

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var email: String
    private lateinit var pass: String
    private lateinit var customDialog: AlertCustomDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        auth = Firebase.auth
        customDialog = AlertCustomDialog(this)


        showPassCheckBox.setOnCheckedChangeListener { btn, isChecked ->
            Log.d(TAG, isChecked.toString())
            if (isChecked) {
                passEditText.transformationMethod = HideReturnsTransformationMethod.getInstance()
            } else {
                passEditText.transformationMethod = PasswordTransformationMethod.getInstance()
            }
        }

        loginButton.setOnClickListener {
            email = emailEditText.text.toString()
            pass = passEditText.text.toString()
            if (email.isEmpty() || !checkEmail(email)) {
                customDialog.showErrorDialog(getString(R.string.login_email_error))
            } else if (pass.isEmpty() || pass.length < 8 ) {
                customDialog.showErrorDialog(getString(R.string.login_pass_error))
            } else {
                signIn(email, pass)
            }

        }
    }

    override fun onStart() {
        super.onStart()

        var currentUser = auth.currentUser
        if (currentUser != null) {
            reload()
        }


    }

    private fun signIn(email: String, password: String) {

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "signInWithEmail:success")
                    val user = auth.currentUser
                    updateUI(user)
                } else {
                    Log.w(TAG, "signInWithEmail:failure", task.exception)
                    customDialog.showErrorDialog(getString(R.string.login_auth_error))
                    updateUI(null)
                }
            }
    }

    private fun updateUI(user: FirebaseUser?) {
        if (user != null) {
            var mainIntent = Intent(this, MainActivity::class.java)
            mainIntent.putExtra("user", user)
            startActivity(mainIntent)
        }
    }

    private fun reload() {

    }

    private fun checkEmail(email: String): Boolean {
        val p = Pattern.compile("^[\\w]{1}[\\w-\\.]*@[\\w-]+\\.[a-z]{2,4}$")
        val m = p.matcher(email)
        return m.matches()
    }

    companion object {
        private const val TAG = "LoginActivity"
    }
}
