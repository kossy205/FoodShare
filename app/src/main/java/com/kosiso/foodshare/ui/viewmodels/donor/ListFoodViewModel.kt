package com.kosiso.foodshare.ui.viewmodels.donor

import android.app.Activity
import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.Timestamp
import com.google.firebase.firestore.GeoPoint
import com.kosiso.foodshare.models.FoodListing
import com.kosiso.foodshare.repository.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.UUID
import javax.inject.Inject


@HiltViewModel
class ListFoodViewModel @Inject constructor(val mainRepository: MainRepository): ViewModel() {

    private val _postFoodResult = MutableLiveData<Result<Void>>()
    val postFoodResult: LiveData<Result<Void>> = _postFoodResult

    private val _storeImageResult = MutableLiveData<Result<String>>()
    val storeImageResult: LiveData<Result<String>> = _storeImageResult

    private val _messageFromViewModel = MutableLiveData<String>()
    val messageFromViewModel: LiveData<String> = _messageFromViewModel

    fun postFoodToFirestore(
        uid: String,
        foodItemName: String,
        foodItemDescription: String,
        foodImg: String,
        listingCategory: String,
        foodWeight: Int,
        status: String,
        currentTimestamp: Timestamp,
        currentLocation: GeoPoint?,
        expiryTimestamp: Timestamp
    ){
        val listFood = FoodListing(
            UUID.randomUUID().toString(),
            uid,
            foodItemName,
            foodItemDescription,
            foodImg,
            listingCategory,
            foodWeight,
            status,
            currentTimestamp,
            currentLocation!!,
            expiryTimestamp
        )
        mainRepository.postFoodToFirestore(listFood)
            .addOnSuccessListener {

                _postFoodResult.value = Result.success(it)
                _messageFromViewModel.value = "Food Item posted Successfully!"
            }
            .addOnFailureListener {
                _postFoodResult.value = Result.failure(it)
                _messageFromViewModel.value = "Failed: ${it.message}"
            }
    }

    // the imageToFirestorage is the image from gallery (in Uri Format) to be uploaded to firebase storage,
    // after this is stored in firebase storage, we get the firebase storage Uri. This sis the uri we...
    // would use to post the food. This way, when we fetch food listings, the uri would be from firebase...
    // storage and can be viewed by anyone with the uri.
    fun uploadImageToFirebaseStorage(activity: Activity, imageToFirestorage:Uri,
//                                     uid: String,
//                                     foodItemName: String,
//                                     foodItemDescription: String,
//                                     listingCategory: String,
//                                     foodWeight: Int,
//                                     status: String,
//                                     currentTimestamp: Timestamp,
//                                     currentLocation: GeoPoint?,
//                                     expiryTimestamp: Timestamp
    ){
        Log.e("upload to firestorage", "clicked")
        mainRepository.uploadImageToFirebaseStorage(activity, imageToFirestorage)
            .addOnSuccessListener {taskSnapshot->
                Log.e("upload to firestorage 1", "successful")
                taskSnapshot.metadata!!.reference!!.downloadUrl
                    .addOnSuccessListener { uri ->
                        Log.e("Downloadable Image URI", uri.toString())
                        _storeImageResult.value = Result.success(uri.toString())

//                        postFoodToFirestore(
//                            uid,
//                            foodItemName,
//                            foodItemDescription,
//                            uri.toString(),
//                            listingCategory,
//                            foodWeight,
//                            status,
//                            currentTimestamp,
//                            currentLocation,
//                            expiryTimestamp
//                        )
                    }
                    .addOnFailureListener {
                        Log.e("Image URI failure", it.message!!)
                        _storeImageResult.value = Result.failure(it)
                        _messageFromViewModel.value = "Failed to downLoad Img Uri: ${it.message}"
                    }
            }
            .addOnFailureListener {

                Log.e("Store img failure", it.message!!)
                _messageFromViewModel.value = "Failed to storeImage: ${it.message}"
                _storeImageResult.value = Result.failure(it)
            }
    }

    fun getCurrentUser() = mainRepository.getCurrentUser()
}