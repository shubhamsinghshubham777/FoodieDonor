package com.shubham.foodiedonor.utils

import android.graphics.drawable.Drawable
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

object Constants {

    val globalReceiverCollectionRef = Firebase.firestore.collection("receivers")
    val globalDonorCollectionRef = Firebase.firestore.collection("donors")
    var globalDonorName = ""
    var globalDonorEmail = ""
    var globalDonorMobile = ""
    var globalDonorAddress = ""
    var globalDonorLongitude = 0.0
    var globalDonorLatitude = 0.0
    lateinit var globalDonorPhoto: Drawable
}