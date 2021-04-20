package com.shubham.foodiedonor.views.fragments.receiverHome

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.viewbinding.library.fragment.viewBinding
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.Query
import com.shubham.foodiedonor.R
import com.shubham.foodiedonor.databinding.FragmentReceiverDonationsBinding
import com.shubham.foodiedonor.models.DonorDonationModel
import com.shubham.foodiedonor.utils.Constants
import com.shubham.foodiedonor.views.fragments.donorHome.DonorDonationsPageListAdapter
import com.shubham.foodiedonor.views.fragments.receiverHome.adapters.ReceiverDonationsPageListAdapter

class ReceiverDonationsFragment : Fragment(R.layout.fragment_receiver_donations) {

    private val binding: FragmentReceiverDonationsBinding by viewBinding()
    private lateinit var receiverDonationsPageListAdapter: ReceiverDonationsPageListAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
    }

    private fun setupRecyclerView() {

        val sharedPreferences = requireActivity().getSharedPreferences(Constants.mySharedPrefName, Context.MODE_PRIVATE)
        val currentReceiverMobileNumber = sharedPreferences.getString("globalReceiverMobile", null)
        val query = Constants.globalReceiverCollectionRef.document(currentReceiverMobileNumber!!).collection("donationsReceived").orderBy("timestamp", Query.Direction.DESCENDING)
        val options = FirestoreRecyclerOptions.Builder<DonorDonationModel>()
            .setQuery(query, DonorDonationModel::class.java)
            .build()
        receiverDonationsPageListAdapter = ReceiverDonationsPageListAdapter(options)

        binding.receiverHomeRecyclerView.apply {
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