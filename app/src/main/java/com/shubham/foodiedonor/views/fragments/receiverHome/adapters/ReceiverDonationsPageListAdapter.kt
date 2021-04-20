package com.shubham.foodiedonor.views.fragments.receiverHome.adapters

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
import com.shubham.foodiedonor.databinding.ReceiverDonationsItem2Binding
import com.shubham.foodiedonor.models.DonorDonationModel
import com.shubham.foodiedonor.models.ReceiverModel
import com.shubham.foodiedonor.views.fragments.receiverHome.ReceiverHomeClickDetailActivity

class ReceiverDonationsPageListAdapter(options: FirestoreRecyclerOptions<DonorDonationModel>) :
    FirestoreRecyclerAdapter<DonorDonationModel, ReceiverDonationsPageListAdapter.ReceiverHomePageListVH>(options) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReceiverHomePageListVH {
        return ReceiverHomePageListVH(ReceiverDonationsItem2Binding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ReceiverHomePageListVH, position: Int, model: DonorDonationModel) {

        holder.binding.apply {
            rdiTimestamp.apply {
                text = model.timestamp!!.toDate().toString()
                isSelected = true
            }
            rdiFrom.apply {
                text = model.from
                isSelected = true
            }
            rdiItems.apply {
                text = model.allItems
                isSelected = true
            }

            when (model.verifiedStatus) {
                "Accepted!" -> {
                    rdiDonationAccepted.apply {
                        text = "Yes!"
                        setTextColor(resources.getColor(R.color.green))
                    }
                }
                "Pending" -> {
                    rdiDonationAccepted.apply {
                        text = "Pending..."
                        setTextColor(resources.getColor(R.color.black))
                    }
                }
                else -> {
                    rdiDonationAccepted.apply {
                        text = "No!"
                        setTextColor(resources.getColor(R.color.accentColor))
                    }
                }
            }

        }

        holder.binding.root.setOnClickListener {
            snapshots.getSnapshot(position)
            val intent = Intent(holder.binding.root.context, ReceiverHomeClickDetailActivity::class.java)
            intent.putExtra("donationReceived", model)
            holder.binding.root.context.startActivity(intent)
        }

    }

    class ReceiverHomePageListVH(val binding: ReceiverDonationsItem2Binding) : RecyclerView.ViewHolder(binding.root) {
    }

}