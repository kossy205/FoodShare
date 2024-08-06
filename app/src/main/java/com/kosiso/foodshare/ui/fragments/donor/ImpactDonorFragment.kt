package com.kosiso.foodshare.ui.fragments.donor

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.kosiso.foodshare.R
import com.kosiso.foodshare.databinding.FragmentImpactDonorBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class ImpactDonorFragment : Fragment(R.layout.fragment_impact_donor) {

    private lateinit var binding: FragmentImpactDonorBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentImpactDonorBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.donorFab.setOnClickListener {
            findNavController().navigate(R.id.action_impactDonorFragment_to_listFoodFragment2)
        }
    }
}