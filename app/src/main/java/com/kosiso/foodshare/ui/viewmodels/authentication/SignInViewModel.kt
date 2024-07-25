package com.kosiso.foodshare.ui.viewmodels.authentication

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

    private val _fetchedResult = MutableLiveData<Result<String>>()
    val fetchedResult: LiveData<Result<String>> = _fetchedResult

    private val _validateForm = MutableLiveData<Boolean>()
    val validateForm: LiveData<Boolean> = _validateForm



    fun signIn(email: String, password: String){
        if (validateForm(email, password)){
            mainRepository.signIn(email, password)
                .addOnSuccessListener {authResult ->
                    // populates the livedata with the signup result, this is then observed by the fragment...
                    //and perform a navigation based on the result of the signup
                    _signInResult.value = Result.success(authResult.user!!)
                }
                .addOnFailureListener {exception->
                    _signInResult.value = Result.failure(exception)
                    _messageFromViewModel.value = exception.message!!
                    Log.d("sign in exception", exception.message!!)
                }
        }
    }


    fun fetchUerDetails(userId: String){
        mainRepository.fetchUserDetailsWithUserId(userId)
            .addOnSuccessListener {document ->

                Log.i("fetchUerDetails success", "$document")
                if(document.exists()){

                    Log.i("fetchUerDetails doc exists", "$document")
                    val userDetails = document.toObject(User::class.java)
                    if (userDetails != null) {
                        _fetchedResult.value = Result.success(userDetails.signUpAs)
                    }

                }

            }
            .addOnFailureListener {
                Log.i("fetchUerDetails failure", it.message.toString())
            }
    }


    private fun validateForm(email: String, password: String): Boolean {
        return when {
            (password.isNullOrEmpty()) -> {
                _validateForm.value = false
                _messageFromViewModel.value = "Please enter password."
                false

            }
            (email.isNullOrEmpty()) -> {
                _validateForm
                _messageFromViewModel.value = "Please enter email."
                false
            }
            else -> {
                _validateForm.value = true
                true
            }
        }
    }
}