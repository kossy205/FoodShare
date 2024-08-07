package com.kosiso.foodshare.ui.fragments.authentication

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.kosiso.foodshare.R
import com.kosiso.foodshare.databinding.FragmentIntroBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class IntroFragment : Fragment(R.layout.fragment_intro) {

    @Inject
    lateinit var firebaseAuth: FirebaseAuth
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

        binding.btnSignIn.setOnClickListener {
            findNavController().navigate(R.id.action_introFragment_to_signInFragment)
        }

        binding.btnSignUp.setOnClickListener {
            findNavController().navigate(R.id.action_introFragment_to_signUpFragment)
        }
    }


}