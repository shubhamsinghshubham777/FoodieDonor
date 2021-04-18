package com.shubham.foodiedonor.views.fragments.donorHome

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.viewbinding.library.activity.viewBinding
import androidx.core.content.res.ResourcesCompat
import androidx.core.widget.NestedScrollView
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.pixelcarrot.base64image.Base64Image
import com.shubham.foodiedonor.R
import com.shubham.foodiedonor.databinding.ActivityDonorHomeClickDetailBinding
import com.shubham.foodiedonor.models.ReceiverModel
import www.sanju.motiontoast.MotionToast

class DonorHomeClickDetailActivity : AppCompatActivity() {

    private val binding: ActivityDonorHomeClickDetailBinding by viewBinding()
    private val TAG = "DonorHomeClickDetailActTAG"
    private lateinit var mMap: SupportMapFragment
    private lateinit var googleMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.collapsingToolbar.setExpandedTitleColor(Color.WHITE)

        setupAllFields()
    }

    @SuppressLint("SetTextI18n")
    private fun setupAllFields() {
        val receiver = intent.getSerializableExtra("receiver") as? ReceiverModel

        binding.donorMasterClickBtn.apply {
            text = "Donate to ${receiver?.name}"
        }

        binding.collapsingToolbar.apply {
            title = receiver?.name
        }

        binding.donorMasterClickScrollView.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->

            if(scrollY == 0) {
                binding.collapsingToolbar.setExpandedTitleColor(Color.WHITE)
            } else {
                binding.collapsingToolbar.setExpandedTitleColor(Color.BLACK)
            }

        })

        Base64Image.decode(receiver?.photo) { bitmap ->
            bitmap?.let {
                val myDrawable = BitmapDrawable(resources, it)
                binding.detailPhoto.setImageBitmap(it)
//                supportActionBar?.setIcon(myDrawable)
            }
        }

        binding.apply {
            dhcdEmail.text = receiver?.email
            dhcdMobile.text = "+91${receiver?.mobile}"
            dhcdAddress.text = receiver?.address
            dhcdMailBtn.setOnClickListener {
                try {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("mailto:"+receiver?.email))
                    intent.putExtra(Intent.EXTRA_SUBJECT, "Request to Donate")
                    startActivity(intent)
                } catch (e: ActivityNotFoundException) {
                    MotionToast.createColorToast(
                        this@DonorHomeClickDetailActivity, "Couldn't find any app to send the email!",
                        MotionToast.TOAST_ERROR,
                        MotionToast.GRAVITY_BOTTOM,
                        MotionToast.LONG_DURATION,
                        ResourcesCompat.getFont(
                            this@DonorHomeClickDetailActivity,
                            R.font.helvetica_regular
                        )
                    )
                }
            }
            dhcdPhoneBtn.setOnClickListener {
                val intent = Intent(Intent.ACTION_DIAL)
                intent.data = Uri.parse("tel:+91${receiver?.mobile}")
                startActivity(intent)
            }
            dhcdRating.apply {
                rating = receiver?.rating!!.toFloat()
                isIndicator = true
            }
        }

        setupGoogleMap(receiver!!.latitude, receiver.longitude)
    }

    private fun setupGoogleMap(latitude: Double, longitude: Double) {
        mMap = supportFragmentManager.findFragmentById(R.id.mapView) as SupportMapFragment
        mMap.getMapAsync(OnMapReadyCallback {
            googleMap = it

            val pos = LatLng(latitude, longitude)

            googleMap.apply {
                uiSettings.isScrollGesturesEnabled = false
                uiSettings.isZoomGesturesEnabled = false
                setOnMapClickListener {
//                    Toast.makeText(this@DonorHomeClickDetailActivity, "Clicked!", Toast.LENGTH_SHORT).show()
                    val uri = "https://www.google.com/maps/dir/?api=1&destination=" + latitude + "," + longitude + "&travelmode=driving"
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
                    startActivity(intent)
                }
                addMarker(MarkerOptions()
                    .position(pos)
                )
                animateCamera(CameraUpdateFactory.newLatLngZoom(pos, 13f))
            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            //to reverse the scene transition animation
            supportFinishAfterTransition()
        }
        return super.onOptionsItemSelected(item)
    }

}