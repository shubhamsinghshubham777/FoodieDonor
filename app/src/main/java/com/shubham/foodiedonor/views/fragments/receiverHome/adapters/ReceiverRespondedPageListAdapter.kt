package com.shubham.foodiedonor.views.fragments.receiverHome.adapters

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

class ReceiverRespondedPageListAdapter(options: FirestoreRecyclerOptions<DonorDonationModel>) :
    FirestoreRecyclerAdapter<DonorDonationModel, ReceiverRespondedPageListAdapter.ReceiverRespondedPageListVH>(options) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReceiverRespondedPageListVH {
        return ReceiverRespondedPageListVH(ReceiverDonationsItem2Binding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ReceiverRespondedPageListVH, position: Int, model: DonorDonationModel) {

        holder.binding.apply {
            rdiTimestamp.apply {
                text = "${model.timestamp.substring(0, 10)} @${model.timestamp.substring(10,16)}"
                isSelected = true
            }
            rdiFrom.apply {
                text = model.from
                isSelected = true
            }
            rdiItems.apply {
                text = model.allItems?.substring(0, model.allItems.length-2)
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

            //RecyclerView item animation
            holder.binding.root.animation = AnimationUtils.loadAnimation(holder.itemView.context, R.anim.recyclerview_anim1)

        }

        holder.binding.root.setOnClickListener {
            snapshots.getSnapshot(position)
            val intent = Intent(holder.binding.root.context, ReceiverHomeClickDetailActivity::class.java)
            intent.putExtra("donationReceived", model)
            holder.binding.root.context.startActivity(intent)
        }

    }

    class ReceiverRespondedPageListVH(val binding: ReceiverDonationsItem2Binding) : RecyclerView.ViewHolder(binding.root) {
    }

}