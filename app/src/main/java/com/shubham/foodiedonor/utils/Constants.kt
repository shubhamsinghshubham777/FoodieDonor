package com.shubham.foodiedonor.utils

import android.graphics.drawable.Drawable
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.shubham.foodiedonor.models.DonateItemModel

object Constants {

    val globalReceiverCollectionRef = Firebase.firestore.collection("receivers")
    val globalDonorCollectionRef = Firebase.firestore.collection("donors")
    val globalFoodDocumentRef = Firebase.firestore.collection("food").document("food_items")
    var globalDonorName = ""
    var globalDonorEmail = ""
    var globalDonorMobile = ""
    var globalDonorAddress = ""
    var globalDonorLongitude = 0.0
    var globalDonorLatitude = 0.0
    lateinit var globalDonorPhoto: Drawable
    lateinit var globalFoodListItemNonBreads: ArrayList<String>
    var donateItemsList : MutableList<DonateItemModel> = mutableListOf<DonateItemModel>()

//    Sample toast
//    MotionToast.createColorToast(this@DonorDonateActivity, "Please enter some amount!", MotionToast.TOAST_WARNING, MotionToast.GRAVITY_BOTTOM, MotionToast.SHORT_DURATION, ResourcesCompat.getFont(this@DonorDonateActivity,R.font.alegreya_sans_sc_medium))
}