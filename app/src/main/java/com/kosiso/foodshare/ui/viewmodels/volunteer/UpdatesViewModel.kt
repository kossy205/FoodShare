package com.kosiso.foodshare.ui.viewmodels.volunteer

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.google.firebase.firestore.GeoPoint
import com.kosiso.foodshare.ForeGroundService
import com.kosiso.foodshare.models.DeliveryRequest
import com.kosiso.foodshare.other.Constants
import com.kosiso.foodshare.repository.LocationRepository
import com.kosiso.foodshare.repository.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class UpdatesViewModel @Inject constructor(val mainRepository: MainRepository, val locationRepository: LocationRepository): ViewModel() {

    private var assignedCusIdObserver: Observer<String>? = null

    private val _deliveryRequestDetails = MutableLiveData<Result<DeliveryRequest>>()
    val deliveryRequestDetails: LiveData<Result<DeliveryRequest>> = _deliveryRequestDetails


    init {
        listenForAssignedCusId()
    }


    private fun listenForAssignedCusId(){
        assignedCusIdObserver = Observer<String> { assignedCusId->
            Log.i("assigned cus id 2", assignedCusId)
            // don't mistake the below condition for "(assignedCusId.isNotEmpty)"
            if (assignedCusId != "") {
                fetchDeliveryRequestDetails(assignedCusId)
            }else{
                Log.i("assignedCusId has an empty string", "empty string")
            }
        }
        ForeGroundService.assignedCusId.observeForever(assignedCusIdObserver!!)
    }


    private fun fetchDeliveryRequestDetails(cusId: String){
        mainRepository.fetchDeliveryRequestDetails(cusId)
            .addOnSuccessListener {document->
                val deliveryRequestDetails = document.toObject(DeliveryRequest::class.java)
                if (deliveryRequestDetails != null) {
                    _deliveryRequestDetails.value = Result.success(deliveryRequestDetails)
                    Log.i("fetch Delivery Request Details", "success: $deliveryRequestDetails")
                }
            }
            .addOnFailureListener {e->
                Log.i("fetch Delivery Request Details", "failed: $e")
                _deliveryRequestDetails.value = Result.failure(e)
            }
    }

    fun changeVolunteerAcceptStatus(acceptStatus: String){
        mainRepository.changeVolunteerAcceptStatus(acceptStatus)
            .addOnSuccessListener {
                returnEverythingToDefault()
                Log.i("change Volunteer Accept Status", "success: $acceptStatus")
            }
            .addOnFailureListener {
                Log.i("change Volunteer Accept Status", "failure: $it")
            }
    }

    private fun changeVolunteerCusAssignedFieldBackToDefault(assignedCusId: String){
        mainRepository.changeVolunteerCusAssignedFieldBackToDefault(assignedCusId)
            .addOnSuccessListener {
                returnEverythingToDefault()
                Log.i("change Volunteer Cus Assigned Field Back To Default", "success: $assignedCusId")
            }
            .addOnFailureListener {
                Log.i("change Volunteer Cus Assigned Field Back To Default", "failure: $it")
            }
    }


    private fun returnEverythingToDefault(){
        // returns "hasAccepted" back to ""
        changeVolunteerAcceptStatus("")
        changeVolunteerCusAssignedFieldBackToDefault("")
    }

    override fun onCleared() {
        super.onCleared()
        // Remove the observer when the ViewModel is cleared to prevent memory leaks
        assignedCusIdObserver?.let {
            ForeGroundService.assignedCusId.removeObserver(it)
        }
        mainRepository.removeFirebaseListener()
    }
}