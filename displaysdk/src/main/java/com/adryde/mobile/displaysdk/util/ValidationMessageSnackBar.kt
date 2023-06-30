import android.graphics.Color
import android.view.Gravity
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.ActionBar
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar

/**
 * This is ValidationMessageSnackBar in this object has support reusable methods to generate
 * SnackBar with related to Error Warning or Success
 *
 * @author Abhishek Mallick
 */
object ValidationMessageSnackBar {

    /**
     * Reusable method to generate Error Message SnackBar
     *
     * @author Abhishek Mallick
     * @param view
     * @param msg
     */
    fun generateErrorMsg(view: View, msg: String) {

        val colorHex = "#DD2121"

        val snackBar = Snackbar.make(view, msg, Snackbar.LENGTH_SHORT)
            .setBackgroundTint(Color.parseColor(colorHex)).setTextColor(Color.WHITE)
            .apply {
                this.view.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)
                    ?.apply {
                        maxLines = 10
                        isSingleLine = false
                    }
            }
            .setDuration(5000)

        val layoutParams = ActionBar.LayoutParams(snackBar.view.layoutParams)

        layoutParams.gravity = Gravity.TOP
        layoutParams.topMargin = 50
        layoutParams.leftMargin = 20
        layoutParams.rightMargin = 20
        snackBar.view.setPadding(0, 0, 0, 0)
        snackBar.view.layoutParams = layoutParams
        snackBar.animationMode = BaseTransientBottomBar.ANIMATION_MODE_FADE


        snackBar.show()
    }

    /**
     * Reusable method to generate Warning Message SnackBar
     *
     * @author Abhishek Mallick
     * @param view
     * @param msg
     */
    fun generateWarningMsg(view: View, msg: String) {

        val colorHex = "#C98C16"

        val snackBar = Snackbar.make(view, msg, Snackbar.LENGTH_SHORT)
            .setBackgroundTint(Color.parseColor(colorHex)).setTextColor(Color.WHITE)
            .apply {
                this.view.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)
                    ?.apply {
                        maxLines = 10
                        isSingleLine = false
                    }
            }
            .setDuration(5000)

        val layoutParams = ActionBar.LayoutParams(snackBar.view.layoutParams)

        layoutParams.gravity = Gravity.TOP
        layoutParams.topMargin = 50
        layoutParams.leftMargin = 20
        layoutParams.rightMargin = 20
        snackBar.view.setPadding(0, 0, 0, 0)
        snackBar.view.layoutParams = layoutParams
        snackBar.animationMode = BaseTransientBottomBar.ANIMATION_MODE_FADE


        snackBar.show()
    }

    /**
     * Reusable method to generate Success Message SnackBar
     *
     * @author Abhishek Mallick
     * @param view
     * @param msg
     */
    fun generateSuccessMsg(view: View, msg: String) {

        val colorHex = "#44C653"

        val snackBar = Snackbar.make(view, msg, Snackbar.LENGTH_SHORT)
            .setBackgroundTint(Color.parseColor(colorHex)).setTextColor(Color.WHITE)
            .apply {
                this.view.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)
                    ?.apply {
                        maxLines = 10
                        isSingleLine = false
                    }
            }
            .setDuration(5000)

        val layoutParams = ActionBar.LayoutParams(snackBar.view.layoutParams)

        layoutParams.gravity = Gravity.TOP
        layoutParams.leftMargin = 20
        layoutParams.rightMargin = 20
        layoutParams.topMargin = 50

        snackBar.view.setPadding(0, 0, 0, 0)
        snackBar.view.layoutParams = layoutParams
        snackBar.animationMode = BaseTransientBottomBar.ANIMATION_MODE_FADE


        snackBar.show()
    }
}