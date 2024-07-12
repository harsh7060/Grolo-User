package com.example.blinkit.models


data class Product(
    var productId: String? = null,
    var productName: String? = null,
    var productPrice: Int? = null,
    var productQuantity: Int? = null,
    var productUnit: String? = null,
    var productStock: Int? = null,
    var productCategory: String? = null,
    var productType: String? = null,
    var itemCount: Int? = null,
    var adminUid: String? = null,
    var productImageUris: ArrayList<String?>?= null
)
