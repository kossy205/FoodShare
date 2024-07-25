package com.kosiso.foodshare.models


import com.google.firebase.Timestamp
import com.google.firebase.firestore.GeoPoint
import java.util.UUID

// the "uniqueId" field is to give the FoodListing a unique Id to be used for differ callback
data class FoodListing(
    val uniqueId:String = "",
    val uid:String = "",
    val nameOfItem:String = "",
    val itemDescription:String = "",
    val foodImg: String,
    val listingCategory:String = "", // we have perishable and non-perishable
    val foodWeight:Int = 0,
    val status:String = "", // we have Unclaimed, Claimed and Delivered
    val foodListedTime: Timestamp = Timestamp.now(),// this is the time the food was listed
    val foodListingLocation: GeoPoint? = null,
    val expiryDate: Timestamp? = null
){
    // No-argument constructor required by Firebase Firestore
    constructor() : this("", "", "", "", "", "",0, "", Timestamp.now(), null, null)
}


