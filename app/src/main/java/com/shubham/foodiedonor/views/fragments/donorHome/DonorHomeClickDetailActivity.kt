package com.shubham.foodiedonor.views.fragments.donorHome

import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.viewbinding.library.activity.viewBinding
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.widget.NestedScrollView
import com.google.android.material.appbar.AppBarLayout
import com.pixelcarrot.base64image.Base64Image
import com.shubham.foodiedonor.R
import com.shubham.foodiedonor.databinding.ActivityDonorHomeClickDetailBinding
import com.shubham.foodiedonor.models.ReceiverModel

class DonorHomeClickDetailActivity : AppCompatActivity() {

    private val binding: ActivityDonorHomeClickDetailBinding by viewBinding()
    private val TAG = "DonorHomeClickDetailActTAG"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        binding.collapsingToolbar.setExpandedTitleColor(Color.WHITE)

        setupAllFields()
    }

    @SuppressLint("SetTextI18n")
    private fun setupAllFields() {
        val receiver = intent.getSerializableExtra("receiver") as? ReceiverModel

        binding.donorMasterClickBtn.apply {
            text = "Donate to ${receiver?.name}"
        }

        binding.collapsingToolbar.title = receiver?.name

        binding.donorMasterClickScrollView.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->

            if(scrollY == 0) {
                binding.collapsingToolbar.setExpandedTitleColor(Color.WHITE)
            } else {
                binding.collapsingToolbar.setExpandedTitleColor(Color.BLACK)
            }

        })

        Base64Image.decode(receiver?.photo) { bitmap ->
            bitmap?.let {
                binding.detailPhoto.setImageBitmap(it)
            }
        }

        binding.textView16.text = receiver.toString()
    }

}