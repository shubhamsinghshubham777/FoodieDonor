package com.shubham.foodiedonor.views

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.viewbinding.library.activity.viewBinding
import com.shubham.foodiedonor.R
import com.shubham.foodiedonor.databinding.ActivityVerifyMobileBinding

class VerifyMobileActivity : AppCompatActivity() {

    private val binding: ActivityVerifyMobileBinding by viewBinding()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        otpConfirmed()
    }

    private fun otpConfirmed() {
        val intent = Intent()
        intent.putExtra("isMobileVerified", true)
        setResult(RESULT_OK, intent)
//        onActivityResult(112, RESULT_OK, intent)
        finish()
    }
}