package com.kosiso.foodshare.ui.fragments.donor

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.SearchView
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.kosiso.foodshare.R
import com.kosiso.foodshare.databinding.FragmentSeeFoodRequestsBinding
import com.kosiso.foodshare.models.FoodRequest
import com.kosiso.foodshare.other.Utilities
import com.kosiso.foodshare.ui.adapter.FoodRequestsAdapter
import com.kosiso.foodshare.ui.viewmodels.donor.SeeFoodRequestsViewModel
import dagger.hilt.android.AndroidEntryPoint

/**
 *  this fragment uses the same adapter (FoodRequestsAdapter) as the "Requests Fragment" in the guest activity
*/

@AndroidEntryPoint
class SeeFoodRequestsFragment : Fragment(R.layout.fragment_see_food_requests) {

    private lateinit var binding: FragmentSeeFoodRequestsBinding
    private val seeFoodRequestsViewModel: SeeFoodRequestsViewModel by viewModels()
    private val adapter: FoodRequestsAdapter = FoodRequestsAdapter()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSeeFoodRequestsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        seeFoodRequestsViewModel.messageFromViewModel.observe(viewLifecycleOwner, Observer {
            Utilities.showErrorSnackBar(it, requireView(), requireContext())
        })

        seeFoodRequestsViewModel.allFoodRequests.observe(viewLifecycleOwner, Observer{ result->
            result.onSuccess {querySnapshot->
                val foodRequestList = mutableListOf<FoodRequest>()
                for (document in querySnapshot){
                    val foodRequest = document.toObject(FoodRequest::class.java)
                    foodRequestList.add(foodRequest)
                }
                adapter.submitList(foodRequestList)
            }
            result.onFailure {
                Utilities.showErrorSnackBar(it.message!!, requireView(), requireContext())
            }
        })

        seeFoodRequestsViewModel.openFoodRequests.observe(viewLifecycleOwner, Observer{ result->
            result.onSuccess {querySnapshot ->

            }
            result.onFailure {
                Utilities.showErrorSnackBar(it.message!!, requireView(), requireContext())
            }
        })

        seeFoodRequestsViewModel.redeemedFoodRequests.observe(viewLifecycleOwner, Observer{ result->
            result.onSuccess {querySnapshot ->

            }
            result.onFailure {
                Utilities.showErrorSnackBar(it.message!!, requireView(), requireContext())
            }
        })

        seeFoodRequestsViewModel.completedFoodRequests.observe(viewLifecycleOwner, Observer{ result->
            result.onSuccess {querySnapshot ->

            }
            result.onFailure {
                Utilities.showErrorSnackBar(it.message!!, requireView(), requireContext())
            }
        })

        seeFoodRequestsViewModel.searchedRequests.observe(viewLifecycleOwner, Observer { result->
            result.onSuccess {querySnapshot->
                val searchedFoods = mutableListOf<FoodRequest>()
                for (document in querySnapshot){
                    Log.i("searched food listings", "$document")
                    val availableFood = document.toObject(FoodRequest::class.java)
                    searchedFoods.add(availableFood)
                }
                adapter.submitList(searchedFoods)
            }
        })

        binding.sv.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {

                seeFoodRequestsViewModel.fetchSearchedDocuments(newText!!)
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
                    0 -> seeFoodRequestsViewModel.fetchAllFoodRequests()
                    1 -> seeFoodRequestsViewModel.fetchOpenFoodRequests()
                    2 -> seeFoodRequestsViewModel.fetchRedeemedFoodRequests()
                    3 -> seeFoodRequestsViewModel.fetchCompletedFoodRequests()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}

        }

        //call function here
        setUpRecyclerView()
    }

    private fun setUpRecyclerView(){
        binding.rvRequests.adapter = adapter
        binding.rvRequests.layoutManager = LinearLayoutManager(requireContext())

    }

}