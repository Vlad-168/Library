package com.vladgroshkov.automatedlibrary

import AlertCustomDialog
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import androidx.annotation.RequiresApi
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.vladgroshkov.automatedlibrary.utils.LoadingUtil
import com.vladgroshkov.automatedlibrary.utils.ValidationUtil
import kotlinx.android.synthetic.main.activity_registration.*

class RegistrationActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var email: String
    private lateinit var pass: String
    private lateinit var pass2: String
    private lateinit var customDialog: AlertCustomDialog
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        auth = Firebase.auth
        customDialog = AlertCustomDialog(this)


        showPassCheckBox_Reg.setOnCheckedChangeListener { btn, isChecked ->
            if (isChecked) {
                passEditText_Reg.transformationMethod = HideReturnsTransformationMethod.getInstance()
            } else {
                passEditText_Reg.transformationMethod = PasswordTransformationMethod.getInstance()
            }
        }

        showPassCheckBox2_Reg.setOnCheckedChangeListener { btn, isChecked ->
            if (isChecked) {
                passEditText2_Reg.transformationMethod = HideReturnsTransformationMethod.getInstance()
            } else {
                passEditText2_Reg.transformationMethod = PasswordTransformationMethod.getInstance()
            }
        }

        registerButton_Reg.setOnClickListener {
            email = emailEditText_Reg.text.toString()
            pass = passEditText_Reg.text.toString()
            pass2 = passEditText2_Reg.text.toString()
            if (!ValidationUtil.isEmailValid(email)) {
                customDialog.showErrorDialog(getString(R.string.login_email_error))
            } else if (!ValidationUtil.isPasswordValid(pass) || !ValidationUtil.isPasswordValid(pass2)) {
                customDialog.showErrorDialog(getString(R.string.login_pass_error))
            } else if (!ValidationUtil.isPasswordsValid(pass, pass2)) {
                customDialog.showErrorDialog(getString(R.string.login_pass_equals_error))
            } else {
                LoadingUtil.showLoadingAction(window, loadingSpinKit_Reg)
                createAccount(email, pass)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun createAccount(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "createUserWithEmail:success")
                    val user = auth.currentUser
                    sendEmailVerification()
                } else {
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    customDialog.showErrorDialog(getString(R.string.registration_error))
                    updateUI(null)
                }
            }
    }

    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        if (currentUser != null) {
            reload()
        }
    }

    private fun updateUI(user: FirebaseUser?) {
        LoadingUtil.hideLoadingAction(window, loadingSpinKit_Reg)
        if (user != null) {
            var mainIntent = Intent(this, MainActivity::class.java)
            mainIntent.putExtra("user", user)
            startActivity(mainIntent)
        }
    }

    private fun reload() {

    }


    override fun onBackPressed() {
        startActivity(Intent(this, RegistrationActivity::class.java))
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun sendEmailVerification() {
        val user = auth.currentUser!!
        user.sendEmailVerification()
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful()) {
                    customDialog.showInfoDialog(getString(R.string.registration_sent_email_success),
                    getDrawable(R.drawable.splash_image))
                    LoadingUtil.showLoadingAction(window, loadingSpinKit_Reg)
                    val timer = object: CountDownTimer(600000L, 1000) {
                        override fun onTick(millisUntilFinished: Long) {
                            user.reload()
                            if (user.isEmailVerified) {
                                updateUI(user)
                                cancel()
                            }
                        }
                        override fun onFinish() {
                            customDialog.showErrorDialog(getString(R.string.registration_timeout_error))
                        }
                    }
                    timer.start()
                } else {
                    customDialog.showErrorDialog(getString(R.string.login_email_error))
                }
            }
    }

    companion object {
        private const val TAG = "RegistrationActivity"
    }
}