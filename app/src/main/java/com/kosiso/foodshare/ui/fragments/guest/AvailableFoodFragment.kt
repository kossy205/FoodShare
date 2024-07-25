package com.kosiso.foodshare.ui.fragments.guest

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kosiso.foodshare.R
import com.kosiso.foodshare.databinding.FragmentAvailableFoodBinding
import com.kosiso.foodshare.databinding.FragmentRequestsBinding


class AvailableFoodFragment : Fragment() {

    private lateinit var binding: FragmentAvailableFoodBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentAvailableFoodBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }
}