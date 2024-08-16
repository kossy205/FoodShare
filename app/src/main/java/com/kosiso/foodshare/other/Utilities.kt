package com.kosiso.foodshare.other

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.text.format.DateUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.webkit.MimeTypeMap
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.Timestamp
import com.kosiso.foodshare.R
import com.kosiso.foodshare.databinding.CustomLoadingDialogBinding
import pub.devrel.easypermissions.EasyPermissions
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object Utilities {

    private var customDialog: Dialog? = null

    fun hasLocationPermissions(context: Context): Boolean {

        // Check if fine and coarse location permissions are not granted
        val fineLocationPermissionGranted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val coarseLocationPermissionGranted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val backGroundLocationPermissionGranted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_BACKGROUND_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        //ACCESS_BACKGROUND_LOCATION permission doesn't need to be requested for android versions below Q
        return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            (fineLocationPermissionGranted && coarseLocationPermissionGranted)
        } else {
            (fineLocationPermissionGranted && coarseLocationPermissionGranted && backGroundLocationPermissionGranted)
        }
    }



    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun hasImagePermissions(context: Context):Boolean{

        val permissions = when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE -> arrayOf(
                Manifest.permission.READ_MEDIA_IMAGES,
                Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED
            )
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> arrayOf(
                Manifest.permission.READ_MEDIA_IMAGES
            )
            else -> arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
        }

        val pendingPermissions = permissions.filter {
            ContextCompat.checkSelfPermission(context, it) != PackageManager.PERMISSION_GRANTED
        }.toTypedArray()

        return if (pendingPermissions.isEmpty()) {
            // All permissions already granted
            Log.i("img has permissions?", "permissions present, can open gallery")

            true
        } else {
            // Permissions need to be requested
            Log.i("img has permissions?", "no permission")

            false
        }
    }



    fun showAlertDialog(context: Context, title:String, message:String, positiveAction:()->Unit, negativeAction:()->Unit ){
        AlertDialog.Builder(context)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("Ok"){_,_->
                positiveAction()
            }
            .setNegativeButton("Cancel"){dialog, _ ->
                negativeAction()
                dialog.dismiss()
            }
            .show()
    }


    fun showCustomDialog(context: Context, message: String): Dialog{

        customDialog?.dismiss()
        customDialog = Dialog(context).apply {
            setContentView(R.layout.custom_loading_dialog)
            setCanceledOnTouchOutside(false)
            val tvCustomDialog: TextView = findViewById(R.id.tv_custom_dialog)
            tvCustomDialog.text = message
            show()
        }
        return customDialog as Dialog
    }

    fun dismissCustomDialog() {
        customDialog?.dismiss()
        customDialog = null
    }

    fun formatTimeAgo(timestamp: Timestamp): String {
        val now = Date()
        val createdAt = timestamp.toDate()

        // Use DateUtils to format the duration
        return DateUtils.getRelativeTimeSpanString(createdAt.time, now.time, DateUtils.MINUTE_IN_MILLIS).toString()
    }

    fun getDayOfMonthSuffix(n: Int): String {
        if (n in 11..13) {
            return "th"
        }
        return when (n % 10) {
            1 -> "st"
            2 -> "nd"
            3 -> "rd"
            else -> "th"
        }
    }

    fun formatDate(timestamp: Timestamp): String {
        val date = timestamp.toDate()

        val dayOfMonth = SimpleDateFormat("d", Locale.getDefault()).format(date)
        val month = SimpleDateFormat("MMM", Locale.getDefault()).format(date)
        val year = SimpleDateFormat("yyyy", Locale.getDefault()).format(date)

        val suffix = getDayOfMonthSuffix(dayOfMonth.toInt())

        return "${dayOfMonth}${suffix} of ${month}, ${year}"
    }

    fun showErrorSnackBar(message: String, view: View, context: Context) {
        val snackBar =
            Snackbar.make(view, message, Snackbar.LENGTH_LONG)
        val snackBarView = snackBar.view
        snackBarView.setBackgroundColor(
            ContextCompat.getColor(
                context,
                R.color.red
            )
        )
        snackBar.show()
    }

    fun showNormalSnackBar(message: String, view: View, context: Context) {
        val snackBar =
            Snackbar.make(view, message, Snackbar.LENGTH_LONG)
        snackBar.show()
    }

    //A function to get the extension of selected image.
    fun getFileExtension(activity: Activity, uri: Uri?): String? {

        return MimeTypeMap.getSingleton()
            .getExtensionFromMimeType(activity.contentResolver.getType(uri!!))
    }
}