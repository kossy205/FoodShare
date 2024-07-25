package com.kosiso.foodshare.ui.fragments.donor

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.tasks.Task
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.rpc.Help.Link
import com.kosiso.foodshare.R
import com.kosiso.foodshare.databinding.FragmentListingsBinding
import com.kosiso.foodshare.models.FoodListing
import com.kosiso.foodshare.other.Utilities
import com.kosiso.foodshare.ui.adapter.ListingsAdapter
import com.kosiso.foodshare.ui.viewmodels.donor.ListingsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ListingsFragment : Fragment() {
    private lateinit var binding: FragmentListingsBinding
    private val listingsViewModel: ListingsViewModel by viewModels()
    private lateinit var listingsAdapter: ListingsAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentListingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        listingsViewModel.messageFromViewModel.observe(viewLifecycleOwner, Observer {
            showErrorSnackBar(it)
        })

        listingsViewModel.allFoodListings.observe(viewLifecycleOwner, Observer {result->
            result.onSuccess {querySnapshot ->
                Log.i("all food listings", "$querySnapshot")
                val listings = mutableListOf<FoodListing>()
                for (document in querySnapshot){
                    Log.i("all food listings", "$document")
                    val foodListing = document.toObject(FoodListing::class.java)
                    listings.add(foodListing)
                }
                listingsAdapter.submitList(listings)
                //observeFilteredList()
            }
        })

        listingsViewModel.claimedFoodListings.observe(viewLifecycleOwner, Observer {result->
            result.onSuccess {querySnapshot ->
                Log.i("claimed food listings", "$querySnapshot")
                val listings = mutableListOf<FoodListing>()
                for (document in querySnapshot){
                    Log.i("claimed food listings", "$document")
                    val foodListing = document.toObject(FoodListing::class.java)
                    listings.add(foodListing)
                }
                listingsAdapter.submitList(listings)
                Log.i("current list3", "${listingsAdapter.currentList()}")
            }
        })

        listingsViewModel.unClaimedFoodListings.observe(viewLifecycleOwner, Observer {result->
            result.onSuccess {querySnapshot ->
                Log.i("unClaimed food listings", "$querySnapshot")
                val listings = mutableListOf<FoodListing>()
                for (document in querySnapshot){
                    Log.i("unclaimed food listings", "$document")
                    val foodListing = document.toObject(FoodListing::class.java)
                    listings.add(foodListing)
                }
                listingsAdapter.submitList(listings)
            }
        })

        listingsViewModel.deliveredFoodListings.observe(viewLifecycleOwner, Observer {result->
            result.onSuccess {querySnapshot ->
                Log.i("delivered food listings", "$querySnapshot")
                val listings = mutableListOf<FoodListing>()
                for (document in querySnapshot){
                    Log.i("delivered food listings", "$document")
                    val foodListing = document.toObject(FoodListing::class.java)
                    listings.add(foodListing)
                }
                listingsAdapter.submitList(listings)
            }
        })

        listingsViewModel.searchedListings.observe(viewLifecycleOwner, Observer {result->
            result.onSuccess {querySnapshot->
                val searchedListings = mutableListOf<FoodListing>()
                for (document in querySnapshot){
                    Log.i("searched food listings", "$document")
                    val foodListing = document.toObject(FoodListing::class.java)
                    searchedListings.add(foodListing)
                }
                listingsAdapter.submitList(searchedListings)
            }
        })


        // call functions here
        setUpRecyclerView()

        binding.sv.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {

                listingsViewModel.fetchSearchedListings(newText!!)
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
                    0 -> listingsViewModel.fetchAllFoodListings()
                    1 -> listingsViewModel.fetchUnclaimedFoodListings()
                    2 -> listingsViewModel.fetchClaimedFoodListings()
                    3 -> listingsViewModel.fetchDeliveredFoodListings()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}

        }
    }

    private fun setUpRecyclerView(){
        listingsAdapter = ListingsAdapter()
        binding.rvListings.adapter = listingsAdapter
        binding.rvListings.layoutManager = LinearLayoutManager(requireContext())

    }


    private fun showErrorSnackBar(message: String) {
        val snackBar =
            Snackbar.make(requireView(), message, Snackbar.LENGTH_LONG)
        val snackBarView = snackBar.view
        snackBarView.setBackgroundColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.red
            )
        )
        snackBar.show()
    }

}