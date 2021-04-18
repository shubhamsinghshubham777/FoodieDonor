package com.shubham.foodiedonor.views

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.viewbinding.library.activity.viewBinding
import androidx.viewpager.widget.ViewPager
import com.shubham.foodiedonor.R
import com.shubham.foodiedonor.databinding.ActivityDonorHomeBinding
import com.shubham.foodiedonor.views.fragments.donorHome.DonorHomePageFragment
import com.shubham.foodiedonor.views.fragments.donorHome.DonorNearbyReceiversFragment
import com.shubham.foodiedonor.views.fragments.donorHome.DonorViewPagerAdapter
import com.tuonbondol.keyboardutil.hideSoftKeyboard

class DonorHomeActivity : AppCompatActivity() {

    private val binding: ActivityDonorHomeBinding by viewBinding()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setupViewPager()
    }

    private fun setupViewPager() {
        val adapter = DonorViewPagerAdapter(supportFragmentManager)
        adapter.addFragment(DonorHomePageFragment(), "Home")
        adapter.addFragment(DonorNearbyReceiversFragment(), "Donations")
        binding.donorHomePageViewPager.apply {
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
        binding.donorHomePageSmartTabLayout.apply {
            setupWithViewPager(binding.donorHomePageViewPager)
            getTabAt(0)!!.setIcon(R.drawable.ic_home).text = "Home"
            getTabAt(1)!!.setIcon(R.drawable.ic_heart_filled).text = "Donations"
        }
    }
}