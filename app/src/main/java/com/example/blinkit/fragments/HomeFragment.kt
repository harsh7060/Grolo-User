package com.example.blinkit.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.blinkit.Adapters.AdapterCategory
import com.example.blinkit.constant.Constants
import com.example.blinkit.R
import com.example.blinkit.databinding.FragmentHomeBinding
import com.example.blinkit.models.Category
import com.example.blinkit.viewModels.UserViewModel


class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private val viewModel: UserViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(layoutInflater)
        setStatusBarColor()
        setAllCategories()
        navigateToSearchFragment()
        get()
        onProfileBtnClick()
        return binding.root
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

    private fun setStatusBarColor() {
        activity?.window?.statusBarColor = resources.getColor(R.color.green)
    }
}