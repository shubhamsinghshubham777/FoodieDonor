package com.shubham.foodiedonor.views.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.viewbinding.library.fragment.viewBinding
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.DialogFragment
import com.google.firebase.firestore.SetOptions
import com.shubham.foodiedonor.R
import com.shubham.foodiedonor.databinding.FragmentCustomDialogBinding
import com.shubham.foodiedonor.utils.Constants
import www.sanju.motiontoast.MotionToast

class CustomDialogFragment(
    private val receiverEmail: String,
    private val receiverName: String,
    private val receiverMobile: String,
    private val donorEmail: String
) : DialogFragment() {

    private val binding: FragmentCustomDialogBinding by viewBinding()
    private val TAG = "CustomDialogFragmentTAG"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_custom_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupAllViews()

    }

    @SuppressLint("SetTextI18n")
    private fun setupAllViews() {
        binding.apply {
            Log.d(TAG, "All data received: $receiverName $receiverEmail $receiverMobile")
            fcdName.text = "Rate $receiverName"
            fcdEmail.text = receiverEmail
            fcdRatingBar.setOnRatingBarChangeListener { simpleRatingBar, rating, fromUser ->
                fcdRatingCount.text = rating.toString()
            }
            fcdSubmitBtn.setOnClickListener {

                fcdLoadingAnimtaion.visibility = View.VISIBLE
                it.isEnabled = false

                val dataMap = mapOf(Pair("rating", fcdRatingCount.text.toString().toFloat()))

                Constants.globalReceiverCollectionRef.document(receiverMobile).collection("ratings")
                    .document(donorEmail)
                    .set(dataMap, SetOptions.merge())
                    .addOnCompleteListener {

                        fcdLoadingAnimtaion.visibility = View.GONE

                        if (it.isSuccessful) {
                            Constants.globalReceiverCollectionRef.document(receiverMobile)
                                .collection("ratings").get()
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        var totalNumberOfRatings = 0
                                        var totalRatingSum = 0.0
                                        for (document in task.result.documents) {
                                            ++totalNumberOfRatings
                                            totalRatingSum += document.get("rating").toString()
                                                .toFloat()
                                        }
                                        val averageRating = totalRatingSum / totalNumberOfRatings

                                        Constants.globalReceiverCollectionRef.document(receiverMobile)
                                            .update("rating", averageRating)
                                            .addOnCompleteListener { receiverRatingUpdate ->
                                                if (receiverRatingUpdate.isSuccessful) {
                                                    MotionToast.createColorToast(
                                                        requireActivity(),
                                                        "Rating submitted successfully!",
                                                        MotionToast.TOAST_SUCCESS,
                                                        MotionToast.GRAVITY_BOTTOM,
                                                        MotionToast.SHORT_DURATION,
                                                        ResourcesCompat.getFont(
                                                            requireActivity(),
                                                            R.font.alegreya_sans_sc_medium
                                                        )
                                                    )
                                                    dismiss()
                                                } else {
                                                    Log.d(
                                                        TAG,
                                                        "Error cause: ${receiverRatingUpdate.exception?.localizedMessage}"
                                                    )
                                                    MotionToast.createColorToast(
                                                        requireActivity(),
                                                        "Couldn't update rating. Please try again!",
                                                        MotionToast.TOAST_ERROR,
                                                        MotionToast.GRAVITY_BOTTOM,
                                                        MotionToast.SHORT_DURATION,
                                                        ResourcesCompat.getFont(
                                                            requireActivity(),
                                                            R.font.alegreya_sans_sc_medium
                                                        )
                                                    )
                                                    dismiss()
                                                }
                                            }
                                        Log.d(TAG, "Total ratings are: $totalNumberOfRatings")
                                    } else {
                                        Log.d(
                                            TAG,
                                            "Error couldn't retrieve all ratings. Caused by: ${it.exception?.localizedMessage}"
                                        )
                                        MotionToast.createColorToast(
                                            requireActivity(),
                                            "Unexpected error!",
                                            MotionToast.TOAST_WARNING,
                                            MotionToast.GRAVITY_BOTTOM,
                                            MotionToast.SHORT_DURATION,
                                            ResourcesCompat.getFont(
                                                requireActivity(),
                                                R.font.alegreya_sans_sc_medium
                                            )
                                        )
                                    }
                                }
                        } else {
                            Log.d(TAG, "Error caused by: ${it.exception?.localizedMessage}")
                        }
                    }
            }
        }
    }
}