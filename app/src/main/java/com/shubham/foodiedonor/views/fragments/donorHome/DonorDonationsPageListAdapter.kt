package com.shubham.foodiedonor.views.fragments.donorHome

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.util.Pair
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityOptionsCompat
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.pixelcarrot.base64image.Base64Image
import com.shubham.foodiedonor.R
import com.shubham.foodiedonor.databinding.DonorHomepageDonationsItemBinding
import com.shubham.foodiedonor.databinding.DonorHomepageItemBinding
import com.shubham.foodiedonor.models.DonorDonationModel
import com.shubham.foodiedonor.models.ReceiverModel

class DonorDonationsPageListAdapter(options: FirestoreRecyclerOptions<DonorDonationModel>) :
    FirestoreRecyclerAdapter<DonorDonationModel, DonorDonationsPageListAdapter.DonorHomePageListVH>(options) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DonorHomePageListVH {
        return DonorHomePageListVH(DonorHomepageDonationsItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: DonorHomePageListVH, position: Int, model: DonorDonationModel) {

        holder.binding.apply {
            dhdiTimeStamp.text = model.timestamp
            dhdiTo.text = model.to
            dhdiItems.apply {
                isSelected = true
                text = model.allItems
            }

            when (model.verifiedStatus) {
                "Accepted!" -> {
                    dhdiDonationAcceptedTv.apply {
                        text = "Yes!"
                        setTextColor(resources.getColor(R.color.green))
                    }
                }
                "Pending" -> {
                    dhdiDonationAcceptedTv.apply {
                        text = "Pending..."
                        setTextColor(resources.getColor(R.color.black))
                    }
                }
                else -> {
                    dhdiDonationAcceptedTv.apply {
                        text = "No!"
                        setTextColor(resources.getColor(R.color.accentColor))
                    }
                }
            }

        }

//        holder.binding.root.setOnClickListener {
//            snapshots.getSnapshot(position)
//            val intent = Intent(holder.binding.root.context, DonorHomeClickDetailActivity::class.java)
//            intent.putExtra("receiver", model)
//            holder.binding.root.context.startActivity(intent)
//        }

    }

    class DonorHomePageListVH(val binding: DonorHomepageDonationsItemBinding) : RecyclerView.ViewHolder(binding.root) {
    }

}