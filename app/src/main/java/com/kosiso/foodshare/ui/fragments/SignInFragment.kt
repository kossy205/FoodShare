package com.kosiso.foodshare.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.kosiso.foodshare.R
import com.kosiso.foodshare.databinding.FragmentSignInBinding
import com.kosiso.foodshare.ui.viewmodels.SignInViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SignInFragment : Fragment(R.layout.fragment_sign_in) {

    private lateinit var binding:FragmentSignInBinding
    private val signInViewModel: SignInViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSignInBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Set status bar color
        requireActivity().window.statusBarColor = resources.getColor(R.color.white)
        // Set navigation bar color
        requireActivity().window.navigationBarColor = resources.getColor(R.color.white)

        binding.btnSignIn.setOnClickListener {
            binding.btnSignIn.visibility = View.GONE
            binding.cvLoading.visibility = View.VISIBLE
            val email: String = binding.etEmail.text.toString().trim { it <= ' ' }
            val password: String = binding.etPassword.text.toString().trim { it <= ' ' }
            signInViewModel.signIn(email, password)
        }

        signInViewModel.messageFromViewModel.observe(viewLifecycleOwner, Observer {
            showErrorSnackBar(it.toString())
        })
        signInViewModel.signInResult.observe(viewLifecycleOwner, Observer {result->
            if (result.isSuccess){
                binding.cvLoading.visibility = View.GONE
                findNavController().navigate(R.id.action_signInFragment_to_majorFragment)
            }else{
                binding.cvLoading.visibility = View.GONE
                binding.btnSignIn.visibility = View.VISIBLE
            }
        })

    }

    private fun showErrorSnackBar(message: String) {
        val snackBar =
            Snackbar.make(requireView(), message, Snackbar.LENGTH_LONG)
        val snackBarView = snackBar.view
        snackBarView.setBackgroundColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.red
            )
        )
        snackBar.show()
    }
}