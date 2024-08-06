package com.kosiso.foodshare.ui.fragments.guest

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.kosiso.foodshare.R
import com.kosiso.foodshare.databinding.FragmentMakeRequestBinding
import com.kosiso.foodshare.databinding.FragmentRequestsBinding
import com.kosiso.foodshare.other.Utilities
import com.kosiso.foodshare.ui.viewmodels.guest.MakeRequestViewModel
import dagger.hilt.android.AndroidEntryPoint
import pub.devrel.easypermissions.AppSettingsDialog
import javax.inject.Inject

@AndroidEntryPoint
class MakeRequestFragment : Fragment() {


    private val makeRequestViewModel: MakeRequestViewModel by viewModels()
    private lateinit var binding: FragmentMakeRequestBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentMakeRequestBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requestCurrentLocation()

        binding.btnMakeFoodRequest.setOnClickListener {
            binding.cvLoading.visibility = View.VISIBLE
            binding.btnMakeFoodRequest.visibility = View.GONE
            val nameOfItem: String = binding.etFoodName.text.toString().trim { it <= ' ' }
            val requestDesc: String = binding.etFoodDescription.text.toString().trim { it <= ' ' }
            val foodWeight: Int? = binding.etFoodWeight.text.toString().trim { it <= ' ' }.toIntOrNull()

            makeRequestViewModel.postFoodRequest(nameOfItem, requestDesc, foodWeight)
        }

        makeRequestViewModel.messageFromViewModel.observe(viewLifecycleOwner, Observer {message->
            message.let {
                Utilities.showErrorSnackBar(message, requireView(), requireContext())
            }
        })

        makeRequestViewModel.validateForm.observe(viewLifecycleOwner, Observer {
            if (it == false){
                binding.cvLoading.visibility = View.GONE
                binding.btnMakeFoodRequest.visibility = View.VISIBLE
            }
        })

        makeRequestViewModel.postFoodRequestResult.observe(viewLifecycleOwner, Observer {result->
            result.onSuccess {
                //binding.cvLoading.visibility = View.GONE
                findNavController().navigate(R.id.action_makeRequestFragment_to_availableFoodFragment2)
            }
            result.onFailure {
                binding.cvLoading.visibility = View.GONE
                binding.btnMakeFoodRequest.visibility = View.VISIBLE
                let {
                    Utilities.showErrorSnackBar(result.isFailure.toString(), requireView(), requireContext())
                }
            }
        })
    }


    private fun requestCurrentLocation(){

        val permissions = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )

        val pendingPermissions = permissions.filter {
            ContextCompat.checkSelfPermission(requireContext(), it) != PackageManager.PERMISSION_GRANTED
        }.toTypedArray()

        if (pendingPermissions.isEmpty()) {
            // All permissions already granted, handle the action
            makeRequestViewModel.getLocationUpdates()
        } else {
            // Request permissions
            requestLocationsPermissionLauncher.launch(pendingPermissions)
        }
    }

    private val requestLocationsPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val granted = permissions.all { it.value }
            if (granted) {
                // All permissions are granted, handle the action
                makeRequestViewModel.getLocationUpdates()
                Log.i("mstart location updates 2", "mstart update")
            } else {
                // Permissions denied, handle accordingly
                if (!ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(),
                        Manifest.permission.ACCESS_FINE_LOCATION) ||
                    !ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(),
                        Manifest.permission.ACCESS_COARSE_LOCATION))
                {
                    //show dialog to lead them to settings app to manually add permissions since they denied it permanently
                    Utilities.showAlertDialog(requireContext(),
                        title = "Location Permission Required",
                        message = "Your current location would be needed for guest to know the location of your donation, please grant permission",
                        positiveAction = {
                            AppSettingsDialog.Builder(this).build().show()
                        },
                        negativeAction = {}
                    )
                    Log.i("mshow alert dialog", "mshown")

                }else {
                    // Permissions denied but can be requested again, show a message or take appropriate action
                    Toast.makeText(requireContext(), "mLocation permissions denied", Toast.LENGTH_SHORT)
                        .show()
                    requestCurrentLocation()
                }
            }
        }



    override fun onDestroy() {
        super.onDestroy()
        makeRequestViewModel.stopLocationUpdates()
        Log.i("mremove location updates", "done")
    }
}