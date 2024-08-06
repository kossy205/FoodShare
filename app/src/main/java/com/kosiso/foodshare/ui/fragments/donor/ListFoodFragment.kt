package com.kosiso.foodshare.ui.fragments.donor

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.app.DatePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.provider.MediaStore
import android.text.format.DateFormat
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.firebase.Timestamp
import com.google.firebase.firestore.GeoPoint
import com.kosiso.foodshare.R
import com.kosiso.foodshare.databinding.FragmentListFoodBinding
import com.kosiso.foodshare.other.Constants
import com.kosiso.foodshare.other.Constants.REQUEST_CODE_LOCATION_PERMISSION
import com.kosiso.foodshare.other.Utilities
import com.kosiso.foodshare.ui.viewmodels.donor.ListFoodViewModel
import dagger.hilt.android.AndroidEntryPoint
import pub.devrel.easypermissions.AppSettingsDialog
import java.io.IOException
import java.util.Calendar
import java.util.Date
import javax.inject.Inject

@AndroidEntryPoint
class ListFoodFragment : Fragment(R.layout.fragment_list_food) {

    @Inject
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    @Inject
    lateinit var locationRequest: LocationRequest
    private lateinit var binding: FragmentListFoodBinding
    private var currentLocation: GeoPoint? = null
    private var imageUri: Uri? = null
    private val listFoodViewModel: ListFoodViewModel by viewModels()
    private lateinit var expiryTimestamp: Timestamp


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentListFoodBinding.inflate(layoutInflater, container, false)
        return binding.root


    }


    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setFoodTypeOptionsDropdown()
        // once we enter this fragment, we start getting location updates
        getCurrentLocation()

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext())
        locationRequest = LocationRequest.create().apply {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            interval = 5000 // 5 seconds
        }

        binding.foodImage.setOnClickListener {
            Log.i("img select clicked","clicked")
            selectImageFromGallery()
        }
        binding.btnListFood.setOnClickListener {
            binding.cvLoading.visibility = View.VISIBLE
            binding.btnListFood.visibility = View.GONE
            // validate listFood form and then using view model, upload the food to firestore
            uploadImageToFirebaseStorage()
        }

        binding.etPerishableDate.setOnClickListener {
            showDatePickerDialog(requireView())
        }


        listFoodViewModel.messageFromViewModel.observe(viewLifecycleOwner, Observer {
            Utilities.showNormalSnackBar(it, requireView(), requireContext())
        })


        listFoodViewModel.postFoodResult.observe(viewLifecycleOwner, Observer {result->
            if(result.isSuccess){
                Utilities.showNormalSnackBar("Food posted successfully", requireView(), requireContext())
                binding.cvLoading.visibility = View.GONE
                fusedLocationProviderClient.removeLocationUpdates(locationCallback)
                findNavController().navigate(R.id.action_listFoodFragment2_to_impactDonorFragment)
            }else{
                binding.cvLoading.visibility = View.GONE
                binding.btnListFood.visibility = View.VISIBLE
            }
        })


        listFoodViewModel.storeImageResult.observe(viewLifecycleOwner, Observer {result->
            result.onSuccess{storedImgUri->

                binding.tvLoading.text = "Image stored successfully"
                postFoodToFirestore(storedImgUri)
            }
            result.onFailure {
                binding.cvLoading.visibility = View.GONE
                binding.btnListFood.visibility = View.VISIBLE
            }

        })
    }

    private fun postFoodToFirestore(storedFoodImgUri: String){
        val uid = listFoodViewModel.getCurrentUser()?.uid.toString()
        val foodItemName: String = binding.etFoodName.text.toString()
        val foodItemDescription = binding.etFoodDescription.text.toString()
        val storedFoodImgUri: String = storedFoodImgUri
        val listingCategory:String = binding.tvFoodType.text.toString()
        val foodWeight: Int = binding.etFoodWeight.text.toString().toInt()
        val status = Constants.UNCLAIMED_LIST_FOOD // we have unclaimed, claimed and delivered
        val currentTimestamp: Timestamp = getCurrentTimestamp()
        val currentLocation: GeoPoint? = currentLocation

        listFoodViewModel.postFoodToFirestore(
            uid,
            foodItemName,
            foodItemDescription,
            storedFoodImgUri,
            listingCategory,
            foodWeight,
            status,
            currentTimestamp,
            currentLocation,
            expiryTimestamp
        )
    }

    private fun showDatePickerDialog(view: android.view.View) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, selectedYear, selectedMonth, selectedDay ->
                val selectedDate = Calendar.getInstance()
                selectedDate.set(selectedYear, selectedMonth, selectedDay)

                // Update EditText with selected date
                binding.etPerishableDate.setText(
                    DateFormat.format("yyyy-MM-dd", selectedDate)
                )

                // Save selected date to Firestore as Timestamp
                expiryTimestamp = Timestamp(selectedDate.time)
            },
            year,
            month,
            day
        )

        // minimum date for DatePickerDialog so users cant select a past date
        datePickerDialog.datePicker.minDate = System.currentTimeMillis() - 1000 // 1 second ago
        datePickerDialog.show()
    }


    private fun uploadImageToFirebaseStorage(){
        val foodImg: Uri = imageUri!!

        listFoodViewModel.uploadImageToFirebaseStorage(requireActivity(), foodImg,)
    }

    private fun getCurrentTimestamp(): Timestamp{
         val currentDate = Date()
        return Timestamp(currentDate)
    }



    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun selectImageFromGallery(){

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
            ContextCompat.checkSelfPermission(requireContext(), it) != PackageManager.PERMISSION_GRANTED
        }.toTypedArray()

        if (pendingPermissions.isEmpty()) {
            // All permissions already granted
            Log.i("img has permissions?", "permissions present, can open gallery")

            openGallery()
        } else {
            // Permissions need to be requested
            Log.i("img has permissions?", "no permission")

            requestImagePermission()
        }

    }

    private fun openGallery(){
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        getContent.launch(intent)
        Log.i("gallery opened","opened")
    }
    private val getContent =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val intentData = result.data
                intentData?.data?.let {uri->
                    // Use the URI as needed
                    imageUri = uri
                    Log.i("gotten img uri","$imageUri")
                    displayImageOnImageView(uri)
                }
            }
        }

    private fun displayImageOnImageView(uri: Uri){
        try {
            // Load the board image in the ImageView.
            Glide
                .with(requireActivity())
                .load(Uri.parse(uri.toString()))
                .centerCrop()
                .placeholder(R.drawable.food_image1)
                .into(binding.foodImage)
            Log.i("image loaded in to img view","done")
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }


    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun requestImagePermission(){
        Log.i("img permission request","request")

        val permissions = when {
            // android 14 and above
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE -> arrayOf(
                Manifest.permission.READ_MEDIA_IMAGES,
                Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED
            )
            // android 13 and above
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> arrayOf(
                Manifest.permission.READ_MEDIA_IMAGES
            )
            // android 12 and below
            else -> arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
        }

        val pendingPermissions = permissions.filter {
            ContextCompat.checkSelfPermission(requireContext(), it) != PackageManager.PERMISSION_GRANTED
        }.toTypedArray()

        if (pendingPermissions.isEmpty()) {
            // All permissions already granted
            openGallery()
        } else {
            // Permissions need to be requested
            Log.i("img official request","request")
            requestImagePermissionLauncher.launch(pendingPermissions)
        }

    }


    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private val requestImagePermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val granted = permissions.all { it.value }
            Log.i("permissions granted", "$granted")

            if (granted) {
                openGallery()
            } else {
                // Handle case where not all permissions are granted

                when {

                    // android 14 and above
                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE -> {

                        if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(),
                                Manifest.permission.READ_MEDIA_IMAGES) ||
                            ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(),
                                Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED)){
                            // Permission denied, but can ask again
                            // Handle this case, show rationale or retry logic
                            Log.i("img permission denied","temporal")
                            requestImagePermission()
                        }else {
                            imgPermissionsDeniedPermanently()
                        }

                    }

                    // android 13 and above
                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> {

                        if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(),
                                Manifest.permission.READ_MEDIA_IMAGES)){
                            // Permission denied, but can ask again
                            // Handle this case, show rationale or retry logic
                            Log.i("img permission denied","temporal")
                            requestImagePermission()
                        }else {
                            imgPermissionsDeniedPermanently()
                        }

                    }

                    // android 12 and below
                    else -> {

                        if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(),
                                Manifest.permission.READ_EXTERNAL_STORAGE)){
                            // Permission denied, but can ask again
                            // Handle this case, show rationale or retry logic
                            Log.i("img permission denied","temporal")
                            requestImagePermission()
                        }else {
                            imgPermissionsDeniedPermanently()
                        }

                    }
                }

            }
        }


    private fun imgPermissionsDeniedPermanently(){
        Log.i("img permission denied","permanent")
        // Permission denied permanently
        // Handle this case, disable features or show settings dialog
        //show dialog to lead them to settings app to manually add permissions since they denied it permanently
        Utilities.showAlertDialog(requireContext(),
            title = "Image Permission Required",
            message = "This permission is needed for you to be able to show the image of your donation, please grant permission",
            positiveAction = {
                AppSettingsDialog.Builder(this).build().show()
            },
            negativeAction = {}
        )
    }


    private fun getCurrentLocation(){

        val permissions = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )

        val pendingPermissions = permissions.filter {
            ContextCompat.checkSelfPermission(requireContext(), it) != PackageManager.PERMISSION_GRANTED
        }.toTypedArray()

        if (pendingPermissions.isEmpty()) {
            // All permissions already granted, handle the action
            startLocationUpdates()
        } else {
            // Request permissions
            requestLocationPermissions()
        }


    }

    @SuppressLint("MissingPermission")
    private fun startLocationUpdates() {
        Log.i("startLocationUpdates", "startLocationUpdates")

        fusedLocationProviderClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
        Log.i("startLocationUpdates 1", "startLocationUpdates")
    }

    private val locationCallback = object : LocationCallback(){
        override fun onLocationResult(result: LocationResult) {
            super.onLocationResult(result)

            Log.i("locationCallback", "locationCallback")
            result?.locations?.let {locations ->
                for(location in locations){
                    val geoPoint: GeoPoint = GeoPoint(location.latitude, location.longitude)
                    Log.i("location updates gotten ", "$geoPoint")
                    currentLocation = geoPoint
                }
            }
        }
    }


    private fun setFoodTypeOptionsDropdown(): Unit{
        val foodTypeOptions = resources.getStringArray(R.array.foodType_options)
        val arrayAdapter = ArrayAdapter(requireContext(), R.layout.options_dropdown, foodTypeOptions)
        return binding.tvFoodType.setAdapter(arrayAdapter)
    }


    // request permission to use location
    private fun requestLocationPermissions() {

        val permissions = arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )

            val pendingPermissions = permissions.filter {
                ContextCompat.checkSelfPermission(requireContext(), it) != PackageManager.PERMISSION_GRANTED
            }.toTypedArray()

            if (pendingPermissions.isEmpty()) {
                // All permissions already granted, handle the action
                startLocationUpdates()
            } else {
                // Request permissions
                requestLocationsPermissionLauncher.launch(pendingPermissions)
            }
        Log.i("no location permission but have asked 1", "has location permission")
    }


    private val requestLocationsPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val granted = permissions.all { it.value }
            if (granted) {
                // All permissions are granted, handle the action
                startLocationUpdates()
                Log.i("start location updates 2", "start update")
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
                        Log.i("show alert dialog", "shown")

                    }else {
                        // Permissions denied but can be requested again, show a message or take appropriate action
                        Toast.makeText(requireContext(), "Location permissions denied", Toast.LENGTH_SHORT)
                            .show()
                        requestLocationPermissions()
                    }
            }
        }

    override fun onDestroy() {
        super.onDestroy()

        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
        Log.i("remove location updates", "done")

    }

}
