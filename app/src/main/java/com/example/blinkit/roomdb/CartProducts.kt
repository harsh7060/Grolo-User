package com.example.blinkit.roomdb

import androidx.annotation.NonNull
import androidx.room.Entity
import androidx.room.PrimaryKey
import javax.annotation.Nonnull

@Entity(tableName = "CartProducts")
data class CartProducts(
    @PrimaryKey
    var productId: String = "random",

    var productName: String? = null,
    var productPrice: String? = null,
    var productQuantity: String? = null,
    var productStock: Int? = null,
    var productImage: String? = null,
    var productCategory: String? = null,
    var adminUid: String? = null,
    var productCount: Int? = null
)