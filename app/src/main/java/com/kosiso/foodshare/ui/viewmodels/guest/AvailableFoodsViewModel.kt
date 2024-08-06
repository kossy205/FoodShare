package com.kosiso.foodshare.ui.viewmodels.guest

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.QuerySnapshot
import com.kosiso.foodshare.other.Constants
import com.kosiso.foodshare.repository.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AvailableFoodsViewModel @Inject constructor(private val mainRepository: MainRepository): ViewModel() {

    private val currentUserId = mainRepository.getCurrentUser()!!.uid

    private val _messageFromViewModel = MutableLiveData<String>()
    val messageFromViewModel: LiveData<String> = _messageFromViewModel

    private val _allFoodListings = MutableLiveData<Result<QuerySnapshot>>()
    val allFoodListings: LiveData<Result<QuerySnapshot>> = _allFoodListings

    private val _searchedAvailableFoods = MutableLiveData<Result<QuerySnapshot>>()
    val searchedAvailableFoods: LiveData<Result<QuerySnapshot>> = _searchedAvailableFoods

    private val _perishableAvailableFoods = MutableLiveData<Result<QuerySnapshot>>()
    val perishableAvailableFoods: LiveData<Result<QuerySnapshot>> = _perishableAvailableFoods

    private val _nonPerishableAvailableFoods = MutableLiveData<Result<QuerySnapshot>>()
    val nonPerishableAvailableFoods: LiveData<Result<QuerySnapshot>> = _nonPerishableAvailableFoods


    fun fetchAllFoodListings(){
        mainRepository.fetchAllAvailableListings()
            .addOnSuccessListener {querySnapshot->
                if(querySnapshot.isEmpty){
                    _messageFromViewModel.value = "No Food Available around you"
                }else{
                    _allFoodListings.value = Result.success(querySnapshot)
                }
            }
    }

    fun fetchPerishableFoodListings(){
        mainRepository.fetchPerishableListings()
            .addOnSuccessListener { querySnapshot->
                if(querySnapshot.isEmpty){
                    _messageFromViewModel.value = "No Delivered Listings To Show"
                }else{
                    _perishableAvailableFoods.value = Result.success(querySnapshot)
                }
            }
    }

    fun fetchNonPerishableFoodListings(){
        mainRepository.fetchNonPerishableListings()
            .addOnSuccessListener { querySnapshot->
                if(querySnapshot.isEmpty){
                    _messageFromViewModel.value = "No Delivered Listings To Show"
                }else{
                    _nonPerishableAvailableFoods.value = Result.success(querySnapshot)
                }
            }
    }


    fun fetchSearchedListings(searchedText: String){
        mainRepository.fetchSearchedDocuments(Constants.LISTINGS, currentUserId, searchedText)
            .addOnSuccessListener {querySnapshot->
                if(querySnapshot.isEmpty){
                    _messageFromViewModel.value = "No Food item matched your search: $searchedText"
                }else{
                    _searchedAvailableFoods.value = Result.success(querySnapshot)
                }
            }
            .addOnFailureListener {
                _messageFromViewModel.value = it.message
                Log.i("search failure", it.message.toString())
            }
    }

}