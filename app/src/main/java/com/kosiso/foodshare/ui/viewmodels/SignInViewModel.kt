package com.kosiso.foodshare.ui.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseUser
import com.kosiso.foodshare.models.User
import com.kosiso.foodshare.repository.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(val mainRepository: MainRepository): ViewModel(){


    private val _messageFromViewModel = MutableLiveData<String>()
    val messageFromViewModel: LiveData<String> = _messageFromViewModel

    private val _signInResult = MutableLiveData<Result<FirebaseUser>>()
    val signInResult: LiveData<Result<FirebaseUser>> = _signInResult

    fun signIn(email: String, password: String){
        if (validateForm(email, password)){
            mainRepository.signIn(email, password)
                .addOnSuccessListener {authResult ->
                    // populates the livedata with the signup result, this is then observed by the fragment...
                    //and perform a navigation based on the result of the signup
                    _signInResult.value = Result.success(authResult.user!!)
                }
                .addOnFailureListener {exception->

                    _messageFromViewModel.value = "unable to sign in"
                    Log.d("sign in exception", exception.message!!)
                }
        }
    }


    private fun validateForm(email: String, password: String): Boolean {
        return when {
            (password.isNullOrEmpty()) -> {
                _messageFromViewModel.value = "Please enter password."
                false
            }
            (email.isNullOrEmpty()) -> {
                _messageFromViewModel.value = "Please enter email."
                false
            }
            else -> {
                true
            }
        }
    }
}