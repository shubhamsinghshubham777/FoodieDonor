package com.shubham.foodiedonor.views

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.viewbinding.library.activity.viewBinding
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.core.widget.doOnTextChanged
import com.fondesa.kpermissions.allGranted
import com.fondesa.kpermissions.extension.permissionsBuilder
import com.fondesa.kpermissions.extension.send
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.raywenderlich.android.validatetor.ValidateTor
import com.shubham.foodiedonor.R
import com.shubham.foodiedonor.databinding.ActivityVerifyMobileBinding
import com.stfalcon.smsverifycatcher.OnSmsCatchListener
import com.stfalcon.smsverifycatcher.SmsVerifyCatcher
import com.tuonbondol.keyboardutil.hideSoftKeyboard
import www.sanju.motiontoast.MotionToast
import java.util.concurrent.TimeUnit

class VerifyMobileActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private val binding: ActivityVerifyMobileBinding by viewBinding()
    private lateinit var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    private val TAG = "VerifyMobileActivityTAG"
    private lateinit var verificationId1: String
    private lateinit var resendToken: PhoneAuthProvider.ForceResendingToken
    private lateinit var smsVerifyCatcher: SmsVerifyCatcher
    private var mobileNumber: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        smsVerifyCatcher = SmsVerifyCatcher(this, OnSmsCatchListener {
            binding.otpEt.setText(it.toString())
            binding.lottieAnimationView4.visibility = View.VISIBLE
            val code = binding.otpEt.text.toString()
            val credential = PhoneAuthProvider.getCredential(verificationId1, code)
            signInWithPhoneAuthCredential(credential)
            binding.verifyOtpBtn.isEnabled = false
        })

        mobileNumber = "+91" + intent.getStringExtra("mobileNumber")
        binding.otpMobileTv.text = "Please enter the OTP sent to $mobileNumber"

        callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                Log.d(TAG, "onVerificationCompleted:$credential")
//                signInWithPhoneAuthCredential(credential)
            }

            override fun onVerificationFailed(e: FirebaseException) {
                if (e is FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                    Log.d(TAG, "onVerificationFailed: ${e.message}")
                } else if (e is FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                    Log.d(TAG, "onVerificationFailed: ${e.message}")
                }
            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                super.onCodeSent(verificationId, token)
                Log.d(TAG, "onCodeSent:$verificationId")

                verificationId1 = verificationId
                resendToken = token

                binding.verifyOtpBtn.apply {

                    isEnabled = true

                    setOnClickListener {
                        hideSoftKeyboard()
                        binding.lottieAnimationView4.visibility = View.VISIBLE
                        val code = binding.otpEt.text.toString()
                        val credential = PhoneAuthProvider.getCredential(verificationId1, code)
                        signInWithPhoneAuthCredential(credential)
                    }

                    doOnTextChanged { text, start, before, count ->
                        if (text.toString().isEmpty()) {
                            binding.otpLayout.error = getString(R.string.required_field)
                        } else {
                            binding.otpLayout.error = null
                        }
                    }

                }

                MotionToast.createColorToast(
                    this@VerifyMobileActivity, "OTP Sent to $mobileNumber",
                    MotionToast.TOAST_INFO,
                    MotionToast.GRAVITY_BOTTOM,
                    MotionToast.LONG_DURATION,
                    ResourcesCompat.getFont(this@VerifyMobileActivity, R.font.helvetica_regular)
                )

            }

        }

        watchOtpEt()

        permissionsBuilder(Manifest.permission.READ_SMS).build().send() { result ->
            if(result.allGranted()) {
                auth = Firebase.auth
                val options = PhoneAuthOptions.newBuilder(auth)
                    .setPhoneNumber(mobileNumber)
                    .setTimeout(120L, TimeUnit.SECONDS)
                    .setActivity(this)
                    .setCallbacks(callbacks)
                    .build()

                PhoneAuthProvider.verifyPhoneNumber(options)
            } else {
                MotionToast.createColorToast(
                    this, "Please accept the permissions to continue!",
                    MotionToast.TOAST_WARNING,
                    MotionToast.GRAVITY_BOTTOM,
                    MotionToast.LONG_DURATION,
                    ResourcesCompat.getFont(
                        this,
                        R.font.helvetica_regular
                    )
                )
            }
        }

    }

    override fun onStart() {
        super.onStart()
        smsVerifyCatcher.onStart()
    }

    override fun onStop() {
        super.onStop()
        smsVerifyCatcher.onStop()
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        smsVerifyCatcher.onRequestPermissionsResult(requestCode, permissions, grantResults);

//        auth = Firebase.auth
//        val options = PhoneAuthOptions.newBuilder(auth)
//            .setPhoneNumber(mobileNumber)
//            .setTimeout(120L, TimeUnit.SECONDS)
//            .setActivity(this)
//            .setCallbacks(callbacks)
//            .build()
//
//        PhoneAuthProvider.verifyPhoneNumber(options)

    }

    private fun watchOtpEt() {
        val validateTor = ValidateTor()

        binding.otpEt.doOnTextChanged { text, start, before, count ->
            if(text.toString().length == 6) {
                binding.verifyOtpBtn.isEnabled = true
            } else {
                binding.verifyOtpBtn.isEnabled = false
            }
        }
    }

    private fun otpConfirmed() {
        val intent = Intent()
        intent.putExtra("isMobileVerified", true)
        setResult(RESULT_FIRST_USER, intent)
        if(intent.getStringExtra("instruction") != "doNotSignout") {
            auth.signOut()
        }
//        onActivityResult(112, RESULT_OK, intent)
        finish()
    }

    private fun otpNotConfirmed() {
        val intent = Intent()
        intent.putExtra("isMobileVerified", false)
        setResult(RESULT_CANCELED, intent)
        if(intent.getStringExtra("instruction") != "doNotSignout") {
            auth.signOut()
        }
//        onActivityResult(112, RESULT_OK, intent)
        finish()
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        Firebase.auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val isNewUser = task.result.additionalUserInfo?.isNewUser
                    Log.d(TAG, "Is this a new user?: $isNewUser")

                    if(isNewUser == true) {
                        otpConfirmed()
                        Log.d(TAG, "signInWithCredential:success")
                        MotionToast.createColorToast(
                            this, "OTP Verification Successful!",
                            MotionToast.TOAST_SUCCESS,
                            MotionToast.GRAVITY_BOTTOM,
                            MotionToast.LONG_DURATION,
                            ResourcesCompat.getFont(this, R.font.helvetica_regular)
                        )
                    } else if(isNewUser == false) {
                        MotionToast.createColorToast(
                            this, "This number already exists!",
                            MotionToast.TOAST_WARNING,
                            MotionToast.GRAVITY_BOTTOM,
                            MotionToast.LONG_DURATION,
                            ResourcesCompat.getFont(this, R.font.helvetica_regular)
                        )

                        otpNotConfirmed()
                    }

                } else {
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        // The verification code entered was invalid
                        Log.d(TAG, "Verification code was invalid")
                        MotionToast.createColorToast(
                            this, "Invalid OTP Entered. Please try again!",
                            MotionToast.TOAST_WARNING,
                            MotionToast.GRAVITY_BOTTOM,
                            MotionToast.LONG_DURATION,
                            ResourcesCompat.getFont(this, R.font.helvetica_regular)
                        )
                    }
                }
            }
    }
}