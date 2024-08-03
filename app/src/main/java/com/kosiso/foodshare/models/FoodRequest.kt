package com.kosiso.foodshare.models


import com.google.firebase.Timestamp
import com.google.firebase.firestore.GeoPoint

// the "uniqueId" field is to give the FoodListing a unique Id to be used for differ callback
data class FoodRequest(
    val uniqueId:String = "",
    val uid:String = "",
    val nameOfItem:String = "",
    val itemDescription:String = "",
    val foodWeight:Int = 0,
    val status:String = "", // we have Open, Redeemed and Completed
    val requestTime: Timestamp = Timestamp.now(),// this is the time the food was requested
    val foodRequestLocation: GeoPoint? = null,

){
    // No-argument constructor required by Firebase Firestore
    constructor() : this("", "", "", "",  0, "", Timestamp.now(), null)
}