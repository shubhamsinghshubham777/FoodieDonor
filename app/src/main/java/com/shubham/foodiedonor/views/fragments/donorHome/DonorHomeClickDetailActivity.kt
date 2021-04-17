package com.shubham.foodiedonor.views.fragments.donorHome

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.viewbinding.library.activity.viewBinding
import android.widget.Toast
import com.shubham.foodiedonor.R
import com.shubham.foodiedonor.databinding.ActivityDonorHomeClickDetailBinding
import com.shubham.foodiedonor.models.ReceiverModel

class DonorHomeClickDetailActivity : AppCompatActivity() {

    private val binding: ActivityDonorHomeClickDetailBinding by viewBinding()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setupAllFields()
    }

    private fun setupAllFields() {
        val receiver = intent.getSerializableExtra("receiver") as? ReceiverModel

        binding.textView15.text = receiver.toString()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == android.R.id.home) {
            supportFinishAfterTransition()
        }
        return super.onOptionsItemSelected(item)
    }
}