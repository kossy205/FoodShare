package com.kosiso.foodshare.di

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.kosiso.foodshare.R
import com.kosiso.foodshare.other.Constants
import com.kosiso.foodshare.ui.activities.VolunteerActivity
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ServiceComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ServiceScoped


@Module
@InstallIn(ServiceComponent::class)
object ServiceModule {

    @ServiceScoped
    @Provides
    fun provideBaseNotificationBuilder(
        @ApplicationContext app: Context,
        pendingIntent: PendingIntent
    ) = NotificationCompat.Builder(app, Constants.NOTIFICATION_CHANNEL_ID)
        .setAutoCancel(false)
        .setOngoing(true)
        .setSmallIcon(R.drawable.ic_location_60)
        .setContentTitle("FoodShare")
        .setContentText("You're now active and would be connected to a delivery soonest.")
        .setContentIntent(pendingIntent)


    @ServiceScoped
    @Provides
    fun provideVolunteerActivityPendingIntent(
        @ApplicationContext app: Context
    ) = PendingIntent.getActivity(
        app,
        0,
        Intent(app, VolunteerActivity::class.java).also {
            it.action = Constants.ACTION_SHOW_VOLUNTEER_UPDATE_FRAGMENT
        },
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )
}