package com.kosiso.foodshare.repository

import androidx.lifecycle.LiveData
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.kosiso.foodshare.models.User
import com.kosiso.foodshare.other.Constants
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class MainRepositoryImplementation @Inject constructor(
    val firebaseAuth: FirebaseAuth, val firestore:FirebaseFirestore): MainRepository{


    override fun signIn(email: String, password: String): Task<AuthResult> {
        return firebaseAuth.signInWithEmailAndPassword(email, password)
    }

    override fun signUp(email: String, password: String): Task<AuthResult> {
        return firebaseAuth.createUserWithEmailAndPassword(email, password)
    }

    override fun registerUserInDb(userInfo: User): Task<Void> {
        return firestore.collection(Constants.USERS)
                .document(getCurrentUser()!!.uid)
                .set(userInfo, SetOptions.merge())

    }

    override fun getCurrentUser(): FirebaseUser? {
        return firebaseAuth.currentUser
    }

    override fun signOut() {
        firebaseAuth.signOut()
    }

}