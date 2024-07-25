package com.kosiso.foodshare.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.kosiso.foodshare.databinding.ItemListingsBinding
import com.kosiso.foodshare.models.FoodListing
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class ListingsAdapter: RecyclerView.Adapter<ListingsAdapter.ListingsViewHolder>(){

    inner class ListingsViewHolder(val binding: ItemListingsBinding): RecyclerView.ViewHolder(binding.root)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListingsViewHolder{
        val binding = ItemListingsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListingsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ListingsAdapter.ListingsViewHolder, position: Int) {
        val foodListing = differ.currentList[position]

        holder.binding.apply {
            itemFoodName.text = foodListing.nameOfItem
            itemFoodDesc.text = "${foodListing.itemDescription.substring(0, 30)}..."
            Glide.with(root.context).load(foodListing.foodImg).into(itemFoodImage)
            itemFoodCategory.text = foodListing.listingCategory
            itemFoodWeight.text = "${foodListing.foodWeight}kg"
            itemFoodStatus.text = foodListing.status

            val dateFormat = SimpleDateFormat("dd.mm.yyyy", Locale.getDefault())
            itemFoodPostDate.text = dateFormat.format(foodListing.foodListedTime.toDate())
            itemFoodExpDate.text = "Exp: ${dateFormat.format(foodListing.expiryDate!!.toDate())}"


        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }



    // used to check between two lists old and new and update only content and items that has changed...
    // instead of rebuilding the whole list again when ever we make changes.
    // it is very helpful for performance as it reduces lag.
    val diffCallback = object : DiffUtil.ItemCallback<FoodListing>() {
        override fun areItemsTheSame(oldItem: FoodListing, newItem: FoodListing): Boolean {
            return oldItem.uniqueId == newItem.uniqueId
        }

        override fun areContentsTheSame(oldItem: FoodListing, newItem: FoodListing): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }
    }

    val differ = AsyncListDiffer(this, diffCallback)

    fun submitList(list: List<FoodListing>) = differ.submitList(list)

    fun clearList() = differ.submitList(emptyList())

    fun currentList() = differ.currentList
}