package com.shubham.foodiedonor.views.fragments.donorHome

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.shubham.foodiedonor.R
import com.shubham.foodiedonor.databinding.DonorHomepageDonationsItemBinding
import com.shubham.foodiedonor.models.DonorDonationModel
import com.shubham.foodiedonor.views.fragments.CustomDialogFragment

class DonorDonationsPageListAdapter(
    options: FirestoreRecyclerOptions<DonorDonationModel>,
    val supportFragmentManager: FragmentManager
) :
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

            dhdiRateBtn.setOnClickListener {
                val dialog = CustomDialogFragment(model.toEmail!!, model.to!!, model.toMobile!!, model.from!!)
                dialog.show(supportFragmentManager, "customDialog")
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