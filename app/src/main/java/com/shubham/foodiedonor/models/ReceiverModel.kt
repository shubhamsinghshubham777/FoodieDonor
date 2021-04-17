package com.shubham.foodiedonor.models

import java.io.Serializable

data class ReceiverModel(
    val name: String ="",
    val email: String ="",
    val mobile: String ="",
    val address: String ="",
    val photo: String ="",
    val mobileVerified: Boolean = false,
    val latitude: Double =0.0,
    val longitude: Double =0.0,
    val cinNumber: String ="",
    val type: String = "receiver",
    val rating: Double = 0.0
): Serializable {}
