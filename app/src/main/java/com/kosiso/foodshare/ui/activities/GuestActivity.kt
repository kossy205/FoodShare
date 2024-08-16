package com.kosiso.foodshare.ui.activities

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.findNavController
import com.kosiso.foodshare.R
import com.kosiso.foodshare.databinding.ActivityGuestBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GuestActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGuestBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityGuestBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val guestNavController = findNavController(R.id.guestNavHostFragment)
        binding.guestBottomNavigationView.setOnItemReselectedListener { /* NO-OPERATION */ }
        binding.guestBottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.availableFoodFragment -> {
                    guestNavController.navigate(R.id.availableFoodFragment2)
                    true
                }
                R.id.requestsFragment -> {
                    guestNavController.navigate(R.id.requestsFragment2)
                    true
                }
                R.id.guestTransitFragment -> {
                    guestNavController.navigate(R.id.guestTransitFragment2)
                    true
                }
                R.id.claimedFoodsFragment -> {
                    guestNavController.navigate(R.id.claimedFoodsFragment2)
                    true
                }
                else -> false
            }
        }


        guestNavController.addOnDestinationChangedListener { _, destination, _ ->
            Log.d("NavigationEvents", "Navigated to ${destination.label}")
            when(destination.id){
                R.id.availableFoodFragment2, R.id.requestsFragment2, R.id.guestTransitFragment2, R.id.claimedFoodsFragment2->{
                    binding.guestBottomNavigationView.visibility = View.VISIBLE
                }
                else ->{
                    binding.guestBottomNavigationView.visibility = View.GONE
                }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return findNavController(R.id.guestNavHostFragment).navigateUp() || super.onSupportNavigateUp()
    }
}