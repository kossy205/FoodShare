package com.kosiso.foodshare.ui.fragments

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kosiso.foodshare.R
import com.kosiso.foodshare.databinding.FragmentMajorBinding
import com.kosiso.foodshare.ui.viewmodels.MajorViewModel

class MajorFragment : Fragment() {


    private val majorViewModel: MajorViewModel by viewModels()
    private lateinit var binding: FragmentMajorBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMajorBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Set status bar color
        requireActivity().window.statusBarColor = resources.getColor(R.color.white)
        // Set navigation bar color
        requireActivity().window.navigationBarColor = resources.getColor(R.color.white)

    }
}