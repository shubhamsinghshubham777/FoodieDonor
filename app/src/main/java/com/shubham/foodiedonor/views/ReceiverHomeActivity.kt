package com.shubham.foodiedonor.views

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.viewbinding.library.activity.viewBinding
import androidx.viewpager.widget.ViewPager
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.shubham.foodiedonor.R
import com.shubham.foodiedonor.databinding.ActivityReceiverHomeBinding
import com.shubham.foodiedonor.views.fragments.donorHome.DonorDonationsFragment
import com.shubham.foodiedonor.views.fragments.donorHome.DonorHomePageFragment
import com.shubham.foodiedonor.views.fragments.donorHome.DonorViewPagerAdapter
import com.shubham.foodiedonor.views.fragments.receiverHome.ReceiverDonationsFragment
import com.shubham.foodiedonor.views.fragments.receiverHome.ReceiverProfileFragment
import com.tuonbondol.keyboardutil.hideSoftKeyboard

class ReceiverHomeActivity : AppCompatActivity() {

    private val binding: ActivityReceiverHomeBinding by viewBinding()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setupViewPager()

//        binding.receiverHomeSignoutBtn.setOnClickListener {
//            Firebase.auth.signOut()
//            startActivity(Intent(this, LoginActivity::class.java))
//            finish()
//        }
    }

    private fun setupViewPager() {
        val adapter = DonorViewPagerAdapter(supportFragmentManager)
        adapter.addFragment(ReceiverDonationsFragment(), "Donations")
        adapter.addFragment(ReceiverProfileFragment(), "Profile")
        binding.receiverHomeViewPager.apply {
            this.adapter = adapter
            offscreenPageLimit = 2
            addOnPageChangeListener(object : ViewPager.OnPageChangeListener{
                override fun onPageScrolled(
                    position: Int,
                    positionOffset: Float,
                    positionOffsetPixels: Int
                ) {
                    hideSoftKeyboard()
                }

                override fun onPageSelected(position: Int) {
                    hideSoftKeyboard()
                }

                override fun onPageScrollStateChanged(state: Int) {
                    hideSoftKeyboard()
                }

            })
        }
        binding.receiverHomePageSmartTabLayout.apply {
            setupWithViewPager(binding.receiverHomeViewPager)
            getTabAt(0)!!.setIcon(R.drawable.ic_heart_filled).text = "Donations"
            getTabAt(1)!!.setIcon(R.drawable.ic_person).text = "Profile"
        }
    }

}