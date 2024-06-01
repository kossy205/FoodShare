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
class SignUpViewModel @Inject constructor(val mainRepository: MainRepository): ViewModel(){


    private val _messageFromViewModel = MutableLiveData<String>()
    val messageFromViewModel: LiveData<String> = _messageFromViewModel

    private val _signupResult = MutableLiveData<Result<FirebaseUser>>()
    val signupResult: LiveData<Result<FirebaseUser>> = _signupResult


    fun signUp(firstName:String, lastname:String, countryCode: Long?, phone: Long?, email: String, password: String){
        if(validateForm(firstName, lastname, countryCode, phone, email, password)){
            mainRepository.signUp(email, password)
                .addOnSuccessListener {authResult->

                    //_signupResult.value = Result.success(authResult.user!!)
                    //register user here
                    val userDetails = authResult.user
                    val user: User = User(
                        userDetails!!.uid,
                        firstName,
                        lastname,
                        countryCode!!,
                        phone!!,
                        email,
                        password
                    )
                    mainRepository.registerUserInDb(user)
                        .addOnSuccessListener {
                            //SHOW SNACKBAR MESSAGE OF SUCCESS IN REGISTRATION AND MOVE THEM BACK TO INTRO ACTIVITY TO SIGN IN
                            _messageFromViewModel.value = "registration successful"
                            Log.d("register user success", "registration successful")

                            // populates the livedata with the signup result, this is then observed by the fragment...
                            //and perform a navigation based on the result of the signup
                            _signupResult.value = Result.success(authResult.user!!)

                        }
                        .addOnFailureListener {exception->
                            _messageFromViewModel.value = "unable to add user info to db"
                            Log.d("register user in db exception", exception.message!!)
                        }

                }
                .addOnFailureListener {exception->

                    _messageFromViewModel.value = "unable to sign up"
                    Log.d("sign up exception", exception.message!!)
                }
        }
    }


    private fun validateForm(firstName:String, lastname:String, countryCode: Long?, phone: Long?, email: String, password: String): Boolean {
        return when {
            (firstName.isNullOrEmpty()) -> {
                _messageFromViewModel.value = "Please enter first name."
                false
            }
            (lastname.isNullOrEmpty()) -> {
                _messageFromViewModel.value = "Please enter last name."
                false
            }
            (countryCode == null) -> {
                _messageFromViewModel.value = "Please enter country."
                false
            }
            (phone == null) -> {
                _messageFromViewModel.value = "Please enter phone."
                false
            }
            (email.isNullOrEmpty()) -> {
                _messageFromViewModel.value = "Please enter email."
                false
            }
            (password.isNullOrEmpty()) -> {
                _messageFromViewModel.value = "Please enter email."
                false
            }
            else -> {
                true
            }
        }
    }


}