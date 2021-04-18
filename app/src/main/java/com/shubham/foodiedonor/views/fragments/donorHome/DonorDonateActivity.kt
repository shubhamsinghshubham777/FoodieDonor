package com.shubham.foodiedonor.views.fragments.donorHome

import android.animation.TimeInterpolator
import android.annotation.SuppressLint
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.viewbinding.library.activity.viewBinding
import android.widget.AdapterView
import android.widget.Toast
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.core.content.res.ResourcesCompat
import androidx.core.widget.doOnTextChanged
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import androidx.transition.Slide
import androidx.transition.Transition
import androidx.transition.TransitionManager
import com.chivorn.smartmaterialspinner.SmartMaterialSpinner
import com.pixelcarrot.base64image.Base64Image
import com.shubham.foodiedonor.R
import com.shubham.foodiedonor.databinding.ActivityDonorDonateBinding
import com.shubham.foodiedonor.models.ReceiverModel
import com.shubham.foodiedonor.utils.Constants.globalDonorName
import com.shubham.foodiedonor.utils.Constants.globalDonorPhoto
import com.shubham.foodiedonor.utils.Constants.globalFoodListItemNonBreads
import com.tuonbondol.keyboardutil.hideSoftKeyboard
import www.sanju.motiontoast.MotionToast

class DonorDonateActivity : AppCompatActivity() {

    private val binding: ActivityDonorDonateBinding by viewBinding()
    private var spFoodItems: SmartMaterialSpinner<*>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setupAllFields()

    }

    @SuppressLint("SetTextI18n")
    private fun setupAllFields() {
        val receiver = intent.getSerializableExtra("receiverInfo") as? ReceiverModel

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
                binding.donorDonateAmount.apply {
                    editText?.text = null
                    error = null
                }
                containerConstraintLayout.transitionToStart()
                MotionToast.createColorToast(this@DonorDonateActivity, "Added!", MotionToast.TOAST_SUCCESS, MotionToast.GRAVITY_BOTTOM, MotionToast.SHORT_DURATION, ResourcesCompat.getFont(this@DonorDonateActivity,R.font.alegreya_sans_sc_medium))

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
                binding.containerConstraintLayout.transitionToEnd()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

        }
    }
}