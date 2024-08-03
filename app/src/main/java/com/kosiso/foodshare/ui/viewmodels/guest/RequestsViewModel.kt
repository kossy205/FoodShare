package com.kosiso.foodshare.ui.viewmodels.guest

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.QuerySnapshot
import com.kosiso.foodshare.repository.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RequestsViewModel @Inject constructor(val mainRepository: MainRepository): ViewModel(){

    private val currentUserId = mainRepository.getCurrentUser()!!.uid

    private val _allFoodRequests = MutableLiveData<Result<QuerySnapshot>>()
    val allFoodRequests: LiveData<Result<QuerySnapshot>> = _allFoodRequests

    private val _openFoodRequests = MutableLiveData<Result<QuerySnapshot>>()
    val openFoodRequests: LiveData<Result<QuerySnapshot>> = _openFoodRequests

    private val _redeemedFoodRequests = MutableLiveData<Result<QuerySnapshot>>()
    val redeemedFoodRequests: LiveData<Result<QuerySnapshot>> = _redeemedFoodRequests

    private val _completedFoodRequests = MutableLiveData<Result<QuerySnapshot>>()
    val completedFoodRequests: LiveData<Result<QuerySnapshot>> = _completedFoodRequests

    private val _messageFromViewModel = MutableLiveData<String>()
    val messageFromViewModel: LiveData<String> = _messageFromViewModel


    fun fetchAllFoodRequests(){
        mainRepository.fetchAllFoodRequests(currentUserId)
            .addOnSuccessListener {querySnapshot->
                if(querySnapshot.isEmpty){
                    _messageFromViewModel.value = "No Requests To Show"
                }else{
                    _allFoodRequests.value = Result.success(querySnapshot)
                }
            }
            .addOnFailureListener {
                _messageFromViewModel.value = it.message
            }
    }

    fun fetchOpenFoodRequests(){
        mainRepository.fetchOpenFoodRequests(currentUserId)
            .addOnSuccessListener {querySnapshot->
                if(querySnapshot.isEmpty){
                    _messageFromViewModel.value = "No Requests To Show"
                }else{
                    _openFoodRequests.value = Result.success(querySnapshot)
                }
            }
    }

    fun fetchRedeemedFoodRequests(){
        mainRepository.fetchRedeemedFoodRequests(currentUserId)
            .addOnSuccessListener {querySnapshot->
                if(querySnapshot.isEmpty){
                    _messageFromViewModel.value = "No Requests To Show"
                }else{
                    _redeemedFoodRequests.value = Result.success(querySnapshot)
                }
            }
    }

    fun fetchCompletedFoodRequests(){
        mainRepository.fetchCompletedFoodRequests(currentUserId)
            .addOnSuccessListener {querySnapshot->
                if(querySnapshot.isEmpty){
                    _messageFromViewModel.value = "No Requests To Show"
                }else{
                    _completedFoodRequests.value = Result.success(querySnapshot)
                }
            }
    }
}