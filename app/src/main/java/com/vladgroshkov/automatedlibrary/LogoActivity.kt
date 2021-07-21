package com.vladgroshkov.automatedlibrary

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.transition.Transition
import android.view.animation.Animation
import android.widget.ImageView
import kotlinx.android.synthetic.main.activity_logo.*

class LogoActivity : AppCompatActivity() {
    private var transition: Transition? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_logo)
        transition = intent.getParcelableExtra(TRANSITION_ANIMATION)
    }

    override fun finish() {
        transition?.let {
            transition = null
            supportFinishAfterTransition().also { overridePendingTransition(0, R.anim.fade_out_logo_activity) }
        } ?: run {
            super.finish()
        }
    }

    companion object {
        const val TRANSITION_ANIMATION = "TRANSITION_ANIMATION"
    }
}
