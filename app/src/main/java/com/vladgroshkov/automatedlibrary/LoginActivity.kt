package com.vladgroshkov.automatedlibrary

import AlertCustomDialog
import android.content.Intent
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.github.ybq.android.spinkit.SpinKitView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.vladgroshkov.automatedlibrary.utils.LoadingUtil.Companion.hideLoadingAction
import com.vladgroshkov.automatedlibrary.utils.LoadingUtil.Companion.showLoadingAction
import com.vladgroshkov.automatedlibrary.utils.ValidationUtil

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var email: String
    private lateinit var pass: String
    private lateinit var customDialog: AlertCustomDialog

    private lateinit var showPassCheckBox: CheckBox
    private lateinit var passEditText: EditText
    private lateinit var loginButton: Button
    private lateinit var emailEditText: EditText
    private lateinit var loginRegisterButton: Button
    private lateinit var loadingSpinKit: SpinKitView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        auth = Firebase.auth
        customDialog = AlertCustomDialog(this)
        if (auth.currentUser != null) {
            startActivity(Intent(this@LoginActivity, MainActivity::class.java))
        }

        showPassCheckBox = findViewById(R.id.showPassCheckBox)
        passEditText = findViewById(R.id.passEditText)
        loginButton = findViewById(R.id.loginButton)
        emailEditText = findViewById(R.id.emailEditText)
        loginRegisterButton = findViewById(R.id.loginRegisterButton)
        loadingSpinKit = findViewById(R.id.loadingSpinKit)

        showPassCheckBox.setOnCheckedChangeListener { btn, isChecked ->
            Log.d(TAG, isChecked.toString())
            if (isChecked) {
                passEditText.transformationMethod =
                    HideReturnsTransformationMethod.getInstance()
            } else {
                passEditText.transformationMethod =
                    PasswordTransformationMethod.getInstance()
            }
        }

        loginButton.setOnClickListener {
            email = emailEditText.text.toString()
            pass = passEditText.text.toString()
            if (!ValidationUtil.isEmailValid(email)) {
                customDialog.showErrorDialog(getString(R.string.login_email_error))
            } else if (!ValidationUtil.isPasswordValid(pass)) {
                customDialog.showErrorDialog(getString(R.string.login_pass_error))
            } else {
                showLoadingAction(window, loadingSpinKit)
                signIn(email, pass)
            }

        }

        loginRegisterButton.setOnClickListener {
            startActivity(Intent(this, RegistrationActivity::class.java))
        }
    }

    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
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
        hideLoadingAction(window, loadingSpinKit)
        if (user != null) {
            var mainIntent = Intent(this, MainActivity::class.java)
            mainIntent.putExtra("user", user)
            startActivity(mainIntent)
        }
    }

    private fun reload() {

    }

    override fun onBackPressed() {
        startActivity(Intent(this, LoginActivity::class.java))
    }

    companion object {
        private const val TAG = "LoginActivity"
    }
}
