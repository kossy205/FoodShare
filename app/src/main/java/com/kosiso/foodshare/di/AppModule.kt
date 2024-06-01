package com.kosiso.foodshare.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.kosiso.foodshare.repository.MainRepository
import com.kosiso.foodshare.repository.MainRepositoryImplementation
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideFirebaseAuth():FirebaseAuth = FirebaseAuth.getInstance()

    @Singleton
    @Provides
    fun provideFirestore():FirebaseFirestore = FirebaseFirestore.getInstance()


    @Singleton
    @Provides
    fun provideMainRepository(firebaseAuth: FirebaseAuth, firestore: FirebaseFirestore): MainRepository {
        return MainRepositoryImplementation(firebaseAuth, firestore)
    }
}