package com.shubham.foodiedonor.views

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.viewbinding.library.activity.viewBinding
import com.shubham.foodiedonor.R
import com.shubham.foodiedonor.databinding.ActivitySignupBinding

class SignupActivity : AppCompatActivity() {

    private val binding: ActivitySignupBinding by viewBinding()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)


    }
}