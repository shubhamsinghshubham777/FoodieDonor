package com.shubham.foodiedonor.views

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.viewbinding.library.activity.viewBinding
import androidx.core.content.res.ResourcesCompat
import androidx.core.widget.doOnTextChanged
import com.google.firebase.auth.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.raywenderlich.android.validatetor.ValidateTor
import com.shubham.foodiedonor.R
import com.shubham.foodiedonor.databinding.ActivityLoginBinding
import com.shubham.foodiedonor.utils.Constants.globalDonorMobile
import com.shubham.foodiedonor.utils.Constants.mySharedPrefName
import com.tuonbondol.keyboardutil.hideSoftKeyboard
import www.sanju.motiontoast.MotionToast

class LoginActivity : AppCompatActivity() {

    private val binding: ActivityLoginBinding by viewBinding()
    private val validateTor = ValidateTor()
    private var isEmailValid = false
    private var isPasswordValid = false
    private var personType1: String = ""
    private val TAG = "LoginActivityTAG"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.emailET.doOnTextChanged { text, start, before, count ->
            if (validateTor.isEmpty(text.toString())) {
                binding.emailLayout.error = getString(R.string.required_field)
                isEmailValid = false
                unlockLoginButton()
            } else {

                if (validateTor.isEmail(text.toString())) {
                    binding.emailLayout.error = null
                    isEmailValid = true
                    unlockLoginButton()
                } else {
                    binding.emailLayout.error = getString(R.string.email_error)
                    isEmailValid = false
                    unlockLoginButton()
                }
            }
        }

        binding.passwordET.doOnTextChanged { text, start, before, count ->
            if (validateTor.isEmpty(text.toString())) {
                binding.passwordLayout.error = getString(R.string.required_field)
                isPasswordValid = false
                unlockLoginButton()
            } else {
                binding.passwordLayout.error = null
                isPasswordValid = true
                unlockLoginButton()
            }
        }

        binding.signupTv.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
        }

        binding.forgotPasswordTv.setOnClickListener {
            startActivity(Intent(this, ForgotPasswordActivity::class.java))
        }

    }

    private fun unlockLoginButton() {

        val sharedPreferences = getSharedPreferences(mySharedPrefName, Context.MODE_PRIVATE)

        if (isEmailValid && isPasswordValid) {
            binding.loginBtn.apply {
                isEnabled = true
                setOnClickListener {
                    this.isEnabled = false
                    hideSoftKeyboard()

                    binding.loadingAnimation.visibility = View.VISIBLE

                    Firebase.auth.signInWithEmailAndPassword(
                        binding.emailET.text.toString(),
                        binding.passwordET.text.toString()
                    )
                        .addOnCompleteListener {
                            if (it.isSuccessful) {

                                val donorCollectionRef = Firebase.firestore.collection("donors")
                                    .whereEqualTo("email", Firebase.auth.currentUser?.email)
                                donorCollectionRef.get()
                                    .addOnCompleteListener { donorTask ->
                                        if (donorTask.result.isEmpty) {
                                            Log.d(TAG, "onCreate: Donor does not exist!")
                                            val receiverCollectionRef =
                                                Firebase.firestore.collection("receivers")
                                            receiverCollectionRef.get()
                                                .addOnCompleteListener { task ->
                                                    if (task.result.isEmpty) { Log.d(TAG, "onCreate: Receiver does not exist!")
                                                        binding.apply {
                                                            loadingAnimation.visibility = View.GONE
                                                            loginBtn.isEnabled = true
                                                        }
                                                        MotionToast.createColorToast(
                                                            this@LoginActivity, "Login failed! Please try again later.",
                                                            MotionToast.TOAST_WARNING,
                                                            MotionToast.GRAVITY_BOTTOM,
                                                            MotionToast.LONG_DURATION,
                                                            ResourcesCompat.getFont(
                                                                this@LoginActivity,
                                                                R.font.helvetica_regular
                                                            )
                                                        )
                                                    } else {
                                                        for (document in task.result.documents) {
                                                            val type = document.getString("type")
                                                            sharedPreferences.edit().apply {
                                                                putString("globalReceiverMobile", document.getString("mobile").toString())
                                                            }.apply()
                                                            Log.d(TAG, "onCreateType: $type")
                                                            if (type == "receiver") {

                                                                val intent = Intent(
                                                                    this@LoginActivity,
                                                                    ReceiverHomeActivity::class.java
                                                                )
                                                                intent.flags =
                                                                    Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                                                startActivity(intent)
                                                            }
                                                        }
                                                    }
                                                }
                                        } else {

                                            for (document in donorTask.result.documents) {
                                                val type = document.getString("type")
                                                sharedPreferences.edit().apply {
                                                    putString("globalDonorMobile", document.getString("mobile"))
                                                    putString("globalDonorPhotoUrl", document.getString("photo"))
                                                }.commit()
                                                Log.d(TAG, "onCreateType: $type")
                                                if (type == "donor") {

                                                    val intent = Intent(
                                                        this@LoginActivity,
                                                        DonorHomeActivity::class.java
                                                    )
                                                    intent.flags =
                                                        Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                                    startActivity(intent)

                                                }
                                            }
                                        }
                                    }

                            } else {
                                binding.loadingAnimation.visibility = View.GONE
                                this.isEnabled = true
                                when (it.exception) {
                                    is FirebaseAuthInvalidCredentialsException -> {
                                        Log.d(TAG, "unlockLoginButton: ${it.exception}")
                                        MotionToast.createColorToast(
                                            this@LoginActivity,
                                            (it.exception as FirebaseAuthInvalidCredentialsException).localizedMessage!!,
                                            MotionToast.TOAST_WARNING,
                                            MotionToast.GRAVITY_BOTTOM,
                                            MotionToast.LONG_DURATION,
                                            ResourcesCompat.getFont(
                                                this@LoginActivity,
                                                R.font.helvetica_regular
                                            )
                                        )
                                    }
                                    is FirebaseAuthInvalidUserException -> {
                                        Log.d(TAG, "unlockLoginButton: ${it.exception}")
                                        MotionToast.createColorToast(
                                            this@LoginActivity, (it.exception as FirebaseAuthInvalidUserException).localizedMessage!!,
                                            MotionToast.TOAST_WARNING,
                                            MotionToast.GRAVITY_BOTTOM,
                                            MotionToast.LONG_DURATION,
                                            ResourcesCompat.getFont(
                                                this@LoginActivity,
                                                R.font.helvetica_regular
                                            )
                                        )
                                    }
                                    is FirebaseAuthException -> {
                                        Log.d(TAG, "unlockLoginButton: ${it.exception}")
                                        MotionToast.createColorToast(
                                            this@LoginActivity, (it.exception as FirebaseAuthException).localizedMessage!!,
                                            MotionToast.TOAST_WARNING,
                                            MotionToast.GRAVITY_BOTTOM,
                                            MotionToast.LONG_DURATION,
                                            ResourcesCompat.getFont(
                                                this@LoginActivity,
                                                R.font.helvetica_regular
                                            )
                                        )
                                    }
                                    else -> {
                                        Log.d(TAG, "unlockLoginButton: ${it.exception}")
                                        MotionToast.createColorToast(
                                            this@LoginActivity, (it.exception as Exception).localizedMessage!!,
                                            MotionToast.TOAST_WARNING,
                                            MotionToast.GRAVITY_BOTTOM,
                                            MotionToast.LONG_DURATION,
                                            ResourcesCompat.getFont(
                                                this@LoginActivity,
                                                R.font.helvetica_regular
                                            )
                                        )
                                    }
                                }
                            }
                        }
                }
            }
        } else {
            binding.loginBtn.apply {
                isEnabled = false
            }
        }
    }
}