package com.shubham.foodiedonor.views

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.viewbinding.library.activity.viewBinding
import androidx.core.view.get
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.shubham.foodiedonor.R
import com.shubham.foodiedonor.databinding.ActivityDonorHomeBinding
import com.shubham.foodiedonor.views.fragments.donorHome.DonorHomePageFragment
import com.shubham.foodiedonor.views.fragments.donorHome.DonorMyProfileFragment
import com.shubham.foodiedonor.views.fragments.donorHome.DonorNearbyReceiversFragment
import com.shubham.foodiedonor.views.fragments.donorHome.DonorViewPagerAdapter

class DonorHomeActivity : AppCompatActivity() {

    private val binding: ActivityDonorHomeBinding by viewBinding()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setupViewPager()

        binding.donorHomeSignoutBtn.setOnClickListener {
            Firebase.auth.signOut()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    private fun setupViewPager() {
        val adapter = DonorViewPagerAdapter(supportFragmentManager)
        adapter.addFragment(DonorHomePageFragment(), "Home")
        adapter.addFragment(DonorNearbyReceiversFragment(), "Nearby Receivers")
        adapter.addFragment( DonorMyProfileFragment(), "Profile")
        binding.donorHomePageViewPager.adapter = adapter
        binding.donorHomePageSmartTabLayout.apply {
            setupWithViewPager(binding.donorHomePageViewPager)
            getTabAt(0)!!.setIcon(R.drawable.ic_home).text = "Home"
            getTabAt(1)!!.setIcon(R.drawable.ic_maps).text = "Donate"
            getTabAt(2)!!.setIcon(R.drawable.ic_person).text = "Profile"
        }
    }
}