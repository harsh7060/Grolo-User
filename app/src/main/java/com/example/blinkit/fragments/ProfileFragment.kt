package com.example.blinkit.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.ViewCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.blinkit.R
import com.example.blinkit.activities.AuthMainActivity
import com.example.blinkit.databinding.AddressBookLayoutBinding
import com.example.blinkit.databinding.FragmentProfileBinding
import com.example.blinkit.databinding.LogoutLayoutBinding
import com.example.blinkit.utils.Utils
import com.example.blinkit.viewModels.UserViewModel

class ProfileFragment : Fragment() {
    private lateinit var binding: FragmentProfileBinding
    private val viewModel: UserViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(layoutInflater)

        setStatusBarAndNavigationBarColors()

        onBackBtnClick()

        onOrdersBtnClick()

        onAddressBtnClick()

        onLogoutBtnClick()

        return binding.root
    }

    private fun onLogoutBtnClick() {
        binding.llLogOut.setOnClickListener{
            val logoutLayoutBinding = LogoutLayoutBinding.inflate(LayoutInflater.from(requireContext()))
            val alertDialog = AlertDialog.Builder(requireContext())
                .setView(logoutLayoutBinding.root)
                .create()
            alertDialog.show()
            alertDialog.setCancelable(false)

            logoutLayoutBinding.btnLogout.setOnClickListener{
                viewModel.logoutUser()
                Utils.showToast(requireContext(), "SignedOut Successfully.")
                startActivity(Intent(requireContext(), AuthMainActivity::class.java))
                requireActivity().finish()
            }
            logoutLayoutBinding.btnCancel.setOnClickListener{
                alertDialog.dismiss()
            }
        }
    }

    private fun onAddressBtnClick() {
        binding.llAdressBook.setOnClickListener{
            val addressBookLayoutBinding = AddressBookLayoutBinding.inflate(LayoutInflater.from(requireContext()))
            viewModel.getUserAddress {address->
                addressBookLayoutBinding.etAddress.setText(address.toString())
            }
            val alertDialog = AlertDialog.Builder(requireContext())
                .setView(addressBookLayoutBinding.root)
                .create()
            alertDialog.show()
            addressBookLayoutBinding.btnEdit.setOnClickListener{
                addressBookLayoutBinding.etAddress.isEnabled = true
            }
            addressBookLayoutBinding.btnSave.setOnClickListener{
                viewModel.saveUserAddress(addressBookLayoutBinding.etAddress.text.toString())
                alertDialog.dismiss()
                Utils.showToast(requireContext(), "Address Updated.")
            }
        }
    }

    private fun onOrdersBtnClick() {
        binding.llOrders.setOnClickListener{
            findNavController().navigate(R.id.action_profileFragment_to_ordersFragment)
        }
    }

    private fun onBackBtnClick() {
        binding.tbProfileFragment.setNavigationOnClickListener{
            findNavController().navigate(R.id.action_profileFragment_to_homeFragment)
        }
    }

    private fun setStatusBarAndNavigationBarColors() {
        activity?.window?.statusBarColor = resources.getColor(R.color.white)
        activity?.window?.navigationBarColor = resources.getColor(R.color.white)
        val windowInsetsController = ViewCompat.getWindowInsetsController(activity?.window?.decorView!!)
        windowInsetsController?.isAppearanceLightStatusBars = true
        windowInsetsController?.isAppearanceLightNavigationBars = true
    }
}