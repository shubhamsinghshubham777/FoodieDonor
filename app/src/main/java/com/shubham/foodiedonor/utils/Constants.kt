package com.shubham.foodiedonor.utils

import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

object Constants {

    val receiverCollectionRef = Firebase.firestore.collection("receivers")
    val donorCollectionRef = Firebase.firestore.collection("donors")

}