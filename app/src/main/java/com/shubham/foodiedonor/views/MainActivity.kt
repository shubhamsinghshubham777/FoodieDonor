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
import com.shubham.foodiedonor.models.DonorModel
import com.shubham.foodiedonor.models.ReceiverModel
import com.shubham.foodiedonor.views.receiver.ReceiverHomeActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import www.sanju.motiontoast.MotionToast
import java.lang.Exception
import java.lang.StringBuilder

class MainActivity : AppCompatActivity() {

    private val binding: ActivityMainBinding by viewBinding()
    private val TAG = "MainActivityTAG"
    private var personType1: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        Log.d(TAG, "onCreate: ${Firebase.auth.currentUser?.email}")
        Log.d(TAG, "onCreate: ${Firebase.auth.currentUser?.uid}")
        Log.d(TAG, "onCreate: ${Firebase.auth.currentUser?.isEmailVerified}")


        if (Firebase.auth.currentUser != null) {

            val donorCollectionRef = Firebase.firestore.collection("users")
                .document("allusers").collection("donors")
                .whereEqualTo("email", Firebase.auth.currentUser?.email)
            donorCollectionRef.get()
                .addOnCompleteListener {
                    if (it.result.isEmpty) {
                        Log.d(TAG, "onCreate: Donor does not exist!")
                        val receiverCollectionRef = Firebase.firestore.collection("users")
                            .document("allusers").collection("receivers")
                        receiverCollectionRef.get()
                            .addOnCompleteListener { task->
                                if (task.result.isEmpty) {
                                    Log.d(TAG, "onCreate: Receiver does not exist!")
                                } else {
                                    for (document in task.result.documents) {
                                        val type = document.getString("type")
                                        Log.d(TAG, "onCreateType: $type")
                                        if (type == "receiver") {
                                            startActivity(Intent(this, ReceiverHomeActivity::class.java))
                                        }
                                    }
                                }
                            }
                    } else {

                        for (document in it.result.documents) {
                            val type = document.getString("type")
                            Log.d(TAG, "onCreateType: $type")
                            if (type == "donor") {
                                startActivity(Intent(this, HomeActivity::class.java))
                            }
                        }
                    }
                }

//            CoroutineScope(Dispatchers.IO).launch {
//                val querySnapshot = Firebase.firestore
//                    .collection("donors")
//                    .whereEqualTo("email", Firebase.auth.currentUser?.email)
//                    .get()
//                    .await()
//
//                for (document in querySnapshot.documents) {
//                    val personType = document.getString("type")
//                    personType1 = personType!!
//                    Log.d(TAG, "Person Type: $personType1")
//
//                    if (personType == "donor") {
//                        startActivity(Intent(this@MainActivity, HomeActivity::class.java))
//                        finish()
//                    } else {
//
//                        CoroutineScope(Dispatchers.IO).launch {
//                            val querySnapshot = Firebase.firestore
//                                .collection("receivers")
//                                .whereEqualTo("email", Firebase.auth.currentUser?.email)
//                                .get()
//                                .await()
//
//                            for (document in querySnapshot.documents) {
//                                val personType = document.getString("type")
//                                personType1 = personType!!
//                                Log.d(TAG, "Person Type: $personType1")
//
//                                if (personType == "receiver") {
//                                    startActivity(Intent(this@MainActivity, ReceiverHomeActivity::class.java))
//                                    finish()
//                                } else {
//                                    MotionToast.createColorToast(
//                                        this@MainActivity, "Please login again!",
//                                        MotionToast.TOAST_WARNING,
//                                        MotionToast.GRAVITY_BOTTOM,
//                                        MotionToast.LONG_DURATION,
//                                        ResourcesCompat.getFont(
//                                            this@MainActivity,
//                                            R.font.helvetica_regular
//                                        )
//                                    )
//                                    startActivity(Intent(this@MainActivity, LoginActivity::class.java))
//                                    finish()
//                                }
//                            }
//
//                        }
//
//                    }
//                }
//
//            }
//
//        } else {
//            startActivity(Intent(this, LoginActivity::class.java))
//            finish()
//        }
//    }
        } else {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

    }


}