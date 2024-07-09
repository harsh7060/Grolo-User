package com.example.blinkit

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.blinkit.activities.UsersMainActivity
import com.example.blinkit.databinding.FragmentOtpBinding
import com.example.blinkit.models.User
import com.example.blinkit.viewModels.AuthViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch


class OtpFragment : Fragment(){
    private val viewModel: AuthViewModel by viewModels()
    private lateinit var binding: FragmentOtpBinding
    private lateinit var userNumber: String
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ):View?{
        binding=FragmentOtpBinding.inflate(layoutInflater)

        getUserNumber()

        customizingEnteringOtp()

        sendOtp()

        onLoginButtonClick()

        onBackButtonClick()

        return binding.root
    }

    private fun onLoginButtonClick() {
        binding.btnLogin.setOnClickListener {
            val editTexts = arrayOf(binding.etOtp1,binding.etOtp2,binding.etOtp3,binding.etOtp4,binding.etOtp5,binding.etOtp6)
            val otp = editTexts.joinToString(""){it.text.toString()}

            if(otp.length!=editTexts.size){
                Utils.showToast(requireContext(),"Please enter the OTP❕")
            }else{
                editTexts.forEach{
                    it.text?.clear(); it.clearFocus()
                }
                verifyOtp(otp)
            }
        }
    }

    private fun verifyOtp(otp: String) {
        val user = User(uid = null, userPhoneNumber = userNumber, userAddress = null)
        viewModel.signInWithPhoneAuthCredential(otp, userNumber, user)
        lifecycleScope.launch {
            viewModel.isSignedInSuccessfully.collect{
                if(it){
                    Utils.hideDialog()
                    Utils.showToast(requireContext(),"Login Successfully")
                    startActivity(Intent(requireContext(),UsersMainActivity::class.java))
                    requireActivity().finish()
                }
            }
        }
    }

    private fun sendOtp() {
        Utils.showDialog(requireContext(), "Sending OTP...")
        viewModel.apply {
            sendOtp(userNumber, requireActivity())
            lifecycleScope.launch{
                sentOtp.collect{
                    if(it){
                        Utils.hideDialog()
                        Utils.showToast(requireContext(),"OTP sent successfully")
                    }
                }
            }
        }
    }

    private fun onBackButtonClick() {
        binding.tbOtpFragment.setNavigationOnClickListener {
            findNavController().navigate(R.id.action_otpFragment_to_loginFragment)
        }
    }

    private fun customizingEnteringOtp() {
        val editTexts = arrayOf(binding.etOtp1,binding.etOtp2,binding.etOtp3,binding.etOtp4,binding.etOtp5,binding.etOtp6)
        for(i in editTexts.indices){
            editTexts[i].addTextChangedListener(object : TextWatcher{
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                override fun afterTextChanged(s: Editable?) {
                    if(s?.length==1){
                        if(i<editTexts.size-1){
                            editTexts[i+1].requestFocus()
                        }
                    }else if(s?.length==0){
                        if(i>0){
                            editTexts[i-1].requestFocus()
                        }
                    }
                }

            })
        }
    }

    private fun getUserNumber() {
        val bundle = arguments
        userNumber = bundle?.getString("number").toString()
        binding.tvUserNumber.text = userNumber
    }
}