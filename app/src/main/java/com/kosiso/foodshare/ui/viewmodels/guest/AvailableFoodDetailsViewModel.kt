package com.kosiso.foodshare.ui.viewmodels.guest


import android.os.CountDownTimer
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import com.google.firebase.Timestamp
import com.google.firebase.firestore.GeoPoint
import com.kosiso.foodshare.ForeGroundService
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
    var volunteerId: String? = null
    private lateinit var geoQueryEventListener: GeoQueryEventListener
    private lateinit var geoQuery: GeoQuery
    private var isGeoQueryActive: Boolean = false
    private var countDownTimer: CountDownTimer? = null
    private lateinit var deliveryAcceptStatusObserver: Observer<String>


    private val _deliveryAcceptResult = MutableLiveData<Result<Boolean>>()
    val deliveryAcceptResult: LiveData<Result<Boolean>> = _deliveryAcceptResult

    private val _listenerRemovedResult = MutableLiveData<Result<Boolean>>()
    val listenerRemovedResult: LiveData<Result<Boolean>> = _listenerRemovedResult

    private val _uploadDeliveryRequest = MutableLiveData<Result<String>>()
    val uploadDeliveryRequest: LiveData<Result<String>> = _uploadDeliveryRequest

    private val _setDeliveryLocationResult = MutableLiveData<Result<String>>()
    val setDeliveryLocationResult: LiveData<Result<String>> = _setDeliveryLocationResult

    private val _messageFromViewModel = MutableLiveData<String>()
    val messageFromViewModel: LiveData<String> = _messageFromViewModel


    fun uploadDeliveryRequest(
        donorId: String,
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
            donorId,
            nameOfItem,
            itemDescription,
            foodImg,
            listingCategory,
            foodWeight,
            status,
            foodListedTime,
            GeoPoint(38.9071928, -77.0368728),
            expiryDate
        )

        mainRepository.uploadDeliveryRequest(mainRepository.getCurrentUser()!!.uid, deliveryRequest)
            .addOnSuccessListener {
                Log.i("delivery request success","success")
                setDeliveryLocation(mainRepository.getCurrentUser()!!.uid, GeoPoint(38.9071928, -77.0368728))
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
        val radius = 3.0
        geoQuery = mainRepository.queryNearByDeliveryAgents(geoPoint, radius)

        geoQueryEventListener = object : GeoQueryEventListener{
                override fun onGeoQueryError(exception: Exception) {
                    Log.i("geoquery error", "$exception")
                }

                override fun onGeoQueryReady() {
                    Log.i("geoquery Ready 1", "onGeoQueryReady")

                    countDownTimer = object : CountDownTimer(90000, 1000){
                        override fun onTick(millisUntilFinished: Long) {
                            if (!hasFoundMatch) {

                                Log.i("geoquery Ready ", "onGeoQueryReady timer")
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
                        fetchVolunteerDetails(volunteerId!!)
                        assignCusToAvailableDeliveryAgent(volunteerId!!)
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
        isGeoQueryActive = true
    }

    fun removeGeoQueryEventListeners(){
        if (isGeoQueryActive) {
            geoQuery.removeGeoQueryEventListener(geoQueryEventListener)
            countDownTimer?.cancel()
            countDownTimer = null
            isGeoQueryActive = false
            _listenerRemovedResult.value = Result.success(true)
            deleteDeliveryRequestDoc()
            Log.i("geoquery remove listener", "Listener removed")
        } else {
            Log.i("geoquery remove listener", "No active listener to remove")
        }
    }

    private fun fetchVolunteerDetails(volunteerId: String){
        mainRepository.fetchUserDetailsWithUserId(volunteerId)
            .addOnSuccessListener {
                Log.i("fetch Volunteer Details", "success: $it")
            }
            .addOnFailureListener {
                Log.i("fetch Volunteer Details", "failure: $it")
            }
    }

    private fun assignCusToAvailableDeliveryAgent(deliveryAgentId: String){
        mainRepository.assignCusToAvailableDeliveryAgent(deliveryAgentId)
            .addOnSuccessListener {
                Log.i("assign Cus To Available Delivery Agent", "success")
                deliveryAcceptStatusListener(deliveryAgentId)
            }
            .addOnFailureListener {
                Log.i("assign Cus To Available Delivery Agent", "failed")
            }
    }

    private fun deliveryAcceptStatusListener(deliveryAgentId: String){
        deliveryAcceptStatusObserver = Observer { acceptStatus ->
            when (acceptStatus) {
                Constants.TRUE -> {
                    _deliveryAcceptResult.value = Result.success(true)
                    Log.i("delivery accept status is ->", acceptStatus)
                }

                Constants.FALSE -> {
                    // hide progress button and show main button
                    // show snackBar to tell guest the delivery was rejected and they should try again
                    _deliveryAcceptResult.value = Result.success(false)
                    removeFirebaseListener()
                    Log.i("delivery accept status is ->", acceptStatus)
                }
            }

        }
        mainRepository.deliveryAcceptStatusListener(deliveryAgentId)
            .observeForever(deliveryAcceptStatusObserver)
    }

    private fun deleteDeliveryRequestDoc(){
        mainRepository.deleteDocFromCollection(Constants.DELIVERY_REQUESTS, mainRepository.getCurrentUser()!!.uid)
            .addOnSuccessListener {
                Log.i("delete Delivery Request Doc ->", "successful")
            }
            .addOnFailureListener {
                Log.i("delete Delivery Request Doc ->", "failed: $it")
            }
    }

    private fun removeDeliveryAcceptStatusObserver(){
        volunteerId?.let {volunteerId->
            deliveryAcceptStatusObserver.let {observer ->
                mainRepository.deliveryAcceptStatusListener(volunteerId)
                    .removeObserver(observer)
                Log.i("delivery Accept Status Observer"," removed")
            }
        }
    }

    private fun removeFirebaseListener(){
        mainRepository.removeFirebaseListener()
    }

    // the volunteer Id here is still the delivery agent id gotten.
    // that should not confuse you
    override fun onCleared() {
        super.onCleared()
        removeDeliveryAcceptStatusObserver()
        removeFirebaseListener()
    }


}