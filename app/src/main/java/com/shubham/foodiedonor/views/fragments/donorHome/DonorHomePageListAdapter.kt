package com.shubham.foodiedonor.views.fragments.donorHome

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.pixelcarrot.base64image.Base64Image
import com.shubham.foodiedonor.R
import com.shubham.foodiedonor.databinding.DonorHomepageItemBinding
import com.shubham.foodiedonor.models.ReceiverModel

class DonorHomePageListAdapter(options: FirestoreRecyclerOptions<ReceiverModel>) :
    FirestoreRecyclerAdapter<ReceiverModel, DonorHomePageListAdapter.DonorHomePageListVH>(options) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DonorHomePageListVH {
        return DonorHomePageListVH(DonorHomepageItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: DonorHomePageListVH, position: Int, model: ReceiverModel) {

        holder.binding.apply {
            dhiOrgName.text = model.name
            dhiAddress.text = model.address
            dhiMobile.text = model.mobile
            dhiRating.text = model.rating.toString()

            Base64Image.decode(model.photo) { bitmap ->
                bitmap?.let {
                    dhiPhoto.setImageBitmap(it)
                }
            }
        }

    }

    class DonorHomePageListVH(val binding: DonorHomepageItemBinding) : RecyclerView.ViewHolder(binding.root) {

    }

}