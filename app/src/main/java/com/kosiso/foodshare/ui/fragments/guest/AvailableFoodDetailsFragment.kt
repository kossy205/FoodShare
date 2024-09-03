package com.kosiso.foodshare.ui.fragments.guest

import FoodListing
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.ContentValues.TAG
import android.graphics.drawable.ClipDrawable
import android.graphics.drawable.LayerDrawable
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.kosiso.foodshare.R
import com.kosiso.foodshare.databinding.CustomLoadingDialogBinding
import com.kosiso.foodshare.databinding.FragmentAvailableFoodDetailsBinding
import com.kosiso.foodshare.models.DeliveryRequest
import com.kosiso.foodshare.other.Constants
import com.kosiso.foodshare.other.Utilities
import com.kosiso.foodshare.ui.viewmodels.guest.AvailableFoodDetailsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AvailableFoodDetailsFragment : Fragment() {

    private lateinit var binding: FragmentAvailableFoodDetailsBinding
    private var map: GoogleMap? = null
    private lateinit var item:FoodListing
    private val availableFoodDetailsViewModel: AvailableFoodDetailsViewModel by viewModels()
    private lateinit var customDialogMessage: String
    //private var dialog: Dialog? = null



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentAvailableFoodDetailsBinding.inflate(inflater, container, false)
        return binding.root

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        customDialogMessage = "Please Wait"

        val item = arguments?.getParcelable<FoodListing>(Constants.ITEM_AVAILABLE_FOOD_DATA_OBJECT)
            item.let {foodListing ->
                Glide
                    .with(requireContext())
                    .load(foodListing?.foodImg)
                    .into(binding.foodImage)

                binding.apply {
                    itemFoodName.text = foodListing?.nameOfItem
                    itemFoodPostDate.text = Utilities.formatTimeAgo(foodListing?.foodListedTime!!)
                    itemFoodCategory.text = foodListing.listingCategory
                    itemFoodWeight.text = "${foodListing.foodWeight}kg"
                    itemFoodExpDate.text = "Exp: ${Utilities.formatDate(foodListing?.expiryDate!!)}"
                    itemFoodDesc.text = foodListing.itemDescription
                }
            }


        binding.apply {
            mapView.onCreate(savedInstanceState)
            binding.mapView.getMapAsync {
                map = it

                val latitude = item!!.foodListingLocation!!.latitude
                val longitude = item!!.foodListingLocation!!.longitude
                val markerPosition = LatLng(latitude, longitude)
                map?.apply {
                    addMarker(MarkerOptions()
                        .position(markerPosition)
                        .title("Food Location")
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_location_60)))
                    moveCamera(CameraUpdateFactory.newLatLng(markerPosition))
                    animateCamera(CameraUpdateFactory.zoomTo(15f)) // Adjust zoom level as needed
                }

            }

            // button claim
            btnClaim.setOnClickListener{
                item.let {foodListing ->
                    availableFoodDetailsViewModel.uploadDeliveryRequest(
                        foodListing?.uid!!,
                        foodListing?.nameOfItem!!,
                        foodListing?.itemDescription!!,
                        foodListing?.foodImg!!,
                        foodListing?.listingCategory!!,
                        foodListing?.foodWeight!!,
                        foodListing?.status!!,
                        foodListing?.foodListedTime!!,
                        foodListing?.foodListingLocation,
                        foodListing?.expiryDate!!
                    )
                }
                binding.cvLoading.visibility = View.VISIBLE
                binding.tvLoadingCancel.visibility = View.VISIBLE
                binding.btnClaim.visibility = View.GONE
                binding.tvCancel.visibility = View.GONE
            }
            //button cancel
            tvCancel.setOnClickListener {
                findNavController().navigate(R.id.action_availableFoodDetailsFragment_to_availableFoodFragment2)
            }
            //button to cancel "find delivery agent" incase user changes his/her mind
            tvLoadingCancel.setOnClickListener {
                availableFoodDetailsViewModel.removeGeoQueryEventListeners()
                binding.cvLoading.visibility = View.GONE
                binding.tvLoadingCancel.visibility = View.GONE
                binding.btnClaim.visibility = View.VISIBLE
                binding.tvCancel.visibility = View.VISIBLE
                Log.i("tv loading cancel clicked", "clicked")
            }
        }



        availableFoodDetailsViewModel.apply {
            // observe live data
            uploadDeliveryRequest.observe(viewLifecycleOwner, Observer {result ->
                result.onSuccess {

                }
                result.onFailure {
                    binding.cvLoading.visibility = View.GONE
                    binding.tvLoadingCancel.visibility = View.GONE
                    binding.btnClaim.visibility = View.VISIBLE
                    binding.tvCancel.visibility = View.VISIBLE
                    Utilities.showErrorSnackBar(it.message!!, requireView(), requireContext())
                }
            })
            setDeliveryLocationResult.observe(viewLifecycleOwner, Observer {result ->
                if(result != null){
                    result.onSuccess {
                        binding.tvLoading.text = it
                    }
                    result.onFailure {
                        binding.cvLoading.visibility = View.GONE
                        binding.tvLoadingCancel.visibility = View.GONE
                        binding.btnClaim.visibility = View.VISIBLE
                        binding.tvCancel.visibility = View.VISIBLE
                        Utilities.showErrorSnackBar(it.message!!, requireView(), requireContext())
                    }
                }else{
                    Log.e("AvailableFoodDetailsFragment", "Result is null")
                }

            })
            deliveryAcceptResult.observe(viewLifecycleOwner, Observer {result->
                result.onSuccess {
                    if(it){
                        // navigate to transit fragment to track deliveries
                    }else{
                        Utilities.showNormalSnackBar("Delivery rejected, try again", requireView(), requireContext())
                        binding.cvLoading.visibility = View.GONE
                        binding.tvLoadingCancel.visibility = View.GONE
                        binding.btnClaim.visibility = View.VISIBLE
                        binding.tvCancel.visibility = View.VISIBLE
                    }
                }
            })
            listenerRemovedResult.observe(viewLifecycleOwner, Observer {result->
                result.onSuccess {
                    if(it){
                        Utilities.showNormalSnackBar("Search for delivery agent stopped. You can still try again", requireView(), requireContext())
                        binding.cvLoading.visibility = View.GONE
                        binding.tvLoadingCancel.visibility = View.GONE
                        binding.btnClaim.visibility = View.VISIBLE
                        binding.tvCancel.visibility = View.VISIBLE
                    }
                }
            })
        }

//        //demo
//        availableFoodDetailsViewModel.setVolunteerLocation(item?.foodListingLocation!!)
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

    override fun onDestroy() {
        super.onDestroy()
    }
}