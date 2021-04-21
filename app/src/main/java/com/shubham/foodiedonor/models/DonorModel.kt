package com.shubham.foodiedonor.models

data class DonorModel(
    val name: String ="",
    val email: String ="",
    val mobile: String ="",
    val address: String ="",
    val photo: String? ="",
    val mobileVerified: Boolean = false,
    val latitude: Double =0.0,
    val longitude: Double =0.0,
    val type: String = "donor"
)
