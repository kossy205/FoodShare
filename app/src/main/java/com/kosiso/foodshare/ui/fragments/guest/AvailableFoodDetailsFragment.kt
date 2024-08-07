package com.kosiso.foodshare.ui.fragments.guest

import FoodListing
import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.kosiso.foodshare.R
import com.kosiso.foodshare.databinding.FragmentAvailableFoodBinding
import com.kosiso.foodshare.databinding.FragmentAvailableFoodDetailsBinding
import com.kosiso.foodshare.other.Constants
import com.kosiso.foodshare.other.Utilities
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AvailableFoodDetailsFragment : Fragment() {

    private lateinit var binding: FragmentAvailableFoodDetailsBinding
    private var map: GoogleMap? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //// Inflate the layout for this fragment
        binding = FragmentAvailableFoodDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val item = arguments?.getParcelable<FoodListing>(Constants.ITEM_AVAILABLE_FOOD_DATA_OBJECT)
            item.let {foodListing ->
                Glide
                    .with(requireContext())
                    .load(foodListing?.foodImg)
                    .into(binding.foodImage)
                binding.itemFoodName.text = foodListing?.nameOfItem
                binding.itemFoodPostDate.text = Utilities.formatTimeAgo(foodListing?.foodListedTime!!)
                binding.itemFoodCategory.text = foodListing.listingCategory
                binding.itemFoodWeight.text = "${foodListing.foodWeight}kg"
                binding.itemFoodExpDate.text = "Exp: ${Utilities.formatDate(foodListing?.expiryDate!!)}"
                binding.itemFoodDesc.text = foodListing.itemDescription
            }
        binding.btnClaim.setOnClickListener {
            Utilities.showErrorSnackBar("Claimed ${item?.nameOfItem}", requireView(), requireContext())
        }
        binding.tvCancel.setOnClickListener {
            Utilities.showErrorSnackBar("Canceled ${item?.nameOfItem}", requireView(), requireContext())
        }
        binding.mapView.onCreate(savedInstanceState)
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