package com.kosiso.foodshare.repository

import FoodListing
import android.app.Activity
import android.net.Uri
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.kosiso.foodshare.models.FoodRequest
import com.kosiso.foodshare.models.User
import com.kosiso.foodshare.other.Constants
import com.kosiso.foodshare.other.Utilities
import javax.inject.Inject

class MainRepositoryImplementation @Inject constructor(
    val firebaseAuth: FirebaseAuth, val firestore:FirebaseFirestore): MainRepository{


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
     * Sign out
     */
    override fun signOut() {
        firebaseAuth.signOut()
    }

}