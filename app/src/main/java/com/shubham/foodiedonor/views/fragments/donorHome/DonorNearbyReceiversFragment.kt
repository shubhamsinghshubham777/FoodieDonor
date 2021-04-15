package com.shubham.foodiedonor.views.fragments.donorHome

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import com.shubham.foodiedonor.R
import com.shubham.foodiedonor.databinding.FragmentDonorNearbyReceiversBinding
import www.sanju.motiontoast.MotionToast

class DonorNearbyReceiversFragment : Fragment(R.layout.fragment_donor_nearby_receivers) {

    private lateinit var binding: FragmentDonorNearbyReceiversBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDonorNearbyReceiversBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.donorNearbyReceiversSwipeRefreshLayout.setOnRefreshListener {

            parentFragmentManager.beginTransaction().detach(this).attach(this).commit()

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

            binding.donorNearbyReceiversSwipeRefreshLayout.isRefreshing = false
        }
    }

}