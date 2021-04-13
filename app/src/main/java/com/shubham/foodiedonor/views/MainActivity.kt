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
import com.shubham.foodiedonor.views.receiver.ReceiverHomeActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import www.sanju.motiontoast.MotionToast

class MainActivity : AppCompatActivity() {

    private val binding: ActivityMainBinding by viewBinding()
    private val TAG = "MainActivityTAG"
    private var personType1: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        Log.d(TAG, "onCreate: ${Firebase.auth.currentUser?.email}")

        if(Firebase.auth.currentUser != null) {

            CoroutineScope(Dispatchers.IO).launch {
                val querySnapshot = Firebase.firestore.collection("donors")
                    .whereEqualTo("email", Firebase.auth.currentUser?.email)
                    .get()
                    .await()

                for(document in querySnapshot.documents) {
                    val personType = document.getString("type")
                    personType1 = personType!!
                    Log.d(TAG, "Person Type: $personType1")

                    if(personType == "donor") {
                        startActivity(Intent(this@MainActivity, HomeActivity::class.java))
                        finish()
                    } else {
                        startActivity(Intent(this@MainActivity, ReceiverHomeActivity::class.java))
                        finish()
                    }
                }

            }

        } else {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
}