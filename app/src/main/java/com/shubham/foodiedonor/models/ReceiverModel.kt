package com.shubham.foodiedonor.models

data class ReceiverModel(
    val name: String,
    val email: String,
    val mobile: String,
    val address: String,
    val photo: String,
    val mobileVerified: Boolean,
    val latitude: Double,
    val longitude: Double,
    val cinNumber: String,
    val type: String = "receiver"
)
