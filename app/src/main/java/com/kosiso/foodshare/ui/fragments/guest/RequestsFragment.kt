package com.kosiso.foodshare.ui.fragments.guest

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.toObject
import com.kosiso.foodshare.R
import com.kosiso.foodshare.databinding.FragmentRequestsBinding
import com.kosiso.foodshare.models.FoodRequest
import com.kosiso.foodshare.other.Utilities
import com.kosiso.foodshare.ui.adapter.FoodRequestsAdapter
import com.kosiso.foodshare.ui.adapter.ListingsAdapter
import com.kosiso.foodshare.ui.viewmodels.guest.RequestsViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class RequestsFragment : Fragment() {


    private lateinit var binding: FragmentRequestsBinding
    private val requestsViewModel: RequestsViewModel by viewModels()
    private val adapter: FoodRequestsAdapter = FoodRequestsAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentRequestsBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requestsViewModel.messageFromViewModel.observe(viewLifecycleOwner, Observer {
            Utilities.showErrorSnackBar(it, requireView(), requireContext())
        })

        requestsViewModel.allFoodRequests.observe(viewLifecycleOwner, Observer{result->
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

        requestsViewModel.openFoodRequests.observe(viewLifecycleOwner, Observer{result->
            result.onSuccess {querySnapshot ->

            }
            result.onFailure {
                Utilities.showErrorSnackBar(it.message!!, requireView(), requireContext())
            }
        })

        requestsViewModel.redeemedFoodRequests.observe(viewLifecycleOwner, Observer{result->
            result.onSuccess {querySnapshot ->

            }
            result.onFailure {
                Utilities.showErrorSnackBar(it.message!!, requireView(), requireContext())
            }
        })

        requestsViewModel.completedFoodRequests.observe(viewLifecycleOwner, Observer{result->
            result.onSuccess {querySnapshot ->

            }
            result.onFailure {
                Utilities.showErrorSnackBar(it.message!!, requireView(), requireContext())
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
                    0 -> requestsViewModel.fetchAllFoodRequests()
                    1 -> requestsViewModel.fetchOpenFoodRequests()
                    2 -> requestsViewModel.fetchRedeemedFoodRequests()
                    3 -> requestsViewModel.fetchCompletedFoodRequests()
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