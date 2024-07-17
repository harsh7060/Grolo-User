package com.example.blinkit.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.blinkit.Adapters.AdapterOrders
import com.example.blinkit.R
import com.example.blinkit.databinding.FragmentOrdersBinding
import com.example.blinkit.models.OrderedItems
import com.example.blinkit.viewModels.UserViewModel
import kotlinx.coroutines.launch

class OrdersFragment : Fragment() {
    private lateinit var binding: FragmentOrdersBinding
    private val viewModel: UserViewModel by viewModels()
    private lateinit var adapterOrders: AdapterOrders

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentOrdersBinding.inflate(layoutInflater)

        onBackBtnClick()

        getAllOrders()

        return binding.root
    }

    private fun getAllOrders() {
        binding.shimmerViewContainer.visibility = View.VISIBLE
        lifecycleScope.launch {
            viewModel.getAllOrders().collect{orderList->
                if(orderList.isNotEmpty()){
                    val orderedList = ArrayList<OrderedItems>()
                    for(orders in orderList){
                        val title = StringBuilder()
                        var totalPrice = 0
                        for(products in orders.orderList!!){
                            val itemPrice = products.productPrice!!.substring(1).toInt()
                            val itemCount = products.productCount
                            totalPrice += (itemPrice* itemCount!!)

                            title.append("${products.productName}, ")
                        }
                        val orederedItems = OrderedItems(orders.orderId, orders.orderDate, orders.orderStatus, title.toString(), totalPrice)
                        orderedList.add(orederedItems)
                    }
                    adapterOrders = AdapterOrders(requireContext(), ::onOrderItemViewClick)
                    binding.rvOrders.adapter = adapterOrders
                    adapterOrders.differ.submitList(orderedList)
                    binding.shimmerViewContainer.visibility = View.GONE
                }
            }
        }
    }

    private fun onOrderItemViewClick(orderedItems: OrderedItems){
        val bundle = Bundle()
        bundle.putInt("status", orderedItems.itemStatus!!)
        bundle.putString("orderId", orderedItems.orderId)

        findNavController().navigate(R.id.action_ordersFragment_to_orderDetailFragment, bundle)
    }

    private fun onBackBtnClick() {
        binding.tbOrderFragment.setNavigationOnClickListener{
            findNavController().navigate(R.id.action_ordersFragment_to_profileFragment)
        }
    }
}