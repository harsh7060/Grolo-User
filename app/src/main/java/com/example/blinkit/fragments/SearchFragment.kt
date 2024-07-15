package com.example.blinkit.fragments

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.blinkit.Adapters.AdapterProduct
import com.example.blinkit.R
import com.example.blinkit.databinding.FragmentSearchBinding
import com.example.blinkit.databinding.ItemViewProductBinding
import com.example.blinkit.interfaces.CartListener
import com.example.blinkit.models.Product
import com.example.blinkit.roomdb.CartProducts
import com.example.blinkit.utils.Utils
import com.example.blinkit.viewModels.UserViewModel
import kotlinx.coroutines.launch

class SearchFragment : Fragment() {
    private lateinit var binding: FragmentSearchBinding
    private lateinit var adapterProduct: AdapterProduct
    val viewModel: UserViewModel by viewModels()
    private var cartListener: CartListener? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchBinding.inflate(inflater)

        setStatusBarAndNavigationBarColors()

        getAllProducts()

        searchProduct()

        backToHomeFragment()

        return binding.root
    }

    private fun searchProduct() {
        binding.etSearch.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }


            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s.toString().trim()
                adapterProduct.getFilter().filter(query)
            }

            override fun afterTextChanged(s: Editable?) {

            }

        })
    }

    private fun backToHomeFragment() {
        binding.etBack.setOnClickListener{
            findNavController().navigate(R.id.action_searchFragment_to_homeFragment)
        }
    }

    private fun getAllProducts() {
        binding.shimmerViewContainer.visibility = View.VISIBLE
        lifecycleScope.launch {
            viewModel.fetchAllProducts().collect{
                if(it.isEmpty()){
                    binding.rvProducts.visibility = View.GONE
                    binding.tvText.visibility = View.VISIBLE
                }else{
                    binding.rvProducts.visibility = View.VISIBLE
                    binding.tvText.visibility = View.GONE
                }
                adapterProduct = AdapterProduct(
                    ::onAddBtnClick,
                    ::onIncrementClick,
                    ::onDecrementClick
                )
                binding.rvProducts.adapter = adapterProduct
                adapterProduct.differ.submitList(it)
                adapterProduct.originalList = it as ArrayList<Product>
                binding.shimmerViewContainer.visibility = View.GONE
            }
        }
    }

    private fun onAddBtnClick(product: Product, productBinding: ItemViewProductBinding) {
        productBinding.tvAdd.visibility = View.GONE
        productBinding.llProductCount.visibility = View.VISIBLE

        var itemCount = productBinding.tvProductCount.text.toString().toInt()
        itemCount++
        productBinding.tvProductCount.text = itemCount.toString()

        cartListener?.showCartLayout(1)

        product.itemCount = itemCount
        lifecycleScope.launch {
            cartListener?.savingCartItemCount(1)
            saveProductInRoomDb(product)
            viewModel.updateItemCount(product, itemCount)
        }
    }

    private fun onIncrementClick(product: Product, productBinding: ItemViewProductBinding) {
        var itemCountInc = productBinding.tvProductCount.text.toString().toInt()
        itemCountInc++

        if(product.productStock!! + 1 > itemCountInc){
            productBinding.tvProductCount.text = itemCountInc.toString()

            cartListener?.showCartLayout(1)

            product.itemCount = itemCountInc
            lifecycleScope.launch {
                cartListener?.savingCartItemCount(1)
                saveProductInRoomDb(product)
                viewModel.updateItemCount(product, itemCountInc)
            }
        }else{
            Utils.showToast(requireContext(), "Product Out of Stock ❗")
        }
    }

    private fun onDecrementClick(product: Product, productBinding: ItemViewProductBinding) {
        var itemCountDec = productBinding.tvProductCount.text.toString().toInt()
        itemCountDec--

        product.itemCount = itemCountDec
        lifecycleScope.launch {
            cartListener?.savingCartItemCount(-1)
            saveProductInRoomDb(product)
            viewModel.updateItemCount(product, itemCountDec)
        }

        if(itemCountDec > 0){
            productBinding.tvProductCount.text = itemCountDec.toString()
        }else{
            lifecycleScope.launch {
                viewModel.deleteCartProduct(product.productId!!)
            }
            productBinding.tvAdd.visibility = View.VISIBLE
            productBinding.llProductCount.visibility = View.GONE
            productBinding.tvProductCount.text = 0.toString()
        }

        cartListener?.showCartLayout(-1)
    }

    private fun saveProductInRoomDb(product: Product) {
        val cartProduct = CartProducts(
            productId = product.productId!!,
            productName = product.productName,
            productQuantity = product.productQuantity.toString() + product.productUnit.toString(),
            productPrice = "₹"+"${product.productPrice}",
            productCount = product.itemCount,
            productStock = product.productStock,
            productImage = product.productImageUris?.get(0)!!,
            productCategory = product.productCategory,
            adminUid = product.adminUid,
            productType = product.productType
        )

        lifecycleScope.launch {
            viewModel.insertCartProduct(cartProduct)
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if(context is CartListener){
            cartListener = context
        }else{
            throw ClassCastException("Please Implement CartListener")
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