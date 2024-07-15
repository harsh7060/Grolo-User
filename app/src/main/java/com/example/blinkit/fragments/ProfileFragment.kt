package com.example.blinkit.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.navigation.fragment.findNavController
import com.example.blinkit.R
import com.example.blinkit.databinding.FragmentProfileBinding

class ProfileFragment : Fragment() {
    private lateinit var binding: FragmentProfileBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(layoutInflater)

        setStatusBarAndNavigationBarColors()

        onBackBtnClick()

        onOrdersBtnClick()

        return binding.root
    }

    private fun onOrdersBtnClick() {
        binding.llOrders.setOnClickListener{
            findNavController().navigate(R.id.action_profileFragment_to_ordersFragment)
        }
    }

    private fun onBackBtnClick() {
        binding.tbProfileFragment.setNavigationOnClickListener{
            findNavController().navigate(R.id.action_profileFragment_to_homeFragment)
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