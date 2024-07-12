package com.example.blinkit.classes

import android.widget.Filter
import com.example.blinkit.Adapters.AdapterProduct
import com.example.blinkit.models.Product
import java.util.Locale

class FilteringProduct(
    val adapter: AdapterProduct,
    val filter: ArrayList<Product>
): Filter() {
    override fun performFiltering(constraint: CharSequence?): FilterResults {
        val result = FilterResults()
        if(!constraint.isNullOrEmpty()){
            val query = constraint.toString().trim().uppercase(Locale.getDefault()).split(" ")
            val filteredList = ArrayList<Product>()
            for (product in filter) {
                if(query.any{
                        product.productName?.uppercase(Locale.getDefault())?.contains(it)==true ||
                        product.productCategory?.uppercase(Locale.getDefault())?.contains(it)==true ||
                        product.productType?.uppercase(Locale.getDefault())?.contains(it)==true ||
                        product.productPrice?.toString()?.uppercase(Locale.getDefault())?.contains(it)==true
                    }){
                    filteredList.add(product)
                }
            }
            result.values = filteredList
            result.count = filteredList.size
        }
        else{
            result.values = filter
            result.count = filter.size
        }

        return result
    }

    override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
        adapter.differ.submitList(results!!.values as ArrayList<Product>)
    }
}