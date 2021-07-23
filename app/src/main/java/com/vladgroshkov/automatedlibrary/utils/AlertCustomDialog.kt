import android.R
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.provider.Settings.Global.getString

class AlertCustomDialog(context: Context) : AlertDialog(context) {
    fun showErrorDialog(message: String?) {
        Builder(context)
            .setIcon(R.drawable.presence_busy)
            .setTitle("Ошибка!")
            .setMessage(message)
            .setNegativeButton("Закрыть") {
                    dialogInterface: DialogInterface, i: Int -> dialogInterface.cancel()
            }
            .show()
    }
}