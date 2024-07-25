package com.kosiso.foodshare.other

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.util.Log
import android.view.View
import android.webkit.MimeTypeMap
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.kosiso.foodshare.R
import pub.devrel.easypermissions.EasyPermissions

object Utilities {



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


//        val readExternalStoragePermissionGranted = ContextCompat.checkSelfPermission(
//            context,
//            Manifest.permission.READ_EXTERNAL_STORAGE
//        ) == PackageManager.PERMISSION_GRANTED
//        val readMediaImagesPermissionGranted = ContextCompat.checkSelfPermission(
//            context,
//            Manifest.permission.READ_MEDIA_IMAGES
//        ) == PackageManager.PERMISSION_GRANTED
//
//
//        return if(Build.VERSION.SDK_INT < Build.VERSION_CODES.Q){
//            (readExternalStoragePermissionGranted && readMediaImagesPermissionGranted )
//        }else{
//            (readExternalStoragePermissionGranted)
//        }
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