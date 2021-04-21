package com.shubham.foodiedonor.views.fragments.receiverHome

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.viewbinding.library.activity.viewBinding
import coil.load
import coil.transform.CircleCropTransformation
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.firestore.ktx.toObject
import com.pixelcarrot.base64image.Base64Image
import com.shubham.foodiedonor.R
import com.shubham.foodiedonor.databinding.ActivityReceiverHomeClickDetailBinding
import com.shubham.foodiedonor.models.DonorDonationModel
import com.shubham.foodiedonor.models.DonorModel
import com.shubham.foodiedonor.utils.Constants.globalDonorCollectionRef

class ReceiverHomeClickDetailActivity : AppCompatActivity() {

    private val binding: ActivityReceiverHomeClickDetailBinding by viewBinding()
    private val TAG = "ReceiverHomeClickDetailTAG"
    private lateinit var mMap: SupportMapFragment
    private lateinit var googleMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setupAllViews()
    }

    @SuppressLint("SetTextI18n")
    private fun setupAllViews() {
        val donationReceived = intent.getParcelableExtra("donationReceived") as? DonorDonationModel
        //        Log.d(TAG, "Received from: ${donationReceived?.from}")

        when (donationReceived?.verifiedStatus) {
            "Accepted!" -> {
                binding.receiverClickAnimation.setAnimation(R.raw.food_verified)
                binding.animationAcceptItem.visibility = View.INVISIBLE
                binding.animationRejectItem.visibility = View.INVISIBLE
                binding.tvMessage.apply {
                    text = "Already accepted! Yay!"
                    setTextColor(resources.getColor(R.color.green))
                }
            }
            "Pending" -> {
                binding.receiverClickAnimation.setAnimation(R.raw.food_pending)
                binding.animationAcceptItem.visibility = View.VISIBLE
                binding.animationRejectItem.visibility = View.VISIBLE
            }
            else -> {
                binding.receiverClickAnimation.setAnimation(R.raw.food_rejected)
                binding.animationAcceptItem.visibility = View.INVISIBLE
                binding.animationRejectItem.visibility = View.INVISIBLE
                binding.tvMessage.apply {
                    text = "You have already rejected this item!"
                    setTextColor(resources.getColor(R.color.accentColor))
                }
            }
        }

        //This is donor email only
        val receivedFrom = donationReceived?.from
        val receivedTimestamp = donationReceived?.timestamp?.toDate().toString()
        val receivedItems = donationReceived?.allItems

        getDonorDetails(receivedFrom)

        binding.apply {
            clickdetailItems.text = receivedItems
            clickdetailEmail.text = receivedFrom
            clickDetailTv1.text = "These items were sent on \n $receivedTimestamp \n by:"
            animationAcceptItem.setOnClickListener {
                animationAcceptItem.playAnimation()
            }
            animationRejectItem.setOnClickListener {
                animationRejectItem.playAnimation()
            }
        }

    }

    @SuppressLint("SetTextI18n")
    private fun getDonorDetails(donorEmail: String?) {
        globalDonorCollectionRef.whereEqualTo("email", donorEmail).get()
            .addOnCompleteListener { donorTask ->
                if (donorTask.result.isEmpty) {
                    Log.d(TAG, "Couldn't get donor details. Cause: ${donorTask.exception?.localizedMessage}")
                } else {
                    for (document in donorTask.result.documents) {
                        val donor = document.toObject<DonorModel>()
                        binding.clickdetailName.text = donor!!.name
                        binding.clickDetailMobile.text = "+91${donor.mobile}"
                        binding.clickdetailPhoto.load(donor.photo) {
                            crossfade(400)
                            transformations(CircleCropTransformation())
                            placeholder(R.drawable.placeholder_image2)
                        }
                        binding.clickDetailCallBtn.setOnClickListener {
                            val intent = Intent(Intent.ACTION_DIAL)
                            intent.data = Uri.parse("tel:+91${donor.mobile}")
                            startActivity(intent)
                        }
                        if(donor.mobileVerified) {
                            binding.clickDetailsVerifiedBadge.visibility = View.VISIBLE
                        } else {
                            binding.clickDetailsVerifiedBadge.visibility = View.INVISIBLE
                        }
                        setupGoogleMap(donor.latitude, donor.longitude)
//                        val donorPhoto = donor.photo
//                        Base64Image.decode(donorPhoto) { bitmap ->
//                            bitmap?.let {
//                                binding.clickdetailPhoto.setImageBitmap(it)
//                            }
//                        }
                    }
                }
            }
    }

    private fun setupGoogleMap(latitude: Double, longitude: Double) {
        mMap = supportFragmentManager.findFragmentById(R.id.mapView2) as SupportMapFragment
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
                addMarker(
                    MarkerOptions()
                    .position(pos)
                )
                animateCamera(CameraUpdateFactory.newLatLngZoom(pos, 13f))
            }
        })
    }

}