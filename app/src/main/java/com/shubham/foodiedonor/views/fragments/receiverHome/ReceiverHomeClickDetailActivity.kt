package com.shubham.foodiedonor.views.fragments.receiverHome

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.viewbinding.library.activity.viewBinding
import com.shubham.foodiedonor.R
import com.shubham.foodiedonor.databinding.ActivityReceiverHomeClickDetailBinding
import com.shubham.foodiedonor.models.DonorDonationModel

class ReceiverHomeClickDetailActivity : AppCompatActivity() {

    private val binding: ActivityReceiverHomeClickDetailBinding by viewBinding()
    private val TAG = "ReceiverHomeClickDetailTAG"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setupAllViews()
    }

    private fun setupAllViews() {
        val donationReceived = intent.getParcelableExtra("donationReceived") as? DonorDonationModel
        Log.d(TAG, "Received from: ${donationReceived?.from}")
    }
}