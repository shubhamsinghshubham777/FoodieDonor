package com.shubham.foodiedonor.views.fragments

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.core.widget.doOnTextChanged
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.pixelcarrot.base64image.Base64Image
import com.raywenderlich.android.validatetor.RegexMatcher
import com.raywenderlich.android.validatetor.ValidateTor
import com.schibstedspain.leku.*
import com.schibstedspain.leku.locale.SearchZoneRect
import com.shubham.foodiedonor.BuildConfig
import com.shubham.foodiedonor.R
import com.shubham.foodiedonor.databinding.FragmentReceiverSignupBinding
import com.shubham.foodiedonor.models.ReceiverModel
import com.shubham.foodiedonor.utils.Constants
import com.shubham.foodiedonor.utils.Constants.MAP_BUTTON_REQUEST_CODE
import com.shubham.foodiedonor.utils.Constants.VERIFY_OTP_REQUEST_CODE
import com.shubham.foodiedonor.utils.Constants.mySharedPrefName
import com.shubham.foodiedonor.views.VerifyMobileActivity
import com.shubham.foodiedonor.views.ReceiverHomeActivity
import www.sanju.motiontoast.MotionToast


class ReceiverSignupFragment : Fragment(R.layout.fragment_receiver_signup) {

//    private var binding: FragmentReceiverSignupBinding by viewBinding()

    private var _binding: FragmentReceiverSignupBinding? = null
    private val binding get() = _binding!!
    private val args: ReceiverSignupFragmentArgs by navArgs()

    private var isNameValid = false
    private var isEmailValid = false
    private var isPasswordValid = false
    private var isRepeatPasswordValid = false
    private var isMobileValid = false
    private var isMobileVerified = false
    private var isAddressValid = false
    private var isProfilePhotoValid = false
    private var isCinValid = false
    private val validateTor = ValidateTor()
    private val validateTorMobileNumber = RegexMatcher()
    private val indianMobileNumberRegex = Regex("^[6-9]\\d{9}\$")
    private var userPhotoFile: Uri = Uri.EMPTY
    private var userPhotoLink: String? = String()
    private val TAG = "receiverSignupFragmentTAG"
    private var latitude = 0.0
    private var longitude = 0.0
    private var address = ""
    private var postalcode = ""
    private var bundle = Bundle.EMPTY
    private var fullAddress = ""
    private var timeZoneId = ""
    private var timeZoneDisplayName = ""
//    private var lekuPoi = LekuPoi.Companion.CREATOR.createFromParcel(null)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReceiverSignupBinding.inflate(inflater, container, false)
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

        setupAllFields()
        observeAllFields()
        unlockSignupButton()

        binding.receiverProfilePhoto.setOnClickListener {
            ImagePicker.with(this)
                .cropSquare()
                .compress(1024)
                .maxResultSize(200, 200)
                .start()
        }

        binding.receiverAddressIv.setOnClickListener {

            val action = ReceiverSignupFragmentDirections.actionReceiverSignupFragmentToMapsFragment(
                binding.receiverNameEt.text.toString(),
                binding.receiverEmailEt.text.toString(),
                binding.receiverMobileEt.text.toString(),
                isMobileVerified,
                binding.receiverPasswordEt.text.toString(),
                binding.receiverRepeatPasswordEt.text.toString(),
                null,
                binding.receiverCinEt.text.toString(),
                latitude.toFloat(),
                longitude.toFloat(),
                findNavController().currentDestination!!.id
            )
            findNavController().navigate(action)

        }

    }

    private fun setupAllFields() {
        binding.apply {
            receiverNameEt.setText(args.name)
            receiverEmailEt.setText(args.email)
            receiverPasswordEt.setText(args.password)
            receiverRepeatPasswordEt.setText(args.repeatPassword)
            receiverMobileEt.setText(args.mobile)
            isMobileVerified = args.mobileVerified
            receiverAddressEt.setText(args.address)
            receiverCinEt.setText(args.cinNumber)
            fullAddress = args.address.toString()
            latitude = args.latitude.toDouble()
            longitude = args.longitude.toDouble()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            val fileUri = data?.data
            binding.receiverProfilePhoto.setImageURI(fileUri)

            if (fileUri != null) {
                userPhotoFile = fileUri
                isProfilePhotoValid = true
                observeAllFields()
                unlockSignupButton()
            }

            if (requestCode == MAP_BUTTON_REQUEST_CODE && data != null) {

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

                binding.receiverAddressEt.setText(fullAddress)
                if (!validateTor.isEmpty(binding.receiverAddressEt.text.toString())) {
                    binding.receiverAddressLayout.error = null
                    isAddressValid = true
                }

            }

            if (requestCode == VERIFY_OTP_REQUEST_CODE) {

            } else if (requestCode == MAP_BUTTON_REQUEST_CODE && data != null) {
                latitude = data.getDoubleExtra(LATITUDE, 0.0)
                longitude = data.getDoubleExtra(LONGITUDE, 0.0)
                address = data.getStringExtra(LOCATION_ADDRESS).toString()
//                lekuPoi = data.getParcelableExtra<LekuPoi>(LEKU_POI)
            }


        }

        if (resultCode == Activity.RESULT_FIRST_USER) {

            isMobileVerified = true

            if (data?.extras != null) {
                isMobileVerified = data.getBooleanExtra("isMobileVerified", false)
                binding.lottieMobileVerification.apply {
                    setAnimation(R.raw.verified)
                    speed = 0.5f
                    playAnimation()
                    binding.lottieMobileVerificationTv.text = "Mobile Verified!"
                    isClickable = false
                    isFocusable = false
                    binding.receiverMobileEt.isClickable = false
                    binding.receiverMobileEt.isFocusable = false
                    Log.d(TAG, "Result First User")
                }
            }

        }

        if (resultCode == Activity.RESULT_CANCELED) {
            Log.d(TAG, "Result Cancelled")
        }

    }

    private fun observeAllFields() {

        if (!binding.receiverNameEt.text.isNullOrEmpty()) {isNameValid = true}
        if (!binding.receiverEmailEt.text.isNullOrEmpty()) {isEmailValid = true}
        if (!binding.receiverPasswordEt.text.isNullOrEmpty()) {isPasswordValid = true}
        if (!binding.receiverRepeatPasswordEt.text.isNullOrEmpty()) {isRepeatPasswordValid = true}
        if (!binding.receiverMobileEt.text.isNullOrEmpty()) {isMobileValid = true}
        if (args.mobileVerified) {isMobileVerified = true}
        if (!binding.receiverAddressEt.text.isNullOrEmpty()) {isAddressValid = true}
        if (!binding.receiverCinEt.text.isNullOrEmpty()) {isCinValid = true}

        binding.receiverNameEt.doOnTextChanged { text, start, before, count ->
            if (validateTor.isEmpty(text.toString())) {
                binding.receiverNameLayout.error = getString(R.string.required_field)
                isNameValid = false
                unlockSignupButton()
            } else {
                binding.receiverNameLayout.error = null
                isNameValid = true
                unlockSignupButton()
            }
        }

        binding.receiverEmailEt.doOnTextChanged { text, start, before, count ->
            if (validateTor.isEmpty(text.toString())) {
                binding.receiverEmailLayout.error = getString(R.string.required_field)
                isEmailValid = false
                unlockSignupButton()
            } else {
                if (validateTor.isEmail(text.toString())) {
                    binding.receiverEmailLayout.error = null
                    isEmailValid = true
                    unlockSignupButton()
                } else {
                    binding.receiverEmailLayout.error = getString(R.string.email_error)
                    isEmailValid = false
                    unlockSignupButton()
                }
            }
        }

        binding.receiverPasswordEt.doOnTextChanged { text, start, before, count ->
            if (validateTor.isEmpty(text.toString())) {
                binding.receiverPasswordLayout.helperText = getString(R.string.required_field)
                isPasswordValid = false
                unlockSignupButton()
            } else {
                if (validateTor.isAtleastLength(text.toString(), 8)
                    && validateTor.hasAtleastOneUppercaseCharacter(text.toString())
                    && validateTor.hasAtleastOneDigit(text.toString())
                    && validateTor.hasAtleastOneSpecialCharacter(text.toString())
                ) {
                    binding.receiverPasswordLayout.helperText = null
                    isPasswordValid = true
                    unlockSignupButton()
                } else {
                    binding.receiverPasswordLayout.helperText = getString(R.string.password_error)
                    isPasswordValid = false
                    unlockSignupButton()
                }
            }
        }

        binding.receiverRepeatPasswordEt.doOnTextChanged { text, start, before, count ->
            if (validateTor.isEmpty(text.toString())) {
                binding.receiverRepeatPasswordLayout.helperText = getString(R.string.required_field)
                isRepeatPasswordValid = false
                unlockSignupButton()
            } else {
                if (text.toString() == binding.receiverPasswordEt.text.toString()) {
                    binding.receiverRepeatPasswordLayout.helperText = null
                    isRepeatPasswordValid = true
                    unlockSignupButton()
                } else {
                    binding.receiverRepeatPasswordLayout.helperText =
                        getString(R.string.repeat_password_error)
                    isRepeatPasswordValid = false
                    unlockSignupButton()
                }
            }
        }

        binding.receiverMobileEt.doOnTextChanged { text, start, before, count ->
            if (validateTor.isEmpty(text.toString())) {
                binding.receiverMobileLayout.error = getString(R.string.required_field)
                isMobileValid = false
                unlockSignupButton()
                binding.lottieMobileVerification.isClickable = false
                binding.lottieMobileVerification.isFocusable = false
            } else {
                if (validateTorMobileNumber.validate(
                        text.toString(),
                        indianMobileNumberRegex.toString()
                    )
                ) {
                    binding.receiverMobileLayout.error = null
                    isMobileValid = true
                    binding.lottieMobileVerification.isClickable = true
                    binding.lottieMobileVerification.isFocusable = true
                    unlockSignupButton()
                    binding.lottieMobileVerification.setOnClickListener {
                        val intent = Intent(
                            requireContext().applicationContext,
                            VerifyMobileActivity::class.java
                        )
                        intent.putExtra("mobileNumber", binding.receiverMobileEt.text.toString())
                        startActivityForResult(intent, VERIFY_OTP_REQUEST_CODE)
                    }
                } else {
                    binding.receiverMobileLayout.error = getString(R.string.mobile_error)
                    isMobileValid = false
                    unlockSignupButton()
                    binding.lottieMobileVerification.isClickable = false
                    binding.lottieMobileVerification.isFocusable = false
                }
            }
        }

        binding.receiverAddressEt.doOnTextChanged { text, start, before, count ->
            if (validateTor.isEmpty(text.toString())) {
                binding.receiverAddressLayout.error = getString(R.string.required_field)
                isAddressValid = false
                unlockSignupButton()
            } else {
                binding.receiverAddressLayout.error = null
                isAddressValid = true
                unlockSignupButton()
            }
        }

        binding.receiverCinEt.doOnTextChanged { text, start, before, count ->
            if (validateTor.isEmpty(text.toString())) {
                binding.receiverCinLayout.error = getString(R.string.required_field)
                isCinValid = false
                unlockSignupButton()
            } else {

                if (text.toString().length == 21) {
                    binding.receiverCinLayout.error = null
                    isCinValid = true
                    unlockSignupButton()
                } else {
                    binding.receiverCinLayout.error = getString(R.string.cin_error)
                    isCinValid = false
                    unlockSignupButton()
                }

            }
        }

    }

    private fun unlockSignupButton() {
        Log.d(TAG, "unlockSignupButton: $isNameValid && $isEmailValid && $isPasswordValid && $isRepeatPasswordValid && $isMobileValid && $isAddressValid && $isProfilePhotoValid && $isCinValid")
        if (isNameValid && isEmailValid && isPasswordValid && isRepeatPasswordValid && isMobileValid && isAddressValid && isProfilePhotoValid && isCinValid) {
            binding.receiverSignupBtn.apply {
                isEnabled = true
                setOnClickListener {

                    binding.receiverSignupLoaderAnimationLottie.visibility = View.VISIBLE
                    //make call to firebase

                    Firebase.auth.createUserWithEmailAndPassword(
                        binding.receiverEmailEt.text.toString(),
                        binding.receiverPasswordEt.text.toString()
                    )
                        .addOnCompleteListener {
                            if (it.isSuccessful) {

                                //save photo to firebase storage
                                val userEmail = binding.receiverEmailEt.text.toString()
                                Log.d(TAG, "userPhotoFile : $userPhotoFile")
                                Firebase.storage.reference.child("$userEmail/userPhoto.jpg")
                                    .putFile(userPhotoFile)
                                    .addOnSuccessListener {

                                        //save receiverMobile to sharedPrefs
                                        requireActivity().getSharedPreferences(mySharedPrefName, Context.MODE_PRIVATE).edit().apply {
                                            putString("globalReceiverMobile", binding.receiverMobileEt.text.toString())
                                        }.apply()

                                        //Get photo link from firebase storage and save it to sharedPref
                                        Firebase.storage.reference.child("$userEmail/userPhoto.jpg")
                                            .downloadUrl.addOnCompleteListener { photo ->
                                                requireActivity().getSharedPreferences(
                                                    mySharedPrefName, Context.MODE_PRIVATE).edit().apply {
                                                    putString("globalReceiverPhotoUrl", photo.result.toString())
                                                }.apply()
                                                userPhotoLink = requireActivity().getSharedPreferences(
                                                    mySharedPrefName, Context.MODE_PRIVATE).getString("globalReceiverPhotoUrl", null)
                                                Log.d(TAG, "user firestorage photo: ${photo.result} & $userPhotoLink")

                                                //save data to firestore
                                                val db = Firebase.firestore
                                                db.collection("receivers")
                                                    .document(binding.receiverMobileEt.text.toString())
                                                    .set(
                                                        ReceiverModel(
                                                            name = binding.receiverNameEt.text.toString(),
                                                            email = binding.receiverEmailEt.text.toString(),
                                                            mobile = binding.receiverMobileEt.text.toString(),
                                                            address = fullAddress,
                                                            photo = userPhotoLink,
                                                            mobileVerified = isMobileVerified,
                                                            latitude = latitude,
                                                            longitude = longitude,
                                                            cinNumber = binding.receiverCinEt.text.toString(),
                                                            rating = 0.0
                                                        )
                                                    )
                                                    .addOnCompleteListener { firestoreTask ->
                                                        if (firestoreTask.isSuccessful) {
                                                            MotionToast.createColorToast(
                                                                requireActivity(), "Sign Up Completed!",
                                                                MotionToast.TOAST_SUCCESS,
                                                                MotionToast.GRAVITY_BOTTOM,
                                                                MotionToast.LONG_DURATION,
                                                                ResourcesCompat.getFont(
                                                                    requireActivity(),
                                                                    R.font.helvetica_regular
                                                                )
                                                            )

                                                            startActivity(
                                                                Intent(
                                                                    requireActivity(),
                                                                    ReceiverHomeActivity::class.java
                                                                )
                                                            )
                                                        } else {
                                                            MotionToast.createColorToast(
                                                                requireActivity(), "Data was not saved!",
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
                                            .addOnFailureListener {

                                            }

                                            }

                            } else {
                                if (it.exception is FirebaseAuthUserCollisionException) {
                                    binding.receiverSignupLoaderAnimationLottie.visibility = View.GONE
                                    MotionToast.createColorToast(
                                        requireActivity(), "Email already exists!",
                                        MotionToast.TOAST_WARNING,
                                        MotionToast.GRAVITY_BOTTOM,
                                        MotionToast.LONG_DURATION,
                                        ResourcesCompat.getFont(
                                            requireActivity(),
                                            R.font.helvetica_regular
                                        )
                                    )
                                } else if(it.exception?.localizedMessage == "The email address is badly formatted.") {
                                    binding.receiverSignupLoaderAnimationLottie.visibility = View.GONE
                                    MotionToast.createColorToast(
                                        requireActivity(),
                                        it.exception?.localizedMessage!!,
                                        MotionToast.TOAST_ERROR,
                                        MotionToast.GRAVITY_BOTTOM,
                                        MotionToast.LONG_DURATION,
                                        ResourcesCompat.getFont(
                                            requireActivity(),
                                            R.font.helvetica_regular
                                        )
                                    )
                                } else {
                                    Log.d(TAG, "unlockSignupButton: ${it.exception?.localizedMessage}")
                                    binding.receiverSignupLoaderAnimationLottie.visibility = View.GONE
                                    MotionToast.createColorToast(
                                        requireActivity(),
                                        "Sign Up Failed! Please try again later.",
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
        } else {
            binding.receiverSignupBtn.apply {
                isEnabled = false
            }
        }
    }

}