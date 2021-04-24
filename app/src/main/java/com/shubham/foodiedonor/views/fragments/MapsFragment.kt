package com.shubham.foodiedonor.views.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.location.Geocoder
import androidx.fragment.app.Fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.viewbinding.library.fragment.viewBinding
import androidx.core.content.res.ResourcesCompat
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.fondesa.kpermissions.allGranted
import com.fondesa.kpermissions.extension.permissionsBuilder
import com.fondesa.kpermissions.extension.send
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.shubham.foodiedonor.R
import com.shubham.foodiedonor.databinding.FragmentMapsBinding
import com.shubham.foodiedonor.models.ReceiverModel
import www.sanju.motiontoast.MotionToast

class MapsFragment : Fragment(), OnMapReadyCallback {

    private lateinit var geocoder: Geocoder
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private val TAG = "MapsFragmentTAG"
    private var latitude: Double = 0.0
    private var longitude: Double = 0.0
    private lateinit var mMap: GoogleMap
    private var address: String = String()
    private val binding: FragmentMapsBinding by viewBinding()
    private val args: MapsFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_maps, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        geocoder = Geocoder(requireActivity())

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        Log.d(TAG, "latitude is: $latitude \n & longitude is: $longitude")
        Log.d(TAG, "Previous fragment was: ${args.stackId}")
    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap?) {

        permissionsBuilder(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION).build().send() {
            if (it.allGranted()) {
                mMap = googleMap!!

                mMap.isMyLocationEnabled = true



                val locationTask = fusedLocationProviderClient.lastLocation
                locationTask.addOnSuccessListener { currentLocation ->
                    latitude = currentLocation.latitude
                    longitude = currentLocation.longitude

                    val myCurrentLocation = LatLng(latitude, longitude)

                    mMap.setOnMapClickListener {
                        mMap.apply {
                            clear()
                            addMarker(MarkerOptions()
                                .position(it)
                            )
                            try {
                                val listOfAddress = geocoder.getFromLocation(it.latitude, it.longitude, 1)
                                val currentAddress = listOfAddress[0]
                                address = currentAddress.getAddressLine(0)
                                Log.d(TAG, "Address is: $address")
                            } catch (e: Exception) {
                                Log.d(TAG, "newmarker addressError: ${e.localizedMessage}")
                            }

                            binding.selectBtn.setOnClickListener {
                                Log.d(TAG, "address on button: $address")
                                if (args.stackId == 2131362368) {
                                    val action = MapsFragmentDirections.actionMapsFragmentToReceiverSignupFragment(
                                        args.name,
                                        args.email,
                                        args.mobile,
                                        args.mobileVerified,
                                        args.password,
                                        args.repeatPassword,
                                        address,
                                        args.cinNumber,
                                        latitude.toFloat(),
                                        longitude.toFloat()
                                    )
                                    findNavController().navigate(action)
                                } else if(args.stackId == 2131362066) {
                                    val action = MapsFragmentDirections.actionMapsFragmentToDonorSignupFragment(
                                        args.name,
                                        args.email,
                                        args.mobile,
                                        args.mobileVerified,
                                        args.password,
                                        args.repeatPassword,
                                        address,
                                        latitude.toFloat(),
                                        longitude.toFloat()
                                    )
                                    findNavController().navigate(action)
                                }

                            }
                        }
                    }

                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myCurrentLocation, 16f))

                    try {
                        val listOfAddress = geocoder.getFromLocation(latitude, longitude, 1)
                        val currentAddress = listOfAddress[0]
                        address = currentAddress.getAddressLine(0)
                        Log.d(TAG, "Address is: $address")
                        binding.selectBtn.setOnClickListener {
                            Log.d(TAG, "address on button: $address")
                            val action = MapsFragmentDirections.actionMapsFragmentToReceiverSignupFragment(
                                args.name,
                                args.email,
                                args.mobile,
                                args.mobileVerified,
                                args.password,
                                args.repeatPassword,
                                address,
                                args.cinNumber,
                                latitude.toFloat(),
                                longitude.toFloat()
                            )
                            findNavController().navigate(action)
                        }

                    } catch (e: Exception) {
                        Log.d(TAG, "on address error: ${e.localizedMessage}")
                    }

                }
                locationTask.addOnFailureListener { exception ->
                    Log.d(TAG, "onStart location retrieval failed! Cause: ${exception.localizedMessage}")
                }

            } else {
                MotionToast.createColorToast(requireActivity(), "Please accept the location permission!", MotionToast.TOAST_WARNING, MotionToast.GRAVITY_BOTTOM, MotionToast.LONG_DURATION, ResourcesCompat.getFont(requireActivity(),R.font.alegreya_sans_sc_medium))
            }
        }

    }
}