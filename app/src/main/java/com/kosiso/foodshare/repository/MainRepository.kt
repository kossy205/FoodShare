package com.kosiso.foodshare.repository

import FoodListing
import android.app.Activity
import android.net.Uri
import androidx.lifecycle.LiveData
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.storage.UploadTask
import com.kosiso.foodshare.models.DeliveryRequest
import com.kosiso.foodshare.models.FoodRequest
import com.kosiso.foodshare.models.User
import org.imperiumlabs.geofirestore.GeoQuery

interface MainRepository {

    fun signIn(email: String, password: String): Task<AuthResult>
    fun signUp(email: String, password: String): Task<AuthResult>
    fun registerUserInDb(user:User): Task<Void>
    fun getCurrentUser(): FirebaseUser?
    fun fetchUserDetailsWithUserId(userId:String): Task<DocumentSnapshot>
    fun signOut()


    fun postFoodToFirestore(listFood: FoodListing): Task<Void>
    fun uploadImageToFirebaseStorage(activity: Activity, imageUri:Uri): UploadTask


    /**
     * Food Listings
     */
    fun fetchAllMyListings(uid: String): Task<QuerySnapshot>
    fun fetchClaimedListings(uid: String): Task<QuerySnapshot>
    fun fetchUnclaimedListings(uid: String): Task<QuerySnapshot>
    fun fetchDeliveredListings(uid: String): Task<QuerySnapshot>


    /**
     * Available Foods
     */
    fun fetchAllAvailableListings(): Task<QuerySnapshot>
    fun fetchPerishableListings(): Task<QuerySnapshot>
    fun fetchNonPerishableListings(): Task<QuerySnapshot>


    /**
     *  Perform Search
     */
    fun fetchSearchedDocuments(collection: String, uid: String, searchedText: String): Task<QuerySnapshot>


    /**
     * Food Request
     */
    fun postFoodRequest(foodRequest: FoodRequest): Task<Void>
    fun fetchAllFoodRequests(uid: String): Task<QuerySnapshot>
    fun fetchOpenFoodRequests(uid: String): Task<QuerySnapshot>
    fun fetchRedeemedFoodRequests(uid: String): Task<QuerySnapshot>
    fun fetchCompletedFoodRequests(uid: String): Task<QuerySnapshot>


    /**
     * Delivery Request
     */
    fun uploadDeliveryRequest(userId: String, deliveryRequest: DeliveryRequest): Task<Void>
    fun queryNearByDeliveryAgents(geoPoint: GeoPoint, radius: Double): GeoQuery


    /**
     * Connecting quest and delivery agent
     */
    fun assignCusToAvailableDeliveryAgent(deliveryAgentId: String): Task<Void>
    fun volunteerCollectionAssignedCusIdListener(): LiveData<DocumentSnapshot>
    fun fetchDeliveryRequestDetails(cusId: String): Task<DocumentSnapshot>
    fun changeVolunteerAcceptStatus(acceptStatus: String): Task<Void>
    fun deliveryAcceptStatusListener(deliveryAgentId: String): LiveData<String>
    fun removeFirebaseListener()
    fun changeVolunteerCusAssignedFieldBackToDefault(assignedCusId: String): Task<Void>



    /**
     * Set location using geofirestore
     */
    fun setLocationUsingGeoFirestore(userId: String, collection: String, location: GeoPoint)



    /**
     *  Delete collection
     */
    fun deleteDocFromCollection(collection: String, documentId: String): Task<Void>


}