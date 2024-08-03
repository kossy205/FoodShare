package com.kosiso.foodshare.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.kosiso.foodshare.databinding.ItemFoodRequestsBinding
import com.kosiso.foodshare.models.FoodRequest
import java.text.SimpleDateFormat
import java.util.Locale

class FoodRequestsAdapter: RecyclerView.Adapter<FoodRequestsAdapter.FoodRequestViewHolder>() {

    inner class FoodRequestViewHolder(val binding: ItemFoodRequestsBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodRequestViewHolder {
        val binding = ItemFoodRequestsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FoodRequestViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: FoodRequestViewHolder, position: Int) {
        val foodRequest = differ.currentList[position]

        holder.binding.apply {
            itemFoodName.text = foodRequest.nameOfItem
            itemFoodDesc.text = foodRequest.itemDescription
            itemFoodWeight.text = "${foodRequest.foodWeight}kg"
            itemFoodStatus.text = foodRequest.status

            val dateFormat = SimpleDateFormat("dd.mm.yyyy", Locale.getDefault())
            itemFoodPostDate.text = dateFormat.format(foodRequest.requestTime.toDate())

        }
    }


    val differCallback = object : DiffUtil.ItemCallback<FoodRequest>(){
        override fun areItemsTheSame(oldItem: FoodRequest, newItem: FoodRequest): Boolean {
            return oldItem.uniqueId == newItem.uniqueId
        }

        override fun areContentsTheSame(oldItem: FoodRequest, newItem: FoodRequest): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }
    }

    val differ = AsyncListDiffer(this, differCallback)

    fun submitList(list: List<FoodRequest>) = differ.submitList(list)


}