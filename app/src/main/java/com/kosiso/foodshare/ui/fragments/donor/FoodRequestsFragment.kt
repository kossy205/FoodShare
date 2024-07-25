package com.kosiso.foodshare.ui.fragments.donor

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kosiso.foodshare.R
import com.kosiso.foodshare.databinding.FragmentFoodRequestsBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FoodRequestsFragment : Fragment(R.layout.fragment_food_requests) {


    private lateinit var binding: FragmentFoodRequestsBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentFoodRequestsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


    }
}