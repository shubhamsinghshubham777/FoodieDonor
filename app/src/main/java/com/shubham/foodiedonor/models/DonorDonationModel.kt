package com.shubham.foodiedonor.models

import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.Timestamp
import com.shubham.foodiedonor.utils.Constants
import kotlinx.android.parcel.Parcelize
import kotlinx.parcelize.Parceler
import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.*

data class DonorDonationModel(
    val timestamp: String = SimpleDateFormat("yyyy.MM.dd HH:mm:s").format(Date()).substring(0, SimpleDateFormat("yyyy.MM.dd HH:mm:s").format(Date()).length-1),
    val from: String? = Constants.globalDonorEmail,
    val to: String? ="",
    val toEmail: String? = "",
    val toMobile: String? = "",
    val allItems: String? ="",
    val verifiedStatus: String? = "Pending"
) : Serializable