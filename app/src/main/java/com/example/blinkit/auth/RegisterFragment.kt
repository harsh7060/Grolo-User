package com.example.blinkit.auth

import android.app.AlertDialog
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
import com.example.blinkit.databinding.FragmentRegisterBinding
import com.example.blinkit.models.User
import com.example.blinkit.utils.Utils
import com.example.blinkit.viewModels.AuthViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class RegisterFragment : Fragment() {
    private lateinit var binding: FragmentRegisterBinding
    private val viewModel: AuthViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRegisterBinding.inflate(layoutInflater)

        setStatusBarAndNavigationBarColors()

        onRegisterBtnClick()

        return binding.root
    }

    private fun onRegisterBtnClick() {
        binding.btnRegister.setOnClickListener{
            val number = binding.etUserNumber.text.toString()
            val email = binding.etUserEmail.text.toString()
            val password = binding.etUserPassword.text.toString()
            val name = binding.etUserName.text.toString()

            Utils.showDialog(requireContext(), "Please Wait...")

            if(email.isEmpty() || password.isEmpty() || number.isEmpty() || name.isEmpty()){
                Utils.showToast(requireContext(), "Please enter a valid details❗")
            }else{
                val user = User(uid = null, userName = name, userEmail = email, userAddress = "", userPassword = "", userPhoneNumber = number)
                viewModel.signUpWithCredentials(email, password, user, requireContext())
                lifecycleScope.launch {
                    viewModel.isSignedUpSuccessfully.collect{
                        if(it){
                            Utils.showToast(requireContext(), "User Registered Successfully✅")
                            Utils.hideDialog()
                            findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
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