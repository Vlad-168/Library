import android.R
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.drawable.Drawable
import androidx.fragment.app.Fragment
import com.vladgroshkov.automatedlibrary.MainActivity

class AlertCustomDialog(context: Context) : AlertDialog(context) {
    fun showErrorDialog(message: String?) {
        Builder(context)
            .setIcon(R.drawable.presence_busy)
            .setTitle("Ошибка!")
            .setMessage(message)
            .setNegativeButton("Закрыть") { dialogInterface: DialogInterface, i: Int ->
                dialogInterface.cancel()
            }
            .show()
    }

    fun showInfoDialog(message: String?, iconDrawable: Drawable?, fragment: Fragment?, activity: Activity, isToFragment: Boolean) {
        Builder(context)
            .setIcon(iconDrawable)
            .setTitle("Информация")
            .setMessage(message)
            .setNegativeButton("Закрыть") { dialogInterface: DialogInterface, i: Int ->
                if (isToFragment) {
                    changeToFragment(fragment!!, activity)
                } else {
                    changeToActivity(fragment!!, activity)
                }
            }
            .show()
    }

    fun showInfoDialogWithoutAction(message: String?, iconDrawable: Drawable?) {
        Builder(context)
            .setIcon(iconDrawable)
            .setTitle("Информация")
            .setMessage(message)
            .setNegativeButton("Закрыть") { dialogInterface: DialogInterface, i: Int ->
                dialogInterface.cancel()
            }
            .show()
    }

    private fun changeToFragment(fragment: Fragment, activity: Activity) {
        (activity as MainActivity).replaceFragment(fragment, fragment::class.java.simpleName)
    }

    private fun changeToActivity(fragment: Fragment, activity: Activity) {
        context.startActivity(Intent(context, activity::class.java))
    }
}