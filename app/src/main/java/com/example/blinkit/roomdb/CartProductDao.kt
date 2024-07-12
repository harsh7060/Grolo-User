package com.example.blinkit.roomdb

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface CartProductDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCartProduct(products: CartProducts)

    @Update
    fun updateCartProduct(products: CartProducts)

    @Query("DELETE FROM CartProducts WHERE productId = :productId")
    fun deleteCartProduct(productId: String)

    @Query("SELECT * FROM CartProducts")
    fun getAllCartProducts(): LiveData<List<CartProducts>>
}