package com.example.blinkit.viewModels

import android.app.Application
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.blinkit.constant.Constants
import com.example.blinkit.models.Orders
import com.example.blinkit.models.Product
import com.example.blinkit.models.User
import com.example.blinkit.roomdb.CartProductDao
import com.example.blinkit.roomdb.CartProducts
import com.example.blinkit.roomdb.CartProductsDatabase
import com.example.blinkit.utils.Utils
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firestore.v1.StructuredQuery.Order
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.callbackFlow
import org.checkerframework.checker.mustcall.qual.MustCallAlias

class UserViewModel(application: Application): AndroidViewModel(application) {

    //Shared Preferences Initialization
    val sharedPreferences: SharedPreferences = application.getSharedPreferences("My_pref", MODE_PRIVATE)
    val cartProductDao : CartProductDao = CartProductsDatabase.getDatabaseInstance(application).cartProductsDao()

    private val _paymentStatus = MutableStateFlow<Boolean>(false)
    val paymentStatus = _paymentStatus

    //Room DB
    suspend fun insertCartProduct(products: CartProducts){
        cartProductDao.insertCartProduct(products)
    }

    suspend fun updateCartProduct(products: CartProducts){
        cartProductDao.updateCartProduct(products)
    }

    suspend fun deleteCartProduct(productId: String){
        cartProductDao.deleteCartProduct(productId)
    }

    fun getAll(): LiveData<List<CartProducts>>{
        return cartProductDao.getAllCartProducts()
    }

    suspend fun deleteCartProducts(){
        cartProductDao.deleteCartProducts()
    }



    //Firebase calls
    fun fetchAllProducts(): Flow<List<Product>> = callbackFlow {
        val db = FirebaseDatabase.getInstance().getReference("Admins").child("All Products")

        val eventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val products = ArrayList<Product>()
                for(product in snapshot.children){
                    val prod = product.getValue(Product::class.java)
                    products.add(prod!!)
                }
                trySend(products)
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        }

        db.addValueEventListener(eventListener)

        awaitClose{db.removeEventListener(eventListener)}

    }

    fun getAllOrders(): Flow<List<Orders>> = callbackFlow {
        val db = FirebaseDatabase.getInstance().getReference("Admins").child("Orders").orderByChild("orderStatus")
        val eventListener = object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val orderList = ArrayList<Orders>()
                for(orders in snapshot.children){
                    val order = orders.getValue(Orders::class.java)
                    if(order?.orderingUserId == Utils.getCurrentUserId()){
                        orderList.add(order)
                    }
                }
                trySend(orderList)
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        }

        db.addValueEventListener(eventListener)
        awaitClose{db.removeEventListener(eventListener)}
    }

    fun getOrderedProducts(orderId: String): Flow<List<CartProducts>> = callbackFlow{
        val db = FirebaseDatabase.getInstance().getReference("Admins").child("Orders").child(orderId)
        val eventListener = object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val order = snapshot.getValue(Orders::class.java)
                trySend(order?.orderList!!)
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        }
        db.addValueEventListener(eventListener)
        awaitClose{db.removeEventListener(eventListener)}
    }

    fun getCategoryProduct(category: String): Flow<List<Product>> = callbackFlow {
        val db = FirebaseDatabase.getInstance().getReference("Admins").child("ProductCategory/${category}")

        val eventListener = object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val products = ArrayList<Product>()
                for(product in snapshot.children){
                    val prod = product.getValue(Product::class.java)
                    products.add(prod!!)
                }
                trySend(products)
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        }

        db.addValueEventListener(eventListener)

        awaitClose{db.removeEventListener(eventListener)}
    }

    fun updateItemCount(product: Product, itemCount: Int) {
        FirebaseDatabase.getInstance().getReference("Admins")
            .child("All Products/${product.productId}").child("itemCount").setValue(itemCount)
        FirebaseDatabase.getInstance().getReference("Admins")
            .child("ProductCategory/${product.productCategory}/${product.productId}")
            .child("itemCount").setValue(itemCount)
        FirebaseDatabase.getInstance().getReference("Admins")
            .child("ProductType/${product.productType}/${product.productId}").child("itemCount")
            .setValue(itemCount)
    }

    fun saveProductsAfterOrder(stock: Int, product: CartProducts){
        FirebaseDatabase.getInstance().getReference("Admins")
            .child("All Products/${product.productId}").child("itemCount").setValue(0)
        FirebaseDatabase.getInstance().getReference("Admins")
            .child("ProductCategory/${product.productCategory}/${product.productId}")
            .child("itemCount").setValue(0)
        FirebaseDatabase.getInstance().getReference("Admins")
            .child("ProductType/${product.productType}/${product.productId}").child("itemCount")
            .setValue(0)


        FirebaseDatabase.getInstance().getReference("Admins")
            .child("All Products/${product.productId}").child("productStock").setValue(stock)
        FirebaseDatabase.getInstance().getReference("Admins")
            .child("ProductCategory/${product.productCategory}/${product.productId}")
            .child("productStock").setValue(stock)
        FirebaseDatabase.getInstance().getReference("Admins")
            .child("ProductType/${product.productType}/${product.productId}").child("productStock")
            .setValue(stock)
    }

    fun saveUserAddress(address: String){
        FirebaseDatabase.getInstance().getReference("All Users").child("Users").child(Utils.getCurrentUserId()).child("userAddress").setValue(address)
    }

    fun getUserAddress(callback: (String) -> Unit){
        val db = FirebaseDatabase.getInstance().getReference("All Users").child("Users").child(Utils.getCurrentUserId()).child("userAddress")
        db.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    val address = snapshot.getValue(String::class.java)
                    callback(address!!)
                }else{
                    callback(null.toString())
                }
            }

            override fun onCancelled(error: DatabaseError) {
                callback(null.toString())
            }

        } )
    }

    fun saveOrderedProducts(orders: Orders){
        FirebaseDatabase.getInstance().getReference("Admins").child("Orders").child(orders.orderId!!).setValue(orders)
    }


    //Shared Preferences
    fun savingCartItemCount(itemCount: Int){
        sharedPreferences.edit().putInt("itemCount", itemCount).apply()
    }

    fun fetchTotalCartItemCount(): MutableLiveData<Int> {
        val totalItemCount = MutableLiveData<Int>()
        totalItemCount.value = sharedPreferences.getInt("itemCount", 0)
        return totalItemCount
    }

    fun saveAddressStatus(){
        sharedPreferences.edit().putBoolean("addressStatus", true).apply()
    }

    fun getAddressStatus(): MutableLiveData<Boolean>{
        val status = MutableLiveData<Boolean>()
        status.value = sharedPreferences.getBoolean("addressStatus", false)
        return status
    }
}