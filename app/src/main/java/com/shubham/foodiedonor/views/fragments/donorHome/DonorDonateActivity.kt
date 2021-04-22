package com.shubham.foodiedonor.views.fragments.donorHome

import android.animation.Animator
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.viewbinding.library.activity.viewBinding
import android.widget.AdapterView
import androidx.core.content.res.ResourcesCompat
import androidx.core.widget.doOnTextChanged
import coil.load
import coil.transform.CircleCropTransformation
import com.chivorn.smartmaterialspinner.SmartMaterialSpinner
import com.pixelcarrot.base64image.Base64Image
import com.shubham.foodiedonor.R
import com.shubham.foodiedonor.databinding.ActivityDonorDonateBinding
import com.shubham.foodiedonor.models.DonateItemModel
import com.shubham.foodiedonor.models.DonorDonationModel
import com.shubham.foodiedonor.models.ReceiverModel
import com.shubham.foodiedonor.utils.Constants
import com.shubham.foodiedonor.utils.Constants.donateItemsList
import com.shubham.foodiedonor.utils.Constants.globalDonationList
import com.shubham.foodiedonor.utils.Constants.globalDonorCollectionRef
import com.shubham.foodiedonor.utils.Constants.globalDonorMobile
import com.shubham.foodiedonor.utils.Constants.globalDonorName
import com.shubham.foodiedonor.utils.Constants.globalFoodListItemNonBreads
import com.shubham.foodiedonor.utils.Constants.globalReceiverCollectionRef
import com.shubham.foodiedonor.views.DonorHomeActivity
import com.tuonbondol.keyboardutil.hideSoftKeyboard
import www.sanju.motiontoast.MotionToast
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*

class DonorDonateActivity : AppCompatActivity() {

    private val binding: ActivityDonorDonateBinding by viewBinding()
    private var spFoodItems: SmartMaterialSpinner<*>? = null
    private var currentFoodItem = ""
    private lateinit var donorDonateAdapter: DonorDonateAdapter
    private val TAG = "DonorDonateActivityTAG"
    private val sdf = SimpleDateFormat("yyyy.MM.dd HH:mm:s")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setupAllFields()
        setupRecyclerView()

        //Clearing the list because by default the list was initialized by a blank value
        globalDonationList = ""

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

            val donorPhoto = getSharedPreferences(Constants.mySharedPrefName, Context.MODE_PRIVATE).getString("globalDonorPhotoUrl", null)
            Log.d(TAG, "DonorPhotoInClickDetail: $donorPhoto")
            if (donorPhoto != null) {
                donateDonorPhoto.load(donorPhoto) {
                    crossfade(400)
                    transformations(CircleCropTransformation())
                    placeholder(R.drawable.placeholder_image2)
                }
            }

//            donateDonorPhoto.setImageDrawable(globalDonorPhoto)

            donateOrgPhoto.load(receiver.photo) {
                crossfade(400)
                transformations(CircleCropTransformation())
                placeholder(R.drawable.placeholder_image2)
            }

//            Base64Image.decode(receiver.photo) {
//                it?.let { myBitmap ->
//                    donateOrgPhoto.setImageBitmap(myBitmap)
//                }
//            }

            btnAddItem.setOnClickListener {
                hideSoftKeyboard()

                donateItemsList.add(DonateItemModel(currentFoodItem, donorDonateAmount.editText?.text.toString().toFloat()))

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

                try {
                    val sb = StringBuilder()

                    for(myString in donateItemsList) {
                        sb.append(myString.itemName+ " - " + myString.itemAmount + "kg, ")
                    }
                    globalDonationList = sb.toString()
                } catch (e: Exception) {
                    Log.d(TAG, "setupAllFields: ${e.localizedMessage}")
                }

                if (donateItemsList.size > 0) {
                    binding.donorDonateSubmitBtn.isEnabled = false
                    val date = Date()
                    val currentTime = sdf.format(date).substring(0, sdf.format(date).length-1)
                    val donorMobile = getSharedPreferences(Constants.mySharedPrefName, Context.MODE_PRIVATE).getString("globalDonorMobile", null)
                    globalDonorCollectionRef.document(donorMobile!!).collection("donations").document(currentTime)
                        .set(
                        DonorDonationModel(to = receiver.name, allItems = globalDonationList)
                    ).addOnSuccessListener {

                            globalReceiverCollectionRef.document(receiver.mobile).collection("donationsReceived").document(currentTime)
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

                                }.addOnFailureListener {

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
                    MotionToast.createColorToast(this@DonorDonateActivity, "Please select some items before submitting!", MotionToast.TOAST_INFO, MotionToast.GRAVITY_BOTTOM, MotionToast.SHORT_DURATION, ResourcesCompat.getFont(this@DonorDonateActivity,R.font.alegreya_sans_sc_medium))
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