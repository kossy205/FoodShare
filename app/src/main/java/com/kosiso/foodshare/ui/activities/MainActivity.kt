package com.kosiso.foodshare.ui.activities

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Observer
import com.google.firebase.auth.FirebaseAuth
import com.kosiso.foodshare.R
import com.kosiso.foodshare.databinding.ActivityCustomSplashScreenBinding
import com.kosiso.foodshare.ui.viewmodels.authentication.SignInViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var firebaseAuth: FirebaseAuth
    private lateinit var binding: ActivityCustomSplashScreenBinding
    private val signInViewModel: SignInViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityCustomSplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.splashScreen)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        Log.i("splash screen", "confirmed")

        // Set the status bar color
        window.statusBarColor = resources.getColor(R.color.main_green)
        // Set the navigation bar color
        window.navigationBarColor = resources.getColor(R.color.main_green)

        // main code
        Handler(Looper.getMainLooper()).postDelayed({
            signUpDestination {activityClass ->
                Log.i("navigate to activity", "${activityClass}")
                if(activityClass != null){
                    startActivity(Intent(this, activityClass))
                    finish()
                }else{
                    startActivity(Intent(this, AuthenticationActivity::class.java))
                    finish()
                }
            }
        }, 3000)
    }


    private fun signUpDestination(callback: (Class<out Activity>?) -> Unit) {
        //first check if the current user is already authenticated
        if(firebaseAuth.currentUser != null){
            Log.i("auth user", firebaseAuth.currentUser!!.uid)
            signInViewModel.fetchUerDetails(firebaseAuth.currentUser!!.uid)
            Log.i("fetchUerDetails", "fetchUerDetails executed")
            signInViewModel.fetchedResult.observe(this, Observer {
                Log.i("result for navigation", it.toString())
                val activityClass = when(it.toString()){
                    "Success(Donor)" -> DonorActivity::class.java
                    "Success(Guest)" -> GuestActivity::class.java
                    "Success(Delivery Volunteer)" -> VolunteerActivity::class.java
                    else -> null
                }
                Log.i("navigate to activity 1", "$activityClass")
                callback(activityClass)
            })
        }else{
            callback(null)
            Log.i("navigate to activity 2", "$callback")
        }
    }
}