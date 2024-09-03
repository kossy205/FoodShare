package com.kosiso.foodshare.repository

import FoodListing
import android.app.Activity
import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.kosiso.foodshare.di.AppModule
import com.kosiso.foodshare.models.DeliveryRequest
import com.kosiso.foodshare.models.FoodRequest
import com.kosiso.foodshare.models.User
import com.kosiso.foodshare.other.Constants
import com.kosiso.foodshare.other.Utilities
import org.imperiumlabs.geofirestore.GeoFirestore
import org.imperiumlabs.geofirestore.GeoQuery
import org.imperiumlabs.geofirestore.extension.setLocation
import org.imperiumlabs.geofirestore.listeners.GeoQueryEventListener
import javax.inject.Inject

class MainRepositoryImplementation @Inject constructor(
    val firebaseAuth: FirebaseAuth,
    val firestore:FirebaseFirestore,
    val geoFirestoreProvider: (String) -> GeoFirestore
): MainRepository{

    private var geoQuery: GeoQuery? = null
    private lateinit var listenerRegistration: ListenerRegistration

    override fun signIn(email: String, password: String): Task<AuthResult> {
        return firebaseAuth.signInWithEmailAndPassword(email, password)
    }

    override fun signUp(email: String, password: String): Task<AuthResult> {
        return firebaseAuth.createUserWithEmailAndPassword(email, password)
    }

    override fun registerUserInDb(user: User): Task<Void> {
        return firestore.collection(Constants.USERS)
                .document(getCurrentUser()!!.uid)
                .set(user, SetOptions.merge())
    }

    override fun postFoodToFirestore(listFood: FoodListing): Task<Void> {
        return firestore.collection(Constants.LISTINGS)
            .document()
            .set(listFood, SetOptions.merge())
    }

    override fun getCurrentUser(): FirebaseUser? {
        return firebaseAuth.currentUser
    }

    override fun fetchUserDetailsWithUserId(userId: String): Task<DocumentSnapshot> {
        return firestore.collection(Constants.USERS)
                 .document(userId)
                 .get()
    }

    override fun uploadImageToFirebaseStorage(activity:Activity, imageUri: Uri): UploadTask {
        val sRef: StorageReference = FirebaseStorage.getInstance().reference.child(
            "FOOD_ITEM_IMAGE" + System.currentTimeMillis() + "."
                    + Utilities.getFileExtension(activity, imageUri))
        return sRef.putFile(imageUri)
    }


    /**
     * Foods Listings
     */
    override fun fetchAllMyListings(uid: String): Task<QuerySnapshot> {
        return firestore.collection(Constants.LISTINGS)
            .whereEqualTo(Constants.USER_ID, uid)
            .get()
    }
    override fun fetchClaimedListings(uid: String): Task<QuerySnapshot> {
        return firestore.collection(Constants.LISTINGS)
            .whereEqualTo(Constants.USER_ID, uid)
            .whereEqualTo(Constants.STATUS, Constants.CLAIMED_LIST_FOOD)
            .get()
    }
    override fun fetchUnclaimedListings(uid: String): Task<QuerySnapshot> {
        return firestore.collection(Constants.LISTINGS)
            .whereEqualTo(Constants.USER_ID, uid)
            .whereEqualTo(Constants.STATUS, Constants.UNCLAIMED_LIST_FOOD)
            .get()
    }
    override fun fetchDeliveredListings(uid: String): Task<QuerySnapshot> {
        return firestore.collection(Constants.LISTINGS)
            .whereEqualTo(Constants.USER_ID, uid)
            .whereEqualTo(Constants.STATUS, Constants.DELIVERED_LIST_FOOD)
            .get()
    }



    /**
     * Available Foods
     */
    override fun fetchAllAvailableListings(): Task<QuerySnapshot> {
        return firestore.collection(Constants.LISTINGS)
            .get()
    }
    override fun fetchPerishableListings(): Task<QuerySnapshot> {
        return firestore.collection(Constants.LISTINGS)
            .whereEqualTo(Constants.LISTING_CATEGORY, Constants.PERISHABLE_FOODS)
            .get()
    }
    override fun fetchNonPerishableListings(): Task<QuerySnapshot> {
        return firestore.collection(Constants.LISTINGS)
            .whereEqualTo(Constants.LISTING_CATEGORY, Constants.NON_PERISHABLE_FOODS)
            .get()
    }


    /**
     * Food Request
     */
    override fun postFoodRequest(foodRequest: FoodRequest): Task<Void> {
        return firestore.collection(Constants.FOOD_REQUESTS)
            .document()
            .set(foodRequest, SetOptions.merge())
    }
    override fun fetchAllFoodRequests(uid: String): Task<QuerySnapshot> {
        return firestore.collection(Constants.FOOD_REQUESTS)
            .whereEqualTo(Constants.USER_ID, uid)
            .get()
    }
    override fun fetchOpenFoodRequests(uid: String): Task<QuerySnapshot> {
        return firestore.collection(Constants.FOOD_REQUESTS)
            .whereEqualTo(Constants.USER_ID, uid)
            .whereEqualTo(Constants.STATUS, Constants.OPEN_FOOD_REQUESTS)
            .get()
    }
    override fun fetchRedeemedFoodRequests(uid: String): Task<QuerySnapshot> {
        return firestore.collection(Constants.FOOD_REQUESTS)
            .whereEqualTo(Constants.USER_ID, uid)
            .whereEqualTo(Constants.STATUS, Constants.REDEEMED_FOOD_REQUESTS)
            .get()
    }
    override fun fetchCompletedFoodRequests(uid: String): Task<QuerySnapshot> {
        return firestore.collection(Constants.FOOD_REQUESTS)
            .whereEqualTo(Constants.USER_ID, uid)
            .whereEqualTo(Constants.STATUS, Constants.COMPLETED_FOOD_REQUESTS)
            .get()
    }


    /**
     * perform search
     */
    override fun fetchSearchedDocuments(collection: String, uid: String, searchedText: String): Task<QuerySnapshot> {
        return firestore.collection(collection)
            .whereEqualTo(Constants.USER_ID, uid)
            .orderBy(Constants.NAME_OF_ITEM)
            .startAt(searchedText)
            .endAt(searchedText + "\uf8ff")
            .get()
    }


    /**
     * Delivery Request
     */
    override fun uploadDeliveryRequest(userId: String, deliveryRequest: DeliveryRequest): Task<Void> {
        return firestore.collection(Constants.DELIVERY_REQUESTS)
            .document(userId)
            .set(deliveryRequest, SetOptions.merge())
    }

    override fun setLocationUsingGeoFirestore(userId: String, collection: String, location: GeoPoint) {
        val geoFirestore = geoFirestoreProvider(collection)
        geoFirestore.setLocation(userId, location)
    }

    override fun queryNearByDeliveryAgents(geoPoint: GeoPoint, radius: Double): GeoQuery {
        val geoFirestore = geoFirestoreProvider(Constants.AVAILABLE_VOLUNTEERS)

        return geoFirestore.queryAtLocation(geoPoint, radius)

    }


    /**
     * Connecting quest and delivery agent
     */
    override fun assignCusToAvailableDeliveryAgent(deliveryAgentId: String): Task<Void> {
        // When a volunteer or delivery agent is found by a guest, the volunteer id is gotten by the guest,
        // ... and in the guest activity, the guest's id is passed to the volunteer "assignedCusId" field of the volunteer using the volunteer id.
        // That's why you have the "getCurrentUser()!!.uid.toString()" there.
        val assignCusId = hashMapOf<String, Any>(
            Constants.ASSIGNED_CUSTOMER_ID to getCurrentUser()!!.uid.toString()
        )
        return firestore.collection(Constants.USERS)
                   .document(deliveryAgentId)
                   .update(assignCusId)
    }

    // for delivery agent to know if they have been found by a guest
    override fun volunteerCollectionAssignedCusIdListener(): LiveData<DocumentSnapshot>{
        val livaDate = MutableLiveData<DocumentSnapshot>()
        val query = firestore.collection(Constants.USERS)
            .document(getCurrentUser()!!.uid)
            listenerRegistration = query.addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.i("volunteer Collection Assigned Cus Id Listener failed.", "$e")
                    return@addSnapshotListener
                }

                if (snapshot != null && snapshot.exists()) {
                    Log.i("volunteer Collection Assigned Cus Id Listener", "$snapshot")
                    livaDate.value = snapshot!!
                }
            }
        return livaDate
    }

    override fun fetchDeliveryRequestDetails(cusId: String): Task<DocumentSnapshot>{
        return firestore.collection(Constants.DELIVERY_REQUESTS)
            .document(cusId)
            .get()
    }

    override fun changeVolunteerAcceptStatus(acceptStatus: String): Task<Void> {
        val hasAcceptStatus = hashMapOf<String, Any>(
            Constants.HAS_ACCEPTED to acceptStatus
        )
        return firestore.collection(Constants.USERS)
            .document(getCurrentUser()!!.uid.toString())
            .update(hasAcceptStatus)
    }


    override fun deliveryAcceptStatusListener(deliveryAgentId: String): LiveData<String> {
        val livaDate = MutableLiveData<String>()
        val query = firestore.collection(Constants.USERS)
            .document(deliveryAgentId)
            listenerRegistration = query.addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.i("delivery Accept Status Listener failed.", "$e")
                    return@addSnapshotListener
                }

                if (snapshot != null && snapshot.exists()) {
                    Log.i("volunteer Collection Assigned Cus Id Listener", "$snapshot")
                    livaDate.value = snapshot.data?.get(Constants.HAS_ACCEPTED).toString()
                }
            }
        return livaDate
    }

    override fun removeFirebaseListener(){
        listenerRegistration.remove()
    }

    override fun changeVolunteerCusAssignedFieldBackToDefault(assignedCusId: String): Task<Void> {
        val assignCus = hashMapOf<String, Any>(
            Constants.ASSIGNED_CUSTOMER_ID to assignedCusId
        )
        return firestore.collection(Constants.USERS)
            .document(getCurrentUser()!!.uid.toString())
            .update(assignCus)
    }




    /**
     *  Delete collection
     */
    override fun deleteDocFromCollection(collection: String, documentId: String): Task<Void> {
        return firestore.collection(collection)
            .document(documentId)
            .delete()
    }


    /**
     * Sign out
     */
    override fun signOut() {
        firebaseAuth.signOut()
    }

}