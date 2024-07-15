package com.example.blinkit.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.blinkit.Adapters.AdapterCartProducts
import com.example.blinkit.R
import com.example.blinkit.databinding.FragmentOrderDetailBinding
import com.example.blinkit.viewModels.UserViewModel
import kotlinx.coroutines.launch

class OrderDetailFragment : Fragment() {
    private lateinit var binding: FragmentOrderDetailBinding
    private val viewModel: UserViewModel by viewModels()
    private lateinit var adapterCartProducts: AdapterCartProducts
    private var status = 0
    private var orderId = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentOrderDetailBinding.inflate(layoutInflater)

        getValues()

        settingStatus()

        onBackBtnClick()

        lifecycleScope.launch {
            getOrderedProducts()
        }

        return binding.root
    }

    private fun onBackBtnClick() {
        binding.tbOrderDetailFragment.setNavigationOnClickListener{
            findNavController().navigate(R.id.action_orderDetailFragment_to_ordersFragment)
        }
    }

    suspend fun getOrderedProducts() {
        viewModel.getOrderedProducts(orderId).collect{cartList->
            adapterCartProducts = AdapterCartProducts()
            binding.rvProductsItems.adapter = adapterCartProducts
            adapterCartProducts.differ.submitList(cartList)
        }
    }

    private fun settingStatus() {
        val greenColor = ContextCompat.getColorStateList(requireContext(), R.color.green)
        val statusViewsMap = mapOf(
            0 to arrayOf(binding.iv1),
            1 to arrayOf(binding.iv1, binding.iv2, binding.view1),
            2 to arrayOf(binding.iv1, binding.iv2, binding.iv3, binding.view1, binding.view2),
            3 to arrayOf(binding.iv1, binding.iv2, binding.iv3, binding.iv4, binding.view1, binding.view2, binding.view3)
        )

        statusViewsMap[status]?.forEach { it.backgroundTintList = greenColor }


    }

    private fun getValues() {
        val bundle = arguments
        status = bundle?.getInt("status")!!
        orderId = bundle.getString("orderId").toString()
    }
}