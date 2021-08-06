package com.vladgroshkov.automatedlibrary.utils

import android.view.View
import android.view.Window
import android.view.WindowManager
import com.github.ybq.android.spinkit.SpinKitView


class LoadingUtil {
    companion object {
        fun hideLoadingAction(window: Window, spinKitView: SpinKitView) {
            spinKitView.visibility = View.GONE
            window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        }

        fun showLoadingAction(window: Window, spinKitView: SpinKitView) {
            spinKitView.visibility = View.VISIBLE
            window.setFlags(
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
            )
        }
    }
}