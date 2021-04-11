package com.shubham.foodiedonor.views.fragments

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.location.Address
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.viewbinding.library.fragment.viewBinding
import androidx.core.content.res.ResourcesCompat
import androidx.core.widget.doOnTextChanged
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuthUserCollisionException
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
import com.shubham.foodiedonor.databinding.FragmentDonorSignupBinding
import com.shubham.foodiedonor.models.DonorModel
import com.shubham.foodiedonor.views.HomeActivity
import www.sanju.motiontoast.MotionToast

class DonorSignupFragment : Fragment(R.layout.fragment_signup_chooser) {

//    private var binding: FragmentDonorSignupBinding by viewBinding()

    private var _binding: FragmentDonorSignupBinding? = null
    private val binding get() = _binding!!

    private var isNameValid = false
    private var isEmailValid = false
    private var isPasswordValid = false
    private var isRepeatPasswordValid = false
    private var isMobileValid = false
    private var isAddressValid = false
    private var isProfilePhotoValid = false
    private val validateTor = ValidateTor()
    private val validateTorMobileNumber = RegexMatcher()
    private val indianMobileNumberRegex = Regex("^[6-9]\\d{9}\$")
    private lateinit var base64Photo: String
    private val TAG = "DonorSignupFragmentTAG"
    private var latitude = 0.0
    private var longitude = 0.0
    private var address = ""
    private var postalcode = ""
    private var bundle = Bundle.EMPTY
    private var fullAddress = ""
    private var timeZoneId = ""
    private var timeZoneDisplayName = ""
    private var mobileVerified = false
//    private var lekuPoi = LekuPoi.Companion.CREATOR.createFromParcel(null)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDonorSignupBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Set not verified animation by default
        binding.lottieMobileVerification.apply {
            setMinFrame(40)
            setMaxFrame(41)
            frame = 40
            repeatCount = 1
            this.playAnimation()
        }

        observeAllFields()

        binding.donorProfilePhoto.setOnClickListener {
            ImagePicker.with(this)
                .cropSquare()
                .compress(1024)
                .maxResultSize(200,200)
                .start()
        }

        binding.donorAddressIv.setOnClickListener {
            val locationPickerIntent = LocationPickerActivity.Builder()
                .withLocation(41.4036299, 2.1743558)
                .withGeolocApiKey(BuildConfig.apiKey)
                .withSearchZone("en_ES")
                .withSearchZone(SearchZoneRect(LatLng(26.525467, -18.910366), LatLng(43.906271, 5.394197)))
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

            startActivityForResult(locationPickerIntent, MAP_BUTTON_REQUEST_CODE)
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(resultCode == Activity.RESULT_OK) {
            val fileUri = data?.data
            binding.donorProfilePhoto.setImageURI(fileUri)
            isProfilePhotoValid = true

            val inputStream = fileUri?.let { context?.contentResolver?.openInputStream(it) }
            val bitmap = BitmapFactory.decodeStream(inputStream)
            Base64Image.encode(bitmap) {
                if (it != null) {
                    base64Photo = it
                    isProfilePhotoValid = true
                    Log.d(TAG, "base64photo is: $base64Photo")
                }
            }

            if(requestCode == MAP_BUTTON_REQUEST_CODE && data != null) {

                latitude = data.getDoubleExtra(LATITUDE, 0.0)
                longitude = data.getDoubleExtra(LONGITUDE, 0.0)
                address = data.getStringExtra(LOCATION_ADDRESS).toString()
                postalcode = data.getStringExtra(ZIPCODE).toString()
                bundle = data.getBundleExtra(TRANSITION_BUNDLE)
//                fullAddress = data.getParcelableExtra<Address>(ADDRESS).toString()
                fullAddress = data.getStringExtra(LOCATION_ADDRESS).toString()
                Log.d(TAG, "Address: $fullAddress")
                timeZoneId = data.getStringExtra(TIME_ZONE_ID).toString()
                timeZoneDisplayName = data.getStringExtra(TIME_ZONE_DISPLAY_NAME).toString()

                binding.donorAddressEt.setText(fullAddress)
                if (!validateTor.isEmpty(binding.donorAddressEt.text.toString())) {
                    binding.donorAddressLayout.error = null
                    isAddressValid = true
                }

            } else if(requestCode == MAP_BUTTON_REQUEST_CODE && data != null) {
                latitude = data.getDoubleExtra(LATITUDE, 0.0)
                longitude = data.getDoubleExtra(LONGITUDE, 0.0)
                address = data.getStringExtra(LOCATION_ADDRESS).toString()
//                lekuPoi = data.getParcelableExtra<LekuPoi>(LEKU_POI)
            }
        }

        if(resultCode == Activity.RESULT_CANCELED) {
            Log.d(TAG, "Result Cancelled")
        }

    }

    private fun observeAllFields() {
        binding.donorNameEt.doOnTextChanged { text, start, before, count ->
            if(validateTor.isEmpty(text.toString())) {
                binding.donorNameLayout.error = getString(R.string.required_field)
                isNameValid = false
                unlockSignupButton()
            } else {
                binding.donorNameLayout.error = null
                isNameValid = true
                unlockSignupButton()
            }
        }

        binding.donorEmailEt.doOnTextChanged { text, start, before, count ->
            if(validateTor.isEmpty(text.toString())) {
                binding.donorEmailLayout.error = getString(R.string.required_field)
                isEmailValid = false
                unlockSignupButton()
            } else {
                if(validateTor.isEmail(text.toString())) {
                    binding.donorEmailLayout.error = null
                    isEmailValid = true
                    unlockSignupButton()
                } else {
                    binding.donorEmailLayout.error = getString(R.string.email_error)
                    isEmailValid = false
                    unlockSignupButton()
                }
            }
        }

        binding.donorPasswordEt.doOnTextChanged { text, start, before, count ->
            if(validateTor.isEmpty(text.toString())) {
                binding.donorPasswordLayout.error = getString(R.string.required_field)
                isPasswordValid = false
                unlockSignupButton()
            } else {
                if(validateTor.isAtleastLength(text.toString(), 8)
                    && validateTor.hasAtleastOneUppercaseCharacter(text.toString())
                    && validateTor.hasAtleastOneDigit(text.toString())
                    && validateTor.hasAtleastOneSpecialCharacter(text.toString())
                ) {
                    binding.donorPasswordLayout.error = null
                    isPasswordValid = true
                    unlockSignupButton()
                } else {
                    binding.donorPasswordLayout.error = getString(R.string.password_error)
                    isPasswordValid = false
                    unlockSignupButton()
                }
            }
        }

        binding.donorRepeatPasswordEt.doOnTextChanged { text, start, before, count ->
            if(validateTor.isEmpty(text.toString())) {
                binding.donorRepeatPasswordLayout.error = getString(R.string.required_field)
                isRepeatPasswordValid = false
                unlockSignupButton()
            } else {
                if(text.toString() == binding.donorPasswordEt.text.toString()) {
                    binding.donorRepeatPasswordLayout.error = null
                    isRepeatPasswordValid = true
                    unlockSignupButton()
                } else {
                    binding.donorRepeatPasswordLayout.error = getString(R.string.repeat_password_error)
                    isRepeatPasswordValid = false
                    unlockSignupButton()
                }
            }
        }

        binding.donorMobileEt.doOnTextChanged { text, start, before, count ->
            if(validateTor.isEmpty(text.toString())) {
                binding.donorMobileLayout.error = getString(R.string.required_field)
                isMobileValid = false
                unlockSignupButton()
            } else {
                if(validateTorMobileNumber.validate(text.toString(), indianMobileNumberRegex.toString())) {
                    binding.donorMobileLayout.error = null
                    isMobileValid = true
                    unlockSignupButton()
                } else {
                    binding.donorMobileLayout.error = getString(R.string.mobile_error)
                    isMobileValid = false
                    unlockSignupButton()
                }
            }
        }

        binding.donorAddressEt.doOnTextChanged { text, start, before, count ->
            if(validateTor.isEmpty(text.toString())) {
                binding.donorAddressLayout.error = getString(R.string.required_field)
                isAddressValid = false
                unlockSignupButton()
            } else {
                binding.donorAddressLayout.error = null
                isAddressValid = true
                unlockSignupButton()
            }
        }

    }

    private fun unlockSignupButton() {
        if( isNameValid && isEmailValid && isPasswordValid && isRepeatPasswordValid && isMobileValid && isAddressValid && isProfilePhotoValid ) {
            binding.donorSignupBtn.apply {
                isEnabled = true
                setOnClickListener {
                    //make call to firebase

                    Firebase.auth.createUserWithEmailAndPassword(binding.donorEmailEt.text.toString(), binding.donorPasswordEt.text.toString())
                        .addOnCompleteListener {
                            if (it.isSuccessful) {
                                //save data to firestore

                                val db = Firebase.firestore
                                db.collection("donors").document(binding.donorMobileEt.text.toString())
                                    .set(
                                        DonorModel(
                                            name = binding.donorNameEt.text.toString(),
                                            email = binding.donorEmailEt.text.toString(),
                                            mobile = binding.donorMobileEt.text.toString(),
                                            address = fullAddress,
                                            photo = base64Photo,
                                            mobileVerified = mobileVerified
                                        )
                                    )
                                    .addOnCompleteListener { firestoreTask ->
                                        if(firestoreTask.isSuccessful) {
                                            MotionToast.createColorToast(requireActivity(),"Sign Up Completed!",
                                                MotionToast.TOAST_SUCCESS,
                                                MotionToast.GRAVITY_BOTTOM,
                                                MotionToast.LONG_DURATION,
                                                ResourcesCompat.getFont(requireActivity(),R.font.helvetica_regular))

                                            startActivity(Intent(requireActivity(), HomeActivity::class.java))
                                        } else {
                                            MotionToast.createColorToast(requireActivity(),"Data was not saved!",
                                                MotionToast.TOAST_ERROR,
                                                MotionToast.GRAVITY_BOTTOM,
                                                MotionToast.LONG_DURATION,
                                                ResourcesCompat.getFont(requireActivity(),R.font.helvetica_regular))
                                        }
                                    }

                            } else {
                                if (it.exception is FirebaseAuthUserCollisionException) {
                                    MotionToast.createColorToast(requireActivity(),"Email already exists!",
                                        MotionToast.TOAST_WARNING,
                                        MotionToast.GRAVITY_BOTTOM,
                                        MotionToast.LONG_DURATION,
                                        ResourcesCompat.getFont(requireActivity(),R.font.helvetica_regular))
                                } else {
                                    MotionToast.createColorToast(requireActivity(),"Sign Up Failed! Please try again later.",
                                        MotionToast.TOAST_ERROR,
                                        MotionToast.GRAVITY_BOTTOM,
                                        MotionToast.LONG_DURATION,
                                        ResourcesCompat.getFont(requireActivity(),R.font.helvetica_regular))
                                }
                            }
                        }
                }
            }
        } else {
            binding.donorSignupBtn.apply {
                isEnabled = false
            }
        }
    }

    companion object {
        const val MAP_BUTTON_REQUEST_CODE = 111
    }

}