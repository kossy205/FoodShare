package com.kosiso.foodshare.ui.fragments.guest

import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.SearchView
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.kosiso.foodshare.R
import com.kosiso.foodshare.databinding.FragmentAvailableFoodBinding
import FoodListing
import android.content.Intent
import com.kosiso.foodshare.other.Constants
import com.kosiso.foodshare.other.Utilities
import com.kosiso.foodshare.ui.activities.DonorActivity
import com.kosiso.foodshare.ui.adapter.AvailableFoodsAdapter
import com.kosiso.foodshare.ui.viewmodels.guest.AvailableFoodsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AvailableFoodFragment : Fragment() {

    private lateinit var binding: FragmentAvailableFoodBinding
    private val availableFoodsViewModel: AvailableFoodsViewModel by viewModels()
    private lateinit var availableFoodsAdapter: AvailableFoodsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentAvailableFoodBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpRecyclerView()

        availableFoodsViewModel.messageFromViewModel.observe(viewLifecycleOwner, Observer {
            Utilities.showErrorSnackBar(it, requireView(), requireContext())
        })


        availableFoodsViewModel.allFoodListings.observe(viewLifecycleOwner, Observer{result->
            result.onSuccess {querySnapshot ->
                Log.i("all available foods", "$querySnapshot")
                val availableFoodsList = mutableListOf<FoodListing>()
                for (document in querySnapshot){
                    Log.i("available food", "$document")
                    val availableFood = document.toObject(FoodListing::class.java)
                    availableFoodsList.add(availableFood)
                }
                availableFoodsAdapter.submitList(availableFoodsList)
            }

        })

        availableFoodsViewModel.perishableAvailableFoods.observe(viewLifecycleOwner, Observer {result->
            result.onSuccess {querySnapshot ->
                Log.i("all perishable foods", "$querySnapshot")
                val perishableFoodsList = mutableListOf<FoodListing>()
                for (document in querySnapshot){
                    Log.i("perishable food", "$document")
                    val perishableFood = document.toObject(FoodListing::class.java)
                    perishableFoodsList.add(perishableFood)
                }
                availableFoodsAdapter.submitList(perishableFoodsList)
            }
        })

        availableFoodsViewModel.nonPerishableAvailableFoods.observe(viewLifecycleOwner, Observer {result->
            result.onSuccess {querySnapshot ->
                Log.i("all non-perishable foods", "$querySnapshot")
                val nonPerishableFoodsList = mutableListOf<FoodListing>()
                for (document in querySnapshot){
                    Log.i("non-perishable food", "$document")
                    val nonPerishableFood = document.toObject(FoodListing::class.java)
                    nonPerishableFoodsList.add(nonPerishableFood)
                }
                availableFoodsAdapter.submitList(nonPerishableFoodsList)
            }
        })

        availableFoodsViewModel.searchedAvailableFoods.observe(viewLifecycleOwner, Observer {result->
            result.onSuccess {querySnapshot->
                val searchedFoods = mutableListOf<FoodListing>()
                for (document in querySnapshot){
                    Log.i("searched food listings", "$document")
                    val availableFood = document.toObject(FoodListing::class.java)
                    searchedFoods.add(availableFood)
                }
                availableFoodsAdapter.submitList(searchedFoods)
            }
        })

        binding.guestFab.setOnClickListener {
            findNavController().navigate(R.id.action_availableFoodFragment2_to_makeRequestFragment)
        }

        binding.sv.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {

                availableFoodsViewModel.fetchSearchedListings(newText!!)
                return true
            }
        })


        binding.spFilter.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                adapterView: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                when(position){
                    0 -> availableFoodsViewModel.fetchAllFoodListings()
                    1 -> availableFoodsViewModel.fetchPerishableFoodListings()
                    2 -> availableFoodsViewModel.fetchNonPerishableFoodListings()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}

        }

        availableFoodsAdapter.onItemClick = {item->
            val bundle = Bundle().apply {
                putParcelable(Constants.ITEM_AVAILABLE_FOOD_DATA_OBJECT, item as Parcelable)
            }
            findNavController().navigate(R.id.action_availableFoodFragment2_to_availableFoodDetailsFragment, bundle)
            Log.i("navigate to details", "${R.id.action_availableFoodFragment2_to_availableFoodDetailsFragment}")
        }
    }

    private fun setUpRecyclerView(){
        availableFoodsAdapter = AvailableFoodsAdapter()
        binding.rvAvailableFoods.adapter = availableFoodsAdapter
        binding.rvAvailableFoods.layoutManager = LinearLayoutManager(requireContext())
    }
}