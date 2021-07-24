package com.vladgroshkov.automatedlibrary.utils

import java.util.regex.Pattern

class ValidationUtil {

    companion object {
        fun isEmailValid(email: String): Boolean {
            val p = Pattern.compile("^[\\w]{1}[\\w-\\.]*@[\\w-]+\\.[a-z]{2,4}$")
            val m = p.matcher(email)
            return m.matches()
        }


        fun isPasswordValid(pass: String): Boolean {
            return pass.length > 7 && pass.isNotEmpty()
        }

        fun isPasswordsValid(pass: String, pass2: String): Boolean {
            return pass == pass2
        }
    }

}