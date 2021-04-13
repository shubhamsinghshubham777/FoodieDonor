package com.shubham.foodiedonor.views

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.viewbinding.library.activity.viewBinding
import androidx.core.content.res.ResourcesCompat
import androidx.core.widget.doOnTextChanged
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.raywenderlich.android.validatetor.ValidateTor
import com.shubham.foodiedonor.R
import com.shubham.foodiedonor.databinding.ActivityLoginBinding
import com.shubham.foodiedonor.views.receiver.ReceiverHomeActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
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
            if(validateTor.isEmpty(text.toString())) {
                binding.emailLayout.error = getString(R.string.required_field)
                isEmailValid = false
                unlockLoginButton()
            } else {

                if(validateTor.isEmail(text.toString())) {
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
            if(validateTor.isEmpty(text.toString())) {
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

    }

    private fun unlockLoginButton() {
        if(isEmailValid && isPasswordValid) {
            binding.loginBtn.apply {
                isEnabled = true
                setOnClickListener {

                    binding.loadingAnimation.visibility = View.VISIBLE

                    Firebase.auth.signInWithEmailAndPassword(binding.emailET.text.toString(), binding.passwordET.text.toString())
                            .addOnCompleteListener {
                                if(it.isSuccessful) {

                                    CoroutineScope(Dispatchers.IO).launch {
                                        val querySnapshot = Firebase.firestore.collection("donors")
                                            .whereEqualTo("email", it.result.user?.email)
                                            .get()
                                            .await()

                                        for(document in querySnapshot.documents) {
                                            val personType = document.getString("type")
                                            personType1 = personType!!
                                            Log.d(TAG, "Person Type: $personType1")

                                            if(personType == "donor") {
                                                binding.loadingAnimation.visibility = View.GONE
                                                startActivity(Intent(this@LoginActivity, HomeActivity::class.java))
                                                finish()
                                            } else {
                                                startActivity(Intent(this@LoginActivity, ReceiverHomeActivity::class.java))
                                                finish()
                                            }
                                        }

                                    }
                                } else {
                                    binding.loadingAnimation.visibility = View.GONE
                                    MotionToast.createColorToast(this@LoginActivity,"Couldn't login. Check credentials!",
                                            MotionToast.TOAST_ERROR,
                                            MotionToast.GRAVITY_BOTTOM,
                                            MotionToast.SHORT_DURATION,
                                            ResourcesCompat.getFont(this@LoginActivity,R.font.helvetica_regular))
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