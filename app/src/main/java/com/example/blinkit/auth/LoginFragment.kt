package com.example.blinkit.auth

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.blinkit.R
import com.example.blinkit.activities.UsersMainActivity
import com.example.blinkit.utils.Utils
import com.example.blinkit.databinding.FragmentLoginBinding
import com.example.blinkit.models.User
import com.example.blinkit.viewModels.AuthViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class LoginFragment : Fragment() {
    private lateinit var binding : FragmentLoginBinding
    private val viewModel : AuthViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(layoutInflater)

        setStatusBarAndNavigationBarColors()

        onSignUpClick()

        onLoginClick()

        return binding.root
    }

    private fun onSignUpClick() {
        binding.tvSignUp.setOnClickListener{
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }
    }

    private fun onLoginClick(){
        binding.btnLogin.setOnClickListener{
            Utils.showDialog(requireContext(), "Logging in...")
            val email = binding.etUserEmail.text.toString()
            val password = binding.etUserPassword.text.toString()
            if(email.isEmpty() || password.isEmpty()){
                Utils.showToast(requireContext(), "Please enter a valid details❗")
            }else{
                viewModel.signInWithCredentials(email, password, requireContext())
                lifecycleScope.launch {
                    viewModel.isSignedInSuccessfully.collect{
                        if(it){
                            Utils.showToast(requireContext(), "Login Successfully")
                            Utils.hideDialog()
                            startActivity(Intent(requireContext(), UsersMainActivity::class.java))
                            requireActivity().finish()
                        }
                    }
                }
            }
        }
    }

    private fun setStatusBarAndNavigationBarColors() {
        activity?.window?.statusBarColor = resources.getColor(R.color.white)
        activity?.window?.navigationBarColor = resources.getColor(R.color.white)
        val windowInsetsController = ViewCompat.getWindowInsetsController(activity?.window?.decorView!!)
        windowInsetsController?.isAppearanceLightStatusBars = true
        windowInsetsController?.isAppearanceLightNavigationBars = true
    }
}
