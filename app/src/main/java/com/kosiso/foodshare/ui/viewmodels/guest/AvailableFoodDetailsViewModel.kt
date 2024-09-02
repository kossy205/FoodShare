package com.kosiso.foodshare.ui.viewmodels.guest


import android.os.CountDownTimer
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.Timestamp
import com.google.firebase.firestore.GeoPoint
import com.kosiso.foodshare.models.DeliveryRequest
import com.kosiso.foodshare.other.Constants
import com.kosiso.foodshare.repository.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import org.imperiumlabs.geofirestore.GeoQuery
import org.imperiumlabs.geofirestore.listeners.GeoQueryEventListener
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class AvailableFoodDetailsViewModel @Inject constructor(val mainRepository: MainRepository): ViewModel(){

    var hasFoundMatch = false
    private lateinit var geoQueryEventListener: GeoQueryEventListener
    private lateinit var geoQuery: GeoQuery

    private val _uploadDeliveryRequest = MutableLiveData<Result<String>>()
    val uploadDeliveryRequest: LiveData<Result<String>> = _uploadDeliveryRequest

    private val _setDeliveryLocationResult = MutableLiveData<Result<String>>()
    val setDeliveryLocationResult: LiveData<Result<String>> = _setDeliveryLocationResult

    private val _messageFromViewModel = MutableLiveData<String>()
    val messageFromViewModel: LiveData<String> = _messageFromViewModel


    fun uploadDeliveryRequest(
        nameOfItem:String,
        itemDescription:String,
        foodImg: String,
        listingCategory:String, // we have perishable and non-perishable
        foodWeight:Int,
        status:String, // we have Unclaimed, Claimed and Delivered
        foodListedTime: Timestamp ,// this is the time the food was listed
        foodListingLocation: GeoPoint?,
        expiryDate: Timestamp?
    ){
        val deliveryRequest = DeliveryRequest(
            UUID.randomUUID().toString(),
            mainRepository.getCurrentUser()!!.uid,
            nameOfItem,
            itemDescription,
            foodImg,
            listingCategory,
            foodWeight,
            status,
            foodListedTime,
            foodListingLocation,
            expiryDate
        )

        mainRepository.uploadDeliveryRequest(mainRepository.getCurrentUser()!!.uid, deliveryRequest)
            .addOnSuccessListener {
                Log.i("delivery request success","success")
                setDeliveryLocation(mainRepository.getCurrentUser()!!.uid, foodListingLocation!!)
                _uploadDeliveryRequest.value = Result.success("Your claim has been posted")
            }
            .addOnFailureListener {
                Log.i("delivery request failure", it.message!!)
            }
    }

    private fun setDeliveryLocation(userId: String, location: GeoPoint){

        mainRepository.setLocationUsingGeoFirestore(userId, Constants.DELIVERY_REQUESTS, location)
        try {
            _setDeliveryLocationResult.value = Result.success("Searching For Delivery Agent...")
            Log.i("set location success", "success")

            queryAvailableVolunteers(location)

        } catch (e: Exception){
            _setDeliveryLocationResult.value = Result.failure(e)
            Log.i("set location failure", "$e")
        }
    }

    private fun queryAvailableVolunteers(geoPoint: GeoPoint){
        var volunteerId = ""
        val radius = 3.0
        geoQuery = mainRepository.queryNearByDeliveryAgents(geoPoint, radius)

        geoQueryEventListener = object : GeoQueryEventListener{
                override fun onGeoQueryError(exception: Exception) {
                    Log.i("geoquery error", "$exception")
                }

                override fun onGeoQueryReady() {
                    Log.i("geoquery Ready 1", "onGeoQueryReady")

                    val countDownTimer = object : CountDownTimer(120000, 1000){
                        override fun onTick(millisUntilFinished: Long) {
                            if (!hasFoundMatch) {

                                Log.i("geoquery Ready ", "onGeoQueryReady")
                                Log.i("geoquery Ready timer ", "remaining time is ${millisUntilFinished/1000}")
                            }else{
                                removeGeoQueryEventListeners()
                                this.cancel()
                            }
                        }

                        override fun onFinish() {
                            if(!hasFoundMatch){
                                removeGeoQueryEventListeners()
                            }
                        }
                    }.start()
                }

                override fun onKeyEntered(documentID: String, location: GeoPoint) {

                    Log.i("geoquery qualified 1", "$volunteerId is in the radius.")
                    if(!hasFoundMatch){
                        hasFoundMatch = true

                        volunteerId = documentID
                        fetchVolunteerDetails(volunteerId)
                        assignCusToAvailableDeliveryAgent(volunteerId)
                        Log.i("geoquery qualified", "$volunteerId is in the radius.")
                        removeGeoQueryEventListeners()
                    }
                }

                override fun onKeyExited(documentID: String) {
                    Log.i("geoquery onKeyExit", "location no longer in the radius")
                }

                override fun onKeyMoved(documentID: String, location: GeoPoint) {
                    Log.i("geoquery onKeyMoved", "moved but still in radius location is $location")
                }
            }

        geoQuery.addGeoQueryEventListener(geoQueryEventListener)

    }

    private fun removeGeoQueryEventListeners(){
        geoQuery.removeGeoQueryEventListener(geoQueryEventListener)
        Log.i("geoquery remove listener", "remove")
    }

    private fun fetchVolunteerDetails(volunteerId: String){
        mainRepository.fetchUserDetailsWithUserId(volunteerId)
            .addOnSuccessListener {

            }
            .addOnFailureListener {

            }
    }

    private fun assignCusToAvailableDeliveryAgent(deliveryAgentId: String){
        mainRepository.assignCusToAvailableDeliveryAgent(deliveryAgentId)
            .addOnSuccessListener {
                Log.i("assign Cus To Available Delivery Agent", "success")
            }
            .addOnFailureListener {
                Log.i("assign Cus To Available Delivery Agent", "failed")
            }
    }

    //demo
//    fun setVolunteerLocation(location: GeoPoint){
//        mainRepository.setLocationUsingGeoFirestore("v1zoqixll3hPdqO1pNUOW8aDK3x2", Constants.AVAILABLE_VOLUNTEERS, location)
//    }


}