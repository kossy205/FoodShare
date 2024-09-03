package com.kosiso.foodshare.ui.fragments.volunteer

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.google.android.gms.maps.GoogleMap
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.kosiso.foodshare.ForeGroundService
import com.kosiso.foodshare.R
import com.kosiso.foodshare.databinding.FragmentUpdatesBinding
import com.kosiso.foodshare.models.DeliveryRequest
import com.kosiso.foodshare.other.Constants
import com.kosiso.foodshare.other.Utilities
import com.kosiso.foodshare.ui.viewmodels.volunteer.UpdatesViewModel
import dagger.hilt.android.AndroidEntryPoint
import pub.devrel.easypermissions.AppSettingsDialog

@AndroidEntryPoint
class UpdatesFragment : Fragment() {

    private lateinit var binding: FragmentUpdatesBinding
    private var map: GoogleMap? = null
    private lateinit var bottomSheetView: View
    // this way of initializing a viewmodel is actually when dagger-hilt is used.
    // It does it automatically and u dont ned to initialize it manually
    // it initializes the viewmodel ones this fragment starts, which means codes in the init block would start being executed.
    //but u have to first be observing something from the viewmodel
    private val updatesViewModel: UpdatesViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentUpdatesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bottomSheetView = layoutInflater.inflate(R.layout.bottom_sheet_layout, null)

        binding.apply {
            mapView.onCreate(savedInstanceState)
            binding.mapView.getMapAsync {
                map = it
            }

            btnOnline.setOnClickListener {
                Log.i("service","start service")
                requestPermission()
                binding.btnOnline.visibility = View.GONE
                binding.btnOffline.visibility = View.VISIBLE
            }
            btnOffline.setOnClickListener {
                Log.i("service","stop service")
                sendCommandToService(Constants.ACTION_STOP)
                binding.btnOffline.visibility = View.GONE
                binding.btnOnline.visibility = View.VISIBLE
            }
            btnFinishDelivery.setOnClickListener {

            }
        }

        //bottom sheet button listeners
        bottomSheetView.findViewById<Button>(R.id.idBtnAccept).setOnClickListener {
            updatesViewModel.changeVolunteerAcceptStatus(Constants.TRUE)
        }
        bottomSheetView.findViewById<TextView>(R.id.idBtnDismiss).setOnClickListener {
            //once the below is successful, we then return everything back to normal
            // checkout its onSuccessListener in viewmodel
            updatesViewModel.changeVolunteerAcceptStatus(Constants.FALSE)
        }

        //
        updatesViewModel.apply {
            deliveryRequestDetails.observe(viewLifecycleOwner, Observer {result->
                result.apply {
                    onSuccess{deliveryRequestDetails->
                        showBottomSheet(deliveryRequestDetails)
                    }
                    onFailure {e->
                        Utilities.showNormalSnackBar(e.message!!, requireView(), requireContext())
                    }
                }
            })


        }
    }

    private fun showBottomSheet(deliveryRequestDetails: DeliveryRequest){
        val dialog = BottomSheetDialog(requireContext())
        val btnClose = bottomSheetView.findViewById<TextView>(R.id.idBtnDismiss)
        btnClose.setOnClickListener {
            dialog.dismiss()
        }
        dialog.setCancelable(false)
        dialog.setContentView(bottomSheetView)
        dialog.show()
        showDeliveryRequestDetails(deliveryRequestDetails)
    }

    private fun showDeliveryRequestDetails(deliveryRequestDetails: DeliveryRequest){
        Glide.with(bottomSheetView).load(deliveryRequestDetails.foodImg).into(bottomSheetView.findViewById(R.id.item_food_image))
        bottomSheetView.apply {
            findViewById<TextView>(R.id.food_item_name).text = deliveryRequestDetails.nameOfItem
            findViewById<TextView>(R.id.item_food_weight).text = "${deliveryRequestDetails.foodWeight}KG"
            findViewById<TextView>(R.id.item_description).text = deliveryRequestDetails.itemDescription
//            findViewById<TextView>(R.id.tv_pickUp).text =
//            findViewById<TextView>(R.id.tv_dropOff).text =
        }

    }

    private fun sendCommandToService(action: String){
        Intent(requireContext(), ForeGroundService::class.java).also{
            it.action = action
            // the "startService(it)" sends the action or command passed to this function to the service.
            // it doesn't mean its used to start the service.
            //the service class is what handles the stopping or starting of the services class
            requireContext().startService(it)
        }
    }



    private fun requestPermission(){
        val permissions = when {
            // android 10 and above
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q -> arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            )
            // android 9 and below
            else -> arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        }

        val pendingPermissions = permissions.filter {
            ContextCompat.checkSelfPermission(requireContext(), it) != PackageManager.PERMISSION_GRANTED
        }.toTypedArray()

        if (pendingPermissions.isEmpty()) {
            // All permissions already granted, handle the action
            sendCommandToService(Constants.ACTION_START)
        } else {
            // Request permissions
            requestPermissionLauncher.launch(pendingPermissions)
        }

    }


    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()){ permissions ->
            val granted = permissions.all { it.value }
            if (granted) {
                // All permissions are granted, handle the action
                sendCommandToService(Constants.ACTION_START)
                Log.i("mstart location updates 2", "mstart update")
            } else {
                // Permissions denied, handle accordingly
                when{
                    //android 10 and above
                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q -> {

                        if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(),
                                Manifest.permission.ACCESS_FINE_LOCATION) ||
                            ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(),
                                Manifest.permission.ACCESS_COARSE_LOCATION) ||
                            ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(),
                                Manifest.permission.ACCESS_BACKGROUND_LOCATION)){
                            // Permission denied, but can ask again
                            // Handle this case, show rationale or retry logic
                            Log.i("img permission denied","temporal")
                            requestPermission()
                        }else {
                            permissionsDeniedPermanently()
                        }
                    }
                    else ->{
                        if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(),
                                Manifest.permission.ACCESS_FINE_LOCATION) ||
                            ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(),
                                Manifest.permission.ACCESS_COARSE_LOCATION)){
                            // Permission denied, but can ask again
                            // Handle this case, show rationale or retry logic
                            Log.i("img permission denied","temporal")
                            requestPermission()
                        }else {
                            permissionsDeniedPermanently()
                        }
                    }
                }
            }
        }


    private fun permissionsDeniedPermanently(){
        Log.i("permission denied","permanent")
        // Permission denied permanently
        // Handle this case, disable features or show settings dialog
        //show dialog to lead them to settings app to manually add permissions since they denied it permanently
        Utilities.showAlertDialog(requireContext(),
            title = "Location Permissions Required",
            message = "This permission is needed for you to be able to connected to a delivery, please grant permission",
            positiveAction = {
                AppSettingsDialog.Builder(this).build().show()
            },
            negativeAction = {}
        )
    }



    override fun onStop() {
        super.onStop()
        binding.mapView.onStop()
    }

    override fun onResume() {
        super.onResume()
        binding.mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        binding.mapView.onPause()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        binding.mapView.onLowMemory()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        binding.mapView.onSaveInstanceState(outState)
    }
}