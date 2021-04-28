package com.shubham.foodiedonor.views.fragments.receiverHome

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import android.viewbinding.library.fragment.viewBinding
import androidx.core.content.res.ResourcesCompat
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.shubham.foodiedonor.R
import com.shubham.foodiedonor.databinding.FragmentReceiverDonationsBinding
import com.shubham.foodiedonor.databinding.FragmentReceiverRespondedBinding
import com.shubham.foodiedonor.models.DonorDonationModel
import com.shubham.foodiedonor.models.ReceiverModel
import com.shubham.foodiedonor.utils.Constants
import com.shubham.foodiedonor.views.LoginActivity
import com.shubham.foodiedonor.views.fragments.receiverHome.adapters.ReceiverDonationsPageListAdapter
import com.shubham.foodiedonor.views.fragments.receiverHome.adapters.ReceiverRespondedPageListAdapter
import dev.shreyaspatil.MaterialDialog.AbstractDialog
import dev.shreyaspatil.MaterialDialog.MaterialDialog
import www.sanju.motiontoast.MotionToast

class ReceiverRespondedFragment : Fragment(R.layout.fragment_receiver_responded) {

    private val binding: FragmentReceiverRespondedBinding by viewBinding()
    private lateinit var receiverDonationsPageListAdapter: ReceiverRespondedPageListAdapter
    private val TAG = "ReceiverRespondedFragmentTAG"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
    }

    private fun setupRecyclerView() {

        val sharedPreferences = requireActivity().getSharedPreferences(Constants.mySharedPrefName, Context.MODE_PRIVATE)
        val filterList = mutableListOf<String>("Accepted!", "Rejected!")
        val currentReceiverMobileNumber = sharedPreferences.getString("globalReceiverMobile", null)
        val query = Constants.globalReceiverCollectionRef.document(currentReceiverMobileNumber!!).collection("donationsReceived")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .whereIn("verifiedStatus", filterList)
        val options = FirestoreRecyclerOptions.Builder<DonorDonationModel>()
            .setQuery(query, DonorDonationModel::class.java)
            .build()
        receiverDonationsPageListAdapter = ReceiverRespondedPageListAdapter(options)

        binding.receiverRespondedRecyclerView.apply {
            setHasFixedSize(true)
            adapter = receiverDonationsPageListAdapter
        }
    }

    override fun onStart() {
        super.onStart()
        receiverDonationsPageListAdapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        receiverDonationsPageListAdapter.stopListening()
    }

}