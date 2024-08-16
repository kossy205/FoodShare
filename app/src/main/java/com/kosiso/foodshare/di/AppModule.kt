package com.kosiso.foodshare.di

import android.content.Context
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.kosiso.foodshare.other.Constants
import com.kosiso.foodshare.repository.LocationRepository
import com.kosiso.foodshare.repository.LocationRepositoryImplementation
import com.kosiso.foodshare.repository.MainRepository
import com.kosiso.foodshare.repository.MainRepositoryImplementation
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import org.imperiumlabs.geofirestore.GeoFirestore
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

//    @Singleton
//    @Provides
//    fun provideDeliveryRequestGeofire(firestore: FirebaseFirestore):GeoFirestore{
//        return GeoFirestore(firestore.collection(Constants.DELIVERY_REQUESTS))
//    }
//
//    @Singleton
//    @Provides
//    fun provideAvailableVolunteersGeofire(firestore: FirebaseFirestore):GeoFirestore{
//        return GeoFirestore(firestore.collection(Constants.AVAILABLE_VOLUNTEERS))
//    }

    @Singleton
    @Provides
    fun provideGeoFirestore(firestore: FirebaseFirestore): (@JvmSuppressWildcards String) -> GeoFirestore {
        return { collectionPath ->
            GeoFirestore(firestore.collection(collectionPath))
        }
    }


    @Singleton
    @Provides
    fun provideMainRepository(firebaseAuth: FirebaseAuth,
                              firestore: FirebaseFirestore,
                              geoFirestoreProvider: (@JvmSuppressWildcards String) -> GeoFirestore): MainRepository {

        return MainRepositoryImplementation(firebaseAuth, firestore, geoFirestoreProvider)
    }

    @Singleton
    @Provides
    fun provideLocationRepository(fusedLocationProviderClient: FusedLocationProviderClient,
                                  locationRequest: LocationRequest): LocationRepository {
        return LocationRepositoryImplementation(fusedLocationProviderClient, locationRequest)
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
            interval = 5000 // 5 seconds
        }
    }

}