package com.kosiso.foodshare.ui.fragments.guest

import FoodListing
import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.kosiso.foodshare.databinding.FragmentAvailableFoodBinding
import com.kosiso.foodshare.databinding.FragmentAvailableFoodDetailsBinding
import com.kosiso.foodshare.other.Constants
import com.kosiso.foodshare.other.Utilities
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AvailableFoodDetailsFragment : Fragment() {

    private lateinit var binding: FragmentAvailableFoodDetailsBinding

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
    }
}