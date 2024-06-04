package com.kosiso.foodshare.ui.fragments

import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat.getColor
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.kosiso.foodshare.R
import com.kosiso.foodshare.databinding.FragmentSplashScreenBinding


class SplashScreenFragment : Fragment() {
    private val hideHandler = Handler(Looper.myLooper()!!)
    private lateinit var binding: FragmentSplashScreenBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            // Android 12 or higher - skip custom splash
            findNavController().navigate(R.id.action_splashScreenFragment_to_introFragment)
        }

        // Continue with existing custom splash screen for lower versions
        binding = FragmentSplashScreenBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set status bar color
        requireActivity().window.statusBarColor = resources.getColor(R.color.blue)
        // Set navigation bar color
        requireActivity().window.navigationBarColor = resources.getColor(R.color.blue)



        // main code
        Handler(Looper.getMainLooper()).postDelayed({
            findNavController().navigate(R.id.action_splashScreenFragment_to_introFragment)
        }, 7000)
    }


}