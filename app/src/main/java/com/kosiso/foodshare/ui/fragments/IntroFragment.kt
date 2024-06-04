package com.kosiso.foodshare.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.kosiso.foodshare.R
import com.kosiso.foodshare.databinding.FragmentIntroBinding


class IntroFragment : Fragment(R.layout.fragment_intro) {

    private lateinit var binding: FragmentIntroBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentIntroBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Set status bar color
        requireActivity().window.statusBarColor = resources.getColor(R.color.white)
        // Set navigation bar color
        requireActivity().window.navigationBarColor = resources.getColor(R.color.white)

        binding.btnSignIn.setOnClickListener {
            findNavController().navigate(R.id.action_introFragment_to_signInFragment)
        }

        binding.btnSignUp.setOnClickListener {
            findNavController().navigate(R.id.action_introFragment_to_signUpFragment)
        }
    }
}