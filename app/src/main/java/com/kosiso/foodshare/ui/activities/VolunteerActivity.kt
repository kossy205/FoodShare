package com.kosiso.foodshare.ui.activities

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.findNavController
import com.kosiso.foodshare.R
import com.kosiso.foodshare.databinding.ActivityVolunteerBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class VolunteerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityVolunteerBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityVolunteerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val volunteerNavController = findNavController(R.id.volunteerNavHostFragment)
        binding.volunteerBottomNavigationView.setOnItemReselectedListener { /* NO-OPERATION */ }
        binding.volunteerBottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.updatesVolunteerFragment -> {
                    volunteerNavController.navigate(R.id.updatesFragment)
                    true
                }
                R.id.historyVolunteerFragment -> {
                    volunteerNavController.navigate(R.id.volunteerHistoryFragment)
                    true
                }
                R.id.impactVolunteerFragment -> {
                    volunteerNavController.navigate(R.id.volunteerImpactFragment)
                    true
                }
                else -> false
            }
        }


        volunteerNavController.addOnDestinationChangedListener { _, destination, _ ->
            Log.d("NavigationEvents", "Navigated to ${destination.label}")
            when(destination.id){
                R.id.updatesFragment, R.id.volunteerHistoryFragment, R.id.volunteerImpactFragment ->{
                    binding.volunteerBottomNavigationView.visibility = View.VISIBLE
                }
                else ->{
                    binding.volunteerBottomNavigationView.visibility = View.GONE
                }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return findNavController(R.id.volunteerBottomNavigationView).navigateUp() || super.onSupportNavigateUp()
    }
}