package com.kosiso.foodshare.ui.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.kosiso.foodshare.R
import com.kosiso.foodshare.databinding.ActivityDonorBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DonorActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDonorBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityDonorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        val donorBottomNavView:BottomNavigationView = binding.donorBottomNavigationView
        val donorNavController = findNavController(R.id.donorNavHostFragment)
        //binding.donorBottomNavigationView.setupWithNavController(donorNavController)
        binding.donorBottomNavigationView.setOnItemReselectedListener { /* NO-OPERATION */ }
        binding.donorBottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.listFoodFragment -> {
                    donorNavController.navigate(R.id.impactDonorFragment)
                    true
                }
                R.id.foodRequestFragment -> {
                    donorNavController.navigate(R.id.foodRequestsFragment)
                    true
                }
                R.id.listingsFragment -> {
                    donorNavController.navigate(R.id.listingsFragment2)
                    true
                }
                R.id.transitFragment -> {
                    donorNavController.navigate(R.id.transitFragment2)
                    true
                }
                else -> false
            }
        }

        donorNavController.addOnDestinationChangedListener {_, destination, _ ->
            Log.d("NavigationEvents", "Navigated to ${destination.label}")
            when(destination.id){
                R.id.impactDonorFragment, R.id.foodRequestsFragment, R.id.listingsFragment2, R.id.transitFragment2->{
                    binding.donorBottomNavigationView.visibility = View.VISIBLE
                }
                else ->{
                    binding.donorBottomNavigationView.visibility = View.GONE
                }
            }
        }

    }

    override fun onSupportNavigateUp(): Boolean {
        return findNavController(R.id.donorNavHostFragment).navigateUp() || super.onSupportNavigateUp()
    }
}