package com.kosiso.foodshare.models

data class User(
    val id: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val countryCode: Long = 0,
    val phone: Long = 0,
    val email: String = "",
    val image: String = "",
    val signUpAs: String = "",
    val assignedCusId: String = "",
    val hasAccepted: String = "",
    val isEnroute: String = ""
)