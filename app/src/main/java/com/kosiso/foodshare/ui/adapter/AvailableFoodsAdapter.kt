package com.kosiso.foodshare.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.kosiso.foodshare.databinding.ItemAvailableFoodsBinding
import com.kosiso.foodshare.models.FoodListing
import java.text.SimpleDateFormat
import java.util.Locale

class AvailableFoodsAdapter: RecyclerView.Adapter<AvailableFoodsAdapter.AvailableFoodsViewHolder>(){

    inner class AvailableFoodsViewHolder(val binding: ItemAvailableFoodsBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AvailableFoodsViewHolder {
        val binding = ItemAvailableFoodsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AvailableFoodsViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: AvailableFoodsViewHolder, position: Int) {
        val foodListing = differ.currentList[position]

        holder.binding.apply {
            itemFoodName.text = foodListing.nameOfItem
            itemFoodDesc.text = "${foodListing.itemDescription.substring(0, 30)}..."
            Glide.with(root.context).load(foodListing.foodImg).into(itemFoodImage)
            itemFoodCategory.text = foodListing.listingCategory
            itemFoodWeight.text = "${foodListing.foodWeight}kg"

            val dateFormat = SimpleDateFormat("dd.mm.yyyy", Locale.getDefault())
            itemFoodPostDate.text = dateFormat.format(foodListing.foodListedTime.toDate())
            itemFoodExpDate.text = "Exp: ${dateFormat.format(foodListing.expiryDate!!.toDate())}"
        }
    }



    val diffCallback = object: DiffUtil.ItemCallback<FoodListing>(){
        override fun areItemsTheSame(oldItem: FoodListing, newItem: FoodListing): Boolean {
            return oldItem.uniqueId == newItem.uniqueId
        }

        override fun areContentsTheSame(oldItem: FoodListing, newItem: FoodListing): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }
    }

    val differ = AsyncListDiffer(this, diffCallback)

    fun submitList(list: List<FoodListing>) = differ.submitList(list)

    fun currentList() = differ.currentList

}