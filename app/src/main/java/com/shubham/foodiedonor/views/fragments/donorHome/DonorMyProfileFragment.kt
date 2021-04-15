package com.shubham.foodiedonor.views.fragments.donorHome

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import coil.load
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.pixelcarrot.base64image.Base64Image
import com.raywenderlich.android.validatetor.RegexMatcher
import com.raywenderlich.android.validatetor.ValidateTor
import com.schibstedspain.leku.*
import com.schibstedspain.leku.locale.SearchZoneRect
import com.shubham.foodiedonor.BuildConfig
import com.shubham.foodiedonor.R
import com.shubham.foodiedonor.databinding.FragmentDonorMyProfileBinding
import com.shubham.foodiedonor.models.DonorModel
import com.shubham.foodiedonor.models.ReceiverModel
import com.shubham.foodiedonor.views.LoginActivity
import com.shubham.foodiedonor.views.VerifyMobileActivity
import com.shubham.foodiedonor.views.fragments.DonorSignupFragment
import com.tuonbondol.keyboardutil.hideSoftKeyboard
import dev.shreyaspatil.MaterialDialog.MaterialDialog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import www.sanju.motiontoast.MotionToast


class DonorMyProfileFragment : Fragment(R.layout.fragment_donor_my_profile) {

    private lateinit var binding: FragmentDonorMyProfileBinding
    private var base64Photo1: String = ""
    private var isMobileVerified = false
    private var firestoreMobileNumber: String = ""
    private var latitude = 0.0
    private var longitude = 0.0
    private val donorCollectionRef = Firebase.firestore.collection("donors")
        .whereEqualTo("email", Firebase.auth.currentUser?.email)
    private val TAG = "DonorMyProfileFragmentTAG"
    private val validateTor = ValidateTor()
    private val validateTorMobileNumber = RegexMatcher()
    private val indianMobileNumberRegex = Regex("^[6-9]\\d{9}\$")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDonorMyProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.donorMyProfileSwipeRefreshLayout.isRefreshing = true

        setupAllLayouts()

        binding.apply {
            donorProfileBtnSignout.bringToFront()

            donorProfileBtnSignout.setOnClickListener {
                signoutBtnAnimationView.playAnimation()
            }

            mobileVerificationAnimation.apply {
                setAnimation(R.raw.notverified)
                playAnimation()
            }

            donorProfileBtnSignout.setOnClickListener {
                val myDialog = MaterialDialog.Builder(requireActivity())
                    .setTitle("Signing Out?")
                    .setMessage("Press the YES button to confirm your sign-out request")
                    .setPositiveButton("Yes", R.drawable.ic_door) { dialogInterface, _ ->
                        Firebase.auth.signOut()
                        startActivity(Intent(requireActivity(), LoginActivity::class.java))
                        requireActivity().finish()
                        dialogInterface.dismiss()
                    }
                    .setNegativeButton("No") { dialogInterface, _ ->
                        dialogInterface.dismiss()
                    }
                    .build()

                myDialog.show()
            }

            donorMyProfileSwipeRefreshLayout.setOnRefreshListener {

                parentFragmentManager.beginTransaction().detach(this@DonorMyProfileFragment)
                    .attach(this@DonorMyProfileFragment).commit()

                binding.donorMyProfileSwipeRefreshLayout.isRefreshing = false
            }

            selectAddressIv.setOnClickListener {
                val locationPickerIntent = LocationPickerActivity.Builder()
                    .withLocation(28.665050640866188, 77.14356996310623)
                    .withGeolocApiKey(BuildConfig.apiKey)
                    .withSearchZone("en_ES")
                    .withSearchZone(
                        SearchZoneRect(
                            LatLng(28.663692694207246, 77.14074199258673),
                            LatLng(28.66842783939638, 77.13294212906699)
                        )
                    )
                    .withDefaultLocaleSearchZone()
                    .shouldReturnOkOnBackPressed()
                    .withGooglePlacesApiKey(BuildConfig.apiKey)
                    .withSatelliteViewHidden()
                    .withGoogleTimeZoneEnabled()
//                .withVoiceSearchHidden()
//                .withStreetHidden()
//                .withCityHidden()
//                .withZipCodeHidden()
//                .withUnnamedRoadHidden()
                    .build(requireContext())

                startActivityForResult(
                    locationPickerIntent,
                    DonorSignupFragment.MAP_BUTTON_REQUEST_CODE
                )
            }

            mobileVerificationAnimation.setOnClickListener {
                val intent = Intent(
                    requireContext().applicationContext,
                    VerifyMobileActivity::class.java
                )
                intent.putExtra("mobileNumber", binding.mobile.text.toString())
                intent.putExtra("instruction", "doNotSignout")
                startActivityForResult(intent, DonorSignupFragment.VERIFY_OTP_REQUEST_CODE)
            }

        }

        donorCollectionRef.get().addOnCompleteListener {
            for (document in it.result.documents) {

                val donor = document.toObject(ReceiverModel::class.java)

                val base64Photo = donor?.photo
                if (base64Photo != null) {
                    base64Photo1 = base64Photo
                }
                Base64Image.decode(base64Photo) { bitmap ->
                    bitmap?.let { receivedPhoto ->

                        binding.apply {
                            donorLoggedInProfilePhoto.setImageBitmap(receivedPhoto)
                            donorMyProfileImageViewLoader.visibility = View.GONE
                            donorMyProfileNameTv.apply {
                                text = donor?.name
                                visibility = View.VISIBLE
                            }
                            name.setText(donor?.name)
                            email.setText(donor?.email)
                            mobile.setText(donor?.mobile)
                            address.setText(donor?.address)
                            isMobileVerified = donor!!.mobileVerified
                            if (isMobileVerified) {
                                mobileVerificationAnimation.apply {
                                    setAnimation(R.raw.verified)
                                    playAnimation()
                                }
                                binding.textView15.text = "Verified!"
                                mobileVerificationAnimation.isClickable = false
                                mobileVerificationAnimation.isFocusable = false
                            } else {
                                mobileVerificationAnimation.apply {
                                    setAnimation(R.raw.notverified)
                                    playAnimation()
                                }
                                binding.textView15.text = "Click to verify!!"
                                mobileVerificationAnimation.isClickable = true
                                mobileVerificationAnimation.isFocusable = true
                            }
                            firestoreMobileNumber = donor.mobile
                        }

                    }
                }
            }

            binding.donorMyProfileSwipeRefreshLayout.isRefreshing = false
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        val fileUri = data?.data
        binding.donorLoggedInProfilePhoto.setImageURI(fileUri)

        val inputStream = fileUri?.let { context?.contentResolver?.openInputStream(it) }
        val bitmap = BitmapFactory.decodeStream(inputStream)
        Base64Image.encode(bitmap) {
            if (it != null) {
                base64Photo1 = it
                Log.d(TAG, "base64photo is: $base64Photo1")
            }
        }

        if (requestCode == DonorSignupFragment.MAP_BUTTON_REQUEST_CODE && data != null) {

            latitude = data.getDoubleExtra(LATITUDE, 0.0)
            longitude = data.getDoubleExtra(LONGITUDE, 0.0)
            binding.address.setText(data.getStringExtra(LOCATION_ADDRESS).toString())
            Log.d(TAG, "Address: ${binding.address.text.toString()}")

        }

        if (requestCode == DonorSignupFragment.VERIFY_OTP_REQUEST_CODE) {

        } else if (requestCode == DonorSignupFragment.MAP_BUTTON_REQUEST_CODE && data != null) {
            latitude = data.getDoubleExtra(LATITUDE, 0.0)
            longitude = data.getDoubleExtra(LONGITUDE, 0.0)
            binding.address.setText(data.getStringExtra(LOCATION_ADDRESS).toString())
//                lekuPoi = data.getParcelableExtra<LekuPoi>(LEKU_POI)
        }

        if (resultCode == Activity.RESULT_FIRST_USER) {

            isMobileVerified = true

            if (data?.extras != null) {
                isMobileVerified = data.getBooleanExtra("isMobileVerified", false)
                binding.mobileVerificationAnimation.apply {
                    setAnimation(R.raw.verified)
                    speed = 0.5f
                    playAnimation()
                    binding.textView15.text = "Mobile Verified!"
                    binding.mobile.error = null
                    isClickable = false
                    isFocusable = false
                    Log.d(TAG, "Result First User")
                }
            }

        }

        if (resultCode == Activity.RESULT_CANCELED) {
            Log.d(TAG, "Result Cancelled")
        }

    }

    @SuppressLint("SetTextI18n")
    fun setupAllLayouts() {

        val validateTor = ValidateTor()

        binding.apply {
            name.apply {
                doOnTextChanged { text, start, before, count ->
                    if (validateTor.isEmpty(text.toString())) {
                        error = getString(R.string.required_field)
                    } else {
                        error = null
                    }
                }
            }

            mobile.apply {
                doOnTextChanged { text, start, before, count ->
                    if (validateTor.isEmpty(text.toString())) {
                        error = getString(R.string.required_field)
                        mobileVerificationAnimation.apply {
                            setAnimation(R.raw.notverified)
                            playAnimation()
                        }
                        binding.textView15.text = "Click to verify!"

                    } else {
                        if (validateTorMobileNumber.validate(
                                text.toString(),
                                indianMobileNumberRegex.toString()
                            )
                        ) {
                            if (text.toString() == firestoreMobileNumber && isMobileVerified) {
                                mobileVerificationAnimation.apply {
                                    setAnimation(R.raw.verified)
                                    playAnimation()
                                }
                                binding.textView15.text = "Verified!"
                                mobileVerificationAnimation.isClickable = false
                                mobileVerificationAnimation.isFocusable = false
                                error = null
                            } else {
                                mobileVerificationAnimation.apply {
                                    setAnimation(R.raw.notverified)
                                    playAnimation()
                                }
                                binding.textView15.text = "Click to verify!!"
                                error = null
                                mobileVerificationAnimation.isClickable = true
                                mobileVerificationAnimation.isFocusable = true
                            }

                        } else {
                            error = getString(R.string.mobile_error)
                            mobileVerificationAnimation.apply {
                                setAnimation(R.raw.notverified)
                                playAnimation()
                            }
                            binding.textView15.text = "Click to verify!!"
                            mobileVerificationAnimation.isClickable = true
                            mobileVerificationAnimation.isFocusable = true
                        }
                    }
                }
            }

            address.apply {
                doOnTextChanged { text, start, before, count ->
                    if (validateTor.isEmpty(text.toString())) {
                        error = getString(R.string.required_field)
                    } else {
                        error = null
                    }
                }
            }

            floatingActionButton.setOnClickListener {
                ImagePicker.with(this@DonorMyProfileFragment)
                    .cropSquare()
                    .maxResultSize(512, 512)
                    .compress(1024)
                    .start()
            }

            saveBtn.setOnClickListener {
                requireActivity().hideSoftKeyboard()
                val donorDocument = Firebase.firestore.collection("donors")
                donorDocument.document(firestoreMobileNumber).delete().addOnCompleteListener {
                    if (it.isSuccessful) {
                        donorDocument.document(mobile.text.toString()).set(
                            DonorModel(
                                name = name.text.toString(),
                                email = email.text.toString(),
                                mobile = mobile.text.toString(),
                                address = address.text.toString(),
                                photo = base64Photo1,
                                mobileVerified = isMobileVerified,
                                latitude = latitude,
                                longitude = longitude
                            )
                        )

                        MotionToast.createColorToast(
                            requireActivity(), "Data updated successfully!",
                            MotionToast.TOAST_SUCCESS,
                            MotionToast.GRAVITY_BOTTOM,
                            MotionToast.LONG_DURATION,
                            ResourcesCompat.getFont(
                                requireActivity(),
                                R.font.helvetica_regular
                            )
                        )
                        parentFragmentManager.beginTransaction().detach(this@DonorMyProfileFragment)
                            .attach(this@DonorMyProfileFragment).commit()

                    } else {
                        Log.d(TAG, "setupAllLayouts: ${it.exception?.message}")
                        MotionToast.createColorToast(
                            requireActivity(), "Couldn't update data. Please try again later.",
                            MotionToast.TOAST_ERROR,
                            MotionToast.GRAVITY_BOTTOM,
                            MotionToast.LONG_DURATION,
                            ResourcesCompat.getFont(
                                requireActivity(),
                                R.font.helvetica_regular
                            )
                        )
                    }
                }
            }

        }
    }
}