package com.shubham.foodiedonor.views.fragments.donorHome

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.pixelcarrot.base64image.Base64Image
import com.shubham.foodiedonor.R
import com.shubham.foodiedonor.databinding.FragmentDonorHomePageBinding
import com.shubham.foodiedonor.models.DonorModel
import com.shubham.foodiedonor.models.ReceiverModel
import com.shubham.foodiedonor.utils.Constants.globalDonorAddress
import com.shubham.foodiedonor.utils.Constants.globalDonorEmail
import com.shubham.foodiedonor.utils.Constants.globalDonorLatitude
import com.shubham.foodiedonor.utils.Constants.globalDonorLongitude
import com.shubham.foodiedonor.utils.Constants.globalDonorMobile
import com.shubham.foodiedonor.utils.Constants.globalDonorName
import com.shubham.foodiedonor.utils.Constants.globalDonorPhoto
import com.shubham.foodiedonor.utils.Constants.mySharedPrefName
import com.shubham.foodiedonor.views.LoginActivity
import dev.shreyaspatil.MaterialDialog.AbstractDialog
import dev.shreyaspatil.MaterialDialog.MaterialDialog
import www.sanju.motiontoast.MotionToast

class DonorHomePageFragment : Fragment(R.layout.fragment_donor_home_page) {

    private lateinit var binding: FragmentDonorHomePageBinding
    private val receiverCollectionRef = Firebase.firestore.collection("receivers")
    private lateinit var donorHomePageListAdapter: DonorHomePageListAdapter
    private val TAG = "DonorHomePageFragmentTAG"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDonorHomePageBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getCurrentUserName()

        setupRecyclerView()

        setupAllViews()

//        binding.donorHomePageSwipeRefreshLayout.setOnRefreshListener {
//
//            requireActivity().supportFragmentManager
//                .beginTransaction()
//                .detach(this)
//                .attach(this)
//                .commit()
//
//            binding.donorHomePageSwipeRefreshLayout.isRefreshing = false
//        }
    }

    private fun setupAllViews() {

        val myDialog = MaterialDialog.Builder(requireActivity())
            .setTitle("Confirm Sign-Out!")
            .setMessage("Do you want to sign-out of the app?")
            .setPositiveButton("Yes", R.drawable.ic_door, AbstractDialog.OnClickListener { dialogInterface, _ ->
                Firebase.auth.signOut()
                requireActivity().getSharedPreferences(mySharedPrefName, Context.MODE_PRIVATE).edit().clear().commit()
                startActivity(Intent(requireActivity(), LoginActivity::class.java))
                requireActivity().finish()
            })
            .setNegativeButton("No", AbstractDialog.OnClickListener { dialogInterface, _ ->
                dialogInterface.dismiss()
            })
            .build()

        binding.apply {
            donorHomePageSignoutBtn.setOnClickListener {
                myDialog.show()
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun getCurrentUserName() {
        Firebase.firestore.collection("donors").whereEqualTo("email", Firebase.auth.currentUser?.email)
            .get()
            .addOnSuccessListener {
                for(document in it.documents) {
                    val currentPerson = document.toObject<DonorModel>()
                    binding.donorHomePageName.text = "Welcome ${currentPerson?.name.toString()}"
                    globalDonorName = currentPerson?.name.toString()
                    globalDonorEmail = currentPerson?.email.toString()
                    globalDonorAddress = currentPerson?.address.toString()
                    globalDonorMobile = currentPerson?.mobile.toString()
                    Base64Image.decode(currentPerson?.photo) { bitmap ->
                        bitmap?.let { myBitmap ->
                            val myDrawable = BitmapDrawable(resources, myBitmap)
                            globalDonorPhoto = myDrawable
                        }
                    }
                    globalDonorLatitude = currentPerson!!.latitude
                    globalDonorLongitude = currentPerson.longitude

                }
            }
    }

    private fun setupRecyclerView() {
        val query = receiverCollectionRef.orderBy("rating", Query.Direction.DESCENDING)
        val options = FirestoreRecyclerOptions.Builder<ReceiverModel>()
            .setQuery(query, ReceiverModel::class.java)
            .build()
        donorHomePageListAdapter = DonorHomePageListAdapter(options)

        binding.donorHomePageRecyclerView.apply {
            setHasFixedSize(true)
            adapter = donorHomePageListAdapter
        }
    }

    override fun onStart() {
        super.onStart()
        donorHomePageListAdapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        donorHomePageListAdapter.stopListening()
    }

}