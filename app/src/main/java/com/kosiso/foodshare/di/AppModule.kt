package com.kosiso.foodshare.di

import android.content.Context
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.kosiso.foodshare.repository.MainRepository
import com.kosiso.foodshare.repository.MainRepositoryImplementation
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
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

    @Singleton
    @Provides
    fun provideFusedLocationClient(@ApplicationContext app: Context):FusedLocationProviderClient{
        return LocationServices.getFusedLocationProviderClient(app)
    }
    @Singleton
    @Provides
    fun providelocationRequest():LocationRequest{
        return LocationRequest.create().apply {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            interval = 10000 // 5 seconds
        }
    }
}