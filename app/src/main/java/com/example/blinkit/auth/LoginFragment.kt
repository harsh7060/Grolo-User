package com.example.blinkit.auth

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.example.blinkit.R
import com.example.blinkit.utils.Utils
import com.example.blinkit.databinding.FragmentLoginBinding

class LoginFragment : Fragment() {
    private lateinit var binding : FragmentLoginBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(layoutInflater)

        getUserNumber()
        onContinueBtnclick()
        return binding.root
    }

    private fun onContinueBtnclick() {
        binding.btnContinue.setOnClickListener{
            val number = binding.etUserNumber.text.toString()
            if(number.isEmpty() || number.length!=10){
                Utils.showToast(requireContext(), "Please enter a valid number❗")
            }else{
                val bundle = Bundle()
                bundle.putString("number",number)
                findNavController().navigate(R.id.action_loginFragment_to_otpFragment,bundle)
            }
        }
    }

    private fun getUserNumber(){
        binding.etUserNumber.addTextChangedListener ( object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(number: CharSequence?, start: Int, before: Int, count: Int) {
                val len = number?.length
                if (len != null) {
                    if (len >= 10) {
                        binding.btnContinue.setBackgroundColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.yellow
                            )
                        )
                    }
                }
            }
            override fun afterTextChanged(s: Editable?) {

            }

        } )
        }
}
