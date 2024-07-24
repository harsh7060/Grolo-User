package com.example.blinkit.viewModels

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.blinkit.utils.Utils
import com.example.blinkit.models.User
import com.google.firebase.FirebaseException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.flow.MutableStateFlow
import java.util.concurrent.TimeUnit

class AuthViewModel: ViewModel(){
    private val _isSignedInSuccessfully = MutableStateFlow<Boolean>(false)
    val isSignedInSuccessfully = _isSignedInSuccessfully

    private val _isSignedUpSuccessfully = MutableStateFlow<Boolean>(false)
    val isSignedUpSuccessfully = _isSignedUpSuccessfully

    private val _isCurrentUser = MutableStateFlow<Boolean>(false)
    val isCurrentUser = _isCurrentUser

    init {
        Utils.getAuthInstance().currentUser?.let {
            _isCurrentUser.value = true
        }
    }

    fun signInWithCredentials(email: String, password: String, context: Context) {
        Utils.getAuthInstance().signInWithEmailAndPassword(email, password)
            .addOnCompleteListener{task->
                if(task.isSuccessful){
                    isSignedInSuccessfully.value = true
                }else{
                    Utils.showToast(context, "Login Unsuccessfully")
                }
            }
    }

    fun signUpWithCredentials(email: String, password: String, user: User, context: Context){
        Utils.getAuthInstance().createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener{task->
                user.uid = Utils.getCurrentUserId()
                if(task.isSuccessful){
                    FirebaseDatabase.getInstance().getReference("All Users").child("Users").child(user.uid!!).setValue(user)
                    isSignedUpSuccessfully.value = true
                }else{
                    Utils.showToast(context, "User Registered Unsuccessfully")
                }
            }
    }
}