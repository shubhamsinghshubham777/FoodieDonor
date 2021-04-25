package com.shubham.foodiedonor.views.fragments.donorHome

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.util.Pair
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.core.app.ActivityOptionsCompat
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.request.CachePolicy
import coil.transform.CircleCropTransformation
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

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: DonorHomePageListVH, position: Int, model: ReceiverModel) {

        holder.binding.apply {
            dhiOrgName.text = model.name
            dhiAddress.text = model.address
            dhiMobile.text = "+91${model.mobile}"
            dhiRating.text = String.format("%.1f", model.rating)
            dhiPhoto.load(model.photo) {
                crossfade(400)
                transformations(CircleCropTransformation())
                placeholder(R.drawable.placeholder_image2)
            }

            if(model.mobileVerified) {
                dhiVerifiedIv.visibility = View.VISIBLE
            } else {
                dhiVerifiedIv.visibility = View.INVISIBLE
            }
            //RecyclerView item animation
            holder.binding.root.animation = AnimationUtils.loadAnimation(holder.itemView.context, R.anim.recyclerview_anim1)
        }

        holder.binding.root.setOnClickListener {
            snapshots.getSnapshot(position)
            val intent = Intent(holder.binding.root.context, DonorHomeClickDetailActivity::class.java)
//            val imageViewPair = androidx.core.util.Pair.create<View, String>(holder.binding.dhiPhoto, "customTN")
//            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(holder.binding.root.context as Activity, imageViewPair)
            intent.putExtra("receiver", model)
            holder.binding.root.context.startActivity(intent)
        }

    }

    class DonorHomePageListVH(val binding: DonorHomepageItemBinding) : RecyclerView.ViewHolder(binding.root) {
    }

}