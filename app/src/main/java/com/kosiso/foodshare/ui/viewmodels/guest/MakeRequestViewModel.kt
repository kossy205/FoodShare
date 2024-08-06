package com.kosiso.foodshare.ui.viewmodels.guest

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.GeoPoint
import com.kosiso.foodshare.models.FoodRequest
import com.kosiso.foodshare.other.Constants
import com.kosiso.foodshare.repository.LocationRepository
import com.kosiso.foodshare.repository.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.Date
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class MakeRequestViewModel @Inject constructor(val mainRepository: MainRepository, val locationRepository: LocationRepository): ViewModel(){

    private lateinit var currentLocation: GeoPoint

    private val _messageFromViewModel = MutableLiveData<String>()
    val messageFromViewModel: LiveData<String> = _messageFromViewModel

    private val _postFoodRequestResult = MutableLiveData<Result<Void>>()
    val postFoodRequestResult: LiveData<Result<Void>> = _postFoodRequestResult

    private val _validateForm = MutableLiveData<Boolean>()
    val validateForm: LiveData<Boolean> = _validateForm


    fun postFoodRequest(nameOfItem:String,
                        itemDescription:String,
                        foodWeight: Int?){

        if(::currentLocation.isInitialized){
            if (validateForm(nameOfItem, itemDescription, foodWeight)){

                val foodRequest = FoodRequest(
                    UUID.randomUUID().toString(),
                    mainRepository.getCurrentUser()!!.uid,
                    nameOfItem,
                    itemDescription,
                    foodWeight!!,
                    Constants.OPEN_FOOD_REQUESTS,
                    getCurrentTimestamp(),
                    currentLocation
                )

                mainRepository.postFoodRequest(foodRequest)
                    .addOnSuccessListener {
                        _postFoodRequestResult.value = Result.success(it)
                        _messageFromViewModel.value = "Request Posted Successfully!"
                        stopLocationUpdates()
                    }
                    .addOnFailureListener {
                        _postFoodRequestResult.value = Result.failure(it)
                        _messageFromViewModel.value = "Request Posted Failed: $it!"
                    }
            }
        }else{
            val customException: Exception = Exception("unable to get current location. pls turn on location")
            _postFoodRequestResult.value = Result.failure(customException)
            _messageFromViewModel.value = "unable to get current location. pls turn on location"
        }

    }


    fun getLocationUpdates(){
        locationRepository.getLocationUpdates(
            {location ->
            val geoPoint = GeoPoint(location.latitude, location.longitude)
                currentLocation = geoPoint
            },
            {exception ->
                _messageFromViewModel.value = exception.message
            }
        )
    }

    fun stopLocationUpdates(){
        locationRepository.stopLocationUpdates()
    }

    private fun getCurrentTimestamp(): Timestamp {
        val currentDate = Date()
        return Timestamp(currentDate)
    }

    private fun validateForm(nameOfItem:String,
                             itemDescription:String,
                             foodWeight: Int?): Boolean {
        return when {
            (nameOfItem.isNullOrEmpty()) -> {
                _validateForm.value = false
                _messageFromViewModel.value = "Please enter name of food."
                false
            }
            (itemDescription.isNullOrEmpty()) -> {
                _validateForm.value = false
                _messageFromViewModel.value = "Please enter description."
                false
            }
            (foodWeight == null) -> {
                _validateForm.value = false
                _messageFromViewModel.value = "Please enter food quantity/weight."
                false
            }
            else -> {
                _validateForm.value = true
                true
            }
        }
    }


}