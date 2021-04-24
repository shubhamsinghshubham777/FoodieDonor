package com.shubham.foodiedonor.views

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.viewbinding.library.activity.viewBinding
import androidx.core.content.res.ResourcesCompat
import androidx.core.widget.doOnTextChanged
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.raywenderlich.android.validatetor.ValidateTor
import com.shubham.foodiedonor.R
import com.shubham.foodiedonor.databinding.ActivityForgotPasswordBinding
import com.tuonbondol.keyboardutil.hideSoftKeyboard
import www.sanju.motiontoast.MotionToast

class ForgotPasswordActivity : AppCompatActivity() {

    private val binding: ActivityForgotPasswordBinding by viewBinding()
    private val TAG = "ForgotPasswordActivityTAG"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setupAllViews()
        observeAllViews()
    }

    private fun observeAllViews() {
        val validateTor = ValidateTor()

        binding.apply {
            forgotPasswordEt.doOnTextChanged { text, start, before, count ->
                if (validateTor.isEmpty(text.toString())) {
                    forgotPasswordTil.error = getString(R.string.required_field)
                    forgotPasswordSubmitBtn.isEnabled = false
                } else {
                    if (validateTor.isEmail(text.toString())) {
                        forgotPasswordTil.error = null
                        forgotPasswordSubmitBtn.isEnabled = true
                    } else {
                        forgotPasswordTil.error = getString(R.string.email_error)
                        forgotPasswordSubmitBtn.isEnabled = false
                    }
                }
            }
        }

    }

    private fun setupAllViews() {
        binding.apply {
            forgotPasswordSubmitBtn.setOnClickListener {
                forgotPasswordLoadingAnimation.visibility = View.VISIBLE
                hideSoftKeyboard()
                val email = binding.forgotPasswordEt.text.toString().trim()
                Firebase.auth.sendPasswordResetEmail(email)
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            forgotPasswordLoadingAnimation.visibility = View.GONE
                            MotionToast.createColorToast(
                                this@ForgotPasswordActivity,
                                "Check your email \n $email",
                                MotionToast.TOAST_SUCCESS,
                                MotionToast.GRAVITY_BOTTOM,
                                MotionToast.LONG_DURATION,
                                ResourcesCompat.getFont(
                                    this@ForgotPasswordActivity,
                                    R.font.alegreya_sans_sc_medium
                                )
                            )
                            finish()
                        } else {
                            forgotPasswordLoadingAnimation.visibility = View.GONE
                            MotionToast.createColorToast(
                                this@ForgotPasswordActivity,
                                "${it.exception!!.localizedMessage}",
                                MotionToast.TOAST_ERROR,
                                MotionToast.GRAVITY_BOTTOM,
                                MotionToast.LONG_DURATION,
                                ResourcesCompat.getFont(
                                    this@ForgotPasswordActivity,
                                    R.font.alegreya_sans_sc_medium
                                )
                            )
                            Log.d(TAG, "Password Reset Error: ${it.exception!!.localizedMessage}")
                        }
                    }
            }
        }
    }
}