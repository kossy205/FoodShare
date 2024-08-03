package com.kosiso.foodshare.ui.viewmodels.donor

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.QuerySnapshot
import com.kosiso.foodshare.models.FoodListing
import com.kosiso.foodshare.repository.MainRepository
import com.kosiso.foodshare.ui.adapter.ListingsAdapter
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ListingsViewModel @Inject constructor(val mainRepository: MainRepository): ViewModel() {

    private var listingsAdapter: ListingsAdapter = ListingsAdapter()

    private val currentUserId = mainRepository.getCurrentUser()!!.uid

    private val _allFoodListings = MutableLiveData<Result<QuerySnapshot>>()
    val allFoodListings: LiveData<Result<QuerySnapshot>> = _allFoodListings

    private val _unClaimedFoodListings = MutableLiveData<Result<QuerySnapshot>>()
    val unClaimedFoodListings: LiveData<Result<QuerySnapshot>> = _unClaimedFoodListings

    private val _claimedFoodListings = MutableLiveData<Result<QuerySnapshot>>()
    val claimedFoodListings: LiveData<Result<QuerySnapshot>> = _claimedFoodListings

    private val _deliveredFoodListings = MutableLiveData<Result<QuerySnapshot>>()
    val deliveredFoodListings: LiveData<Result<QuerySnapshot>> = _deliveredFoodListings

    private val _searchedListings = MutableLiveData<Result<QuerySnapshot>>()
    val searchedListings: LiveData<Result<QuerySnapshot>> = _searchedListings

    private val _messageFromViewModel = MutableLiveData<String>()
    val messageFromViewModel: LiveData<String> = _messageFromViewModel





    fun fetchAllFoodListings(){
        mainRepository.fetchAllListings(currentUserId)
            .addOnSuccessListener {querySnapshot->
                if(querySnapshot.isEmpty){
                    _messageFromViewModel.value = "No Listings To Show"
                }else{
                    _allFoodListings.value = Result.success(querySnapshot)
                }
            }
    }

    fun fetchClaimedFoodListings(){
        mainRepository.fetchClaimedListings(currentUserId)
            .addOnSuccessListener {querySnapshot->
                if(querySnapshot.isEmpty){
                    _messageFromViewModel.value = "No Claimed Listings To Show"
                }else{
                    _claimedFoodListings.value = Result.success(querySnapshot)
                }
            }
    }

    fun fetchUnclaimedFoodListings(){
        mainRepository.fetchUnclaimedListings(currentUserId)
            .addOnSuccessListener {querySnapshot->
                if(querySnapshot.isEmpty){
                    _messageFromViewModel.value = "No Unclaimed Listings To Show"
                }else{
                    _unClaimedFoodListings.value = Result.success(querySnapshot)
                }
            }
    }

    fun fetchDeliveredFoodListings(){
        mainRepository.fetchDeliveredListings(currentUserId)
            .addOnSuccessListener {querySnapshot->
                if(querySnapshot.isEmpty){
                    _messageFromViewModel.value = "No Delivered Listings To Show"
                }else{
                    _deliveredFoodListings.value = Result.success(querySnapshot)
                }
            }
    }

    fun fetchSearchedListings(searchedText: String){
        mainRepository.fetchSearchedListings(currentUserId, searchedText)
            .addOnSuccessListener {querySnapshot->
                if(querySnapshot.isEmpty){
                    _messageFromViewModel.value = "No Food item matched your search: $searchedText"
                }else{
                    _searchedListings.value = Result.success(querySnapshot)
                }
            }
            .addOnFailureListener {
                _messageFromViewModel.value = it.message
                Log.i("search failure", it.message.toString())
            }
    }

}