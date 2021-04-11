package com.shubham.foodiedonor.models

data class DonorModel(
    val name: String,
    val email: String,
    val mobile: String,
    val address: String,
    val photo: String,
    val mobileVerified: Boolean
)
