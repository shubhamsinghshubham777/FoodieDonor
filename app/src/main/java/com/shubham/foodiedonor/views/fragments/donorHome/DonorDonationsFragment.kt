package com.shubham.foodiedonor.views.fragments.donorHome

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.Query
import com.shubham.foodiedonor.R
import com.shubham.foodiedonor.databinding.FragmentDonorDonationsBinding
import com.shubham.foodiedonor.models.DonorDonationModel
import com.shubham.foodiedonor.utils.Constants.globalDonorCollectionRef
import com.shubham.foodiedonor.utils.Constants.mySharedPrefName

class DonorDonationsFragment : Fragment(R.layout.fragment_donor_donations) {

    private lateinit var binding: FragmentDonorDonationsBinding
    private lateinit var donorDonationsPageListAdapter: DonorDonationsPageListAdapter
    private val TAG = "DonorDonationsFragmentTAG"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDonorDonationsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()

//        binding.donorNearbyReceiversSwipeRefreshLayout.setOnRefreshListener {
//
//            parentFragmentManager.beginTransaction().detach(this).attach(this).commit()
//
//            binding.donorNearbyReceiversSwipeRefreshLayout.isRefreshing = false
//        }
    }

    private fun setupRecyclerView() {
        val sharedPreferences = requireActivity().getSharedPreferences(mySharedPrefName, Context.MODE_PRIVATE)
        val currentDonorMobileNumber = sharedPreferences.getString("globalDonorMobile", null)
        val query = globalDonorCollectionRef.document(currentDonorMobileNumber!!).collection("donations").orderBy("timestamp", Query.Direction.DESCENDING)
        val options = FirestoreRecyclerOptions.Builder<DonorDonationModel>()
            .setQuery(query, DonorDonationModel::class.java)
            .build()
        donorDonationsPageListAdapter = DonorDonationsPageListAdapter(options, requireActivity().supportFragmentManager)

        binding.donorDonationsPageRv.apply {
            setHasFixedSize(true)
            adapter = donorDonationsPageListAdapter
        }
    }

    override fun onStart() {
        super.onStart()
        donorDonationsPageListAdapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        donorDonationsPageListAdapter.stopListening()
    }

}