package com.kosiso.foodshare.repository

import android.app.Activity
import android.net.Uri
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.storage.UploadTask
import com.kosiso.foodshare.models.FoodListing
import com.kosiso.foodshare.models.User

interface MainRepository {

    fun signIn(email: String, password: String): Task<AuthResult>
    fun signUp(email: String, password: String): Task<AuthResult>
    fun registerUserInDb(user:User): Task<Void>
    fun getCurrentUser(): FirebaseUser?
    fun fetchUserDetailsWithUserId(userId:String): Task<DocumentSnapshot>
    fun signOut()
    fun postFoodToFirestore(listFood: FoodListing): Task<Void>
    fun uploadImageToFirebaseStorage(activity: Activity, imageUri:Uri): UploadTask
    fun fetchAllListings(uid: String): Task<QuerySnapshot>
    fun fetchClaimedListings(uid: String): Task<QuerySnapshot>
    fun fetchUnclaimedListings(uid: String): Task<QuerySnapshot>
    fun fetchDeliveredListings(uid: String): Task<QuerySnapshot>
    fun fetchSearchedListings(uid: String, searchedText: String): Task<QuerySnapshot>

}