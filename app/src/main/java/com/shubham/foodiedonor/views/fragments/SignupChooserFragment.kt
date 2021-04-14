package com.shubham.foodiedonor.views.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.viewbinding.library.fragment.viewBinding
import androidx.navigation.fragment.findNavController
import com.shubham.foodiedonor.R
import com.shubham.foodiedonor.databinding.FragmentSignupChooserBinding

class SignupChooserFragment : Fragment(R.layout.fragment_signup_chooser) {

    private val binding : FragmentSignupChooserBinding by viewBinding()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.donorIv.setOnClickListener {
            val action = SignupChooserFragmentDirections.actionSignupChooserFragmentToDonorSignupFragment()
            findNavController().navigate(action)
        }

        binding.receiverIv.setOnClickListener {
            val action = SignupChooserFragmentDirections.actionSignupChooserFragmentToReceiverSignupFragment()
            findNavController().navigate(action)
        }
    }

}