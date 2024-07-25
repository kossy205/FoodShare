package com.kosiso.foodshare.ui.fragments.authentication

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.kosiso.foodshare.R
import com.kosiso.foodshare.databinding.FragmentSignupBinding
import com.kosiso.foodshare.ui.viewmodels.authentication.SignUpViewModel
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SignUpFragment : Fragment(R.layout.fragment_signup) {

    private lateinit var binding: FragmentSignupBinding
    private val signUpViewmodel: SignUpViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Initialize the binding variable
        binding = FragmentSignupBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setSignUpOptionsDropdown()

        binding.btnSignUp.setOnClickListener {
            binding.btnSignUp.visibility = View.GONE
            binding.cvLoading.visibility = View.VISIBLE
            val firstName: String = binding.etFirstName.text.toString().trim { it <= ' ' }
            val lastName: String = binding.etLastName.text.toString().trim { it <= ' ' }
            val phone: Long? = binding.etCountryCode.text.toString().trim { it <= ' ' }.toLongOrNull()
            val countryCode: Long? = binding.etPhone.text.toString().trim { it <= ' ' }.toLongOrNull()
            val email: String = binding.etEmail.text.toString().trim { it <= ' ' }
            val password: String = binding.etPassword.text.toString().trim { it <= ' ' }
            val signUpAs: String = binding.tvPurpose.text.toString()
            signUpViewmodel.signUp(firstName, lastName, countryCode, phone, email, password, signUpAs)

            restoreSignUpBtnIfFormIsNotValid()
        }

        signUpViewmodel.messageFromViewModel.observe(viewLifecycleOwner, Observer { error->
            error?.let { showErrorSnackBar(it.toString()) }
        })

        signUpViewmodel.signupResult.observe(viewLifecycleOwner, Observer {result->

            if(result.isSuccess){
                binding.cvLoading.visibility = View.GONE
                findNavController().navigate(R.id.action_signUpFragment_to_signInFragment)
            }else{
                binding.cvLoading.visibility = View.GONE
                binding.btnSignUp.visibility = View.VISIBLE
                let {
                    showErrorSnackBar(result.isFailure.toString())
                }
            }
        })

    }

    private fun restoreSignUpBtnIfFormIsNotValid(){
        signUpViewmodel.validateForm.observe(viewLifecycleOwner, Observer {
            if(it == false){
                binding.cvLoading.visibility = View.GONE
                binding.btnSignUp.visibility = View.VISIBLE
            }
        })
    }

    fun showErrorSnackBar(message: String) {
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
    fun setSignUpOptionsDropdown(){
        val signUpOptions = resources.getStringArray(R.array.signup_options)
        val arrayAdapter = ArrayAdapter(requireContext(), R.layout.options_dropdown, signUpOptions)
        binding.tvPurpose.setAdapter(arrayAdapter)
    }
}