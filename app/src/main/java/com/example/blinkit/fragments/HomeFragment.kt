package com.example.blinkit.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.blinkit.Adapters.AdapterBestseller
import com.example.blinkit.Adapters.AdapterCategory
import com.example.blinkit.Adapters.AdapterProduct
import com.example.blinkit.constant.Constants
import com.example.blinkit.R
import com.example.blinkit.databinding.BgSeeAllBinding
import com.example.blinkit.databinding.FragmentHomeBinding
import com.example.blinkit.databinding.ItemViewProductBinding
import com.example.blinkit.interfaces.CartListener
import com.example.blinkit.models.Bestseller
import com.example.blinkit.models.Category
import com.example.blinkit.models.Product
import com.example.blinkit.roomdb.CartProducts
import com.example.blinkit.utils.Utils
import com.example.blinkit.viewModels.UserViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.launch


class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private val viewModel: UserViewModel by viewModels()
    private lateinit var adapterBestseller: AdapterBestseller
    private lateinit var adapterProduct: AdapterProduct
    private var cartListener: CartListener? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(layoutInflater)
        setStatusBarColor()
        setAllCategories()
        fetchBestseller()
        navigateToSearchFragment()
        get()
        onProfileBtnClick()
        return binding.root
    }

    private fun fetchBestseller() {
        binding.shimmerViewContainer.visibility = View.VISIBLE
        lifecycleScope.launch {
            viewModel.fetchProductTypes().collect{
                adapterBestseller = AdapterBestseller(::onSeeAllButtonClick)
                binding.rvBestSellers.adapter = adapterBestseller
                adapterBestseller.differ.submitList(it)
                binding.shimmerViewContainer.visibility = View.GONE
            }
        }
    }

    private fun onProfileBtnClick() {
        binding.ivProfile.setOnClickListener{
            findNavController().navigate(R.id.action_homeFragment_to_profileFragment)
        }
    }

    private fun get(){
        viewModel.getAll().observe(viewLifecycleOwner){
            for(i in it){
                Log.d("vvv", i.productName.toString())
                Log.d("vvv", i.productCount.toString())
            }
        }
    }

    private fun navigateToSearchFragment() {
        binding.searchCV.setOnClickListener{
            findNavController().navigate(R.id.action_homeFragment_to_searchFragment)
        }
    }

    private fun setAllCategories() {
        val categoryList = ArrayList<Category>()
        for(i in 0 until Constants.allProductCategory.size){
            categoryList.add(Category(Constants.allProductCategory[i], Constants.allProductCategoryIcon[i]))
        }

        binding.rvCategories.adapter = AdapterCategory(categoryList,::onCategoryIconClick)
    }

    private fun onCategoryIconClick(category: Category){
        val bundle = Bundle()
        bundle.putString("category", category.title)
        findNavController().navigate(R.id.action_homeFragment_to_categoryFragment, bundle)
    }

    private fun onSeeAllButtonClick(productType: Bestseller){
        val bsSeeAll = BgSeeAllBinding.inflate(LayoutInflater.from(requireContext()))

        val bg = BottomSheetDialog(requireContext())
        bg.setContentView(bsSeeAll.root)

        adapterProduct = AdapterProduct(::onAddBtnClick, ::onIncrementClick, ::onDecrementClick)
        bsSeeAll.rvProducts.adapter = adapterProduct
        adapterProduct.differ.submitList(productType.products)

        bg.show()
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


    private fun setStatusBarColor() {
        activity?.window?.statusBarColor = resources.getColor(R.color.green)
    }
}