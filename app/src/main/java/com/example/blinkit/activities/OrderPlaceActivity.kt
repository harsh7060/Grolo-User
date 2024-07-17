package com.example.blinkit.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.lifecycle.lifecycleScope
import com.example.blinkit.Adapters.AdapterCartProducts
import com.example.blinkit.R
import com.example.blinkit.databinding.ActivityOrderPlaceBinding
import com.example.blinkit.databinding.AddressLayoutBinding
import com.example.blinkit.interfaces.CartListener
import com.example.blinkit.models.Orders
import com.example.blinkit.utils.Utils
import com.example.blinkit.viewModels.UserViewModel
import com.google.firebase.database.FirebaseDatabase
import com.razorpay.Checkout
import com.razorpay.PaymentData
import com.razorpay.PaymentResultWithDataListener
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.security.MessageDigest


class OrderPlaceActivity : AppCompatActivity(), PaymentResultWithDataListener {
    private lateinit var binding: ActivityOrderPlaceBinding
    private val viewModel: UserViewModel by viewModels()
    private lateinit var adapterCartProducts: AdapterCartProducts
    private lateinit var userPhoneNumber: String
    private var cartListener: CartListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderPlaceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setStatusBarAndNavigationBarColors()

        getCurrentUserPhoneNumber()

        initializeRazorPay()

        getAllCartProducts()

        onOrderPlacedBtnClick()

        onBackBtnClick()

    }

    private fun getCurrentUserPhoneNumber() {
        val uid = Utils.getCurrentUserId()
        val db = FirebaseDatabase.getInstance().getReference("All Users").child("Users").child(uid)
        db.get().addOnSuccessListener {
            userPhoneNumber = it.child("userPhoneNumber").value.toString()
        }

    }

    private fun initializeRazorPay() {
        Checkout.preload(this)
        val co = Checkout()
        co.setKeyID("rzp_test_BHGM8UcCWOtupI")
    }

    private fun onOrderPlacedBtnClick() {
        binding.btnNext.setOnClickListener{
            viewModel.getAddressStatus().observe(this){status ->
                if(status){
                    getPaymentView()
                }else{
                    val addressLayoutBinding = AddressLayoutBinding.inflate(LayoutInflater.from(this))
                    val alertDialog = AlertDialog.Builder(this)
                        .setView(addressLayoutBinding.root)
                        .create()
                    alertDialog.show()

                    addressLayoutBinding.btnAddAddress.setOnClickListener{
                        saveAddress(alertDialog, addressLayoutBinding)
                    }
                }
            }
        }
    }

    private fun getPaymentView() {
        val activity: Activity = this
        val co = Checkout()
        try {
            val options = JSONObject()
            options.put("name","Blinkit")
            options.put("description","Total Order Price")
            options.put("image","https://drive.google.com/file/d/1A3aCHQ042O9Mjc7QYl93Otd80CwVnSY1/view?usp=sharing")
            options.put("theme.color", "#FFBE16")
            options.put("currency","INR")
//            options.put("order_id", "order_DBJOWzybf0sJbb")
            options.put("amount",(binding.tvGrandTotal.text.toString().toInt()*100).toString())

            val retryObj = JSONObject();
            retryObj.put("enabled", false);
            retryObj.put("max_count", 4);
            options.put("retry", retryObj);

            val prefill = JSONObject()
            prefill.put("email"," ")
            prefill.put("contact",userPhoneNumber)

            options.put("prefill",prefill)
            co.open(activity,options)
        }catch (e: Exception){
            Utils.showToast(this,  e.message.toString())
            e.printStackTrace()
        }
    }

    private fun saveAddress(alertDialog: AlertDialog, addressLayoutBinding: AddressLayoutBinding) {
        Utils.showDialog(this, "Please wait...Saving address")
        val userPinCode = addressLayoutBinding.etPincode.text.toString()
        val userPhoneNo = addressLayoutBinding.etPhoneNo.text.toString()
        val userState = addressLayoutBinding.etState.text.toString()
        val userCity = addressLayoutBinding.etCity.text.toString()
        val userAddress = addressLayoutBinding.etAddress.text.toString()

        val address = "$userAddress, $userCity ($userState)- $userPinCode, $userPhoneNo"

        lifecycleScope.launch {
            viewModel.saveUserAddress(address)
            viewModel.saveAddressStatus()
        }
        Utils.showToast(this, "Address Saved Successfully ✅")
        alertDialog.dismiss()

        getPaymentView()
    }

    private fun onBackBtnClick() {
        binding.tbOrderFragment.setNavigationOnClickListener{
            startActivity(Intent(this, UsersMainActivity::class.java))
            finish()
        }
    }

    private fun setStatusBarAndNavigationBarColors() {
        window?.statusBarColor = resources.getColor(R.color.white)
        window?.navigationBarColor = resources.getColor(R.color.white)
        val windowInsetsController = ViewCompat.getWindowInsetsController(window?.decorView!!)
        windowInsetsController?.isAppearanceLightStatusBars = true
        windowInsetsController?.isAppearanceLightNavigationBars = true
    }

    private fun getAllCartProducts() {
        viewModel.getAll().observe(this){cartProductList ->
            adapterCartProducts = AdapterCartProducts()
            binding.rvProductsItems.adapter = adapterCartProducts
            adapterCartProducts.differ.submitList(cartProductList)

            var totalPrice = 0
            for(products in cartProductList){
                val itemPrice = products.productPrice!!.substring(1).toInt()
                val itemCount = products.productCount
                totalPrice += (itemPrice* itemCount!!)
            }

            binding.tvSubTotal.text = totalPrice.toString()

            if(totalPrice < 150){
                binding.tvDeliveryCharge.text = "30"
                totalPrice+=30
            }
            binding.tvGrandTotal.text = totalPrice.toString()
        }
    }

    override fun onPaymentSuccess(p0: String?, p1: PaymentData?) {
        Utils.showToast(this, "Payment Done ✅")
        saveOrder()
        lifecycleScope.launch {
            deleteCartProducts()
        }
        Utils.hideDialog()

        startActivity(Intent(this, UsersMainActivity::class.java))
        finish()
    }

    private suspend fun deleteCartProducts() {
        viewModel.deleteCartProducts()
        viewModel.savingCartItemCount(0)
        cartListener?.hideCartLayout()
    }

    private fun saveOrder() {
        viewModel.getAll().observe(this){cartProductList ->
            if(cartProductList.isNotEmpty()){
                viewModel.getUserAddress { address->
                    val order = Orders(
                        orderId = Utils.getRandomId(),
                        orderList = cartProductList,
                        userAddress = address,
                        orderDate = Utils.getCurrentDate(),
                        orderingUserId = Utils.getCurrentUserId(),
                        orderStatus = 0
                    )
                    viewModel.saveOrderedProducts(order)
                    lifecycleScope.launch {
                        viewModel.sendNotification(cartProductList[0].adminUid!!, "Ordered", "An Order is Received.")
                    }
                }
                for(product in cartProductList){
                    val count = product.productCount
                    val stock = product.productStock?.minus(count!!)
                    if (stock != null) {
                        viewModel.saveProductsAfterOrder(stock, product)
                    }
                }
            }
        }
    }

    override fun onPaymentError(p0: Int, p1: String?, p2: PaymentData?) {
        Utils.showToast(this, "Payment Failed ❌ ${p1.toString()}")
    }
}