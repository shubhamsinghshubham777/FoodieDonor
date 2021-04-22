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
        setupAllViews()
    }

    @SuppressLint("SetTextI18n")
    private fun setupAllViews() {
        val currentReceiverMobile = requireActivity().getSharedPreferences(Constants.mySharedPrefName, Context.MODE_PRIVATE).getString("globalReceiverMobile", null)
        Constants.globalReceiverCollectionRef.document(currentReceiverMobile!!).get()
            .addOnSuccessListener {
                val receiver = it.toObject<ReceiverModel>()
                binding.apply {
                    receiverRespondedPageName.text = "Welcome ${receiver?.name}"
                    receiverRespondedPageSignoutBtn.setOnClickListener {

                        val myDialog = MaterialDialog.Builder(requireActivity())
                            .setAnimation(R.raw.logout)
                            .setTitle("Confirm sign-out ${receiver?.name}?")
                            .setMessage("Do you want to sign-out of this account?")
                            .setPositiveButton("Yes", AbstractDialog.OnClickListener { dialogInterface, _ ->
                                Firebase.auth.signOut()
                                requireActivity().getSharedPreferences(Constants.mySharedPrefName, Context.MODE_PRIVATE).edit().clear().apply()
                                val intent = Intent(requireActivity(), LoginActivity::class.java)
                                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                                startActivity(intent)
                                dialogInterface.dismiss()
                            })
                            .setNegativeButton("No", AbstractDialog.OnClickListener { dialogInterface, _ ->
                                dialogInterface.dismiss()
                            }).build()

                        myDialog.show()
                    }
                }
            }
            .addOnFailureListener {
                MotionToast.createColorToast(requireActivity(), "Unexpected error occurred. Please try again later.", MotionToast.TOAST_ERROR, MotionToast.GRAVITY_BOTTOM, MotionToast.LONG_DURATION, ResourcesCompat.getFont(requireActivity(), R.font.alegreya_sans_sc_medium))
                Log.d(TAG, "Error cause: ${it.localizedMessage}")
            }
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