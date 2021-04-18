package com.shubham.foodiedonor.views.fragments.donorHome

import android.app.Activity
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.shubham.foodiedonor.R
import com.shubham.foodiedonor.databinding.DonorDonateItemBinding
import com.shubham.foodiedonor.models.DonateItemModel
import com.shubham.foodiedonor.utils.Constants.donateItemsList
import dev.shreyaspatil.MaterialDialog.AbstractDialog
import dev.shreyaspatil.MaterialDialog.MaterialDialog

class DonorDonateAdapter : RecyclerView.Adapter<DonorDonateAdapter.DonorDonateViewHolder>() {
    private val TAG = "DonorDonateAdapterTAG"

    inner class DonorDonateViewHolder(val binding: DonorDonateItemBinding) : RecyclerView.ViewHolder(binding.root)

    private val diffCallback = object : DiffUtil.ItemCallback<DonateItemModel>() {
        override fun areItemsTheSame(oldItem: DonateItemModel, newItem: DonateItemModel): Boolean {
            return oldItem.itemName == newItem.itemName
        }

        override fun areContentsTheSame(oldItem: DonateItemModel, newItem: DonateItemModel): Boolean {
            return oldItem == newItem
        }
    }

    private val differ = AsyncListDiffer(this, diffCallback)
    var items: List<DonateItemModel>
        get() = differ.currentList
        set(value) { differ.submitList(value) }

    override fun getItemCount() = items.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DonorDonateViewHolder {
        return DonorDonateViewHolder(DonorDonateItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        ))
    }

    override fun onBindViewHolder(holder: DonorDonateViewHolder, position: Int) {
        holder.binding.apply {
            val donateItem = items[position]
//            val toShowCount = position+1
//            ddiCount.text = toShowCount.toString()
            ddiAmount.text = donateItem.itemAmount.toString()
            ddiName.text = donateItem.itemName
            ddiPlus.setOnClickListener {
                var currentAmount = ddiAmount.text.toString().toInt()
                ++currentAmount
                ddiAmount.text = currentAmount.toString()
            }
            ddiMinus.setOnClickListener {
                var currentAmount = ddiAmount.text.toString().toInt()

                if(currentAmount == 1) {
                    val myDialog = MaterialDialog.Builder(holder.binding.root.context as Activity)
                        .setPositiveButton("Delete", R.drawable.ic_delete, AbstractDialog.OnClickListener { dialogInterface, _ ->
                            donateItemsList.remove(donateItem)
//                            notifyItemRemoved(position)
                            notifyDataSetChanged()
                            Log.d(TAG, "Size of list is: ${donateItemsList.size}")
                            Log.d(TAG, "Size of list position is: $position")
                            --currentAmount
                            dialogInterface.dismiss()
                        })
                        .setNegativeButton("Cancel", AbstractDialog.OnClickListener { dialogInterface, _ ->
                            dialogInterface.dismiss()
                        })
                        .setTitle("Confirm Delete?")
                        .setMessage("Do you want to delete this item from the list? \n ${ddiName.text}")
                        .build()

                    myDialog.show()
                } else {
                    --currentAmount
                    ddiAmount.text = currentAmount.toString()
                }

            }
            ddiDelete.setOnClickListener {

                val myDialog = MaterialDialog.Builder(holder.binding.root.context as Activity)
                    .setPositiveButton(
                        "Delete",
                        R.drawable.ic_delete,
                        AbstractDialog.OnClickListener { dialogInterface, _ ->
                            donateItemsList.remove(donateItem)
//                            notifyItemRemoved(position)
                            notifyDataSetChanged()
                            dialogInterface.dismiss()
                            Log.d(TAG, "Size of list is: ${donateItemsList.size}")
                            Log.d(TAG, "Size of list position is: $position")
                        })
                    .setNegativeButton("Cancel", AbstractDialog.OnClickListener { dialogInterface, _ ->
                        dialogInterface.dismiss()
                    })
                    .setTitle("Confirm Delete?")
                    .setMessage("Do you want to delete this item from the list? \n ${ddiName.text}")
                    .build()

                myDialog.show()
            }
        }
    }
}