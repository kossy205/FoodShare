package com.kosiso.foodshare.ui.fragments.authentication

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.kosiso.foodshare.R
import com.kosiso.foodshare.databinding.FragmentSignInBinding
import com.kosiso.foodshare.ui.activities.DonorActivity
import com.kosiso.foodshare.ui.activities.GuestActivity
import com.kosiso.foodshare.ui.activities.VolunteerActivity
import com.kosiso.foodshare.ui.viewmodels.authentication.SignInViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SignInFragment : Fragment(R.layout.fragment_sign_in) {

    @Inject
    lateinit var firebaseAuth: FirebaseAuth
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

        binding.btnSignIn.setOnClickListener {
            binding.btnSignIn.visibility = View.GONE
            binding.cvLoading.visibility = View.VISIBLE
            val email: String = binding.etEmail.text.toString().trim { it <= ' ' }
            val password: String = binding.etPassword.text.toString().trim { it <= ' ' }
            signInViewModel.signIn(email, password)

            restoreSignInBtnIfFormIsNotValid()
        }

        signInViewModel.messageFromViewModel.observe(viewLifecycleOwner, Observer {
            showErrorSnackBar(it.toString())
        })
        signInViewModel.signInResult.observe(viewLifecycleOwner, Observer {result->
            when{
                result.isSuccess ->{
                    binding.cvLoading.visibility = View.GONE

                    signInDestination {activityClass ->
                        if(activityClass != null){
                            startActivity(Intent(requireActivity(),activityClass))
                            requireActivity().finish()
                        }
                    }
                }
                result.isFailure ->{
                    binding.cvLoading.visibility = View.GONE
                    binding.btnSignIn.visibility = View.VISIBLE
                    showErrorSnackBar(result.isFailure.toString())
                }
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


    private fun signInDestination(callback: (Class<out Activity>?) -> Unit){
        Log.i("auth user", firebaseAuth.currentUser!!.uid)
        signInViewModel.fetchUerDetails(firebaseAuth.currentUser!!.uid)
        signInViewModel.fetchedResult.observe(viewLifecycleOwner, Observer {
            val activityClass = when(it.toString()){
                "Success(Donor)" -> DonorActivity::class.java
                "Success(Guest)" -> GuestActivity::class.java
                "Success(Delivery Volunteer)" -> VolunteerActivity::class.java
                else -> null
            }
            Log.i("navigate to activity 1", "$activityClass")
            callback(activityClass)
        })
    }

    private fun restoreSignInBtnIfFormIsNotValid(){
        signInViewModel.validateForm.observe(viewLifecycleOwner, Observer {
            if(it == false){
                binding.cvLoading.visibility = View.GONE
                binding.btnSignIn.visibility = View.VISIBLE
            }
        })
    }
}