package com.kosiso.foodshare

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.firestore.GeoPoint
import com.kosiso.foodshare.other.Constants
import com.kosiso.foodshare.repository.LocationRepository
import com.kosiso.foodshare.repository.MainRepository
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ForeGroundService: LifecycleService(){

    @Inject
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    @Inject
    lateinit var baseNotificationBuilder: NotificationCompat.Builder
    @Inject
    lateinit var locationRepository: LocationRepository
    @Inject
    lateinit var mainRepository: MainRepository

    companion object{
        val isTracking = MutableLiveData<Boolean>()
        private val _assignedCusId = MutableLiveData<String>()
        val assignedCusId: LiveData<String> = _assignedCusId
    }
    private fun postInitialValues(){
        isTracking.postValue(false)
    }

    override fun onCreate() {
        super.onCreate()
        postInitialValues()
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        isTracking.observe(this, Observer {
            updateLocationTracking(it)
        })
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            when(it.action){
                Constants.ACTION_START -> {
                    startForegroundService()
                    volunteerCollectionAssignedCusIdListener()
                }
                Constants.ACTION_STOP -> killService()
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }


    private fun startForegroundService(){
        Log.i("service 1","start service")
        isTracking.postValue(true)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            createNotificationChannel(notificationManager)
        }

        startForeground(Constants.NOTIFICATION_ID, baseNotificationBuilder.build())

    }

    private fun killService(){
        Log.i("service 1","stop service")
        postInitialValues()
        stopForeground(true)
        stopSelf()
    }


    private fun volunteerCollectionAssignedCusIdListener(){
        mainRepository.volunteerCollectionAssignedCusIdListener().observeForever{snapshot->
            val assignedCusId = snapshot.data?.get(Constants.ASSIGNED_CUSTOMER_ID)
            // dont mistake this condition for "(assignedCusId.isNotEmpty)"
            if(assignedCusId != ""){
                _assignedCusId.value = assignedCusId.toString()
                Log.i("assigned cus id", "$assignedCusId")
            }else{
                Log.i("assigned cus id is empty", "empty")
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun updateLocationTracking(isTracking: Boolean){
        Log.i("service isTracking","$isTracking")
        if(isTracking){
            locationRepository.getLocationUpdates(
                {location ->
                val geoPoint = GeoPoint(location.latitude, location.longitude)
                Log.i("service location","$geoPoint")
                },{exception ->
                    Log.i("service location exception","$exception")
                })
        }else{
            locationRepository.stopLocationUpdates()
            Log.i("service location stopped","stopped")
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(notificationManager: NotificationManager){
        // WE USED NOTIFICATION OF LOW IMPORTANCE BECAUSE WE DON'T WANT IT TO MK SOUNDS WHENEVER WE UPDATE NOTIFICATION
        // WE WOULD BE UPDATING NOTIFICATION EVERY SECOND
        val channel = NotificationChannel(
            Constants.NOTIFICATION_CHANNEL_ID,
            Constants.NOTIFICATION_CHANNEL_NAME,
            NotificationManager.IMPORTANCE_LOW
        )
        notificationManager.createNotificationChannel(channel)
    }

}