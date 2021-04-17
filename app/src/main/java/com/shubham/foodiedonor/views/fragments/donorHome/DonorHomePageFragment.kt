package com.shubham.foodiedonor.views.fragments.donorHome

import android.annotation.SuppressLint
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
import com.shubham.foodiedonor.R
import com.shubham.foodiedonor.databinding.FragmentDonorHomePageBinding
import com.shubham.foodiedonor.models.DonorModel
import com.shubham.foodiedonor.models.ReceiverModel
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

        binding.donorHomePageSwipeRefreshLayout.setOnRefreshListener {

            requireActivity().supportFragmentManager
                .beginTransaction()
                .detach(this)
                .attach(this)
                .commit()

            binding.donorHomePageSwipeRefreshLayout.isRefreshing = false
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