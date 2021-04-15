package com.shubham.foodiedonor.views.fragments.donorHome

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.pixelcarrot.base64image.Base64Image
import com.shubham.foodiedonor.R
import com.shubham.foodiedonor.databinding.FragmentDonorMyProfileBinding
import com.shubham.foodiedonor.models.ReceiverModel
import com.shubham.foodiedonor.views.LoginActivity
import dev.shreyaspatil.MaterialDialog.AbstractDialog
import dev.shreyaspatil.MaterialDialog.MaterialDialog
import www.sanju.motiontoast.MotionToast

class DonorMyProfileFragment : Fragment(R.layout.fragment_donor_my_profile) {

    private lateinit var binding: FragmentDonorMyProfileBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDonorMyProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Firebase.firestore.collection("donors")
            .whereEqualTo("email", Firebase.auth.currentUser?.email).get()
            .addOnCompleteListener {
                for (document in it.result.documents) {

                    val donor = document.toObject(ReceiverModel::class.java)

                    val base64Photo = donor?.photo
                    Base64Image.decode(base64Photo) { bitmap ->
                        bitmap?.let { receivedPhoto ->
                            binding.donorLoggedInProfilePhoto.setImageBitmap(receivedPhoto)
                        }
                    }
                }
            }

        binding.donorProfileBtnSignout.setOnClickListener {
            val myDialog = MaterialDialog.Builder(requireActivity())
                .setTitle("Signing Out?")
                .setMessage("Press the YES button to confirm your sign-out request")
                .setPositiveButton("Yes", R.drawable.ic_door) { dialogInterface, _ ->
                    Firebase.auth.signOut()
                    startActivity(Intent(requireActivity(), LoginActivity::class.java))
                    requireActivity().finish()
                    dialogInterface.dismiss()
                }
                .setNegativeButton("No") { dialogInterface, _ ->
                    dialogInterface.dismiss()
                }
                .build()

            myDialog.show()
        }

        binding.donorMyProfileSwipeRefreshLayout.setOnRefreshListener {
            MotionToast.createColorToast(
                requireActivity(), "Page refreshed!.",
                MotionToast.TOAST_SUCCESS,
                MotionToast.GRAVITY_BOTTOM,
                MotionToast.LONG_DURATION,
                ResourcesCompat.getFont(
                    requireActivity(),
                    R.font.helvetica_regular
                )
            )

            binding.donorMyProfileSwipeRefreshLayout.isRefreshing = false
        }
    }

}