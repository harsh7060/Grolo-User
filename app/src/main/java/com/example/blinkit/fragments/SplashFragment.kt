package com.example.blinkit.fragments

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.blinkit.R
import com.example.blinkit.activities.UsersMainActivity
import com.example.blinkit.databinding.FragmentSplashBinding
import com.example.blinkit.viewModels.AuthViewModel
import kotlinx.coroutines.launch

class SplashFragment : Fragment() {
    private val viewModel: AuthViewModel by viewModels()
    private lateinit var binding: FragmentSplashBinding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = FragmentSplashBinding.inflate(layoutInflater)
        setStatusBarAndNavigationBarColors()
        Handler(Looper.getMainLooper()).postDelayed({
            lifecycleScope.launch {
                viewModel.isCurrentUser.collect{
                    if(it){
                        startActivity(Intent(requireActivity(), UsersMainActivity::class.java))
                        requireActivity().finish()
                    }else{
                        findNavController().navigate(R.id.action_splashFragment_to_loginFragment)
                    }
                }
            }
        },2000)
        return binding.root
    }
    private fun setStatusBarAndNavigationBarColors() {
        activity?.window?.statusBarColor = resources.getColor(R.color.yellow)
        activity?.window?.navigationBarColor = resources.getColor(R.color.yellow)
        val windowInsetsController = ViewCompat.getWindowInsetsController(activity?.window?.decorView!!)
        windowInsetsController?.isAppearanceLightStatusBars = true
        windowInsetsController?.isAppearanceLightNavigationBars = true
    }
}