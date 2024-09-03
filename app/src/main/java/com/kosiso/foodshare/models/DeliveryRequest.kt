package com.kosiso.foodshare.models

import com.google.firebase.Timestamp
import com.google.firebase.firestore.GeoPoint

data class DeliveryRequest(
    val uniqueId:String = "",
    val guestId:String = "",
    val donorId:String = "",
    val nameOfItem:String = "",
    val itemDescription:String = "",
    val foodImg: String = "",
    val listingCategory:String = "", // we have perishable and non-perishable
    val foodWeight:Int = 0,
    val status:String = "", // we have Unclaimed, Claimed and Delivered
    val foodListedTime: Timestamp = Timestamp.now(),// this is the time the food was listed
    val foodListingLocation: GeoPoint? = null,
    val expiryDate: Timestamp? = null
)
