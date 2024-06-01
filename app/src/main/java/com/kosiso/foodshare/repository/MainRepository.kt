package com.kosiso.foodshare.repository

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseUser
import com.kosiso.foodshare.models.User

interface MainRepository {

    fun signIn(email: String, password: String): Task<AuthResult>
    fun signUp(email: String, password: String): Task<AuthResult>
    fun registerUserInDb(user:User): Task<Void>
    fun getCurrentUser(): FirebaseUser?
    fun signOut()

}