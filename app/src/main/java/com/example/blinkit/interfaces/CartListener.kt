package com.example.blinkit.interfaces

interface CartListener {
    fun showCartLayout(itemCount: Int)

    fun savingCartItemCount(itemCount: Int)
}