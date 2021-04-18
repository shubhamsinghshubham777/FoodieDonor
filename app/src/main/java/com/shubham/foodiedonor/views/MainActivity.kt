package com.shubham.foodiedonor.views

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.viewbinding.library.activity.viewBinding
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.res.ResourcesCompat
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.shubham.foodiedonor.R
import com.shubham.foodiedonor.databinding.ActivityMainBinding
import com.shubham.foodiedonor.utils.Constants.globalFoodDocumentRef
import com.shubham.foodiedonor.utils.Constants.globalFoodListItemNonBreads
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import www.sanju.motiontoast.MotionToast

class MainActivity : AppCompatActivity() {

//    private val binding: ActivityMainBinding by viewBinding()
    private lateinit var binding: ActivityMainBinding
    private val TAG = "MainActivityTAG"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_FoodieDonor)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        saveFoodItemsToGlobalConstant()



//        Log.d(TAG, "onCreate: ${Firebase.auth.currentUser?.email}")
//        Log.d(TAG, "onCreate: ${Firebase.auth.currentUser?.uid}")
//        Log.d(TAG, "onCreate: ${Firebase.auth.currentUser?.isEmailVerified}")


        if (Firebase.auth.currentUser != null) {

            val donorCollectionRef = Firebase.firestore.collection("donors")
                .whereEqualTo("email", Firebase.auth.currentUser?.email)
            donorCollectionRef.get()
                .addOnCompleteListener {
                    if (it.result.isEmpty) {
                        Log.d(TAG, "onCreate: Donor does not exist!")
                        val receiverCollectionRef = Firebase.firestore.collection("receivers")
                        receiverCollectionRef.get()
                            .addOnCompleteListener { task ->
                                if (task.result.isEmpty) {
                                    Log.d(TAG, "onCreate: Receiver does not exist!")
                                } else {
                                    for (document in task.result.documents) {
                                        val type = document.getString("type")
                                        Log.d(TAG, "onCreateType: $type")
                                        if (type == "receiver") {
                                            val intent =
                                                Intent(this, ReceiverHomeActivity::class.java)
                                            intent.flags =
                                                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                            startActivity(intent)
                                            return@addOnCompleteListener
                                        }
                                    }
                                }
                            }
                    } else {

                        for (document in it.result.documents) {
                            val type = document.getString("type")
                            Log.d(TAG, "onCreateType: $type")
                            if (type == "donor") {
                                val intent = Intent(this, DonorHomeActivity::class.java)
                                intent.flags =
                                    Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                startActivity(intent)
                                return@addOnCompleteListener
                            }
                        }
                    }
                }
        } else {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

    }

    private fun saveFoodItemsToGlobalConstant() {

        globalFoodDocumentRef.get().addOnSuccessListener {
            globalFoodListItemNonBreads = it.get("non-breads") as ArrayList<String>
            Log.d(TAG, "saveFoodItemsToGlobalConstant: $globalFoodListItemNonBreads")
        }
            .addOnFailureListener {
                    MotionToast.createColorToast(this, "Some internal issue! Please contact your administrator", MotionToast.TOAST_ERROR, MotionToast.GRAVITY_BOTTOM, MotionToast.LONG_DURATION, ResourcesCompat.getFont(this,R.font.alegreya_sans_sc_medium))
                Log.d(TAG, "onErrorResponse: ${it.message}")
            }

    }


}