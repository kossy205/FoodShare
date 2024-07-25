package com.kosiso.foodshare.repository

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
import com.kosiso.foodshare.models.FoodListing
import com.kosiso.foodshare.models.User
import com.kosiso.foodshare.other.Constants
import com.kosiso.foodshare.other.Utilities
import java.util.Locale
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

    override fun fetchAllListings(uid: String): Task<QuerySnapshot> {
        return firestore.collection(Constants.LISTINGS)
            .whereEqualTo(Constants.USER_ID, uid)
            .get()
    }

    override fun fetchClaimedListings(uid: String): Task<QuerySnapshot> {
        return firestore.collection(Constants.LISTINGS)
            .whereEqualTo(Constants.USER_ID, uid)
            .whereEqualTo(Constants.LIST_FOOD_STATUS, Constants.CLAIMED_LIST_FOOD)
            .get()
    }

    override fun fetchUnclaimedListings(uid: String): Task<QuerySnapshot> {
        return firestore.collection(Constants.LISTINGS)
            .whereEqualTo(Constants.USER_ID, uid)
            .whereEqualTo(Constants.LIST_FOOD_STATUS, Constants.UNCLAIMED_LIST_FOOD)
            .get()
    }

    override fun fetchDeliveredListings(uid: String): Task<QuerySnapshot> {
        return firestore.collection(Constants.LISTINGS)
            .whereEqualTo(Constants.USER_ID, uid)
            .whereEqualTo(Constants.LIST_FOOD_STATUS, Constants.DELIVERED_LIST_FOOD)
            .get()
    }

    override fun fetchSearchedListings(uid: String, searchedText: String): Task<QuerySnapshot> {
        return firestore.collection(Constants.LISTINGS)
            .whereEqualTo(Constants.USER_ID, uid)
            .orderBy(Constants.NAME_OF_ITEM)
            .startAt(searchedText)
            .endAt(searchedText + "\uf8ff")
            .get()
    }

    override fun signOut() {
        firebaseAuth.signOut()
    }

}