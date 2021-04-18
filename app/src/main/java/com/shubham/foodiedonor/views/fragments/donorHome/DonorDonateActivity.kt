package com.shubham.foodiedonor.views.fragments.donorHome

import android.animation.Animator
import android.animation.TimeInterpolator
import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.viewbinding.library.activity.viewBinding
import android.widget.AdapterView
import android.widget.Toast
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.size
import androidx.core.widget.doOnTextChanged
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import androidx.transition.Slide
import androidx.transition.Transition
import androidx.transition.TransitionManager
import com.chivorn.smartmaterialspinner.SmartMaterialSpinner
import com.google.firebase.Timestamp
import com.pixelcarrot.base64image.Base64Image
import com.shubham.foodiedonor.R
import com.shubham.foodiedonor.databinding.ActivityDonorDonateBinding
import com.shubham.foodiedonor.models.DonateItemModel
import com.shubham.foodiedonor.models.DonorDonationModel
import com.shubham.foodiedonor.models.ReceiverModel
import com.shubham.foodiedonor.utils.Constants.donateItemsList
import com.shubham.foodiedonor.utils.Constants.globalDonationList
import com.shubham.foodiedonor.utils.Constants.globalDonorCollectionRef
import com.shubham.foodiedonor.utils.Constants.globalDonorMobile
import com.shubham.foodiedonor.utils.Constants.globalDonorName
import com.shubham.foodiedonor.utils.Constants.globalDonorPhoto
import com.shubham.foodiedonor.utils.Constants.globalFoodListItemNonBreads
import com.shubham.foodiedonor.views.DonorHomeActivity
import com.tuonbondol.keyboardutil.hideSoftKeyboard
import www.sanju.motiontoast.MotionToast
import javax.inject.Singleton

class DonorDonateActivity : AppCompatActivity() {

    private val binding: ActivityDonorDonateBinding by viewBinding()
    private var spFoodItems: SmartMaterialSpinner<*>? = null
    private var currentFoodItem = ""
    private lateinit var donorDonateAdapter: DonorDonateAdapter
    private val TAG = "DonorDonateActivityTAG"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setupAllFields()
        setupRecyclerView()

    }

    private fun setupRecyclerView() = binding.donateRecyclerView.apply {
            donorDonateAdapter = DonorDonateAdapter()
            adapter = donorDonateAdapter
        }

    @SuppressLint("SetTextI18n")
    private fun setupAllFields() {
        val receiver = intent.getSerializableExtra("receiverInfo") as? ReceiverModel

        binding.donationCheckAnimation.bringToFront()
        binding.donationFailedAnimation.bringToFront()

        binding.apply {
            donateDonorName.text = globalDonorName
            donateOrgName.text = receiver!!.name
            donateDonorPhoto.setImageDrawable(globalDonorPhoto)

            Base64Image.decode(receiver.photo) {
                it?.let { myBitmap ->
                    donateOrgPhoto.setImageBitmap(myBitmap)
                }
            }

            btnAddItem.setOnClickListener {
                hideSoftKeyboard()

                donateItemsList.add(DonateItemModel(currentFoodItem, donorDonateAmount.editText?.text.toString().toInt()))

                Log.d(TAG, "Size of list is: ${donateItemsList.size}")

                donorDonateAdapter.apply {
                    items = donateItemsList
                    notifyItemInserted(donateItemsList.size)
                }

                binding.donorDonateAmount.apply {
                    editText?.text = null
                    error = null
                }
                containerConstraintLayout.transitionToStart()
//                MotionToast.createColorToast(this@DonorDonateActivity, "Added!", MotionToast.TOAST_SUCCESS, MotionToast.GRAVITY_BOTTOM, MotionToast.SHORT_DURATION, ResourcesCompat.getFont(this@DonorDonateActivity,R.font.alegreya_sans_sc_medium))

            }

            donorDonateAmount.editText?.doOnTextChanged { text, start, before, count ->
                if(text.toString().isEmpty()) {
                    btnAddItem.isEnabled = false
                    donorDonateAmount.error = "Please enter some amount!"
                } else {
                    btnAddItem.isEnabled = true
                    donorDonateAmount.error = null
                }
            }

            donorDonateSubmitBtn.setOnClickListener {

                val sb = StringBuilder()

                for(myString in donateItemsList) {
                    sb.append("$myString, \n")
                }

                globalDonationList = sb.toString()

                if (donateItemsList.size > 0) {
                    it.isEnabled = true
                    globalDonorCollectionRef.document(globalDonorMobile).collection("donations").document(Timestamp.now().toString())
                        .set(
                        DonorDonationModel(to = receiver.name, allItems = globalDonationList)
                    ).addOnSuccessListener {
                        binding.donationCheckAnimation.apply {
                            visibility = View.VISIBLE
                            binding.donationFailedAnimation.visibility = View.GONE
                            playAnimation()
                            addAnimatorListener(object : Animator.AnimatorListener{
                                override fun onAnimationStart(animation: Animator?) {

                                }

                                override fun onAnimationEnd(animation: Animator?) {
                                    MotionToast.createColorToast(this@DonorDonateActivity, "Request Successful!", MotionToast.TOAST_SUCCESS, MotionToast.GRAVITY_BOTTOM, MotionToast.SHORT_DURATION, ResourcesCompat.getFont(this@DonorDonateActivity,R.font.alegreya_sans_sc_medium))
                                    donateItemsList.clear()
                                    globalDonationList = ""
                                    val intent = Intent(this@DonorDonateActivity, DonorHomeActivity::class.java)
                                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                                    startActivity(intent)
                                    finish()
                                }

                                override fun onAnimationCancel(animation: Animator?) {
                                }

                                override fun onAnimationRepeat(animation: Animator?) {
                                }

                            })
                        }
                        }
                        .addOnFailureListener {
                            binding.donationFailedAnimation.apply {
                                visibility = View.VISIBLE
                                binding.donationCheckAnimation.visibility = View.GONE
                                binding.transparentImageViewBackground.visibility = View.VISIBLE
                                playAnimation()
                                addAnimatorListener(object : Animator.AnimatorListener{
                                    override fun onAnimationStart(animation: Animator?) {
                                    }

                                    override fun onAnimationEnd(animation: Animator?) {
                                        MotionToast.createColorToast(this@DonorDonateActivity, "Request Failed!", MotionToast.TOAST_ERROR, MotionToast.GRAVITY_BOTTOM, MotionToast.SHORT_DURATION, ResourcesCompat.getFont(this@DonorDonateActivity,R.font.alegreya_sans_sc_medium))
                                    }

                                    override fun onAnimationCancel(animation: Animator?) {
                                    }

                                    override fun onAnimationRepeat(animation: Animator?) {
                                    }
                                })
                            }
                        }
                } else {
                    it.isEnabled = false
                }
            }
        }

        spFoodItems = binding.spFoodItems
        val customList = globalFoodListItemNonBreads
        (spFoodItems as SmartMaterialSpinner<*>).item = customList
        (spFoodItems as SmartMaterialSpinner<*>).onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                currentFoodItem = customList[position]
//                MotionToast.createColorToast(this@DonorDonateActivity, "Clicked on: $currentFoodItem!", MotionToast.TOAST_INFO, MotionToast.GRAVITY_BOTTOM, MotionToast.SHORT_DURATION, ResourcesCompat.getFont(this@DonorDonateActivity,R.font.alegreya_sans_sc_medium))
                binding.containerConstraintLayout.transitionToEnd()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

        }
    }
}