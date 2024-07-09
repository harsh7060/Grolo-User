package com.example.blinkit.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.blinkit.Adapters.AdapterCategory
import com.example.blinkit.Constants
import com.example.blinkit.R
import com.example.blinkit.databinding.FragmentHomeBinding
import com.example.blinkit.models.Category


class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(layoutInflater)
        setStatusBarColor()
        setAllCategories()
        navigateToSearchFragment()
        return binding.root
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

        binding.rvCategories.adapter = AdapterCategory(categoryList)
    }

    private fun setStatusBarColor() {
        activity?.window?.statusBarColor = resources.getColor(R.color.green)
    }
}