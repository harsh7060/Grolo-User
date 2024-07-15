package com.example.blinkit.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.blinkit.Adapters.AdapterCartProducts
import com.example.blinkit.R
import com.example.blinkit.databinding.ActivityUsersMainBinding
import com.example.blinkit.databinding.BsCartProductsBinding
import com.example.blinkit.interfaces.CartListener
import com.example.blinkit.roomdb.CartProducts
import com.example.blinkit.viewModels.UserViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog

class UsersMainActivity : AppCompatActivity(), CartListener {
    private lateinit var binding: ActivityUsersMainBinding
    private val viewModel: UserViewModel by viewModels()
    private lateinit var cartProductList: List<CartProducts>
    private lateinit var adapterCartProducts: AdapterCartProducts

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUsersMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getAllCartProducts()

        getTotalItemCountInCart()

        onCartClick()

        onNextBtnClick()
    }

    private fun onNextBtnClick() {
        binding.btnNext.setOnClickListener{
            startActivity(Intent(this, OrderPlaceActivity::class.java))
        }
    }

    private fun getAllCartProducts(){
        viewModel.getAll().observe(this){
            cartProductList = it
        }
    }

    private fun onCartClick() {
        binding.llItemCart.setOnClickListener{
            val bsCartProductsBinding = BsCartProductsBinding.inflate(LayoutInflater.from(this))

            val bs = BottomSheetDialog(this)
            bs.setContentView(bsCartProductsBinding.root)

            bsCartProductsBinding.btnNext.setOnClickListener{
                startActivity(Intent(this, OrderPlaceActivity::class.java))
            }

            bsCartProductsBinding.tvNumberOfProducts.text = binding.tvNumberOfProducts.text

            adapterCartProducts = AdapterCartProducts()
            bsCartProductsBinding.rvProductsItems.adapter = adapterCartProducts
            adapterCartProducts.differ.submitList(cartProductList)

            bs.show()

        }
    }

    private fun getTotalItemCountInCart() {
        viewModel.fetchTotalCartItemCount().observe(this){
            if(it > 0){
                binding.llCart.visibility = View.VISIBLE
                binding.tvNumberOfProducts.text = it.toString()
            }else{
                binding.llCart.visibility = View.GONE
            }
        }
    }

    override fun showCartLayout(itemCount: Int) {
        val previousCount = binding.tvNumberOfProducts.text.toString().toInt()
        val updatedCount = previousCount + itemCount

        if(updatedCount > 0){
            binding.llCart.visibility = View.VISIBLE
            binding.tvNumberOfProducts.text = updatedCount.toString()
        }else{
            binding.llCart.visibility = View.GONE
            binding.tvNumberOfProducts.text = 0.toString()
        }
    }

    override fun savingCartItemCount(itemCount: Int) {
        viewModel.fetchTotalCartItemCount().observe(this){
            viewModel.savingCartItemCount(it + itemCount)
        }
    }

    override fun hideCartLayout() {
        binding.llCart.visibility = View.GONE
        binding.tvNumberOfProducts.text = 0.toString()
    }
}