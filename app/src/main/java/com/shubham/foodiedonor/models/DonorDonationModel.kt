package com.shubham.foodiedonor.models

import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.Timestamp
import com.shubham.foodiedonor.utils.Constants
import kotlinx.android.parcel.Parcelize
import kotlinx.parcelize.Parceler
import java.io.Serializable

@Parcelize
data class DonorDonationModel(
    val timestamp: Timestamp? = Timestamp.now(),
    val from: String? = Constants.globalDonorEmail,
    val to: String? ="",
    val allItems: String? ="",
    val verifiedStatus: String? = "Pending"
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readParcelable(Timestamp::class.java.classLoader),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    ) {
    }

    companion object : Parceler<DonorDonationModel> {

        override fun DonorDonationModel.write(parcel: Parcel, flags: Int) {
            parcel.writeParcelable(timestamp, flags)
            parcel.writeString(from)
            parcel.writeString(to)
            parcel.writeString(allItems)
            parcel.writeString(verifiedStatus)
        }

        override fun create(parcel: Parcel): DonorDonationModel {
            return DonorDonationModel(parcel)
        }
    }
}