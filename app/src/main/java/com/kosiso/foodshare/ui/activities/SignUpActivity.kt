package com.kosiso.foodshare.ui.activities

import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Observer
import com.kosiso.foodshare.R
import com.kosiso.foodshare.databinding.ActivitySignUpBinding
import com.kosiso.foodshare.ui.viewmodels.SignUpViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SignUpActivity : BaseActivity() {

    private val signUpViewmodel: SignUpViewModel by viewModels()
    private lateinit var binding: ActivitySignUpBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.signUp)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSignUpOptionsDropdown()

        binding.btnSignUp.setOnClickListener {
            val firstName: String = binding.etFirstName.text.toString()
            val lastName: String = binding.etLastName.text.toString()
            val phone: Long? = binding.etCountryCode.text.toString().toLongOrNull()
            val countryCode: Long? = binding.etPhone.text.toString().toLongOrNull()
            val email: String = binding.etEmail.text.toString().trim { it <= ' ' }
            val password: String = binding.etPassword.text.toString().trim { it <= ' ' }
            signUpViewmodel.signUp(firstName, lastName, countryCode, phone, email, password)
        }

        signUpViewmodel.messageFromViewModel.observe(this, Observer { error->
            error?.let { showErrorSnackBar(it.toString()) }
        })

    }

    fun setSignUpOptionsDropdown(){
        val signUpOptions = resources.getStringArray(R.array.signup_options)
        val arrayAdapter = ArrayAdapter(this, R.layout.signup_options_dropdown, signUpOptions)
        binding.tvPurpose.setAdapter(arrayAdapter)
    }
}