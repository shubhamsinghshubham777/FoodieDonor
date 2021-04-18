package com.shubham.foodiedonor.models

import com.google.firebase.Timestamp
import com.shubham.foodiedonor.utils.Constants
import java.io.Serializable

data class DonorDonationModel(
    val timestamp: Timestamp = Timestamp.now(),
    val from: String = Constants.globalDonorEmail,
    val to: String ="",
    val allItems: String ="",
    val verified: Boolean = false
): Serializable
